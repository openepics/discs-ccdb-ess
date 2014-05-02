/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "comp_type_asm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CompTypeAsm.findAll", query = "SELECT c FROM CompTypeAsm c"),
    @NamedQuery(name = "CompTypeAsm.findByParentType", query = "SELECT c FROM CompTypeAsm c WHERE c.compTypeAsmPK.parentType = :parentType"),
    @NamedQuery(name = "CompTypeAsm.findByChildPosition", query = "SELECT c FROM CompTypeAsm c WHERE c.compTypeAsmPK.childPosition = :childPosition"),
    @NamedQuery(name = "CompTypeAsm.findByDescription", query = "SELECT c FROM CompTypeAsm c WHERE c.description = :description"),
    @NamedQuery(name = "CompTypeAsm.findByModifiedAt", query = "SELECT c FROM CompTypeAsm c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "CompTypeAsm.findByModifiedBy", query = "SELECT c FROM CompTypeAsm c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "CompTypeAsm.findByVersion", query = "SELECT c FROM CompTypeAsm c WHERE c.version = :version")})
public class CompTypeAsm implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CompTypeAsmPK compTypeAsmPK;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "child_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType childType;
    @JoinColumn(name = "parent_type", referencedColumnName = "component_type_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComponentType componentType;

    public CompTypeAsm() {
    }

    public CompTypeAsm(CompTypeAsmPK compTypeAsmPK) {
        this.compTypeAsmPK = compTypeAsmPK;
    }

    public CompTypeAsm(CompTypeAsmPK compTypeAsmPK, Date modifiedAt, String modifiedBy, int version) {
        this.compTypeAsmPK = compTypeAsmPK;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public CompTypeAsm(String parentType, String childPosition) {
        this.compTypeAsmPK = new CompTypeAsmPK(parentType, childPosition);
    }

    public CompTypeAsmPK getCompTypeAsmPK() {
        return compTypeAsmPK;
    }

    public void setCompTypeAsmPK(CompTypeAsmPK compTypeAsmPK) {
        this.compTypeAsmPK = compTypeAsmPK;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ComponentType getChildType() {
        return childType;
    }

    public void setChildType(ComponentType childType) {
        this.childType = childType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (compTypeAsmPK != null ? compTypeAsmPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CompTypeAsm)) {
            return false;
        }
        CompTypeAsm other = (CompTypeAsm) object;
        if ((this.compTypeAsmPK == null && other.compTypeAsmPK != null) || (this.compTypeAsmPK != null && !this.compTypeAsmPK.equals(other.compTypeAsmPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.CompTypeAsm[ compTypeAsmPK=" + compTypeAsmPK + " ]";
    }
    
}
