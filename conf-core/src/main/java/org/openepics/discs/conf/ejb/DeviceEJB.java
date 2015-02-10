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
import javax.inject.Inject;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

import com.google.common.base.Preconditions;

/**
 * DAO service for accessing device instances.
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class DeviceEJB extends DAO<Device> {

    @Inject private ComptypeEJB componentTypesEJB;

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

    /**
     * Adds a new device and creates all its defined property values in a single transaction
     * @param device the {@link Device} the add
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addDeviceAndPropertyDefs(Device device) {
        add(device);
        List<ComptypePropertyValue> propertyDefinitions = componentTypesEJB.findPropertyDefinitions(device.getComponentType());
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            if (propertyDefinition.isDefinitionTargetDevice()) {
                final DevicePropertyValue devicePropertyValue = new DevicePropertyValue(false);
                devicePropertyValue.setProperty(propertyDefinition.getProperty());
                devicePropertyValue.setDevice(device);
                addChild(devicePropertyValue);
            }
        }
    }

    @Override
    protected Class<Device> getEntityClass() {
        return Device.class;
    }

    @Override
    protected boolean isPropertyValueTypeUnique(PropertyValue child, Device parent) {
        Preconditions.checkNotNull(child);
        Preconditions.checkNotNull(parent);
        final Value value = child.getPropValue();
        if (value == null) {
            return true;
        }
        final List<PropertyValue> results = em.createNamedQuery("DevicePropertyValue.findSamePropertyValueByType",
                                                        PropertyValue.class)
                    .setParameter("componentType", parent.getComponentType())
                    .setParameter("property", child.getProperty())
                    .setParameter("propValue", value).setMaxResults(2).getResultList();
        // value is unique if there is no property value with the same value, or the only one found us the entity itself
        return (results.size() < 2) && (results.isEmpty() || results.get(0).equals(child));
    }
}
