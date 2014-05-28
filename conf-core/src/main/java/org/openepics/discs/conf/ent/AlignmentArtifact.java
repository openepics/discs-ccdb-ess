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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentArtifact.findAll", query = "SELECT a FROM AlignmentArtifact a"),
    @NamedQuery(name = "AlignmentArtifact.findByArtifactId", query = "SELECT a FROM AlignmentArtifact a WHERE a.artifactId = :artifactId"),
    @NamedQuery(name = "AlignmentArtifact.findByName", query = "SELECT a FROM AlignmentArtifact a WHERE a.name = :name"),
    @NamedQuery(name = "AlignmentArtifact.findByIsInternal", query = "SELECT a FROM AlignmentArtifact a WHERE a.isInternal = :isInternal"),
    @NamedQuery(name = "AlignmentArtifact.findByDescription", query = "SELECT a FROM AlignmentArtifact a WHERE a.description = :description"),
    @NamedQuery(name = "AlignmentArtifact.findByModifiedBy", query = "SELECT a FROM AlignmentArtifact a WHERE a.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "AlignmentArtifact.findByModifiedAt", query = "SELECT a FROM AlignmentArtifact a WHERE a.modifiedAt = :modifiedAt")})
public class AlignmentArtifact implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "artifact_id")
    private Integer artifactId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_internal")
    private boolean isInternal;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "uri")
    private String uri;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    @JoinColumn(name = "alignment_record", referencedColumnName = "alignment_record_id")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    public AlignmentArtifact() {
    }

    public AlignmentArtifact(Integer artifactId) {
        this.artifactId = artifactId;
    }

    public AlignmentArtifact(Integer artifactId, String name, boolean isInternal, String uri, String modifiedBy, Date modifiedAt) {
        this.artifactId = artifactId;
        this.name = name;
        this.isInternal = isInternal;
        this.uri = uri;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    public Integer getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Integer artifactId) {
        this.artifactId = artifactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }

    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (artifactId != null ? artifactId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlignmentArtifact)) {
            return false;
        }
        AlignmentArtifact other = (AlignmentArtifact) object;
        if ((this.artifactId == null && other.artifactId != null) || (this.artifactId != null && !this.artifactId.equals(other.artifactId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.AlignmentArtifact[ artifactId=" + artifactId + " ]";
    }
    
}
