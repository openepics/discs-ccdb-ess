/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;

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
    @NamedQuery(name = "SlotPair.findBySlotPairId", query = "SELECT s FROM SlotPair s WHERE s.slotPairId = :slotPairId"),
    @NamedQuery(name = "SlotPair.findByVersion", query = "SELECT s FROM SlotPair s WHERE s.version = :version")})
public class SlotPair implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "slot_pair_id")
    private Integer slotPairId;
    @Version
    private Long version;
    @JoinColumn(name = "child_slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot childSlot;
    @JoinColumn(name = "slot_relation", referencedColumnName = "slot_relation_id")
    @ManyToOne(optional = false)
    private SlotRelation slotRelation;
    @JoinColumn(name = "parent_slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot parentSlot;

    public SlotPair() {
    }

    public Integer getSlotPairId() {
        return slotPairId;
    }

    protected Long getVersion() {
        return version;
    }

    protected void setVersion(Long version) {
        this.version = version;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlotPair)) {
            return false;
        }
        SlotPair other = (SlotPair) object;
        if ((this.slotPairId == null && other.slotPairId != null) || (this.slotPairId != null && !this.slotPairId.equals(other.slotPairId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotPair[ slotPairId=" + slotPairId + " ]";
    }

}
