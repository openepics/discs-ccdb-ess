package org.openepics.discs.conf.ent;

import java.io.Serializable;

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
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot_pair")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotPair.findAll", query = "SELECT s FROM SlotPair s"),
    @NamedQuery(name = "SlotPair.findByParentChildRelation", query = "SELECT s FROM SlotPair s "
            + "WHERE s.childSlot.name LIKE :childName "
                + "AND s.parentSlot.name = :parentName AND s.slotRelation.name = :relationName"),
    @NamedQuery(name = "SlotPair.findById", query = "SELECT s FROM SlotPair s WHERE s.id = :id"),
    @NamedQuery(name = "SlotPair.findSlotPairsByChildAndRelation", query = "SELECT s FROM SlotPair s "
            + "WHERE s.childSlot = :childSlot AND s.slotRelation.name = :relationName")
})
public class SlotPair implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    private Long version;

    @JoinColumn(name = "child_slot")
    @ManyToOne(optional = false)
    private Slot childSlot;

    @JoinColumn(name = "slot_relation")
    @ManyToOne(optional = false)
    private SlotRelation slotRelation;

    @JoinColumn(name = "parent_slot")
    @ManyToOne(optional = false)
    private Slot parentSlot;

    public SlotPair() {
    }

    public SlotPair(Slot childSlot, Slot parentSlot, SlotRelation slotRelation) {
        this.childSlot = childSlot;
        this.parentSlot = parentSlot;
        this.slotRelation = slotRelation;
    }

    public Long getId() {
        return id;
    }

    public Slot getChildSlot() {
        return childSlot;
    }
    public void setChildSlot(Slot childSlot) {
        this.childSlot = childSlot;
    }

    public SlotRelation getSlotRelation() {
        return slotRelation;
    }
    public void setSlotRelation(SlotRelation slotRelation) {
        this.slotRelation = slotRelation;
    }

    public Slot getParentSlot() {
        return parentSlot;
    }
    public void setParentSlot(Slot parentSlot) {
        this.parentSlot = parentSlot;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SlotPair)) {
            return false;
        }

        SlotPair other = (SlotPair) object;
        if (this.id == null && other.id != null) {
            return false;
        }

        if (this.id != null) {
            // return true for same DB entity
            return this.id.equals(other.id);
        }

        return this==object;
    }

    public Long getVersion() { return version; }

    @Override
    public String toString() {
        return "SlotPair[ id=" + id + " ]";
    }
}
