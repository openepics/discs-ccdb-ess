/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.ccdb.model;

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
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Entity
@Table(name = "property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Property.findAll", query = "SELECT p FROM Property p"),
    @NamedQuery(name = "Property.findAllOrderedByName", query = "SELECT p FROM Property p ORDER BY p.name"),
    @NamedQuery(name = "Property.findByPropertyId", query = "SELECT p FROM Property p WHERE p.id = :id"),
    @NamedQuery(name = "Property.findByName", query = "SELECT p FROM Property p WHERE p.name = :name"),
    @NamedQuery(name = "Property.findByUnit", query = "SELECT p from Property p WHERE p.unit = :unit"),
    @NamedQuery(name = "Property.findByDataType", query = "SELECT p FROM Property p WHERE p.dataType = :dataType"),
    @NamedQuery(name = "Property.findByModifiedBy", query = "SELECT p FROM Property p "
            + "WHERE p.modifiedBy = :modifiedBy")
})
public class Property extends ConfigurationEntity implements NamedEntity {
    private static final long serialVersionUID = 7015867086270956395L;

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

    @JoinColumn(name = "data_type")
    @ManyToOne(optional = false)
    private DataType dataType;

    @JoinColumn(name = "unit")
    @ManyToOne
    private Unit unit;

    @Basic(optional = false)
    @Column(name = "value_unique")
    @Enumerated(EnumType.STRING)
    private PropertyValueUniqueness valueUniqueness = PropertyValueUniqueness.NONE;

    public Property() {
    }

    /** Constructs a property with a name and description.
     * @param name The name of the property
     * @param description User specified description
     */
    public Property(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /** Copy constructor
     * @param prop the {@link Property} to copy
     */
    public Property(Property prop) {
        this.name = prop.name;
        this.description = prop.description;
        this.dataType = prop.dataType;
        this.unit = prop.unit;
        this.valueUniqueness = prop.valueUniqueness;
    }

    /**
     * @return The name of the property
     */
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return User specified description
     */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The data type required for the property value
     */
    public DataType getDataType() {
        return dataType;
    }
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return The physics unit (optional) for the property value
     */
    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /** @return the type of the property value uniqueness as defined in {@link PropertyValueUniqueness} enumeration */
    public PropertyValueUniqueness getValueUniqueness() {
        return valueUniqueness;
    }
    public void setValueUniqueness(PropertyValueUniqueness valueUniqueness) {
        this.valueUniqueness = valueUniqueness;
    }

    @Override
    public String toString() {
        return "Property[ propertyId=" + id + " ]";
    }
}
