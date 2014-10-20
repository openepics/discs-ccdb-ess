package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.util.As;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

@Stateless
@ComponentTypesLoaderQualifier
public class ComponentTypesDataLoader extends AbstractDataLoader implements DataLoader {
    @Inject private ComptypeEJB comptypeEJB;
    @Inject private PropertyEJB propertyEJB;
    private int nameIndex, descriptionIndex;

    @Override
    public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {
        loaderResult = new DataLoaderResult();
        final List<String> fields = ImmutableList.of("NAME", "DESCRIPTION");
        /*
         * List does not contain any rows that do not have a value (command) in
         * the first column. There should be no commands before "HEADER".
         */
        List<String> headerRow = inputRows.get(0);
        checkForDuplicateHeaderEntries(headerRow);
        if (rowResult.isError()) {
            loaderResult.addResult(rowResult);
            return loaderResult;
        }

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
                    checkForDuplicateHeaderEntries(headerRow);
                    if (rowResult.isError()) {
                        loaderResult.addResult(rowResult);
                        return loaderResult;
                    }
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

                if (name == null) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(nameIndex)));
                    continue; //Continue to next row
                }

                switch (command) {
                case CMD_UPDATE:
                    final ComponentType componentTypeToUpdate = comptypeEJB.findByName(name);
                    if (componentTypeToUpdate != null) {
                        if (componentTypeToUpdate.getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                            continue;
                        }
                        try {
                            componentTypeToUpdate.setDescription(description);
                            addOrUpdateProperties(componentTypeToUpdate, indexByPropertyName, row, rowNumber);
                            if (rowResult.isError()) {
                                continue;
                            }
                        } catch (Exception e) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                        }
                    } else {
                        try {
                            final ComponentType compTypeToAdd = new ComponentType(name);
                            compTypeToAdd.setDescription(description);
                            comptypeEJB.add(compTypeToAdd);
                            addOrUpdateProperties(compTypeToAdd, indexByPropertyName, row, rowNumber);
                            if (rowResult.isError()) {
                                continue;
                            }
                        } catch (Exception e) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                        }
                    }
                    break;
                case CMD_DELETE:
                    final ComponentType componentTypeToDelete = comptypeEJB.findByName(name);
                    try {
                        if (componentTypeToDelete == null) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                            continue;
                        } else {
                            if (componentTypeToDelete.getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                continue;
                            }
                            comptypeEJB.delete(componentTypeToDelete);
                        }
                    } catch (Exception e) {
                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                    }
                    break;
                case CMD_RENAME:
                    try {
                        final int startOldNameMarkerIndex = name.indexOf("[");
                        final int endOldNameMarkerIndex = name.indexOf("]");
                        if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.RENAME_MISFORMAT, rowNumber, headerRow.get(nameIndex)));
                            continue;
                        }

                        final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                        final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                        final ComponentType componentTypeToRename = comptypeEJB.findByName(oldName);
                        if (componentTypeToRename != null) {
                            if (componentTypeToRename.getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                continue;
                            }
                            if (comptypeEJB.findByName(newName) != null) {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, rowNumber, headerRow.get(nameIndex)));
                                continue;
                            } else {
                                componentTypeToRename.setName(newName);
                                comptypeEJB.save(componentTypeToRename);
                            }
                        } else {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                            continue;
                        }
                    } catch (Exception e) {
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
        nameIndex = header.indexOf("NAME");
        descriptionIndex = header.indexOf("DESCRIPTION");


        if (nameIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "NAME"));
        } else if (descriptionIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "DESCRIPTION"));
        }
    }

    private void checkPropertyAssociation(Map<String, Integer> properties, String rowNumber) {
        for (Entry<String, Integer> entry : properties.entrySet()) {
            final String propertyName = entry.getKey();
            final @Nullable Property property = propertyEJB.findByName(propertyName);
            if (property == null) {
                rowResult.addMessage(new ValidationMessage(ErrorMessage.PROPERTY_NOT_FOUND, rowNumber, entry.getKey()));
            } else {
                final PropertyAssociation propAssociation = property.getAssociation();
                if (propAssociation != PropertyAssociation.ALL && propAssociation != PropertyAssociation.TYPE && propAssociation != PropertyAssociation.TYPE_DEVICE && propAssociation != PropertyAssociation.TYPE_SLOT) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.PROPERTY_ASSOCIATION_FAILURE, rowNumber, propertyName));
                }
            }
        }
    }

    private void addOrUpdateProperties(ComponentType compType, Map<String, Integer> properties, List<String> row, String rowNumber) {
        final List<ComptypePropertyValue> compTypeProperties = new ArrayList<>();
        if (compType.getComptypePropertyList() != null) {
            compTypeProperties.addAll(compType.getComptypePropertyList());
        }
        final Map<Property, ComptypePropertyValue> compTypePropertyByProperty = new HashMap<>();

        for (ComptypePropertyValue compProperty : compTypeProperties) {
            compTypePropertyByProperty.put(compProperty.getProperty(), compProperty);
        }

        for (Entry<String, Integer> entry : properties.entrySet()) {
            final String propertyName = entry.getKey();
            final int propertyIndex = entry.getValue();
            final @Nullable Property property = propertyEJB.findByName(propertyName);
            final @Nullable String propertyValue = row.get(propertyIndex);
            if (compTypePropertyByProperty.containsKey(property)) {
                final ComptypePropertyValue compTypePropertyToUpdate = compTypePropertyByProperty.get(property);
                if (propertyValue == null) {
                    comptypeEJB.deleteChild(compTypePropertyToUpdate);
                } else {
                    compTypePropertyToUpdate.setPropValue(Conversion.stringToValue(propertyValue, property));
                    comptypeEJB.saveChild(compTypePropertyToUpdate);
                }

            } else if (propertyValue != null) {
                final ComptypePropertyValue comptypePropertyToAdd = new ComptypePropertyValue(false);
                comptypePropertyToAdd.setProperty(property);
                comptypePropertyToAdd.setPropValue(Conversion.stringToValue(propertyValue, property));
                comptypePropertyToAdd.setComponentType(compType);
                comptypeEJB.addChild(comptypePropertyToAdd);
            }
        }
    }
}
