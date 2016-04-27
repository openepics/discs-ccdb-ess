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

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Alignment record contains alignment information for Slots
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_record", indexes = { @Index(columnList = "slot"), @Index(columnList = "device") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentRecord.findAll", query = "SELECT a FROM AlignmentRecord a"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentRecordId", query = "SELECT a FROM AlignmentRecord a "
            + "WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentRecord.findByRecordNumber", query = "SELECT a FROM AlignmentRecord a "
            + "WHERE a.recordNumber = :recordNumber"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentDate", query = "SELECT a FROM AlignmentRecord a "
            + "WHERE a.alignmentDate = :alignmentDate"),
    @NamedQuery(name = "AlignmentRecord.findByModifiedBy", query = "SELECT a FROM AlignmentRecord a "
            + "WHERE a.modifiedBy = :modifiedBy")
})
public class AlignmentRecord extends ConfigurationEntity
    implements EntityWithProperties, EntityWithArtifacts {

    private static final long serialVersionUID = -2801428073110847383L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "record_number")
    private String recordNumber;

    @Basic(optional = false)
    @NotNull
    @Column(name = "alignment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date alignmentDate;

    @Embedded
    private PositionInformation alignmentInfo = new PositionInformation();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentArtifact> alignmentArtifactList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentPropertyValue> alignmentPropertyList;

    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    @JoinColumn(name = "device")
    @ManyToOne(optional = false)
    private Device device;

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "alignment_tag",
               joinColumns = { @JoinColumn(name = "alignment_id") },
               inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    protected AlignmentRecord() {
    }

    /** Constructs a new alignment record
     * @param recordNumber a string identifying this alignment record
     * @param alignmentDate the timestamp of the alignment record
     */
    public AlignmentRecord(String recordNumber, Date alignmentDate) {
        this.recordNumber = recordNumber;
        this.alignmentDate = new Date(alignmentDate.getTime());
    }

    public String getRecordNumber() {
        return recordNumber;
    }
    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    /**
     * @return a new copy of the alignment record timestamp
     */
    public Date getAlignmentDate() {
        return alignmentDate != null ? new Date(alignmentDate.getTime()) : null;
    }
    public void setAlignmentDate(Date alignmentDate) {
        this.alignmentDate = new Date(alignmentDate.getTime());
    }

    public PositionInformation getAlignmentInformation() {
        return alignmentInfo;
    }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentArtifact> getAlignmentArtifactList() {
        return alignmentArtifactList;
    }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentPropertyValue> getAlignmentPropertyList() {
        return alignmentPropertyList;
    }

    public Slot getSlot() {
        return slot;
    }
    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PropertyValue> List<T> getEntityPropertyList() {
        return (List<T>) getAlignmentPropertyList();
    }

    @XmlTransient
    @JsonIgnore
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Artifact> List<T> getEntityArtifactList() {
        return (List<T>) getAlignmentArtifactList();
    }

    @Override
    public String toString() {
        return "AlignmentRecord[ alignmentRecordId=" + id + " ]";
    }

}
