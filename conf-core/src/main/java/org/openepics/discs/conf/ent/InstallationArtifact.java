package org.openepics.discs.conf.ent;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "installation_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InstallationArtifact.findAll", query = "SELECT i FROM InstallationArtifact i"),
    @NamedQuery(name = "InstallationArtifact.findByArtifactId", query = "SELECT i FROM InstallationArtifact i WHERE i.id = :id"),
    @NamedQuery(name = "InstallationArtifact.findByName", query = "SELECT i FROM InstallationArtifact i WHERE i.name = :name"),
    @NamedQuery(name = "InstallationArtifact.findByIsInternal", query = "SELECT i FROM InstallationArtifact i WHERE i.isInternal = :isInternal"),
    @NamedQuery(name = "InstallationArtifact.findByDescription", query = "SELECT i FROM InstallationArtifact i WHERE i.description = :description"),
    @NamedQuery(name = "InstallationArtifact.findByModifiedBy", query = "SELECT i FROM InstallationArtifact i WHERE i.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "InstallationArtifact.findByModifiedAt", query = "SELECT i FROM InstallationArtifact i WHERE i.modifiedAt = :modifiedAt")})
public class InstallationArtifact extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

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
    @Column(name = "uri", columnDefinition="TEXT")
    private String uri;

    @JoinColumn(name = "installation_record", referencedColumnName = "installation_record_id")
    @ManyToOne(optional = false)
    private InstallationRecord installationRecord;

    protected InstallationArtifact() {
    }

    public InstallationArtifact(String name, boolean isInternal, String uri, String modifiedBy) {
        this.name = name;
        this.isInternal = isInternal;
        this.uri = uri;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
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

    public InstallationRecord getInstallationRecord() {
        return installationRecord;
    }

    public void setInstallationRecord(InstallationRecord installationRecord) {
        this.installationRecord = installationRecord;
    }

    @Override
    public String toString() {
        return "InstallationArtifact[ artifactId=" + id + " ]";
    }

}
