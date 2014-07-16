package org.openepics.discs.conf.ent;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
public class Unit extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

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

    public Unit(String unitName, String quantity, String symbol, String baseUnitExpr, String description, String modifiedBy) {
        this.unitName = unitName;
        this.quantity = quantity;
        this.symbol = symbol;
        this.baseUnitExpr = baseUnitExpr;
        this.description = description;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
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
    public String toString() {
        return "Unit[ unitId=" + id + " ]";
    }

}
