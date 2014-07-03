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
@Table(name = "slot_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotProperty.findAll", query = "SELECT s FROM SlotProperty s"),
    @NamedQuery(name = "SlotProperty.findBySlotPropId", query = "SELECT s FROM SlotProperty s WHERE s.slotPropId = :slotPropId"),
    @NamedQuery(name = "SlotProperty.findByInRepository", query = "SELECT s FROM SlotProperty s WHERE s.inRepository = :inRepository"),
    @NamedQuery(name = "SlotProperty.findByModifiedAt", query = "SELECT s FROM SlotProperty s WHERE s.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "SlotProperty.findByModifiedBy", query = "SELECT s FROM SlotProperty s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "SlotProperty.findByVersion", query = "SELECT s FROM SlotProperty s WHERE s.version = :version")})
public class SlotProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "slot_prop_id")
    private Integer slotPropId;
    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;
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
    @Version
    private Long version;
    @JoinColumn(name = "unit", referencedColumnName = "unit_id")
    @ManyToOne
    private Unit unit;
    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;
    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;

    protected SlotProperty() {
    }

    public SlotProperty(boolean inRepository, String modifiedBy) {
        this.inRepository = inRepository;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getSlotPropId() {
        return slotPropId;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public boolean getInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (slotPropId != null ? slotPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlotProperty)) {
            return false;
        }
        SlotProperty other = (SlotProperty) object;
        if ((this.slotPropId == null && other.slotPropId != null) || (this.slotPropId != null && !this.slotPropId.equals(other.slotPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotProperty[ slotPropId=" + slotPropId + " ]";
    }

}
