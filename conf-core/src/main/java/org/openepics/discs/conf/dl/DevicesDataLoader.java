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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.util.UnhandledCaseException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Data loader for loading device instances.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
@DevicesLoaderQualifier
public class DevicesDataLoader extends AbstractEntityWithPropertiesDataLoader<DevicePropertyValue>
                                                                                        implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(DevicesDataLoader.class.getCanonicalName());

    // Header column name constants
    private static final String HDR_SERIAL = "INVENTORY ID";
    private static final String HDR_CTYPE = "TYPE";
    private static final String HDR_PROP_NAME = "PROPERTY NAME";
    private static final String HDR_PROP_VALUE = "PROPERTY VALUE";

    private static final int COL_INDEX_CTYPE = 1;
    private static final int COL_INDEX_SERIAL = 2;
    private static final int COL_INDEX_PROP_NAME = 3;
    private static final int COL_INDEX_PROP_VALUE = 4;


    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_CTYPE));

    private String serialFld, componentTypeFld, propNameFld, propValueFld;

    @Inject private ComptypeEJB comptypeEJB;
    @Inject private DeviceEJB deviceEJB;

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected @Nullable Integer getUniqueColumnIndex() {
        return new Integer(COL_INDEX_SERIAL);
    }

    @Override
    protected void assignMembersForCurrentRow() {
        serialFld = readCurrentRowCellForHeader(COL_INDEX_SERIAL);
        componentTypeFld = readCurrentRowCellForHeader(COL_INDEX_CTYPE);
        propNameFld = readCurrentRowCellForHeader(COL_INDEX_PROP_NAME);
        propValueFld = readCurrentRowCellForHeader(COL_INDEX_PROP_VALUE);
    }

    @Override
    protected void handleUpdate(String actualCommand) {
        final Device deviceToUpdate = deviceEJB.findDeviceBySerialNumber(serialFld);
        if (deviceToUpdate != null) {
            final @Nullable ComponentType compType = comptypeEJB.findByName(componentTypeFld);
            if (compType == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            } else {
                try {
                    if (DataLoader.CMD_UPDATE_DEVICE.equals(actualCommand)) {
                        addOrUpdateDevice(deviceToUpdate, compType);
                    } else {
                        final ErrorMessage propError = addOrUpdateProperty(deviceToUpdate, propNameFld, propValueFld);
                        if (propError != null) {
                            switch (propError) {
                                case PROPERTY_NOT_FOUND:
                                    result.addRowMessage(propError, HDR_PROP_NAME);
                                    break;
                                case ENTITY_NOT_FOUND:
                                    result.addRowMessage(propError, HDR_PROP_NAME);
                                    break;
                                default:
                                        throw new UnhandledCaseException();
                            }
                        }
                    }
                } catch (EJBTransactionRolledbackException e) {
                    handleLoadingError(LOGGER, e);
                }
            }
        } else {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_SERIAL);
        }
    }

    @Override
    protected void handleCreate() {
        final Device deviceToUpdate = deviceEJB.findDeviceBySerialNumber(serialFld);
        if (deviceToUpdate == null) {
            final @Nullable ComponentType compType = comptypeEJB.findByName(componentTypeFld);
            if (compType == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            } else {
                try {
                    final Device newDevice = new Device(serialFld);
                    addOrUpdateDevice(newDevice, compType);
                    deviceEJB.addDeviceAndPropertyDefs(newDevice);
                } catch (EJBTransactionRolledbackException e) {
                    handleLoadingError(LOGGER, e);
                }
            }
        } else {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_SERIAL);
        }
    }

    @Override
    protected void handleDelete(String actualCommand) {
        final @Nullable Device deviceToDelete = deviceEJB.findDeviceBySerialNumber(serialFld);
        if (deviceToDelete == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_SERIAL);
        } else {
            try {
                if (DataLoader.CMD_DELETE_DEVICE.equals(actualCommand)) {
                    deviceEJB.delete(deviceToDelete);
                } else {
                    final ErrorMessage propError = addOrUpdateProperty(deviceToDelete, propNameFld, null);
                    if (propError != null) {
                        switch (propError) {
                            case PROPERTY_NOT_FOUND:
                                result.addRowMessage(propError, HDR_PROP_NAME);
                                break;
                            case ENTITY_NOT_FOUND:
                                result.addRowMessage(propError, HDR_PROP_NAME);
                                break;
                            default:
                                throw new UnhandledCaseException();
                        }
                    }
                }
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        }
    }

    @Override
    protected void handleRename() {
        result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, CMD_RENAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DAO<Device> getDAO() {
        return deviceEJB;
    }

    private void addOrUpdateDevice(Device device, ComponentType compType) {
        device.setComponentType(compType);
    }

    @Override
    public int getDataWidth() {
        return 5;
    }

    @Override
    protected void setUpIndexesForFields() {
        final Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        mapBuilder.put(HDR_SERIAL, COL_INDEX_SERIAL);
        mapBuilder.put(HDR_CTYPE, COL_INDEX_CTYPE);
        mapBuilder.put(HDR_PROP_NAME, COL_INDEX_PROP_NAME);
        mapBuilder.put(HDR_PROP_VALUE, COL_INDEX_PROP_VALUE);

        indicies = mapBuilder.build();
    }

    @Override
    public int getImportDataStartIndex() {
        // the devices template starts with data in row 10 (0 based == 9)
        return 9;
    }

}
