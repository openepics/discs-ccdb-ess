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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Unit;

/**
 * Implementation of data loader for properties.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
@PropertiesLoaderQualifier
public class PropertiesDataLoader extends AbstractDataLoader implements DataLoader {
    private static final Logger LOGGER = Logger.getLogger(PropertiesDataLoader.class.getCanonicalName());

    // Header column name constants
    private static final String HDR_NAME = "NAME";
    private static final String HDR_ASSOCIATION = "ASSOCIATION";
    private static final String HDR_UNIT = "UNIT";
    private static final String HDR_DATATYPE = "DATA-TYPE";
    private static final String HDR_DESC = "DESCRIPTION";
    private static final String HDR_UNIQUE = "UNIQUE";

    private static final List<String> KNOWN_COLUMNS = Arrays.asList(HDR_NAME, HDR_ASSOCIATION, HDR_UNIT,
            HDR_DATATYPE, HDR_DESC, HDR_UNIQUE);
    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_ASSOCIATION,
            HDR_DATATYPE, HDR_DESC));

    @Inject private PropertyEJB propertyEJB;
    @Inject private DataTypeEJB dataTypeEJB;
    @Inject private UnitEJB unitEJB;

    /**
     * Cached properties
     */
    private Map<String, Property> propertyByName;

    // Row data for individual cells within a row
    private String nameFld, associationFld, unitFld, dataTypeFld, descFld;
    private PropertyValueUniqueness uniqueFld;

    /**
     * Local cache of all properties by their names to speed up operations.
     */
    @Override
    protected void init() {
        super.init();

        propertyByName = new HashMap<>();
        for (Property property : propertyEJB.findAll()) {
            propertyByName.put(property.getName(), property);
        }
    }

    @Override
    protected List<String> getKnownColumnNames() {
        return KNOWN_COLUMNS;
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected String getUniqueColumnName() {
        return HDR_NAME;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        nameFld = readCurrentRowCellForHeader(HDR_NAME);
        unitFld = readCurrentRowCellForHeader(HDR_UNIT);
        dataTypeFld = readCurrentRowCellForHeader(HDR_DATATYPE);
        descFld = readCurrentRowCellForHeader(HDR_DESC);
        associationFld = readCurrentRowCellForHeader(HDR_ASSOCIATION);
        uniqueFld = uniquenessAsValue(readCurrentRowCellForHeader(HDR_UNIQUE));
    }

    @Override
    protected void handleUpdate() {
        if (propertyByName.containsKey(nameFld)) {
            try {
                final Property propertyToUpdate = propertyByName.get(nameFld);
                propertyToUpdate.setDescription(descFld);
                final boolean inUse = propertyEJB.isPropertyUsed(propertyToUpdate);
                setPropertyAssociation(propertyToUpdate, associationFld, inUse);
                setPropertyUnit(propertyToUpdate, unitFld, inUse);
                setPropertyDataType(propertyToUpdate, dataTypeFld, inUse);
                setPropertyUniqueness(propertyToUpdate, uniqueFld, inUse);
                if (!result.isRowError()) {
                    propertyEJB.save(propertyToUpdate);
                }
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        } else {
            try {
                final Property propertyToAdd = new Property(nameFld, descFld);
                setPropertyAssociation(propertyToAdd, associationFld, false);
                setPropertyUnit(propertyToAdd, unitFld, false);
                setPropertyDataType(propertyToAdd, dataTypeFld, false);
                setPropertyUniqueness(propertyToAdd, uniqueFld, false);
                propertyToAdd.setValueUniqueness(uniqueFld);
                if (!result.isRowError()) {
                    propertyEJB.add(propertyToAdd);
                    propertyByName.put(propertyToAdd.getName(), propertyToAdd);
                }
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        }
    }

    @Override
    protected void handleDelete() {
        try {
            final Property propertyToDelete = propertyByName.get(nameFld);
            if (propertyToDelete == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            } else {
                propertyEJB.delete(propertyToDelete);
                propertyByName.remove(propertyToDelete.getName());
            }
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
    }

    @Override
    protected void handleRename() {
        try {
            final int startOldNameMarkerIndex = nameFld.indexOf("[");
            final int endOldNameMarkerIndex = nameFld.indexOf("]");
            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                result.addRowMessage(ErrorMessage.RENAME_MISFORMAT, HDR_NAME);
                return;
            }

            final String oldName = nameFld.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
            final String newName = nameFld.substring(endOldNameMarkerIndex + 1).trim();

            if (propertyByName.containsKey(oldName)) {
                if (propertyByName.containsKey(newName)) {
                    result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
                } else {
                    final Property propertyToRename = propertyByName.get(oldName);
                    propertyToRename.setName(newName);
                    propertyEJB.save(propertyToRename);
                    propertyByName.remove(oldName);
                    propertyByName.put(newName, propertyToRename);
                }
            } else {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            }
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
    }

    private void setPropertyUnit(Property property, @Nullable String unit, final boolean inUse) {
        if (unit != null) {
            final Unit newUnit = unitEJB.findByName(unit);
            if (newUnit != null) {
                // is modification allowed
                if (inUse && !newUnit.equals(property.getUnit())) {
                    result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_UNIT);
                } else {
                    property.setUnit(newUnit);
                }
            } else {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_UNIT);
            }
        } else {
            if (inUse && (property.getUnit() != null)) {
                result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_UNIT);
            } else {
                property.setUnit(null);
            }
        }
    }

    private void setPropertyDataType(Property property, String dataType, final boolean inUse) {
        final DataType newDataType = dataTypeEJB.findByName(dataType);
        if (newDataType != null) {
            if (inUse && !newDataType.equals(property.getDataType())) {
                result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_DATATYPE);
            } else {
                property.setDataType(newDataType);
            }
        } else {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_DATATYPE);
        }
    }

    private void setPropertyUniqueness(Property property, PropertyValueUniqueness unique, final boolean inUse) {
        if (inUse && property.getValueUniqueness() != unique) {
            result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_UNIQUE);
        } else {
            property.setValueUniqueness(unique);
        }
    }

    private void setPropertyAssociation(Property setAssociationProperty, String association, final boolean inUse) {
        boolean associationType = false;
        boolean associationSlot = false;
        boolean associationDevice = false;
        boolean associationAlignment = false;

        final String associationCaps = association == null ? PropertyAssociation.TYPE : association.toUpperCase();

        if (associationCaps.contains(PropertyAssociation.ALL)) {
            associationType = true;
            associationSlot = true;
            associationDevice = true;
            associationAlignment = true;
        } else {
            if (associationCaps.contains(PropertyAssociation.DEVICE)) {
                associationDevice = true;
            }
            if (associationCaps.contains(PropertyAssociation.SLOT)) {
                associationSlot = true;
            }
            if (associationCaps.contains(PropertyAssociation.TYPE)) {
                associationType = true;
            }
            if (associationCaps.contains(PropertyAssociation.ALIGNMENT)) {
                associationAlignment = true;
            }
        }

        final boolean associationChange = (associationType != setAssociationProperty.isTypeAssociation())
                                        || (associationSlot != setAssociationProperty.isSlotAssociation())
                                        || (associationDevice != setAssociationProperty.isDeviceAssociation())
                                        || (associationAlignment != setAssociationProperty.isAlignmentAssociation());

        if (inUse && associationChange) {
            result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_ASSOCIATION);
        } else {
            // apply new values only if changed
            if (associationChange) {
                setAssociationProperty.setNoneAssociation();
                setAssociationProperty.setAlignmentAssociation(associationAlignment);
                setAssociationProperty.setTypeAssociation(associationType);
                setAssociationProperty.setSlotAssociation(associationSlot);
                setAssociationProperty.setDeviceAssociation(associationDevice);
            }

            // default if nothing was set
            if (setAssociationProperty.isAssociationNone()) {
                setAssociationProperty.setTypeAssociation(true);
            }
        }
    }

    private PropertyValueUniqueness uniquenessAsValue(String uniqueness) {
        PropertyValueUniqueness uniquenessValue = PropertyValueUniqueness.NONE;
        if (uniqueness != null) {
            try {
                uniquenessValue = PropertyValueUniqueness.valueOf(uniqueness.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                result.addRowMessage(ErrorMessage.UNIQUE_INCORRECT, HDR_UNIQUE);
            }
        }
        return uniquenessValue;
    }
}
