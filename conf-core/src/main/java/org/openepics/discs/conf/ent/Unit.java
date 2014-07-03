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
@Table(name = "unit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Unit.findAll", query = "SELECT u FROM Unit u"),
    @NamedQuery(name = "Unit.findByUnitName", query = "SELECT u FROM Unit u WHERE u.unitName = :unitName"),
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_id")
    private Integer unitId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "unit_name", unique=true)
    private String unitName;
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
    @Version
    private Long version;
    @OneToMany(mappedBy = "unit")
    private List<ComptypeProperty> comptypePropertyList;
    @OneToMany(mappedBy = "unit")
    private List<DeviceProperty> devicePropertyList;
    @OneToMany(mappedBy = "unit")
    private List<Property> propertyList;
    @OneToMany(mappedBy = "unit")
    private List<SlotProperty> slotPropertyList;

    protected Unit() {
    }

    public Unit(String unitName, String quantity, String symbol, String baseUnitExpr, String description, Date modifiedAt, String modifiedBy) {
        this.unitName = unitName;
        this.quantity = quantity;
        this.symbol = symbol;
        this.baseUnitExpr = baseUnitExpr;
        this.description = description;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    protected long getVersion() {
        return version;
    }

    protected void setVersion(long version) {
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
        hash += (unitName != null ? unitName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Unit)) {
            return false;
        }
        Unit other = (Unit) object;
        if ((this.unitName == null && other.unitName != null) || (this.unitName != null && !this.unitName.equals(other.unitName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Unit[ unitId=" + unitName + " ]";
    }

}
