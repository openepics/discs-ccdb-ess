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
package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Data Type used in various {@link Property} entities
 *
 * @author vuppala
 */
@Entity
@Table(name = "data_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataType.findAll", query = "SELECT d FROM DataType d"),
    @NamedQuery(name = "DataType.findByName", query = "SELECT d FROM DataType d WHERE d.name = :name"),
    @NamedQuery(name = "DataType.findByDataTypeId", query = "SELECT d FROM DataType d WHERE d.id = :id"),
    @NamedQuery(name = "DataType.findByModifiedBy", query = "SELECT d FROM DataType d WHERE d.modifiedBy = :modifiedBy")
})
public class DataType extends ConfigurationEntity {
    private static final long serialVersionUID = 8190792924852505638L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name", unique = true)
    private String name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "scalar")
    private boolean scalar;

    @Column(name = "definition", columnDefinition="TEXT")
    private String definition;

    protected DataType() {
    }

    /**
     * Constructs a new data type.
     *
     * @param name a unique name of the data type
     * @param description user defined description
     * @param scalar <code>true</code> if the values of this type are scalars, <code>false</code> otherwise
     * @param definition the data type definition. Used only for user defined enumerations so far.
     */
    public DataType(String name, String description, boolean scalar, String definition) {
        this.name = name;
        this.description = description;
        this.scalar = scalar;
        this.definition = definition;
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

    public boolean isScalar() {
        return scalar;
    }
    public void setScalar(boolean scalar) {
        this.scalar = scalar;
    }

    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "DataType[ dataTypeId=" + id + " ]";
    }
}
