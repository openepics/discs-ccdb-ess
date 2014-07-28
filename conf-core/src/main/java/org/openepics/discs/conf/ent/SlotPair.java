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
    @NamedQuery(name = "SlotPair.findBySlotPairId", query = "SELECT s FROM SlotPair s WHERE s.slotPairId = :slotPairId")})
public class SlotPair implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_pair_id")
    private Integer slotPairId;

    @Version
    private Long version;

    @JoinColumn(name = "child_slot", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Slot childSlot;

    @JoinColumn(name = "slot_relation", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SlotRelation slotRelation;

    @JoinColumn(name = "parent_slot", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Slot parentSlot;

    public SlotPair() {
    }

    public Integer getSlotPairId() {
        return slotPairId;
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
        int hash = 0;
        hash += (slotPairId != null ? slotPairId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SlotPair)) return false;

        SlotPair other = (SlotPair) object;
        if (this.slotPairId == null && other.slotPairId != null) return false;
        if (this.slotPairId != null) return this.slotPairId.equals(other.slotPairId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "SlotPair[ slotPairId=" + slotPairId + " ]";
    }

}
