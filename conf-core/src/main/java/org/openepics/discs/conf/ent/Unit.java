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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "unit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Unit.findAll", query = "SELECT u FROM Unit u"),
    @NamedQuery(name = "Unit.findByUnitId", query = "SELECT u FROM Unit u WHERE u.unitId = :unitId"),
    @NamedQuery(name = "Unit.findByQuantity", query = "SELECT u FROM Unit u WHERE u.quantity = :quantity"),
    @NamedQuery(name = "Unit.findBySymbol", query = "SELECT u FROM Unit u WHERE u.symbol = :symbol"),
    @NamedQuery(name = "Unit.findByDescription", query = "SELECT u FROM Unit u WHERE u.description = :description"),
    @NamedQuery(name = "Unit.findByBaseUnitExpr", query = "SELECT u FROM Unit u WHERE u.baseUnitExpr = :baseUnitExpr"),
    @NamedQuery(name = "Unit.findByModifiedAt", query = "SELECT u FROM Unit u WHERE u.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "Unit.findByModifiedBy", query = "SELECT u FROM Unit u WHERE u.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "Unit.findByVersion", query = "SELECT u FROM Unit u WHERE u.version = :version")})
public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "unit_id")
    private String unitId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "quantity")
    private String quantity;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "symbol")
    private String symbol;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Column(name = "base_unit_expr")
    private String baseUnitExpr;
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
    @OneToMany(mappedBy = "unit")
    private List<ComptypeProperty> comptypePropertyList;
    @OneToMany(mappedBy = "unit")
    private List<DeviceProperty> devicePropertyList;
    @OneToMany(mappedBy = "unit")
    private List<Property> propertyList;
    @OneToMany(mappedBy = "unit")
    private List<SlotProperty> slotPropertyList;

    public Unit() {
    }

    public Unit(String unitId) {
        this.unitId = unitId;
    }

    public Unit(String unitId, String quantity, String symbol, String baseUnitExpr, String description, Date modifiedAt, String modifiedBy, int version) {
        this.unitId = unitId;
        this.quantity = quantity;
        this.symbol = symbol;
        this.baseUnitExpr = baseUnitExpr;
        this.description = description;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUnitExpr() {
        return baseUnitExpr;
    }

    public void setBaseUnitExpr(String baseUnitExpr) {
        this.baseUnitExpr = baseUnitExpr;
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

    @XmlTransient
    public List<ComptypeProperty> getComptypePropertyList() {
        return comptypePropertyList;
    }

    public void setComptypePropertyList(List<ComptypeProperty> comptypePropertyList) {
        this.comptypePropertyList = comptypePropertyList;
    }

    @XmlTransient
    public List<DeviceProperty> getDevicePropertyList() {
        return devicePropertyList;
    }

    public void setDevicePropertyList(List<DeviceProperty> devicePropertyList) {
        this.devicePropertyList = devicePropertyList;
    }

    @XmlTransient
    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    @XmlTransient
    public List<SlotProperty> getSlotPropertyList() {
        return slotPropertyList;
    }

    public void setSlotPropertyList(List<SlotProperty> slotPropertyList) {
        this.slotPropertyList = slotPropertyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitId != null ? unitId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Unit)) {
            return false;
        }
        Unit other = (Unit) object;
        if ((this.unitId == null && other.unitId != null) || (this.unitId != null && !this.unitId.equals(other.unitId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Unit[ unitId=" + unitId + " ]";
    }
    
}
