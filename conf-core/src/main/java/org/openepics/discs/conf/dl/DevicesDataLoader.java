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
import org.openepics.discs.conf.ent.DeviceStatus;

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
    private static final String HDR_DESC = "DESCRIPTION";
    private static final String HDR_STATUS = "STATUS";
    private static final String HDR_MANUF_SERIAL = "MANUF-SERIAL";
    private static final String HDR_LOCATION = "LOCATION";
    private static final String HDR_PURCHASE_ORDER = "PURCHASE-ORDER";
    private static final String HDR_ASM_POSITION = "ASM-POSITION";
    private static final String HDR_ASM_DESC = "ASM-DESCRIPTION";
    private static final String HDR_MANUFACTURER = "MANUFACTURER";
    private static final String HDR_MANUF_MODEL = "MANUF-MODEL";

    private static final int COL_INDEX_SERIAL = -1; // TODO fix
    private static final int COL_INDEX_CTYPE = -1; // TODO fix
    private static final int COL_INDEX_DESC = -1; // TODO fix
    private static final int COL_INDEX_STATUS = -1; // TODO fix
    private static final int COL_INDEX_MANUF_SERIAL = -1; // TODO fix
    private static final int COL_INDEX_LOCATION = -1; // TODO fix
    private static final int COL_INDEX_PURCHASE_ORDER = -1; // TODO fix
    private static final int COL_INDEX_ASM_POSITION = -1; // TODO fix
    private static final int COL_INDEX_ASM_DESC = -1; // TODO fix
    private static final int COL_INDEX_MANUFACTURER = -1; // TODO fix
    private static final int COL_INDEX_MANUF_MODEL = -1; // TODO fix


    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_CTYPE));

    private String serial, componentType, description, manufSerial, location, purchaseOrder, asmPosition;
    private String asmDescription, manufacturer, manufModel;
    private DeviceStatus status;

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
        description = readCurrentRowCellForHeader(COL_INDEX_DESC);
        status = setDeviceStatus(readCurrentRowCellForHeader(COL_INDEX_STATUS));
        manufSerial = readCurrentRowCellForHeader(COL_INDEX_MANUF_SERIAL);
        location = readCurrentRowCellForHeader(COL_INDEX_LOCATION);
        purchaseOrder = readCurrentRowCellForHeader(COL_INDEX_PURCHASE_ORDER);
        asmPosition = readCurrentRowCellForHeader(COL_INDEX_ASM_POSITION);
        asmDescription = readCurrentRowCellForHeader(COL_INDEX_ASM_DESC);
        manufacturer = readCurrentRowCellForHeader(COL_INDEX_MANUFACTURER);
        manufModel = readCurrentRowCellForHeader(COL_INDEX_MANUF_MODEL);
    }

    @Override
    protected void handleUpdate() {
        if (deviceEJB.findDeviceBySerialNumber(serial) != null) {
            final @Nullable ComponentType compType = comptypeEJB.findByName(componentType);
            if (compType == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            } else {
                try {
                    final Device deviceToUpdate = deviceEJB.findDeviceBySerialNumber(serial);
                    addOrUpdateDevice(deviceToUpdate, compType, description, status, manufSerial, location,
                            purchaseOrder, asmPosition, asmDescription, manufacturer, manufModel);
                    addOrUpdateProperties(deviceToUpdate);
                } catch (EJBTransactionRolledbackException e) {
                    handleLoadingError(LOGGER, e);
                }
            }
        } else {
            final @Nullable ComponentType compType = comptypeEJB.findByName(componentType);
            if (compType == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            } else {
                try {
                    final Device newDevice = new Device(serial);
                    addOrUpdateDevice(newDevice, compType, description, status, manufSerial, location, purchaseOrder,
                            asmPosition, asmDescription, manufacturer, manufModel);
                    deviceEJB.add(newDevice);
                    addOrUpdateProperties(newDevice);
                } catch (EJBTransactionRolledbackException e) {
                    handleLoadingError(LOGGER, e);
                }
            }
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

    private void addOrUpdateDevice(Device device, ComponentType compType, String description, DeviceStatus status,
            String manufSerial, String location, String purchaseOrder, String asmPosition, String asmDescription,
            String manufacturer, String manufModel) {
        device.setComponentType(compType);
        device.setDescription(description);
        device.setAssemblyPosition(asmPosition);
        device.setStatus(status);
        device.setManufacturer(manufacturer);
        device.setManufacturerModel(manufModel);
        device.setManufacturerSerialNumber(manufSerial);
        device.setAssemblyDescription(asmDescription);
        device.setLocation(location);
        device.setPurchaseOrder(purchaseOrder);
    }

    private DeviceStatus setDeviceStatus(@Nullable String deviceStatusString) {
        final DeviceStatus deviceStatus;
        if (deviceStatusString == null) {
            deviceStatus = null;
        } else if (deviceStatusString.equalsIgnoreCase(DeviceStatus.IN_FABRICATION.name())) {
            deviceStatus = DeviceStatus.IN_FABRICATION;
        } else if (deviceStatusString.equalsIgnoreCase(DeviceStatus.READY.name())) {
            deviceStatus = DeviceStatus.READY;
        } else if (deviceStatusString.equalsIgnoreCase(DeviceStatus.SPARE.name())) {
            deviceStatus = DeviceStatus.SPARE;
        } else if (deviceStatusString.equalsIgnoreCase(DeviceStatus.UNDER_REPAIR.name())) {
            deviceStatus = DeviceStatus.UNDER_REPAIR;
        } else if (deviceStatusString.equalsIgnoreCase(DeviceStatus.UNDER_TESTING.name())) {
            deviceStatus = DeviceStatus.UNDER_TESTING;
        } else {
            result.addRowMessage(ErrorMessage.DEVICE_STATUS_NOT_FOUND, HDR_STATUS);
            return null;
        }
        return deviceStatus;
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
