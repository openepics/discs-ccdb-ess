/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.DeviceStatus;
import org.openepics.discs.conf.util.Utility;

import com.google.common.base.Preconditions;


/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class DevicesByTypeManager implements Serializable {
    private static final Logger logger = Logger.getLogger(DevicesByTypeManager.class.getCanonicalName());

    @EJB private ComptypeEJB componentTypesEJB;
    @EJB private DeviceEJB deviceEJB;

    private List<ComponentType> deviceTypes;
    private ComponentType selectedComponentType;

    private List<Device> devices;
    private Device selectedDevice;

    private String serialNumber;
    private String location;
    private String purchaseOrder;
    private DeviceStatus status;
    private String description;
    private String manufacturer;
    private String manufModel;
    private String manufSerialNumber;

    public DevicesByTypeManager() {
    }

    /**
     * Removes the component types from the devices based on name. Used to remove "_ROOT" and "_GRP"
     */
    private void removeInternalTypes() {
        int found = 0;
        final ListIterator<ComponentType> dtIterator = deviceTypes.listIterator();
        while (dtIterator.hasNext()) {
            final String elementName = dtIterator.next().getName();
            if (elementName.equalsIgnoreCase(SlotEJB.ROOT_COMPONENT_TYPE) || elementName.equalsIgnoreCase(SlotEJB.GRP_COMPONENT_TYPE)) {
                dtIterator.remove();
                found++;
                if (found >= 2) break;
            }
        }
    }

    /**
     * Creates a new device instance and adds all properties to it which are defined by device type.
     */
    public void onDeviceAdd() {
        final Device newDevice = new Device(serialNumber);
        newDevice.setComponentType(selectedComponentType);
        newDevice.setLocation(location);
        newDevice.setPurchaseOrder(purchaseOrder);
        newDevice.setStatus(status);
        newDevice.setDescription(description);
        newDevice.setManufacturer(manufacturer);
        newDevice.setManufacturerModel(manufModel);
        newDevice.setManufacturerSerialNumber(manufSerialNumber);

        deviceEJB.add(newDevice);

        // Get all property definitions and create device properties
        List<ComptypePropertyValue> propertyDefinitions = componentTypesEJB.findPropertyDefinitions(selectedComponentType);
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            final DevicePropertyValue devicePropertyValue = new DevicePropertyValue(false);
            devicePropertyValue.setProperty(propertyDefinition.getProperty());
            devicePropertyValue.setDevice(newDevice);
            deviceEJB.addChild(devicePropertyValue);
        }

        resetDeviceDialogFields();
        prepareDevicesForDisplay();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Device saved.", null));
    }

    /**
     * Event handler which handles the device delete
     */
    public void onDeviceDelete() {
        Preconditions.checkNotNull(selectedDevice);

        try {
            // TODO check if the device is installed. Prevent deletion if yes.
            deviceEJB.delete(selectedDevice);

            selectedDevice = null;
            prepareDevicesForDisplay();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Device deleted.", null));
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The property could not be deleted because it is used.");
            } else {
                throw e;
            }
        }
    }

    /**
     * The event handler which saves the modifications to the device.
     */
    public void onDeviceModify() {
        Preconditions.checkNotNull(selectedDevice);

        selectedDevice.setSerialNumber(serialNumber);
        selectedDevice.setLocation(location);
        selectedDevice.setPurchaseOrder(purchaseOrder);
        selectedDevice.setStatus(status);
        selectedDevice.setDescription(description);
        selectedDevice.setManufacturer(manufacturer);
        selectedDevice.setManufacturerModel(manufModel);
        selectedDevice.setManufacturerSerialNumber(manufSerialNumber);

        deviceEJB.save(selectedDevice);

        resetDeviceDialogFields();
        prepareDevicesForDisplay();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Device updated.", null));
    }

    private void resetDeviceDialogFields() {
        serialNumber = null;
        location = null;
        purchaseOrder = null;
        status = null;
        description = null;
        manufacturer = null;
        manufModel = null;
        manufSerialNumber = null;
    }

    /**
     * This method sets the fields for to values that are displayed in the device modify dialog.
     */
    public void prepareForModify() {
        Preconditions.checkNotNull(selectedDevice);

        serialNumber = selectedDevice.getSerialNumber();
        location = selectedDevice.getLocation();
        purchaseOrder = selectedDevice.getPurchaseOrder();
        status = selectedDevice.getStatus();
        description = selectedDevice.getDescription();
        manufacturer = selectedDevice.getManufacturer();
        manufModel = selectedDevice.getManufacturerModel();
        manufSerialNumber = selectedDevice.getManufacturerSerialNumber();
    }

    public String deviceDetailsRedirect(Long id) { return "device-details.xhtml?faces-redirect=true&id=" + id; }

    public List<ComponentType> getDeviceTypes() {
        if (deviceTypes == null) {
            deviceTypes = componentTypesEJB.findComponentTypeOrderedByName();
            removeInternalTypes();
        }
        return deviceTypes;
    }
    public void setDeviceTypes(List<ComponentType> deviceTypes) { this.deviceTypes = deviceTypes; }

    public ComponentType getSelectedComponentType() { return selectedComponentType; }
    public void setSelectedComponentType(ComponentType selectedComponentType) { this.selectedComponentType = selectedComponentType; }

    public void prepareDevicesForDisplay() {
        this.devices = deviceEJB.findDevicesByComponentType(selectedComponentType);
    }

    public List<Device> getDevices() { return devices; }
    public void setDevices(List<Device> devices) { this.devices = devices; }

    public Device getSelectedDevice() { return selectedDevice; }
    public void setSelectedDevice(Device selectedDevice) { this.selectedDevice = selectedDevice; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(String purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus status) { this.status = status; }
    public DeviceStatus[] getStatuses() {
        return DeviceStatus.values();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getManufModel() { return manufModel; }
    public void setManufModel(String manufModel) { this.manufModel = manufModel; }

    public String getManufSerialNumber() { return manufSerialNumber; }
    public void setManufSerialNumber(String manufSerialNumber) { this.manufSerialNumber = manufSerialNumber; }

}
