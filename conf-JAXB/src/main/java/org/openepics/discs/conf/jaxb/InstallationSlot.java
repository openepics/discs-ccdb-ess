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

import java.util.ArrayList;
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
@XmlSeeAlso({PropertyValue.class})
public class InstallationSlot {
    private String name;
    private String description;

    @XmlElement private DeviceType deviceType;

    @XmlElementWrapper(name = "parents")
    @XmlElement(name = "parent")
    private List<String> parents = new ArrayList<>();

    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    private List<String> children = new ArrayList<>();

    @XmlElementWrapper(name = "powers")
    @XmlElement(name = "power")
    private List<String> powers = new ArrayList<>();

    @XmlElementWrapper(name = "poweredBy")
    @XmlElement(name = "powerBy")
    private List<String> poweredBy = new ArrayList<>();

    @XmlElementWrapper(name = "controls")
    @XmlElement(name = "control")
    private List<String> controls = new ArrayList<>();

    @XmlElementWrapper(name = "controlledBy")
    @XmlElement(name = "controlBy")
    private List<String> controlledBy = new ArrayList<>();

    @XmlElementWrapper(name = "properties")
    @XmlAnyElement(lax = true)
    private List<PropertyValue> properties;

    public InstallationSlot() { }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public List<String> getParents() { return parents; }
    public void setParents(List<String> parents) { this.parents = parents; }

    public List<String> getChildren() { return children; }
    public void setChildren(List<String> children) { this.children = children; }

    public List<String> getPowers() { return powers; }
    public void setPowers(List<String> powers) { this.powers = powers; }

    public List<String> getPoweredBy() { return poweredBy; }
    public void setPoweredBy(List<String> poweredBy) { this.poweredBy = poweredBy; }

    public List<String> getControls() { return controls; }
    public void setControls(List<String> controls) { this.controls = controls; }

    public List<String> getControlledBy() { return controlledBy; }
    public void setControlledBy(List<String> controlledBy) { this.controlledBy = controlledBy; }

    public List<PropertyValue> getProperties() { return properties; }
    public void setProperties(List<PropertyValue> properties) { this.properties = properties; }
}
