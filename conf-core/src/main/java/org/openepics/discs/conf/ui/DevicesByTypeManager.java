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
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;


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

}
