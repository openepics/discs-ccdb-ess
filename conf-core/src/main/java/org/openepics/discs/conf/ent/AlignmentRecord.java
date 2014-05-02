/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentRecord.findAll", query = "SELECT a FROM AlignmentRecord a"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentRecordId", query = "SELECT a FROM AlignmentRecord a WHERE a.alignmentRecordId = :alignmentRecordId"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentDate", query = "SELECT a FROM AlignmentRecord a WHERE a.alignmentDate = :alignmentDate"),
    @NamedQuery(name = "AlignmentRecord.findByModifiedAt", query = "SELECT a FROM AlignmentRecord a WHERE a.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "AlignmentRecord.findByModifiedBy", query = "SELECT a FROM AlignmentRecord a WHERE a.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "AlignmentRecord.findByVersion", query = "SELECT a FROM AlignmentRecord a WHERE a.version = :version")})
public class AlignmentRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "alignment_record_id")
    private Integer alignmentRecordId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "alignment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date alignmentDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;
    @JoinColumn(name = "physical_component", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device physicalComponent;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentArtifact> alignmentArtifactList;

    public AlignmentRecord() {
    }

    public AlignmentRecord(Integer alignmentRecordId) {
        this.alignmentRecordId = alignmentRecordId;
    }

    public AlignmentRecord(Integer alignmentRecordId, Date alignmentDate, Date modifiedAt, String modifiedBy, int version) {
        this.alignmentRecordId = alignmentRecordId;
        this.alignmentDate = alignmentDate;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public Integer getAlignmentRecordId() {
        return alignmentRecordId;
    }

    public void setAlignmentRecordId(Integer alignmentRecordId) {
        this.alignmentRecordId = alignmentRecordId;
    }

    public Date getAlignmentDate() {
        return alignmentDate;
    }

    public void setAlignmentDate(Date alignmentDate) {
        this.alignmentDate = alignmentDate;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Device getPhysicalComponent() {
        return physicalComponent;
    }

    public void setPhysicalComponent(Device physicalComponent) {
        this.physicalComponent = physicalComponent;
    }

    @XmlTransient
    public List<AlignmentArtifact> getAlignmentArtifactList() {
        return alignmentArtifactList;
    }

    public void setAlignmentArtifactList(List<AlignmentArtifact> alignmentArtifactList) {
        this.alignmentArtifactList = alignmentArtifactList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (alignmentRecordId != null ? alignmentRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlignmentRecord)) {
            return false;
        }
        AlignmentRecord other = (AlignmentRecord) object;
        if ((this.alignmentRecordId == null && other.alignmentRecordId != null) || (this.alignmentRecordId != null && !this.alignmentRecordId.equals(other.alignmentRecordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.AlignmentRecord[ alignmentRecordId=" + alignmentRecordId + " ]";
    }
    
}
