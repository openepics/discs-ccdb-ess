package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
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
@Table(name = "slot_relation", indexes = { @Index(columnList = "name") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotRelation.findAll", query = "SELECT s FROM SlotRelation s"),
    @NamedQuery(name = "SlotRelation.findBySlotRelationId", query = "SELECT s FROM SlotRelation s WHERE s.id = :id"),
    @NamedQuery(name = "SlotRelation.findByName", query = "SELECT s FROM SlotRelation s WHERE s.name = :name")
})
public class SlotRelation extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private SlotRelationName name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "iname")
    private String iname;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    protected SlotRelation() {
    }

    public SlotRelation(SlotRelationName name) {
        setName(name);
    }

    public SlotRelationName getName() {
        return name;
    }
    public String getNameAsString() {
        return name.toString().toLowerCase();
    }

    public void setName(SlotRelationName name) {
        this.name = name;
        if (name == SlotRelationName.CONTAINS) {
            iname = "contained in";
        } else if (name == SlotRelationName.POWERS) {
            iname = "powered by";
        } else if (name == SlotRelationName.CONTROLS) {
            iname = "controlled by";
        }
    }

    public String getIname() {
        return iname;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SlotRelation[ slotRelationId=" + id + " ]";
    }
}
