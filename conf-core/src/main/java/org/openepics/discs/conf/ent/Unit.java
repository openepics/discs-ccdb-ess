package org.openepics.discs.conf.ent;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "unit", indexes = { @Index(columnList = "unit_name") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Unit.findAll", query = "SELECT u FROM Unit u"),
    @NamedQuery(name = "Unit.findByName", query = "SELECT u FROM Unit u WHERE u.name = :name"),
    @NamedQuery(name = "Unit.findByQuantity", query = "SELECT u FROM Unit u WHERE u.quantity = :quantity"),
    @NamedQuery(name = "Unit.findBySymbol", query = "SELECT u FROM Unit u WHERE u.symbol = :symbol"),
    @NamedQuery(name = "Unit.findByModifiedBy", query = "SELECT u FROM Unit u WHERE u.modifiedBy = :modifiedBy")
})
public class Unit extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "unit_name", unique=true)
    private String name;

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

    @OneToMany(mappedBy = "unit")
    private List<PropertyValue> propertyValuesList;

    protected Unit() {
    }

    public Unit(String unitName, String quantity, String symbol, String description) {
        this.name = unitName;
        this.quantity = quantity;
        this.symbol = symbol;
        this.description = description;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    @XmlTransient
    @JsonIgnore
    public List<PropertyValue> getPropertyValuesList() {
        return propertyValuesList;
    }

    @Override
    public String toString() {
        return "Unit[ unitId=" + id + " ]";
    }
}
