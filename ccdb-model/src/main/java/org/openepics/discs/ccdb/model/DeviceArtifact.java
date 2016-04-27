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
package org.openepics.discs.ccdb.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An {@link Artifact} used in device instances
 *
 * @author vuppala
 */
@Entity
@Table(name = "device_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceArtifact.findAll", query = "SELECT d FROM DeviceArtifact d"),
    @NamedQuery(name = "DeviceArtifact.findByArtifactId", query = "SELECT d FROM DeviceArtifact d WHERE d.id = :id"),
    @NamedQuery(name = "DeviceArtifact.findByName", query = "SELECT d FROM DeviceArtifact d WHERE d.name = :name"),
    @NamedQuery(name = "DeviceArtifact.findByIsInternal", query = "SELECT d FROM DeviceArtifact d "
            + "WHERE d.isInternal = :isInternal"),
    @NamedQuery(name = "DeviceArtifact.findByModifiedBy", query = "SELECT d FROM DeviceArtifact d "
            + "WHERE d.modifiedBy = :modifiedBy")
})
public class DeviceArtifact extends Artifact {
    private static final long serialVersionUID = -6839832536999872940L;

    @JoinColumn(name = "device")
    @ManyToOne(optional = false)
    private Device device;

    public DeviceArtifact() { }

    /** Constructs a new device instance artifact
     * @param name the name of the artifact
     * @param isInternal <code>true</code> if the artifact is a file attachment, <code>false</code> if it's an URL.
     * @param description the user specified description
     * @param uri the user specified URL
     */
    public DeviceArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public EntityWithArtifacts getArtifactsParent() {
        return getDevice();
    }

    @Override
    public void setArtifactsParent(EntityWithArtifacts parent) {
        setDevice((Device) parent);   
    }

    @Override
    public String toString() {
        return "DeviceArtifact[ artifactId=" + id + " ]";
    }
}
