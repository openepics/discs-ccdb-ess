/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui.common;

import javax.annotation.Nullable;

import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.util.UnhandledCaseException;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class EntityAttributeView {
    private String name;
    private @Nullable DataType type;
    private @Nullable Unit unit;
    private @Nullable String value;
    private @Nullable String kind;
    private Object entity;
    private EntityType entityType;

    public EntityAttributeView(Object entity, EntityType entityType) {
        this.entity = entity;
        this.entityType = entityType;
        setParameters();
    }

    private void setParameters() {
        if (entityType == EntityType.COMPONENT_TYPE) {
            setComponentTypeParameters();
        } else if (entityType == EntityType.DEVICE) {
            setDeviceParameters();
        } else if (entityType == EntityType.SLOT) {
            setSlotParameters();
        } else {
            throw new UnhandledCaseException();
        }
    }

    private void setComponentTypeParameters() {
        if (entity instanceof ComptypePropertyValue) {
            final ComptypePropertyValue compTypePropertyValue = (ComptypePropertyValue) entity;
            name = compTypePropertyValue.getProperty().getName();
            type = compTypePropertyValue.getProperty().getDataType();
            unit = compTypePropertyValue.getProperty().getUnit();
            value = compTypePropertyValue.getPropValue();
            if (value == null || value.length() == 0) {
                kind = "Definition";
            } else {
                kind = "Type property";
            }
        } else if (entity instanceof ComptypeArtifact) {
            final ComptypeArtifact compTypeArtifact = (ComptypeArtifact) entity;
            name = compTypeArtifact.getName();
        } else if (entity instanceof Tag) {
            setTagParameters();
        } else {
            throw new UnhandledCaseException();
        }
    }

    private void setDeviceParameters() {
        if (entity instanceof DevicePropertyValue) {
            final DevicePropertyValue devicePropertyValue = (DevicePropertyValue) entity;
            name = devicePropertyValue.getProperty().getName();
            type = devicePropertyValue.getProperty().getDataType();
            unit = devicePropertyValue.getProperty().getUnit();
            value = devicePropertyValue.getPropValue();
        } else if (entity instanceof DeviceArtifact) {
            final DeviceArtifact deviceArtifact = (DeviceArtifact) entity;
            name = deviceArtifact.getName();
        } else if (entity instanceof Tag) {
            setTagParameters();
        } else {
            throw new UnhandledCaseException();
        }
    }

    private void setSlotParameters() {
        if (entity instanceof SlotPropertyValue) {
            final SlotPropertyValue slotPropertyValue = (SlotPropertyValue) entity;
            name = slotPropertyValue.getProperty().getName();
            type = slotPropertyValue.getProperty().getDataType();
            unit = slotPropertyValue.getProperty().getUnit();
            value = slotPropertyValue.getPropValue();
        } else if (entity instanceof DeviceArtifact) {
            final SlotArtifact slotArtifact = (SlotArtifact) entity;
            name = slotArtifact.getName();
        } else if (entity instanceof Tag) {
            setTagParameters();
        } else {
            throw new UnhandledCaseException();
        }
    }

    private void setTagParameters() { name = ((Tag) entity).getName(); }

    public String getName() { return name; }
    public DataType getType() { return type; }
    public Unit getUnit() { return unit; }
    public String getValue() { return value; }
    public String getKind() { return kind; }
    public Object getObject() {return entity; }
}
