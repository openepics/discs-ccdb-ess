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
@Table(name = "slot_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotArtifact.findAll", query = "SELECT s FROM SlotArtifact s"),
    @NamedQuery(name = "SlotArtifact.findByArtifactId", query = "SELECT s FROM SlotArtifact s WHERE s.id = :id"),
    @NamedQuery(name = "SlotArtifact.findByIsInternal", query = "SELECT s FROM SlotArtifact s WHERE s.isInternal = :isInternal"),
    @NamedQuery(name = "SlotArtifact.findByModifiedBy", query = "SELECT s FROM SlotArtifact s WHERE s.modifiedBy = :modifiedBy")})
public class SlotArtifact extends Artifact {
    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    public SlotArtifact() { }

    public SlotArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
    }

    public Slot getSlot() { return slot; }
    public void setSlot(Slot slot) { this.slot = slot; }

    @Override
    public String toString() {
        return "SlotArtifact[ artifactId=" + id + " ]";
    }
}
