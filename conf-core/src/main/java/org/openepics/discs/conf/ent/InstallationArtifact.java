package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An {@link Artifact} attached to {@link InstallationRecord} entities
 *
 * @author vuppala
 */
@Entity
@Table(name = "installation_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InstallationArtifact.findAll", query = "SELECT i FROM InstallationArtifact i"),
    @NamedQuery(name = "InstallationArtifact.findByArtifactId", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.id = :id"),
    @NamedQuery(name = "InstallationArtifact.findByName", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.name = :name"),
    @NamedQuery(name = "InstallationArtifact.findByIsInternal", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.isInternal = :isInternal"),
    @NamedQuery(name = "InstallationArtifact.findByModifiedBy", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.modifiedBy = :modifiedBy")
})
public class InstallationArtifact extends Artifact {
    @JoinColumn(name = "installation_record")
    @ManyToOne(optional = false)
    private InstallationRecord installationRecord;

    protected InstallationArtifact() { }

    public InstallationArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
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
