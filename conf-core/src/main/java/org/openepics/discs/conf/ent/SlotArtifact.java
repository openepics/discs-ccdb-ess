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
@Table(name = "slot_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotArtifact.findAll", query = "SELECT s FROM SlotArtifact s"),
    @NamedQuery(name = "SlotArtifact.findByArtifactId", query = "SELECT s FROM SlotArtifact s WHERE s.artifactId = :artifactId"),
    @NamedQuery(name = "SlotArtifact.findByName", query = "SELECT s FROM SlotArtifact s WHERE s.name = :name"),
    @NamedQuery(name = "SlotArtifact.findByIsInternal", query = "SELECT s FROM SlotArtifact s WHERE s.isInternal = :isInternal"),
    @NamedQuery(name = "SlotArtifact.findByDescription", query = "SELECT s FROM SlotArtifact s WHERE s.description = :description"),
    @NamedQuery(name = "SlotArtifact.findByModifiedBy", query = "SELECT s FROM SlotArtifact s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "SlotArtifact.findByModifiedAt", query = "SELECT s FROM SlotArtifact s WHERE s.modifiedAt = :modifiedAt")})
public class SlotArtifact extends ConfigurationEntity {
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

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "uri", columnDefinition="TEXT")
    private String uri;

    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;

    protected SlotArtifact() {
    }

    public SlotArtifact(String name, boolean isInternal, String description, String uri, String modifiedBy) {
        this.name = name;
        this.isInternal = isInternal;
        this.description = description;
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

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "SlotArtifact[ artifactId=" + id + " ]";
    }

}
