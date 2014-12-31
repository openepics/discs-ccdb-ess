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
import javax.persistence.Embedded;
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
 * An installation slot for devices
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot", indexes = { @Index(columnList = "name") , @Index(columnList = "component_type"),
        @Index(columnList = "is_hosting_slot") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Slot.findAll", query = "SELECT s FROM Slot s"),
    @NamedQuery(name = "Slot.findBySlotId", query = "SELECT s FROM Slot s WHERE s.id = :id"),
    @NamedQuery(name = "Slot.findByName", query = "SELECT s FROM Slot s WHERE s.name = :name"),
    @NamedQuery(name = "Slot.findByNameAndHosting", query = "SELECT s FROM Slot s WHERE s.name = :name AND s.isHostingSlot = :isHostingSlot"),
    @NamedQuery(name = "Slot.findByNameContaining", query = "SELECT s FROM Slot s WHERE s.name LIKE :name"),
    @NamedQuery(name = "Slot.findByIsHostingSlot", query = "SELECT s FROM Slot s "
            + "WHERE s.isHostingSlot = :isHostingSlot"),
    @NamedQuery(name = "Slot.findByBeamlinePosition", query = "SELECT s FROM Slot s "
            + "WHERE s.beamlinePosition = :beamlinePosition"),
    @NamedQuery(name = "Slot.findByModifiedBy", query = "SELECT s FROM Slot s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "Slot.findByComponentType", query = "SELECT s FROM Slot s "
            + "WHERE s.componentType = :componentType")
})
public class Slot extends ConfigurationEntity {
    private static final long serialVersionUID = -1267956206090538337L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_hosting_slot")
    private boolean isHostingSlot;

    @Column(name = "beamline_position")
    private Double beamlinePosition;

    @Embedded
    private PositionInformation positionInfo = new PositionInformation();

    @Size(max = 255)
    @Column(name = "asm_comment")
    private String asmComment;

    @Size(max = 16)
    @Column(name = "asm_position")
    private String asmPosition;

    @Size(max = 255)
    @Column(name = "comment")
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<SlotArtifact> slotArtifactList = new ArrayList<>();

    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @OneToMany(mappedBy = "asmSlot")
    private List<Slot> slotList = new ArrayList<>();

    @JoinColumn(name = "asm_slot")
    @ManyToOne
    private Slot asmSlot;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childSlot")
    private List<SlotPair> pairsInWhichThisSlotIsAChild = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentSlot")
    private List<SlotPair> pairsInWhichThisSlotIsAParent = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<SlotPropertyValue> slotPropertyList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<InstallationRecord> installationRecordList = new ArrayList<>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "slot_tag",
               joinColumns = { @JoinColumn(name = "slot_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    protected Slot() {
    }

    /** Constructs a new slot.
     * @param name a new name of the slot
     * @param isHostingSlot <code>true</code> if the Slot is and "installation slot",
     * <code>false</code> if the Slot is a container
     */
    public Slot(String name, boolean isHostingSlot) {
        this.name = name;
        this.isHostingSlot = isHostingSlot;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHostingSlot() {
        return isHostingSlot;
    }
    public void setHostingSlot(boolean isHostingSlot) {
        this.isHostingSlot = isHostingSlot;
    }

    public Double getBeamlinePosition() {
        return beamlinePosition;
    }
    public void setBeamlinePosition(Double beamlinePosition) {
        this.beamlinePosition = beamlinePosition;
    }

    /**
     * @return The {@link PositionInformation} associated with the instalaltion slot.
     */
    public PositionInformation getPositionInformation()
    {
        // Due to some weirdness Hibernate clears the initialized field when loading data
        // Added this lazy initialization as convenience
        if (positionInfo==null) {
            positionInfo = new PositionInformation();
        }
        return positionInfo;
    }

    public String getAssemblyComment() {
        return asmComment;
    }
    public void setAssemblyComment(String asmComment) {
        this.asmComment = asmComment;
    }

    public String getAssemblyPosition() {
        return asmPosition;
    }
    public void setAssemblyPosition(String asmPosition) {
        this.asmPosition = asmPosition;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotArtifact> getSlotArtifactList() {
        return slotArtifactList;
    }

    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @XmlTransient
    @JsonIgnore
    public List<Slot> getSlotList() {
        return slotList;
    }

    public Slot getAssemblySlot() {
        return asmSlot;
    }
    public void setAssemblySlot(Slot asmSlot) {
        this.asmSlot = asmSlot;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotPair> getPairsInWhichThisSlotIsAChildList() {
        return pairsInWhichThisSlotIsAChild;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotPair> getPairsInWhichThisSlotIsAParentList() {
        return pairsInWhichThisSlotIsAParent;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotPropertyValue> getSlotPropertyList() {
        return slotPropertyList;
    }

    @XmlTransient
    @JsonIgnore
    public List<InstallationRecord> getInstallationRecordList() {
        return installationRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() {
        return tags;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Slot[ slotId=" + id + " ]";
    }
}
