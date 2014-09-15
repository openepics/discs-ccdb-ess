package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Properties are given to Devices, Device Types and INstallation Slots
 *
 * @author vuppala
 */
@Entity
@Table(name = "property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Property.findAll", query = "SELECT p FROM Property p"),
    @NamedQuery(name = "Property.findByPropertyId", query = "SELECT p FROM Property p WHERE p.id = :id"),
    @NamedQuery(name = "Property.findByName", query = "SELECT p FROM Property p WHERE p.name = :name"),
    @NamedQuery(name = "Property.findByAssociation", query = "SELECT p FROM Property p "
            + "WHERE p.association = :association"),
    @NamedQuery(name = "Property.findByModifiedBy", query = "SELECT p FROM Property p "
            + "WHERE p.modifiedBy = :modifiedBy")
})
public class Property extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name", unique = true)
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "association", length = 12)
    private PropertyAssociation association;

    @JoinColumn(name = "data_type")
    @ManyToOne(optional = false)
    private DataType dataType;

    @JoinColumn(name = "unit")
    @ManyToOne
    private Unit unit;

    protected Property() {
    }

    public Property(String name, String description, PropertyAssociation association) {
        this.name = name;
        this.description = description;
        this.association = association;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public PropertyAssociation getAssociation() {
        return association;
    }
    public void setAssociation(PropertyAssociation association) {
        this.association = association;
    }

    public DataType getDataType() {
        return dataType;
    }
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Property[ propertyId=" + id + " ]";
    }
}
