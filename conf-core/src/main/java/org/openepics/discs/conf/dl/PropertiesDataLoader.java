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
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
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
    private Map<String, Property> propertyByName;
    private int nameIndex, associationIndex, unitIndex, dataTypeIndex, descriptionIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {
        init();


        /*
         * List does not contain any rows that do not have a value (command)
         * in the first column. There should be no commands before "HEADER".
         */
        List<String> headerRow = inputRows.get(0);
        checkForDuplicateHeaderEntries(headerRow);
        if (rowResult.isError()) {
            loaderResult.addResult(rowResult);
            return loaderResult;
        }

        setUpIndexesForFields(headerRow);

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
                    if (rowResult.isError()) {
                        return loaderResult;
                    } else {
                        continue; // skip the rest of the processing for HEADER row
                    }
                } else if (row.get(commandIndex).equals(CMD_END)) {
                    break;
                }

                final String command = As.notNull(row.get(commandIndex).toUpperCase());
                final @Nullable String name = row.get(nameIndex);
                final @Nullable String unit = unitIndex == -1 ? null : row.get(unitIndex);
                final @Nullable String dataType = row.get(dataTypeIndex);
                final @Nullable String description = row.get(descriptionIndex);
                final @Nullable String association = row.get(associationIndex);
                final Date modifiedAt = new Date();
                final String modifiedBy = loginManager.getUserid();

                if (name == null) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(nameIndex)));
                } else if (dataType == null && !command.equals(CMD_RENAME) && !command.equals(CMD_DELETE)) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(dataTypeIndex)));
                } else if (description == null && !command.equals(CMD_RENAME) && !command.equals(CMD_DELETE)) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(descriptionIndex)));
                } else if (association == null && !command.equals(CMD_RENAME) && !command.equals(CMD_DELETE)) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(associationIndex)));
                }

                if (!rowResult.isError()) {
                    switch (command) {
                    case CMD_UPDATE:
                        if (propertyByName.containsKey(name)) {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.UPDATE)) {
                                final Property propertyToUpdate = propertyByName.get(name);
                                propertyToUpdate.setDescription(description);
                                propertyToUpdate.setAssociation(propertyAssociation(association));
                                propertyToUpdate.setModifiedAt(modifiedAt);
                                propertyToUpdate.setModifiedBy(modifiedBy);
                                setPropertyFields(propertyToUpdate, unit, dataType, rowNumber);
                                if (rowResult.isError()) {
                                    continue;
                                } else {
                                    configurationEJB.saveProperty(propertyToUpdate);
                                }
                            } else {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                            }
                        } else {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.CREATE)) {
                                final Property propertyToAdd = new Property(name, description, propertyAssociation(association), modifiedBy);
                                setPropertyFields(propertyToAdd, unit, dataType, rowNumber);
                                if (rowResult.isError()) {
                                    continue;
                                } else {
                                    configurationEJB.addProperty(propertyToAdd);
                                    propertyByName.put(propertyToAdd.getName(), propertyToAdd);
                                }
                            } else {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                            }
                        }
                        break;
                    case CMD_DELETE:
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.DELETE)) {
                            final Property propertyToDelete = propertyByName.get(name);
                            if (propertyToDelete == null) {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                                continue;
                            } else {
                                configurationEJB.deleteProperty(propertyToDelete);
                                propertyByName.remove(propertyToDelete.getName());
                            }
                        } else {
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                        }
                        break;
                    case CMD_RENAME:
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.RENAME)) {
                            final int startOldNameMarkerIndex = name.indexOf("[");
                            final int endOldNameMarkerIndex = name.indexOf("]");
                            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                                rowResult.addMessage(new ValidationMessage(ErrorMessage.RENAME_MISFORMAT, rowNumber, headerRow.get(nameIndex)));
                                continue;
                            }

                            final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                            final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                            if (propertyByName.containsKey(oldName)) {
                                if (propertyByName.containsKey(newName)) {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, rowNumber, headerRow.get(nameIndex)));
                                    continue;
                                } else {
                                    final Property propertyToRename = propertyByName.get(oldName);
                                    propertyToRename.setName(newName);
                                    configurationEJB.saveProperty(propertyToRename);
                                    propertyByName.remove(oldName);
                                    propertyByName.put(newName, propertyToRename);
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
        }
        loaderResult.addResult(rowResult);
        return loaderResult;
    }


    @Override protected void setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        rowResult = new DataLoaderResult();
        nameIndex = header.indexOf("NAME");
        associationIndex = header.indexOf("ASSOCIATION");
        unitIndex = header.indexOf("UNIT");
        dataTypeIndex = header.indexOf("DATA-TYPE");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "NAME"));
        } else if (associationIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "ASSOCIATION"));
        } else if (unitIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "UNIT"));
        } else if (dataTypeIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "DATA-TYPE"));
        } else if (descriptionIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "DESCRIPTION"));
        }
    }

    /**
     * Local cache of all properties by their names to speed up operations.
     */
    private void init() {
        loaderResult = new DataLoaderResult();
        propertyByName = new HashMap<>();
        for (Property property : configurationEJB.findProperties()) {
            propertyByName.put(property.getName(), property);
        }
    }

    private void setPropertyFields(Property property, @Nullable String unit, String dataType, String rowNumber) {
        if (unit != null) {
            final Unit newUnit = configurationEJB.findUnitByName(unit);
            if (newUnit != null) {
                property.setUnit(newUnit);
            } else {
                rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, "UNIT"));
            }
        } else {
            property.setUnit(null);
        }

        final DataType newDataType = configurationEJB.findDataTypeByName(dataType);
        if (newDataType != null) {
            property.setDataType(newDataType);
        } else {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, "DATA-TYPE"));
        }
    }

    private PropertyAssociation propertyAssociation(String association) {
        if (association == null) {
            return PropertyAssociation.TYPE;
        } else if (association.equalsIgnoreCase(PropertyAssociation.ALL.name())) {
            return PropertyAssociation.ALL;
        } else if (association.equalsIgnoreCase(PropertyAssociation.DEVICE.name())) {
            return PropertyAssociation.DEVICE;
        } else if (association.equalsIgnoreCase(PropertyAssociation.SLOT.name())) {
            return PropertyAssociation.SLOT;
        } else if (association.equalsIgnoreCase(PropertyAssociation.SLOT_DEVICE.name())) {
            return PropertyAssociation.SLOT_DEVICE;
        } else if (association.equalsIgnoreCase(PropertyAssociation.TYPE.name())) {
            return PropertyAssociation.TYPE;
        } else if (association.equalsIgnoreCase(PropertyAssociation.TYPE_DEVICE.name())) {
            return PropertyAssociation.TYPE_DEVICE;
        } else if (association.equalsIgnoreCase(PropertyAssociation.TYPE_SLOT.name())) {
            return PropertyAssociation.TYPE_SLOT;
        } else {
            return PropertyAssociation.TYPE;
        }
    }
}
