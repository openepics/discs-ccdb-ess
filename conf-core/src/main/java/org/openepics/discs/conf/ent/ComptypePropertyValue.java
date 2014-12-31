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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {@link PropertyValue} attached to a {@link ComponentType} entity
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_property_value", indexes = { @Index(columnList = "component_type, prop_value") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypePropertyValue.findAll", query = "SELECT c FROM ComptypePropertyValue c"),
    @NamedQuery(name = "ComptypePropertyValue.findPropertyDefs", query = "SELECT c FROM ComptypePropertyValue c "
            + "WHERE c.componentType = :componentType AND c.isPropertyDefinition = TRUE"),
    @NamedQuery(name = "ComptypePropertyValue.findByInRepository", query = "SELECT c FROM ComptypePropertyValue c "
            + "WHERE c.inRepository = :inRepository"),
    @NamedQuery(name = "ComptypePropertyValue.findByModifiedBy", query = "SELECT c FROM ComptypePropertyValue c "
            + "WHERE c.modifiedBy = :modifiedBy")
})
public class ComptypePropertyValue extends PropertyValue {
    private static final long serialVersionUID = -5402331155307049268L;

    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_property_definition")
    private boolean isPropertyDefinition;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_def_target_slot")
    private boolean isDefinitionTargetSlot;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_def_target_device")
    private boolean isDefinitionTargetDevice;

    public ComptypePropertyValue() { }

    /**
     * Constructs a new property value
     *
     * @param inRepository <code>false</code>
     */
    public ComptypePropertyValue(boolean inRepository) {
        super(inRepository);
    }

    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public boolean isPropertyDefinition() {
        return isPropertyDefinition;
    }

    public void setPropertyDefinition(boolean isPropertyDefinition) {
        this.isPropertyDefinition = isPropertyDefinition;
    }

    public boolean isDefinitionTargetSlot() {
        return isDefinitionTargetSlot;
    }

    public void setDefinitionTargetSlot(boolean isDefinitionTargetSlot) {
        this.isDefinitionTargetSlot = isDefinitionTargetSlot;
    }

    public boolean isDefinitionTargetDevice() {
        return isDefinitionTargetDevice;
    }

    public void setDefinitionTargetDevice(boolean isDefinitionTargetDevice) {
        this.isDefinitionTargetDevice = isDefinitionTargetDevice;
    }

    @Override
    public String toString() {
        return "ComptypeProperty[ ctypePropId=" + id + " ]";
    }
}
