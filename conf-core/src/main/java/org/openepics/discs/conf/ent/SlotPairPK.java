/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author vuppala
 */
@Embeddable
public class SlotPairPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "slot_relation")
    private int slotRelation;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_slot")
    private int parentSlot;
    @Basic(optional = false)
    @NotNull
    @Column(name = "child_slot")
    private int childSlot;

    public SlotPairPK() {
    }

    public SlotPairPK(int slotRelation, int parentSlot, int childSlot) {
        this.slotRelation = slotRelation;
        this.parentSlot = parentSlot;
        this.childSlot = childSlot;
    }

    public int getSlotRelation() {
        return slotRelation;
    }

    public void setSlotRelation(int slotRelation) {
        this.slotRelation = slotRelation;
    }

    public int getParentSlot() {
        return parentSlot;
    }

    public void setParentSlot(int parentSlot) {
        this.parentSlot = parentSlot;
    }

    public int getChildSlot() {
        return childSlot;
    }

    public void setChildSlot(int childSlot) {
        this.childSlot = childSlot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) slotRelation;
        hash += (int) parentSlot;
        hash += (int) childSlot;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlotPairPK)) {
            return false;
        }
        SlotPairPK other = (SlotPairPK) object;
        if (this.slotRelation != other.slotRelation) {
            return false;
        }
        if (this.parentSlot != other.parentSlot) {
            return false;
        }
        if (this.childSlot != other.childSlot) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotPairPK[ slotRelation=" + slotRelation + ", parentSlot=" + parentSlot + ", childSlot=" + childSlot + " ]";
    }
    
}
