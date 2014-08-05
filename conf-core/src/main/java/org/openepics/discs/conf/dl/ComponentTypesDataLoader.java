package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeProperty;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.As;

import com.google.common.base.Objects;

@Stateless
@ComponentTypesLoaderQualifier
public class ComponentTypesDataLoader extends AbstractDataLoader implements DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private ComptypeEJB comptypeEJB;
    @Inject private ConfigurationEJB configurationEJB;
    private int nameIndex, descriptionIndex;

    @Override
    public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {
        loaderResult = new DataLoaderResult();
        final ArrayList<String> fields = new ArrayList<>();
        fields.add("NAME");
        fields.add("DESCRIPTION");
        /*
         * List does not contain any rows that do not have a value (command) in
         * the first column. There should be no commands before "HEADER".
         */
        List<String> headerRow = inputRows.get(0);

        setUpIndexesForFields(headerRow);
        HashMap<String, Integer> indexByPropertyName = indexByPropertyName(fields, headerRow);
        checkPropertyAssociation(indexByPropertyName, headerRow.get(0));

        if (rowResult.isError()) {
            loaderResult.addResult(rowResult);
            return loaderResult;
        } else {
            for (List<String> row : inputRows.subList(1, inputRows.size())) {
                final String rowNumber = row.get(0);
                loaderResult.addResult(rowResult);
                rowResult = new DataLoaderResult();
                if (Objects.equal(row.get(commandIndex), CMD_HEADER)) {
                    headerRow = row;
                    setUpIndexesForFields(headerRow);
                    indexByPropertyName = indexByPropertyName(fields, headerRow);
                    checkPropertyAssociation(indexByPropertyName, rowNumber);
                    if (rowResult.isError()) {
                        return loaderResult;
                    } else {
                        continue; // skip the rest of the processing for HEADER
                                  // row
                    }
                } else if (row.get(commandIndex).equals(CMD_END)) {
                    break;
                }

                final String command = As.notNull(row.get(commandIndex).toUpperCase());
                final @Nullable String name = row.get(nameIndex);
                final @Nullable String description = descriptionIndex == -1 ? null : row.get(descriptionIndex);
                final String modifiedBy = loginManager.getUserid();

                if (name == null) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(nameIndex)));
                    continue; //Continue to next row
                }

                switch (command) {
                case CMD_UPDATE:
                    final ComponentType componentTypeToUpdate = comptypeEJB.findComponentTypeByName(name);
                    if (componentTypeToUpdate != null) {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
                            componentTypeToUpdate.setDescription(description);
                            addOrUpdateProperties(componentTypeToUpdate, indexByPropertyName, row, rowNumber, modifiedBy);
                            if (rowResult.isError()) {
                                continue;
                            }
                        } else {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                        }
                    } else {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.CREATE)) {
                            final ComponentType compTypeToAdd = new ComponentType(name, modifiedBy);
                            compTypeToAdd.setDescription(description);
                            comptypeEJB.addComponentType(compTypeToAdd);
                            addOrUpdateProperties(compTypeToAdd, indexByPropertyName, row, rowNumber, modifiedBy);
                            if (rowResult.isError()) {
                                continue;
                            }
                        } else {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                        }
                    }
                    break;
                case CMD_DELETE:
                    final ComponentType componentTypeToDelete = comptypeEJB.findComponentTypeByName(name);
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.DELETE)) {
                        if (componentTypeToDelete == null) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                            continue;
                        } else {
                            comptypeEJB.deleteComponentType(componentTypeToDelete);
                        }
                    } else {
                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                    }
                    break;
                case CMD_RENAME:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.RENAME)) {
                        final int startOldNameMarkerIndex = name.indexOf("[");
                        final int endOldNameMarkerIndex = name.indexOf("]");
                        if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.RENAME_MISFORMAT, rowNumber, headerRow.get(nameIndex)));
                            continue;
                        }

                        final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                        final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                        final ComponentType componentTypeToRename = comptypeEJB.findComponentTypeByName(oldName);
                        if (componentTypeToRename != null) {
                            if (comptypeEJB.findComponentTypeByName(newName) != null) {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, rowNumber, headerRow.get(nameIndex)));
                                continue;
                            } else {
                                componentTypeToRename.setName(newName);
                                comptypeEJB.saveComponentType(componentTypeToRename);
                            }
                        } else {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                            continue;
                        }
                    } else {
                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                    }
                    break;
                default:
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, rowNumber, headerRow.get(commandIndex)));
                }
            }
        }
        loaderResult.addResult(rowResult);
        return loaderResult;
    }

    @Override
    protected void setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        rowResult = new DataLoaderResult();
        nameIndex = setUpFieldIndex(header, "NAME");
        descriptionIndex = setUpFieldIndex(header, "DESCRIPTION");


        if (nameIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "NAME"));
        } else if (descriptionIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "DESCRIPTION"));
        }
    }

    private void checkPropertyAssociation(Map<String, Integer> properties, String rowNumber) {
        final Iterator<String> propertiesIterator = properties.keySet().iterator();
        while (propertiesIterator.hasNext()) {
            final String propertyName = propertiesIterator.next();
            final @Nullable Property property = configurationEJB.findPropertyByName(propertyName);
            if (property == null) {
                rowResult.addMessage(new ValidationMessage(ErrorMessage.PROPERTY_NOT_FOUND, rowNumber, propertyName));
            } else {
                final PropertyAssociation propAssociation = property.getAssociation();
                if (propAssociation != PropertyAssociation.ALL && propAssociation != PropertyAssociation.TYPE && propAssociation != PropertyAssociation.TYPE_DEVICE && propAssociation != PropertyAssociation.TYPE_SLOT) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.PROPERTY_ASSOCIATION_FAILURE, rowNumber, propertyName));
                }
            }
        }
    }

    private void addOrUpdateProperties(ComponentType compType, Map<String, Integer> properties, List<String> row, String rowNumber, String modifiedBy) {
        final Iterator<String> propertiesIterator = properties.keySet().iterator();
        final List<ComptypeProperty> compTypeProperties = new ArrayList<>();
        if (compType.getComptypePropertyList() != null) {
            compTypeProperties.addAll(compType.getComptypePropertyList());
        }
        final Map<Property, ComptypeProperty> compTypePropertyByProperty = new HashMap<>();

        for (ComptypeProperty compProperty : compTypeProperties) {
            compTypePropertyByProperty.put(compProperty.getProperty(), compProperty);
        }

        while (propertiesIterator.hasNext()) {
            final String propertyName = propertiesIterator.next();
            final int propertyIndex = properties.get(propertyName);
            final @Nullable Property property = configurationEJB.findPropertyByName(propertyName);
            final @Nullable String propertyValue = row.get(propertyIndex);
            if (compTypePropertyByProperty.containsKey(property)) {
                final ComptypeProperty compTypePropertyToUpdate = compTypePropertyByProperty.get(property);
                if (propertyValue == null) {
                    comptypeEJB.deleteCompTypeProp(compTypePropertyToUpdate);
                } else {
                    compTypePropertyToUpdate.setPropValue(propertyValue);
                    compTypePropertyToUpdate.setModifiedBy(modifiedBy);
                    comptypeEJB.saveCompTypeProp(compTypePropertyToUpdate, false);
                }

            } else if (propertyValue != null) {
                final ComptypeProperty comptypePropertyToAdd = new ComptypeProperty(false, modifiedBy);
                comptypePropertyToAdd.setProperty(property);
                comptypePropertyToAdd.setPropValue(propertyValue);
                comptypePropertyToAdd.setComponentType(compType);
                comptypeEJB.addCompTypeProp(comptypePropertyToAdd);
            }
        }
    }
}
