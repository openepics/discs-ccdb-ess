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
package org.openepics.discs.conf.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 * DAO service for accessing device instances.
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class DeviceEJB extends DAO<Device> {
    /**
     * Searches for a device instance in the database, by its serial number
     *
     * @param serialNumber the {@link String} serial number
     * @return a device entity matching the criteria or <code>null</code>
     */
    public Device findDeviceBySerialNumber(String serialNumber) {
        return findByName(serialNumber);
    }

    /** Finds a list of device instances of a specified component type.
     * @param componentType - the component type to search for.
     * @return The list of instances of a specified component type.
     */
    public List<Device> findDevicesByComponentType(ComponentType componentType) {
        if (componentType == null) {
            return new ArrayList<>();
        }

        return em.createNamedQuery("Device.findByComponentType", Device.class)
                .setParameter("componentType", componentType).getResultList();
    }

    @Override
    protected void defineEntity() {
        defineEntityClass(Device.class);

        defineParentChildInterface(DevicePropertyValue.class, new ParentChildInterface<Device, DevicePropertyValue>() {
            @Override
            public List<DevicePropertyValue> getChildCollection(Device device) {
                return device.getDevicePropertyList();
            }
            @Override
            public Device getParentFromChild(DevicePropertyValue child) {
                return child.getDevice();
            }
        });

        defineParentChildInterface(DeviceArtifact.class, new ParentChildInterface<Device, DeviceArtifact>() {

            @Override
            public List<DeviceArtifact> getChildCollection(Device device) {
                return device.getDeviceArtifactList();

            }

            @Override
            public Device getParentFromChild(DeviceArtifact child) {
                return child.getDevice();
            }
        });
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void bulkDeleteUndefinedDeviceProps(ComponentType compType, Property prop) {
        List<Device> devicesOfAppropriateType = em.createNamedQuery("Device.findByComponentType", Device.class)
                .setParameter("componentType", compType).getResultList();
        for (Device dev : devicesOfAppropriateType) {
            DevicePropertyValue valueToDelete = null;
            for (DevicePropertyValue dpv : dev.getDevicePropertyList()) {
                if (dpv.getProperty().equals(prop) && dpv.getPropValue() == null) {
                    valueToDelete = dpv;
                    break;
                }
            }
            if (valueToDelete != null) {
                deleteChild(valueToDelete);
            }
        }
    }
}
