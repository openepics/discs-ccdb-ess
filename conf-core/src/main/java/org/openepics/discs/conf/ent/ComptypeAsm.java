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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_asm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypeAsm.findAll", query = "SELECT c FROM ComptypeAsm c"),
    @NamedQuery(name = "ComptypeAsm.findByComptypeAsmId", query = "SELECT c FROM ComptypeAsm c WHERE c.comptypeAsmId = :comptypeAsmId"),
    @NamedQuery(name = "ComptypeAsm.findByChildPosition", query = "SELECT c FROM ComptypeAsm c WHERE c.childPosition = :childPosition"),
    @NamedQuery(name = "ComptypeAsm.findByDescription", query = "SELECT c FROM ComptypeAsm c WHERE c.description = :description"),
    @NamedQuery(name = "ComptypeAsm.findByModifiedAt", query = "SELECT c FROM ComptypeAsm c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "ComptypeAsm.findByModifiedBy", query = "SELECT c FROM ComptypeAsm c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComptypeAsm.findByVersion", query = "SELECT c FROM ComptypeAsm c WHERE c.version = :version")})
public class ComptypeAsm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "comptype_asm_id")
    private Integer comptypeAsmId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "child_position")
    private String childPosition;
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
    @Version
    private Long version;
    @JoinColumn(name = "child_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType childType;
    @JoinColumn(name = "parent_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType parentType;

    protected ComptypeAsm() {
    }

    public ComptypeAsm(String childPosition, String modifiedBy) {
        this.childPosition = childPosition;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getComptypeAsmId() {
        return comptypeAsmId;
    }

    public String getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(String childPosition) {
        this.childPosition = childPosition;
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

    protected Long getVersion() {
        return version;
    }

    protected void setVersion(Long version) {
        this.version = version;
    }

    public ComponentType getChildType() {
        return childType;
    }

    public void setChildType(ComponentType childType) {
        this.childType = childType;
    }

    public ComponentType getParentType() {
        return parentType;
    }

    public void setParentType(ComponentType parentType) {
        this.parentType = parentType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (comptypeAsmId != null ? comptypeAsmId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComptypeAsm)) return false;

        ComptypeAsm other = (ComptypeAsm) object;
        if (this.comptypeAsmId == null && other.comptypeAsmId != null) return false;
        if (this.comptypeAsmId != null) return this.comptypeAsmId.equals(other.comptypeAsmId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.ComptypeAsm[ comptypeAsmId=" + comptypeAsmId + " ]";
    }

}
