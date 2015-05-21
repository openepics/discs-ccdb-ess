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

import org.apache.commons.lang.NotImplementedException;
import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;

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
    private static final String HDR_SERIAL = "SERIAL";
    private static final String HDR_CTYPE = "CTYPE";
    private static final String HDR_ASM_POSITION = "ASM-POSITION";
    private static final String HDR_ASM_DESC = "ASM-DESCRIPTION";

    private static final int COL_INDEX_SERIAL = -1; // TODO fix
    private static final int COL_INDEX_CTYPE = -1; // TODO fix
    private static final int COL_INDEX_ASM_POSITION = -1; // TODO fix
    private static final int COL_INDEX_ASM_DESC = -1; // TODO fix


    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_CTYPE));

    private String serial, componentType, asmPosition, asmDescription;

    @Inject private ComptypeEJB comptypeEJB;
    @Inject private DeviceEJB deviceEJB;

    @Override
    protected void init() {
        super.init();
        setPropertyValueClass(DevicePropertyValue.class);
    }

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
        serial = readCurrentRowCellForHeader(COL_INDEX_SERIAL);
        componentType = readCurrentRowCellForHeader(COL_INDEX_CTYPE);
        asmPosition = readCurrentRowCellForHeader(COL_INDEX_ASM_POSITION);
        asmDescription = readCurrentRowCellForHeader(COL_INDEX_ASM_DESC);
    }

    @Override
    protected void handleUpdate() {
        final Device deviceToUpdate = deviceEJB.findDeviceBySerialNumber(serial);
        if (deviceToUpdate != null) {
            final @Nullable ComponentType compType = comptypeEJB.findByName(componentType);
            if (compType == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            } else {
                try {
                    addOrUpdateDevice(deviceToUpdate, compType, asmPosition, asmDescription);
                    addOrUpdateProperties(deviceToUpdate);
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
        final Device deviceToUpdate = deviceEJB.findDeviceBySerialNumber(serial);
        if (deviceToUpdate == null) {
            final @Nullable ComponentType compType = comptypeEJB.findByName(componentType);
            if (compType == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            } else {
                try {
                    final Device newDevice = new Device(serial);
                    addOrUpdateDevice(newDevice, compType, asmPosition, asmDescription);
                    deviceEJB.add(newDevice);
                    addOrUpdateProperties(newDevice);
                } catch (EJBTransactionRolledbackException e) {
                    handleLoadingError(LOGGER, e);
                }
            }
        } else {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_SERIAL);
        }
    }

    @Override
    protected void handleDelete() {
        final @Nullable Device deviceToDelete = deviceEJB.findDeviceBySerialNumber(serial);
        if (deviceToDelete == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_SERIAL);
        } else {
            try {
                deviceEJB.delete(deviceToDelete);
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

    private void addOrUpdateDevice(Device device, ComponentType compType, String asmPosition,
                                            String asmDescription) {
        device.setComponentType(compType);
        device.setAssemblyPosition(asmPosition);
        device.setAssemblyDescription(asmDescription);
    }

    @Override
    public int getDataWidth() {
        // TODO set the data width
        throw new NotImplementedException();
    }

    @Override
    protected void setUpIndexesForFields() {
        // TODO implement
        throw new NotImplementedException();
    }
}
