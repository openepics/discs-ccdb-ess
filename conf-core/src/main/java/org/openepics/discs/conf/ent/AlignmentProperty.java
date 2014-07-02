/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentProperty.findAll", query = "SELECT a FROM AlignmentProperty a"),
    @NamedQuery(name = "AlignmentProperty.findByAlignPropId", query = "SELECT a FROM AlignmentProperty a WHERE a.alignPropId = :alignPropId"),
    @NamedQuery(name = "AlignmentProperty.findByInRepository", query = "SELECT a FROM AlignmentProperty a WHERE a.inRepository = :inRepository"),
    @NamedQuery(name = "AlignmentProperty.findByModifiedAt", query = "SELECT a FROM AlignmentProperty a WHERE a.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "AlignmentProperty.findByModifiedBy", query = "SELECT a FROM AlignmentProperty a WHERE a.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "AlignmentProperty.findByVersion", query = "SELECT a FROM AlignmentProperty a WHERE a.version = :version")})
public class AlignmentProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "align_prop_id")
    private Integer alignPropId;
    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;
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
    @Version
    private Long version;
    @JoinColumn(name = "alignment_record", referencedColumnName = "alignment_record_id")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;
    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;

    public AlignmentProperty() {
    }

    public AlignmentProperty(Integer alignPropId) {
        this.alignPropId = alignPropId;
    }

    public AlignmentProperty(Integer alignPropId, boolean inRepository, Date modifiedAt, String modifiedBy) {
        this.alignPropId = alignPropId;
        this.inRepository = inRepository;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

    public Integer getAlignPropId() {
        return alignPropId;
    }

    public void setAlignPropId(Integer alignPropId) {
        this.alignPropId = alignPropId;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public boolean getInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
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

    protected long getVersion() {
        return version;
    }

    protected void setVersion(long version) {
        this.version = version;
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }

    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (alignPropId != null ? alignPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlignmentProperty)) {
            return false;
        }
        AlignmentProperty other = (AlignmentProperty) object;
        if ((this.alignPropId == null && other.alignPropId != null) || (this.alignPropId != null && !this.alignPropId.equals(other.alignPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.AlignmentProperty[ alignPropId=" + alignPropId + " ]";
    }

}
