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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.util.Utility;

import com.google.common.base.Preconditions;


/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class DevicesByTypeManager implements Serializable {
    @Inject private ComptypeEJB componentTypesEJB;
    @Inject private DeviceEJB deviceEJB;
    @Inject private InstallationEJB installationEJB;

    private List<ComponentType> deviceTypes;
    private List<ComponentType> filteredComponentTypes;
    private ComponentType selectedComponentType;

    private List<Device> devices;
    private Device selectedDevice;

    private String serialNumber;
    private String description;

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
        newDevice.setDescription(description);

        deviceEJB.addDeviceAndPropertyDefs(newDevice);

        resetDeviceDialogFields();
        prepareDevicesForDisplay();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Device saved.", null));
    }

    /**
     * Event handler which handles the device delete
     */
    public void onDeviceDelete() {
        Preconditions.checkNotNull(selectedDevice);
        
        if (installationEJB.getActiveInstallationRecordForDevice(selectedDevice) == null) {
            try {                
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
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "Device instance could not be deleted because it is installed.");
        }
    }

    private void resetDeviceDialogFields() {
        serialNumber = null;
        description = null;
    }

    public String deviceDetailsRedirect(Long id) {
        return "device-details.xhtml?faces-redirect=true&id=" + id;
    }

    public List<ComponentType> getDeviceTypes() {
        if (deviceTypes == null) {
            deviceTypes = componentTypesEJB.findComponentTypeOrderedByName();
            removeInternalTypes();
        }
        return deviceTypes;
    }
    public void setDeviceTypes(List<ComponentType> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public ComponentType getSelectedComponentType() {
        return selectedComponentType;
    }
    public void setSelectedComponentType(ComponentType selectedComponentType) {
        this.selectedComponentType = selectedComponentType;
    }

    public void prepareDevicesForDisplay() {
        this.devices = deviceEJB.findDevicesByComponentType(selectedComponentType);
    }

    public List<Device> getDevices() {
        return devices;
    }
    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Device getSelectedDevice() {
        return selectedDevice;
    }
    public void setSelectedDevice(Device selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<ComponentType> getFilteredComponentTypes() {
        return filteredComponentTypes;
    }
    public void setFilteredComponentTypes(List<ComponentType> filteredComponentTypes) {
        this.filteredComponentTypes = filteredComponentTypes;
    }
}
