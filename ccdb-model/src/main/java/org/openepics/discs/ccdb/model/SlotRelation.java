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
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot_relation", indexes = { @Index(columnList = "name") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotRelation.findAll", query = "SELECT s FROM SlotRelation s"),
    @NamedQuery(name = "SlotRelation.findBySlotRelationId", query = "SELECT s FROM SlotRelation s WHERE s.id = :id"),
    @NamedQuery(name = "SlotRelation.findByName", query = "SELECT s FROM SlotRelation s WHERE s.name = :name")
})
public class SlotRelation extends ConfigurationEntity {
    private static final long serialVersionUID = 2591452847410218530L;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private SlotRelationName name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "iname")
    private String iname;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    protected SlotRelation() {
    }

    /** Constructs a new slot relation. There are only 3 possible relationship types.
     * @see SlotRelationName
     * @param name a {@link SlotRelationName}
     */
    public SlotRelation(SlotRelationName name) {
        setName(name);
    }

    public SlotRelationName getName() {
        return name;
    }

    /**
     * @return a String representation of the relationship:
     * <ul>
     * <li>CONTAINS</li>
     * <li>POWERS</li>
     * <li>CONTROLS</li>
     * </ul>
     */
    public String getNameAsString() {
        return name.toString();
    }

    /**
     * The method also sets the string for "reverse relationship" at the same time.
     *
     * @param name the relationship to set
     */
    public void setName(SlotRelationName name) {
        this.name = name;
        iname = name.inverseName();
    }

    public String getIname() {
        return name.inverseName();
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SlotRelation[ slotRelationId=" + id + " ]";
    }
}
