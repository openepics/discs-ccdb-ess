package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
    @NamedQuery(name = "DeviceArtifact.findByArtifactId", query = "SELECT d FROM DeviceArtifact d WHERE d.id = :id"),
    @NamedQuery(name = "DeviceArtifact.findByName", query = "SELECT d FROM DeviceArtifact d WHERE d.name = :name"),
    @NamedQuery(name = "DeviceArtifact.findByIsInternal", query = "SELECT d FROM DeviceArtifact d WHERE d.isInternal = :isInternal"),
    @NamedQuery(name = "DeviceArtifact.findByModifiedBy", query = "SELECT d FROM DeviceArtifact d WHERE d.modifiedBy = :modifiedBy")})
public class DeviceArtifact extends Artifact {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "device", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Device device;

    protected DeviceArtifact() { }

    public DeviceArtifact(String name, boolean isInternal, String description, String uri, String modifiedBy) {
        super(name, isInternal, description, uri, modifiedBy);
    }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    @Override
    public String toString() {
        return "DeviceArtifact[ artifactId=" + id + " ]";
    }
}
