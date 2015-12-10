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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "device", indexes = { @Index(columnList = "serial_number", unique = true), @Index(columnList = "component_type") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d"),
    // device instance does not have a name. This named query is introduced to satisfy the ReadOnlyDAO assumption.
    @NamedQuery(name = "Device.findByName", query = "SELECT d FROM Device d WHERE d.serialNumber = :name"),
    @NamedQuery(name = "Device.findByComponentType", query = "SELECT d FROM Device d "
            + "WHERE d.componentType = :componentType"),
    @NamedQuery(name = "Device.uninstalledDevicesByType", query = "SELECT d from Device d "
            + "WHERE d.componentType = :componentType "
            + "AND (NOT EXISTS (SELECT ir FROM InstallationRecord ir WHERE d = ir.device) "
            + "OR NOT EXISTS (SELECT ir FROM InstallationRecord ir WHERE d = ir.device AND ir.uninstallDate IS NULL))")
})

public class Device extends ConfigurationEntity
    implements EntityWithProperties, EntityWithArtifacts, EntityWithTags, NamedEntity {

    private static final long serialVersionUID = 113778637670841841L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "serial_number")
    private String serialNumber;

    @Size(max = 16)
    @Column(name = "asm_position")
    private String asmPosition;

    @Size(max = 255)
    @Column(name = "asm_description")
    private String asmDescription;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DevicePropertyValue> devicePropertyList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<AlignmentRecord> alignmentRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<InstallationRecord> installationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DeviceArtifact> deviceArtifactList = new ArrayList<>();

    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @OneToMany(mappedBy = "asmParent")
    private List<Device> deviceList = new ArrayList<>();

    @JoinColumn(name = "asm_parent")
    @ManyToOne
    private Device asmParent;

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "device_tag",
               joinColumns = { @JoinColumn(name = "device_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "device_authorization",
                joinColumns = { @JoinColumn(name = "device_id") },
                inverseJoinColumns = { @JoinColumn(name = "auth_data_id") })
    private List<Authorization> authorizationData;

    protected Device() {
    }

    /**
     * Constructs a new device instance
     *
     * @param serialNumber a unique inventory ID
     */
    public Device(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAssemblyPosition() {
        return asmPosition;
    }
    public void setAssemblyPosition(String asmPosition) {
        this.asmPosition = asmPosition;
    }

    public String getAssemblyDescription() {
        return asmDescription;
    }
    public void setAssemblyDescription(String asmDescription) {
        this.asmDescription = asmDescription;
    }

    @XmlTransient
    @JsonIgnore
    public List<DevicePropertyValue> getDevicePropertyList() {
        return devicePropertyList;
    }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentRecord> getAlignmentRecordList() {
        return alignmentRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public List<InstallationRecord> getInstallationRecordList() {
        return installationRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public List<DeviceArtifact> getDeviceArtifactList() {
        return deviceArtifactList;
    }

    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @XmlTransient
    @JsonIgnore
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public Device getAssemblyParent() {
        return asmParent;
    }
    public void setAssemblyParent(Device asmParent) {
        this.asmParent = asmParent;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() {
        return tags;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @XmlTransient
    @JsonIgnore
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PropertyValue> List<T> getEntityPropertyList() {
        return (List<T>) getDevicePropertyList();
    }

    @XmlTransient
    @JsonIgnore
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Artifact> List<T> getEntityArtifactList() {
        return (List<T>) getDeviceArtifactList();
    }

    @Override
    public String toString() {
        return "Device[ deviceId=" + id + " ]";
    }

    @Override
    public String getName() {
        return serialNumber;
    }

}
