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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.annotations.PropertiesLoader;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Unit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Implementation of data loader for properties.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
@PropertiesLoader
public class PropertiesDataLoader extends AbstractDataLoader implements DataLoader {
    private static final Logger LOGGER = Logger.getLogger(PropertiesDataLoader.class.getCanonicalName());

    // Header column name constants
    private static final String HDR_NAME = "NAME";
    private static final String HDR_UNIT = "UNIT";
    private static final String HDR_DATATYPE = "DATA-TYPE";
    private static final String HDR_DESC = "DESCRIPTION";
    private static final String HDR_UNIQUE = "UNIQUE";

    private static final int COL_INDEX_NAME = 1;
    private static final int COL_INDEX_DESC = 2;
    private static final int COL_INDEX_DATATYPE = 3;
    private static final int COL_INDEX_UNIT = 4;
    private static final int COL_INDEX_UNIQUE = 5;

    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(
                                                Arrays.asList(HDR_DATATYPE, HDR_DESC, HDR_DATATYPE, HDR_UNIQUE));

    @Inject private PropertyEJB propertyEJB;
    @Inject private DataTypeEJB dataTypeEJB;
    @Inject private UnitEJB unitEJB;

    /**
     * Cached properties
     */
    private Map<String, Property> propertyByName;

    // Row data for individual cells within a row
    private String nameFld, unitFld, dataTypeFld, descFld;
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
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected @Nullable Integer getUniqueColumnIndex() {
        return COL_INDEX_NAME;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        nameFld = readCurrentRowCellForHeader(COL_INDEX_NAME);
        unitFld = readCurrentRowCellForHeader(COL_INDEX_UNIT);
        dataTypeFld = readCurrentRowCellForHeader(COL_INDEX_DATATYPE);
        descFld = readCurrentRowCellForHeader(COL_INDEX_DESC);
        uniqueFld = uniquenessAsValue(readCurrentRowCellForHeader(COL_INDEX_UNIQUE));
    }

    @Override
    protected void handleUpdate(String actualCommand) {
        if (propertyByName.containsKey(nameFld)) {
            try {
                final Property propertyToUpdate = propertyByName.get(nameFld);
                propertyToUpdate.setDescription(descFld);
                final boolean inUse = propertyEJB.isPropertyUsed(propertyToUpdate);
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
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
        }
    }

    @Override
    protected void handleCreate(String actualCommand) {
        if (!propertyByName.containsKey(nameFld)) {
            try {
                final Property propertyToAdd = new Property(nameFld, descFld);
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
        } else {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
        }
    }


    @Override
    protected void handleDelete(String actualCommand) {
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

    private PropertyValueUniqueness uniquenessAsValue(String uniqueness) {
        PropertyValueUniqueness uniquenessValue = PropertyValueUniqueness.NONE;
        if (uniqueness != null) {
            try {
                uniquenessValue = PropertyValueUniqueness.valueOf(uniqueness.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.FINE, "Incorrect value for property uniqueness.", e);
                result.addRowMessage(ErrorMessage.UNIQUE_INCORRECT, HDR_UNIQUE);
            }
        }
        return uniquenessValue;
    }

    @Override
    public int getDataWidth() {
        return 6;
    }

    @Override
    protected void setUpIndexesForFields() {
        final Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        mapBuilder.put(HDR_NAME, COL_INDEX_NAME);
        mapBuilder.put(HDR_DESC, COL_INDEX_DESC);
        mapBuilder.put(HDR_UNIT, COL_INDEX_UNIT);
        mapBuilder.put(HDR_DATATYPE, COL_INDEX_DATATYPE);
        mapBuilder.put(HDR_UNIQUE, COL_INDEX_UNIQUE);

        indicies = mapBuilder.build();
    }
}
