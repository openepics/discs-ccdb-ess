package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureReason;
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
    @PersistenceContext private EntityManager em;
    private int nameIndex, descriptionIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {

        if (inputRows != null && inputRows.size() > 0) {
            final ArrayList<String> fields = new ArrayList<>();
            fields.add("NAME");
            fields.add("DESCRIPTION");
            /*
             * List does not contain any rows that do not have a value (command)
             * in the first column. There should be no commands before "HEADER".
             */
            List<String> headerRow = inputRows.get(0);

            DataLoaderResult fieldsIndexSetupResult = setUpIndexesForFields(headerRow);
            HashMap<String,Integer> indexByPropertyName = indexByPropertyName(fields, headerRow);

            if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                return fieldsIndexSetupResult;
            } else {
                for (List<String> row : inputRows.subList(1, inputRows.size())) {
                    final String rowNumber = row.get(0);
                    if (Objects.equal(row.get(1), CMD_HEADER)) {
                        headerRow = row;
                        fieldsIndexSetupResult = setUpIndexesForFields(headerRow);
                        indexByPropertyName = indexByPropertyName(fields, headerRow);
                        if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                            return fieldsIndexSetupResult;
                        } else {
                            continue; // skip the rest of the processing for HEADER row
                        }
                    } else if (row.get(1).equals(CMD_END)) {
                        break;
                    }

                    final String command = As.notNull(row.get(1).toUpperCase());
                    final @Nullable String name = row.get(nameIndex);
                    final @Nullable String description = row.get(descriptionIndex);
                    final String modifiedBy = loginManager.getUserid();

                    if (name == null) {
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.REQUIRED_FIELD_MISSING);
                    }

                    switch (command) {
                    case CMD_UPDATE:
                        final ComponentType componentTypeToUpdate = comptypeEJB.findComponentTypeByName(name);
                        if (componentTypeToUpdate != null) {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
                                //TODO Update
                            } else {
                                return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.UPDATE);
                            }
                        } else {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.CREATE)) {
                                final ComponentType compTypeToAdd = new ComponentType(name, modifiedBy);
                                compTypeToAdd.setDescription(description);
                                comptypeEJB.addComponentType(compTypeToAdd);
                                DataLoaderResult addPropertiesResult = addProperties(compTypeToAdd, indexByPropertyName, row, rowNumber, modifiedBy);
                                if (addPropertiesResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                                    return addPropertiesResult;
                                }
                            } else {
                                return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.CREATE);
                            }
                        }
                        break;
                    case CMD_DELETE:
                        final ComponentType componentTypeToDelete = comptypeEJB.findComponentTypeByName(name);
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.DELETE)) {
                            if (componentTypeToDelete == null) {
                                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.COMPONENT_TYPE);
                            } else {
                                comptypeEJB.deleteComponentType(componentTypeToDelete);
                            }
                        } else {
                            return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.DELETE);
                        }
                        break;
                    case CMD_RENAME:
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.COMPONENT_TYPE, EntityTypeOperation.RENAME)) {
                            final int startOldNameMarkerIndex = name.indexOf("[");
                            final int endOldNameMarkerIndex = name.indexOf("]");
                            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                                return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.RENAME_MISFORMAT);
                            }

                            final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                            final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                            final ComponentType componentTypeToRename = comptypeEJB.findComponentTypeByName(oldName);
                            if (componentTypeToRename != null) {
                                if (comptypeEJB.findComponentTypeByName(newName) != null) {
                                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.DUPLICATE_ENTITY);
                                } else {
                                    componentTypeToRename.setName(newName);
                                    comptypeEJB.saveComponentType(componentTypeToRename);
                                }
                            } else {
                                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.COMPONENT_TYPE);
                            }
                        } else {
                            return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.RENAME);
                        }
                        break;
                    default:
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.COMMAND_NOT_VALID);
                    }
                }
            }
        }

        return new DataLoaderResult.SuccessDataLoaderResult();
    }

    @Override protected DataLoaderResult setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        nameIndex = header.indexOf("NAME");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1 || descriptionIndex == -1) {
            return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.HEADER_FIELD_MISSING);
        } else {
            return new DataLoaderResult.SuccessDataLoaderResult();
        }
    }

    private DataLoaderResult addProperties(ComponentType compType, Map<String,Integer> properties, List<String> row, String rowNumber, String modifiedBy) {
        final Iterator<String> propertiesIterator = properties.keySet().iterator();
        while (propertiesIterator.hasNext()) {
            final String propertyName = propertiesIterator.next();
            final int propertyIndex = properties.get(propertyName);
            final @Nullable Property property = configurationEJB.findPropertyByName(propertyName);
            final @Nullable String propertyValue = row.get(propertyIndex);
            if (property == null) {
                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.PROPERTY);
            } else if (propertyValue != null) {
                if (property.getAssociation().equals(PropertyAssociation.ALL) || property.getAssociation().equals(PropertyAssociation.TYPE) ||
                        property.getAssociation().equals(PropertyAssociation.TYPE_DEVICE) || property.getAssociation().equals(PropertyAssociation.TYPE_SLOT)) {
                    ComptypeProperty comptypePropertyToAdd = new ComptypeProperty(false, modifiedBy);
                    comptypePropertyToAdd.setProperty(property);
                    comptypePropertyToAdd.setPropValue(propertyValue);
                    comptypePropertyToAdd.setComponentType(compType);
                    comptypeEJB.addCompTypeProp(comptypePropertyToAdd);
                } else {
                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.WRONG_VALUE);
                }
            }

        }

        return new DataLoaderResult.SuccessDataLoaderResult();
    }

}
