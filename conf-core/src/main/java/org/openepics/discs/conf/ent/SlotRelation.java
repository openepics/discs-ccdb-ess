/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot_relation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotRelation.findAll", query = "SELECT s FROM SlotRelation s"),
    @NamedQuery(name = "SlotRelation.findBySlotRelationId", query = "SELECT s FROM SlotRelation s WHERE s.slotRelationId = :slotRelationId"),
    @NamedQuery(name = "SlotRelation.findByName", query = "SELECT s FROM SlotRelation s WHERE s.name = :name"),
    @NamedQuery(name = "SlotRelation.findByIname", query = "SELECT s FROM SlotRelation s WHERE s.iname = :iname"),
    @NamedQuery(name = "SlotRelation.findByDescription", query = "SELECT s FROM SlotRelation s WHERE s.description = :description"),
    @NamedQuery(name = "SlotRelation.findByModifiedAt", query = "SELECT s FROM SlotRelation s WHERE s.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "SlotRelation.findByModifiedBy", query = "SELECT s FROM SlotRelation s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "SlotRelation.findByVersion", query = "SELECT s FROM SlotRelation s WHERE s.version = :version")})
public class SlotRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "slot_relation_id")
    private Integer slotRelationId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "iname")
    private String iname;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slotRelation")
    private List<SlotPair> slotPairList;

    protected SlotRelation() {
    }

    public SlotRelation(String name, String iname, String modifiedBy) {
        this.name = name;
        this.iname = iname;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getSlotRelationId() {
        return slotRelationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIname() {
        return iname;
    }

    public void setIname(String iname) {
        this.iname = iname;
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

    @XmlTransient
    public List<SlotPair> getSlotPairList() {
        return slotPairList;
    }

    public void setSlotPairList(List<SlotPair> slotPairList) {
        this.slotPairList = slotPairList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (slotRelationId != null ? slotRelationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SlotRelation)) return false;

        SlotRelation other = (SlotRelation) object;
        if (this.slotRelationId == null && other.slotRelationId != null) return false;
        if (this.slotRelationId != null) return this.slotRelationId.equals(other.slotRelationId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotRelation[ slotRelationId=" + slotRelationId + " ]";
    }

}
