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
@Table(name = "device_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceArtifact.findAll", query = "SELECT d FROM DeviceArtifact d"),
    @NamedQuery(name = "DeviceArtifact.findByArtifactId", query = "SELECT d FROM DeviceArtifact d WHERE d.artifactId = :artifactId"),
    @NamedQuery(name = "DeviceArtifact.findByName", query = "SELECT d FROM DeviceArtifact d WHERE d.name = :name"),
    @NamedQuery(name = "DeviceArtifact.findByIsInternal", query = "SELECT d FROM DeviceArtifact d WHERE d.isInternal = :isInternal"),
    @NamedQuery(name = "DeviceArtifact.findByDescription", query = "SELECT d FROM DeviceArtifact d WHERE d.description = :description"),
    @NamedQuery(name = "DeviceArtifact.findByModifiedBy", query = "SELECT d FROM DeviceArtifact d WHERE d.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "DeviceArtifact.findByModifiedAt", query = "SELECT d FROM DeviceArtifact d WHERE d.modifiedAt = :modifiedAt")})
public class DeviceArtifact implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "uri", columnDefinition="TEXT")
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

    @Version
    private Long version;

    @JoinColumn(name = "device", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device device;

    protected DeviceArtifact() {
    }

    public DeviceArtifact(String name, boolean isInternal, String description, String uri, String modifiedBy) {
        this.name = name;
        this.isInternal = isInternal;
        this.description = description;
        this.uri = uri;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getArtifactId() {
        return artifactId;
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

    protected Long getVersion() {
        return version;
    }

    protected void setVersion(Long version) {
        this.version = version;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (artifactId != null ? artifactId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DeviceArtifact)) return false;

        DeviceArtifact other = (DeviceArtifact) object;
        if (this.artifactId == null && other.artifactId != null) return false;
        if (this.artifactId != null) return this.artifactId.equals(other.artifactId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "DeviceArtifact[ artifactId=" + artifactId + " ]";
    }

}
