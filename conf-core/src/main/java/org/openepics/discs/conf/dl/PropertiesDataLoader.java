/**
 *
 */
package org.openepics.discs.conf.dl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.NotAuthorizedException;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureReason;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.As;

import com.google.common.base.Objects;

/**
 * Implementation of data loader for properties
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless
@PropertiesLoaderQualifier
public class PropertiesDataLoader extends AbstractDataLoader implements DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private ConfigurationEJB configurationEJB;
    @PersistenceContext private EntityManager em;
    private Map<String, Property> propertyByName;
    private int nameIndex, associationIndex, unitIndex, dataTypeIndex, descriptionIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {
        init();

        if (inputRows != null && inputRows.size() > 0) {
            /*
             * List does not contain any rows that do not have a value (command)
             * in the first column. There should be no commands before "HEADER".
             */
            List<String> headerRow = inputRows.get(0);

            DataLoaderResult fieldsIndexSetupResult = setUpIndexesForFields(headerRow);

            if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                return fieldsIndexSetupResult;
            } else {
                for (List<String> row : inputRows.subList(1, inputRows.size())) {
                    final String rowNumber = row.get(0);
                    if (Objects.equal(row.get(1), CMD_HEADER)) {
                        headerRow = row;
                        setUpIndexesForFields(headerRow);
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
                    final @Nullable String unit = row.get(unitIndex);
                    final @Nullable String dataType = row.get(dataTypeIndex);
                    final @Nullable String description = row.get(descriptionIndex);
                    final @Nullable String association = row.get(associationIndex);
                    final Date modifiedAt = new Date();
                    final String modifiedBy = loginManager.getUserid();

                    if (name == null || unit == null || dataType == null || description == null || association == null) {
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.REQUIRED_FIELD_MISSING);
                    }

                    switch (command) {
                    case CMD_UPDATE:
                        if (propertyByName.containsKey(name)) {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.UPDATE)) {
                                final Property propertyToUpdate = propertyByName.get(name);
                                propertyToUpdate.setDescription(description);
                                propertyToUpdate.setAssociation(association);
                                propertyToUpdate.setModifiedAt(modifiedAt);
                                propertyToUpdate.setModifiedBy(modifiedBy);
                                final DataLoaderResult setPropertyFieldsResult = setPropertyFields(propertyToUpdate, unit, dataType, rowNumber);
                                if (setPropertyFieldsResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                                    return setPropertyFieldsResult;
                                } else {
                                    configurationEJB.saveProperty(propertyToUpdate);
                                }
                            } else {
                                return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.UPDATE);
                            }
                        } else {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.CREATE)) {
                                final Property propertyToAdd = new Property(null, name, description, association, modifiedAt, modifiedBy, 0);
                                final DataLoaderResult setPropertyFieldsResult = setPropertyFields(propertyToAdd, unit, dataType, rowNumber);
                                if (setPropertyFieldsResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                                    return setPropertyFieldsResult;
                                } else {
                                    configurationEJB.addProperty(propertyToAdd);
                                    propertyByName.put(propertyToAdd.getName(), propertyToAdd);
                                }
                            } else {
                                return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.CREATE);
                            }
                        }
                        break;
                    case CMD_DELETE:
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.DELETE)) {
                            final Property propertyToDelete = propertyByName.get(name);
                            if (propertyToDelete == null) {
                                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.PROPERTY);
                            } else {
                                configurationEJB.deleteProperty(propertyToDelete);
                                propertyByName.remove(propertyToDelete.getName());
                            }
                        } else {
                            throw new NotAuthorizedException(EntityTypeOperation.DELETE, EntityType.PROPERTY);
                        }
                        break;
                    case CMD_RENAME:
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.RENAME)) {
                            final int startOldNameMarkerIndex = name.indexOf("[");
                            final int endOldNameMarkerIndex = name.indexOf("]");
                            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                                return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.RENAME_MISFORMAT);
                            }

                            final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                            final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                            if (propertyByName.containsKey(oldName)) {
                                if (propertyByName.containsKey(newName)) {
                                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.DUPLICATE_ENTITY);
                                } else {
                                    final Property propertyToRename = propertyByName.get(oldName);
                                    propertyToRename.setName(newName);
                                    configurationEJB.saveProperty(propertyToRename);
                                    propertyByName.remove(oldName);
                                    propertyByName.put(newName, propertyToRename);
                                }
                            } else {
                                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.PROPERTY);
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
        associationIndex = header.indexOf("ASSOCIATION");
        unitIndex = header.indexOf("UNIT");
        dataTypeIndex = header.indexOf("DATA-TYPE");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1 || associationIndex == -1 || unitIndex == -1 || dataTypeIndex == -1 || descriptionIndex == -1) {
            return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.HEADER_FIELD_MISSING);
        } else {
            return new DataLoaderResult.SuccessDataLoaderResult();
        }
    }

    /**
     * Local cache of all properties by their names to speed up operations.
     */
    private void init() {
        propertyByName = new HashMap<>();
        for (Property property : configurationEJB.findProperties()) {
            propertyByName.put(property.getName(), property);
        }
    }

    private DataLoaderResult setPropertyFields(Property property, String unit, String dataType, String rowNumber) {
        final Unit newUnit = configurationEJB.findUnitByName(unit);
        if (newUnit != null) {
            property.setUnit(newUnit);
        } else {
            return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.UNIT);
        }

        final DataType newDataType = configurationEJB.findDataType(dataType);
        if (newDataType != null) {
            property.setDataType(newDataType);
        } else {
            return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.DATA_TYPE);
        }

        return new DataLoaderResult.SuccessDataLoaderResult();
    }
}
