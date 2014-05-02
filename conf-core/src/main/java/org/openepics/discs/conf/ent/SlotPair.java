/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
    @NamedQuery(name = "SlotPair.findBySlotRelation", query = "SELECT s FROM SlotPair s WHERE s.slotPairPK.slotRelation = :slotRelation"),
    @NamedQuery(name = "SlotPair.findByParentSlot", query = "SELECT s FROM SlotPair s WHERE s.slotPairPK.parentSlot = :parentSlot"),
    @NamedQuery(name = "SlotPair.findByChildSlot", query = "SELECT s FROM SlotPair s WHERE s.slotPairPK.childSlot = :childSlot"),
    @NamedQuery(name = "SlotPair.findByVersion", query = "SELECT s FROM SlotPair s WHERE s.version = :version")})
public class SlotPair implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SlotPairPK slotPairPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "child_slot", referencedColumnName = "slot_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Slot slot;
    @JoinColumn(name = "slot_relation", referencedColumnName = "slot_relation_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SlotRelation slotRelation1;
    @JoinColumn(name = "parent_slot", referencedColumnName = "slot_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Slot slot1;

    public SlotPair() {
    }

    public SlotPair(SlotPairPK slotPairPK) {
        this.slotPairPK = slotPairPK;
    }

    public SlotPair(SlotPairPK slotPairPK, int version) {
        this.slotPairPK = slotPairPK;
        this.version = version;
    }

    public SlotPair(int slotRelation, int parentSlot, int childSlot) {
        this.slotPairPK = new SlotPairPK(slotRelation, parentSlot, childSlot);
    }

    public SlotPairPK getSlotPairPK() {
        return slotPairPK;
    }

    public void setSlotPairPK(SlotPairPK slotPairPK) {
        this.slotPairPK = slotPairPK;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public SlotRelation getSlotRelation1() {
        return slotRelation1;
    }

    public void setSlotRelation1(SlotRelation slotRelation1) {
        this.slotRelation1 = slotRelation1;
    }

    public Slot getSlot1() {
        return slot1;
    }

    public void setSlot1(Slot slot1) {
        this.slot1 = slot1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (slotPairPK != null ? slotPairPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlotPair)) {
            return false;
        }
        SlotPair other = (SlotPair) object;
        if ((this.slotPairPK == null && other.slotPairPK != null) || (this.slotPairPK != null && !this.slotPairPK.equals(other.slotPairPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotPair[ slotPairPK=" + slotPairPK + " ]";
    }
    
}
