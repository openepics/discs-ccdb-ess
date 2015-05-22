/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Cable Database.
 * Cable Database is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This is data transfer object representing a CCDB installation slot for JSON and XML serialization.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
@XmlRootElement(name = "installationSlot")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({InstallationSlotBasic.class, PropertyValue.class})
public class InstallationSlot {
    private String name;
    private String desription;

    @XmlElement private DeviceType deviceType;

    @XmlElementWrapper(name = "parents")
    @XmlAnyElement(lax = true)
    private List<InstallationSlotBasic> parents;

    @XmlElementWrapper(name = "children")
    @XmlAnyElement(lax = true)
    private List<InstallationSlotBasic> children;

    @XmlElementWrapper(name = "properties")
    @XmlAnyElement(lax = true)
    private List<PropertyValue> properties;

    public InstallationSlot() {
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDesription() {
        return desription;
    }
    public void setDesription(String desription) {
        this.desription = desription;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public List<InstallationSlotBasic> getParents() {
        return parents;
    }
    public void setParents(List<InstallationSlotBasic> parents) {
        this.parents = parents;
    }

    public List<InstallationSlotBasic> getChildren() {
        return children;
    }
    public void setChildren(List<InstallationSlotBasic> children) {
        this.children = children;
    }

    public List<PropertyValue> getProperties() {
        return properties;
    }
    public void setProperties(List<PropertyValue> properties) {
        this.properties = properties;
    }
}
