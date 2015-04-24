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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Assembly information for {@link ComponentType}s
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_asm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypeAsm.findAll", query = "SELECT c FROM ComptypeAsm c"),
    @NamedQuery(name = "ComptypeAsm.findByComptypeAsmId", query = "SELECT c FROM ComptypeAsm c WHERE c.id = :id"),
    @NamedQuery(name = "ComptypeAsm.findByModifiedBy", query = "SELECT c FROM ComptypeAsm c "
            + "WHERE c.modifiedBy = :modifiedBy")
})
public class ComptypeAsm extends ConfigurationEntity {
    private static final long serialVersionUID = 473793777108391666L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "child_position")
    private String childPosition;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "child_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType childType;

    @JoinColumn(name = "parent_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType parentType;

    protected ComptypeAsm() {
    }

    /**
     * A new device type assembly
     *
     * @param childPosition the child position
     */
    public ComptypeAsm(String childPosition) {
        this.childPosition = childPosition;
    }

    public String getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(String childPosition) {
        this.childPosition = childPosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComponentType getChildType() {
        return childType;
    }

    public void setChildType(ComponentType childType) {
        this.childType = childType;
    }

    public ComponentType getParentType() {
        return parentType;
    }

    public void setParentType(ComponentType parentType) {
        this.parentType = parentType;
    }

    @Override
    public String toString() {
        return "ComptypeAsm[ comptypeAsmId=" + id + " ]";
    }

}
