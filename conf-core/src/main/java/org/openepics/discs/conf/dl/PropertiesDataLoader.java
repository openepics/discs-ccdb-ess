/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
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
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Unit;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Implementation of data loader for properties
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 */
@Stateless
@PropertiesLoaderQualifier
public class PropertiesDataLoader extends AbstractDataLoader implements DataLoader {
    @Inject private PropertyEJB propertyEJB;
    @Inject private DataTypeEJB dataTypeEJB;
    @Inject private UnitEJB unitEJB;

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

        if (!rowResult.isError()) {
            for (List<String> row : inputRows.subList(1, inputRows.size())) {
                final String rowNumber = row.get(0);
                loaderResult.addResult(rowResult);
                rowResult = new DataLoaderResult();
                if (Objects.equal(row.get(commandIndex), CMD_HEADER)) {
                    headerRow = row;
                    checkForDuplicateHeaderEntries(headerRow);
                    if (rowResult.isError()) {
                        loaderResult.addResult(rowResult);
                    } else {
                        setUpIndexesForFields(headerRow);
                    }
                    if (rowResult.isError()) {
                        return loaderResult;
                    } else {
                        continue; // skip the rest of the processing for HEADER row
                    }
                } else if (row.get(commandIndex).equals(CMD_END)) {
                    break;
                }

                final String command = Preconditions.checkNotNull(row.get(commandIndex).toUpperCase());
                final @Nullable String name = row.get(nameIndex);
                final @Nullable String unit = unitIndex == -1 ? null : row.get(unitIndex);
                final @Nullable String dataType = row.get(dataTypeIndex);
                final @Nullable String description = row.get(descriptionIndex);
                final @Nullable String association = row.get(associationIndex);
                final Date modifiedAt = new Date();

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
                                try {
                                    final Property propertyToUpdate = propertyByName.get(name);
                                    propertyToUpdate.setDescription(description);
                                    setPropertyAssociation(association, propertyToUpdate);
                                    propertyToUpdate.setModifiedAt(modifiedAt);
                                    setPropertyFields(propertyToUpdate, unit, dataType, rowNumber);
                                    if (rowResult.isError()) {
                                        continue;
                                    } else {
                                        propertyEJB.save(propertyToUpdate);
                                    }
                                } catch (Exception e) {
                                    if (e instanceof SecurityException)
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                }
                            } else {
                                try {
                                    final Property propertyToAdd = new Property(name, description);
                                    setPropertyAssociation(association, propertyToAdd);
                                    setPropertyFields(propertyToAdd, unit, dataType, rowNumber);
                                    if (rowResult.isError()) {
                                        continue;
                                    } else {
                                        propertyEJB.add(propertyToAdd);
                                        propertyByName.put(propertyToAdd.getName(), propertyToAdd);
                                    }
                                } catch (Exception e) {
                                    if (e instanceof SecurityException)
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                }
                            }
                            break;
                        case CMD_DELETE:
                            try {
                                final Property propertyToDelete = propertyByName.get(name);
                                if (propertyToDelete == null) {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                                    continue;
                                } else {
                                    propertyEJB.delete(propertyToDelete);
                                    propertyByName.remove(propertyToDelete.getName());
                                }
                            } catch (Exception e) {
                                if (e instanceof SecurityException)
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

                                if (propertyByName.containsKey(oldName)) {
                                    if (propertyByName.containsKey(newName)) {
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, rowNumber, headerRow.get(nameIndex)));
                                        continue;
                                    } else {
                                        final Property propertyToRename = propertyByName.get(oldName);
                                        propertyToRename.setName(newName);
                                        propertyEJB.save(propertyToRename);
                                        propertyByName.remove(oldName);
                                        propertyByName.put(newName, propertyToRename);
                                    }
                                } else {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                                    continue;
                                }
                            } catch (Exception e) {
                                if (e instanceof SecurityException)
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
        for (Property property : propertyEJB.findAll()) {
            propertyByName.put(property.getName(), property);
        }
    }

    private void setPropertyFields(Property property, @Nullable String unit, String dataType, String rowNumber) {
        if (unit != null) {
            final Unit newUnit = unitEJB.findByName(unit);
            if (newUnit != null) {
                property.setUnit(newUnit);
            } else {
                rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, "UNIT"));
            }
        } else {
            property.setUnit(null);
        }

        final DataType newDataType = dataTypeEJB.findByName(dataType);
        if (newDataType != null) {
            property.setDataType(newDataType);
        } else {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, "DATA-TYPE"));
        }
    }

    private void setPropertyAssociation(String association, Property setAssociationProperty) {
        setAssociationProperty.setNoneAssociation();

        final String associationCaps = association == null ? PropertyAssociation.TYPE : association.toUpperCase();

        if (associationCaps.contains(PropertyAssociation.ALL)) {
            setAssociationProperty.setAllAssociation();
            return;
        }
        if (associationCaps.contains(PropertyAssociation.DEVICE)) {
            setAssociationProperty.setDeviceAssociation(true);
        }
        if (associationCaps.contains(PropertyAssociation.SLOT)) {
            setAssociationProperty.setSlotAssociation(true);
        }
        if (associationCaps.contains(PropertyAssociation.TYPE)) {
            setAssociationProperty.setTypeAssociation(true);
        }
        if (associationCaps.contains(PropertyAssociation.ALIGNMENT)) {
            setAssociationProperty.setAlignmentAssociation(true);
        }

        // default if nothing was set
        if (setAssociationProperty.isAssociationNone()) {
            setAssociationProperty.setTypeAssociation(true);
        }
    }
}
