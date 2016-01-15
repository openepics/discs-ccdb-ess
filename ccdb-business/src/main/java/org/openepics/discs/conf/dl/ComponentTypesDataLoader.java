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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.annotations.ComponentTypesLoader;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Implementation of data loader for device types.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
@Stateless
@ComponentTypesLoader
public class ComponentTypesDataLoader extends AbstractEntityWithPropertiesDataLoader<ComptypePropertyValue>
    implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(ComponentTypesDataLoader.class.getCanonicalName());

    // Header column name constants
    protected static final String HDR_NAME = "NAME";
    protected static final String HDR_DESC = "DESCRIPTION";
    protected static final String HDR_PROP_TYPE = "PROPERTY TYPE";
    protected static final String HDR_PROP_NAME = "PROPERTY NAME";
    protected static final String HDR_PROP_VALUE = "PROPERTY VALUE";

    private static final int COL_INDEX_NAME = 1;
    private static final int COL_INDEX_DESC = 2;
    private static final int COL_INDEX_PROP_TYPE = 3;
    private static final int COL_INDEX_PROP_NAME = 4;
    private static final int COL_INDEX_PROP_VALUE = 5;

    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>();

    // Fields for row cells
    private String nameFld, descriptionFld, propTypeFld, propNameFld, propValueFld;;

    @Inject private ComptypeEJB comptypeEJB;

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected @Nullable Integer getUniqueColumnIndex() {
        return COL_INDEX_NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DAO<ComponentType> getDAO() {
        return comptypeEJB;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        nameFld = readCurrentRowCellForHeader(COL_INDEX_NAME);
        descriptionFld = readCurrentRowCellForHeader(COL_INDEX_DESC);
        propTypeFld = readCurrentRowCellForHeader(COL_INDEX_PROP_TYPE);
        propNameFld = readCurrentRowCellForHeader(COL_INDEX_PROP_NAME);
        propValueFld = readCurrentRowCellForHeader(COL_INDEX_PROP_VALUE);
    }

    @Override
    protected void handleUpdate(String actualCommand) {
        final ComponentType componentTypeToUpdate = comptypeEJB.findByName(nameFld);
        if (componentTypeToUpdate != null) {
            if (componentTypeToUpdate.getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, AbstractDataLoader.HDR_OPERATION);
                return;
            }
            try {
                if (DataLoader.CMD_UPDATE_DEVICE_TYPE.equals(actualCommand)) {
                    componentTypeToUpdate.setDescription(descriptionFld);
                } else {
                    if (propNameFld != null) {
                        final ComptypePropertyValue comptypePropertyValue =
                                (ComptypePropertyValue) getPropertyValue(componentTypeToUpdate, propNameFld,
                                                                            HDR_PROP_NAME);
                        if (comptypePropertyValue != null) {
                            if (!comptypePropertyValue.isPropertyDefinition()) {
                                addOrUpdateProperty(componentTypeToUpdate, propNameFld, propValueFld, HDR_PROP_NAME);
                            } else {
                                result.addRowMessage(ErrorMessage.PROPERTY_TYPE_INCORRECT, HDR_PROP_TYPE);
                            }
                        }
                    } else {
                        result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_NAME);
                    }
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
        final ComponentType componentTypeToUpdate = comptypeEJB.findByName(nameFld);
        if (DataLoader.CMD_CREATE_DEVICE_TYPE.equals(actualCommand)) {
            if (componentTypeToUpdate == null) {
                try {
                    final ComponentType compTypeToAdd = new ComponentType(nameFld);
                    compTypeToAdd.setDescription(descriptionFld);
                    comptypeEJB.add(compTypeToAdd);
                } catch (EJBTransactionRolledbackException e) {
                    handleLoadingError(LOGGER, e);
                }
            } else {
                result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
            }
        } else {
            if (componentTypeToUpdate != null) {
                addPropertyValue(componentTypeToUpdate);
            } else {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            }
        }
    }

    @Override
    protected void handleDelete(String actualCommand) {
        final ComponentType componentTypeToDelete = comptypeEJB.findByName(nameFld);
        try {
            if (componentTypeToDelete == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            } else {
                if (SlotEJB.ROOT_COMPONENT_TYPE.equals(componentTypeToDelete.getName())) {
                    result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, AbstractDataLoader.HDR_OPERATION);
                    return;
                }

                if (DataLoader.CMD_DELETE_DEVICE_TYPE.equals(actualCommand)) {
                    comptypeEJB.delete(componentTypeToDelete);
                } else {
                    if (propNameFld != null) {
                        final ComptypePropertyValue comptypePropertyValue =
                                (ComptypePropertyValue) getPropertyValue(componentTypeToDelete, propNameFld,
                                                                            HDR_PROP_NAME);
                        // Property defs can only be deleted through UI to properly handle already assigned prop values
                        if (comptypePropertyValue != null) {
                            if (!comptypePropertyValue.isPropertyDefinition()) {
                                comptypeEJB.deleteChild(comptypePropertyValue);
                            } else {
                                result.addRowMessage(ErrorMessage.PROPERTY_TYPE_INCORRECT, HDR_PROP_TYPE);
                            }
                        }
                    } else {
                        result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_NAME);
                    }
                }
            }
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
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
        mapBuilder.put(HDR_PROP_TYPE, COL_INDEX_PROP_TYPE);
        mapBuilder.put(HDR_PROP_NAME, COL_INDEX_PROP_NAME);
        mapBuilder.put(HDR_PROP_VALUE, COL_INDEX_PROP_VALUE);

        indicies = mapBuilder.build();
    }

    @Override
    public int getImportDataStartIndex() {
        // the new template starts with data in row 11 (0 based == 10)
        return 10;
    }

    private void addPropertyValue(ComponentType comptypeToUpdate) {
        if (Strings.isNullOrEmpty(propNameFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_NAME);
            return;
        }

        if (Strings.isNullOrEmpty(propTypeFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_TYPE);
            return;
        }

        // does property exist
        final @Nullable Property property = propertyEJB.findByName(propNameFld);
        if (property == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_PROP_NAME);
            return;
        }

        // is this property value already added
        for (final ComptypePropertyValue value : comptypeToUpdate.getComptypePropertyList()) {
            if (value.getProperty().equals(property)) {
                result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_PROP_NAME);
                return;
            }
        }

        final ComptypePropertyValue propertyValue = new ComptypePropertyValue(false);
        propertyValue.setProperty(property);
        propertyValue.setComponentType(comptypeToUpdate);
        switch (propTypeFld) {
            case DataLoader.PROP_TYPE_DEV_TYPE:
                if (Strings.isNullOrEmpty(propValueFld)) {
                    result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_VALUE);
                    return;
                }
                propertyValue.setPropValue(Conversion.stringToValue(propValueFld, property.getDataType()));
                break;
            case DataLoader.PROP_TYPE_SLOT:
                propertyValue.setPropertyDefinition(true);
                propertyValue.setDefinitionTargetSlot(true);
                break;
            case DataLoader.PROP_TYPE_DEV_INSTANCE:
                propertyValue.setPropertyDefinition(true);
                propertyValue.setDefinitionTargetDevice(true);
                break;
            default:
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_PROP_TYPE);
                return;
        }
        comptypeEJB.addChild(propertyValue);
    }
}
