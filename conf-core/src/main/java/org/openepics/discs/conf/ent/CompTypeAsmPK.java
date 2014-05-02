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
import javax.validation.constraints.Size;

/**
 *
 * @author vuppala
 */
@Embeddable
public class CompTypeAsmPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "parent_type")
    private String parentType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "child_position")
    private String childPosition;

    public CompTypeAsmPK() {
    }

    public CompTypeAsmPK(String parentType, String childPosition) {
        this.parentType = parentType;
        this.childPosition = childPosition;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(String childPosition) {
        this.childPosition = childPosition;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (parentType != null ? parentType.hashCode() : 0);
        hash += (childPosition != null ? childPosition.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CompTypeAsmPK)) {
            return false;
        }
        CompTypeAsmPK other = (CompTypeAsmPK) object;
        if ((this.parentType == null && other.parentType != null) || (this.parentType != null && !this.parentType.equals(other.parentType))) {
            return false;
        }
        if ((this.childPosition == null && other.childPosition != null) || (this.childPosition != null && !this.childPosition.equals(other.childPosition))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.CompTypeAsmPK[ parentType=" + parentType + ", childPosition=" + childPosition + " ]";
    }
    
}
