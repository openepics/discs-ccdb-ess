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
@Table(name = "ls_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LsArtifact.findAll", query = "SELECT l FROM LsArtifact l"),
    @NamedQuery(name = "LsArtifact.findByArtifactId", query = "SELECT l FROM LsArtifact l WHERE l.artifactId = :artifactId"),
    @NamedQuery(name = "LsArtifact.findByName", query = "SELECT l FROM LsArtifact l WHERE l.name = :name"),
    @NamedQuery(name = "LsArtifact.findByDescription", query = "SELECT l FROM LsArtifact l WHERE l.description = :description"),
    @NamedQuery(name = "LsArtifact.findByModifiedBy", query = "SELECT l FROM LsArtifact l WHERE l.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "LsArtifact.findByModifiedAt", query = "SELECT l FROM LsArtifact l WHERE l.modifiedAt = :modifiedAt")})
public class LsArtifact implements Serializable {
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
    @Size(min = 1, max = 255)
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
    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;

    public LsArtifact() {
    }

    public LsArtifact(Integer artifactId) {
        this.artifactId = artifactId;
    }

    public LsArtifact(Integer artifactId, String name, String description, String uri, String modifiedBy, Date modifiedAt) {
        this.artifactId = artifactId;
        this.name = name;
        this.description = description;
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

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
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
        if (!(object instanceof LsArtifact)) {
            return false;
        }
        LsArtifact other = (LsArtifact) object;
        if ((this.artifactId == null && other.artifactId != null) || (this.artifactId != null && !this.artifactId.equals(other.artifactId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.LsArtifact[ artifactId=" + artifactId + " ]";
    }
    
}
