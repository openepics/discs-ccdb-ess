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
import javax.persistence.Lob;
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
@Table(name = "slot_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotProperty.findAll", query = "SELECT s FROM SlotProperty s"),
    @NamedQuery(name = "SlotProperty.findBySlot", query = "SELECT s FROM SlotProperty s WHERE s.slotPropertyPK.slot = :slot"),
    @NamedQuery(name = "SlotProperty.findByProperty", query = "SELECT s FROM SlotProperty s WHERE s.slotPropertyPK.property = :property"),
    @NamedQuery(name = "SlotProperty.findByModifiedAt", query = "SELECT s FROM SlotProperty s WHERE s.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "SlotProperty.findByModifiedBy", query = "SELECT s FROM SlotProperty s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "SlotProperty.findByVersion", query = "SELECT s FROM SlotProperty s WHERE s.version = :version")})
public class SlotProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SlotPropertyPK slotPropertyPK;
    @Lob
    @Size(max = 65535)
    @Column(name = "value")
    private String value;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.DATE)
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
    @JoinColumn(name = "property", referencedColumnName = "property_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Property property1;
    @JoinColumn(name = "slot", referencedColumnName = "slot_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Slot slot1;

    public SlotProperty() {
    }

    public SlotProperty(SlotPropertyPK slotPropertyPK) {
        this.slotPropertyPK = slotPropertyPK;
    }

    public SlotProperty(SlotPropertyPK slotPropertyPK, Date modifiedAt, String modifiedBy, int version) {
        this.slotPropertyPK = slotPropertyPK;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public SlotProperty(int slot, String property) {
        this.slotPropertyPK = new SlotPropertyPK(slot, property);
    }

    public SlotPropertyPK getSlotPropertyPK() {
        return slotPropertyPK;
    }

    public void setSlotPropertyPK(SlotPropertyPK slotPropertyPK) {
        this.slotPropertyPK = slotPropertyPK;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Property getProperty1() {
        return property1;
    }

    public void setProperty1(Property property1) {
        this.property1 = property1;
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
        hash += (slotPropertyPK != null ? slotPropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlotProperty)) {
            return false;
        }
        SlotProperty other = (SlotProperty) object;
        if ((this.slotPropertyPK == null && other.slotPropertyPK != null) || (this.slotPropertyPK != null && !this.slotPropertyPK.equals(other.slotPropertyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotProperty[ slotPropertyPK=" + slotPropertyPK + " ]";
    }
    
}
