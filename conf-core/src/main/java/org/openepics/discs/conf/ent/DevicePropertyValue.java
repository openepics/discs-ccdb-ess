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
package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.openepics.discs.conf.ejb.EntityWithProperties;

/**
 * {@link PropertyValue} used with Device Instances
 *
 * @author vuppala
 */
@Entity
@Table(name = "device_property_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DevicePropertyValue.findAll", query = "SELECT d FROM DevicePropertyValue d"),
    @NamedQuery(name = "DevicePropertyValue.findByDevPropId", query = "SELECT d FROM DevicePropertyValue d "
            + "WHERE d.id = :id"),
    @NamedQuery(name = "DevicePropertyValue.findByInRepository", query = "SELECT d FROM DevicePropertyValue d "
            + "WHERE d.inRepository = :inRepository"),
    @NamedQuery(name = "DevicePropertyValue.findByModifiedBy", query = "SELECT d FROM DevicePropertyValue d "
            + "WHERE d.modifiedBy = :modifiedBy")
})
public class DevicePropertyValue extends PropertyValue {
    private static final long serialVersionUID = -1803230486932708585L;

    @JoinColumn(name = "device")
    @ManyToOne(optional = false)
    private Device device;

    public DevicePropertyValue() { }

    /**
     * Constructs a new device instance property value
     *
     * @param inRepository <code>false</code>
     */
    public DevicePropertyValue(boolean inRepository) {
        super(inRepository);
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public void setPropertiesParent(EntityWithProperties owner) {
        setDevice((Device) owner);
    }

    @Override
    public EntityWithProperties getPropertiesParent() {
        return getDevice();
    }

    @Override
    public String toString() {
        return "DeviceProperty[ devPropId=" + id + " ]";
    }
}
