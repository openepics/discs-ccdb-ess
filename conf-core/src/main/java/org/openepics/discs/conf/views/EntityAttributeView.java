/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.views;

import javax.annotation.Nullable;

import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.PropertyValue;
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
    private boolean hasFile;
    private boolean hasURL;
    private Object entity;

    public EntityAttributeView(Object entity) {
        this.entity = entity;
        setParameters();
    }

    private void setParameters() {
        if (entity instanceof ComptypePropertyValue) {
            setComponentTypeParameters();
        } else if (entity instanceof PropertyValue) {
            setPropValueParameters();
        } else if (entity instanceof Artifact) {
            setArtifactParameters();
        } else if (entity instanceof Tag) {
            setTagParameters();
        } else {
            throw new UnhandledCaseException();
        }
    }

    private void setComponentTypeParameters() {
        setPropValueParameters();
        if (value == null) {
            kind = "Definition";
        } else {
            kind = "Type property";
        }
    }

    private void setPropValueParameters() {
        final PropertyValue devicePropertyValue = (PropertyValue) entity;
        name = devicePropertyValue.getProperty().getName();
        type = devicePropertyValue.getProperty().getDataType();
        unit = devicePropertyValue.getProperty().getUnit();
        value = devicePropertyValue.getPropValue();
        kind = "Property";
    }

    private void setArtifactParameters() {
        final Artifact artifact = (Artifact) entity;
        name = artifact.getName();
        hasFile = artifact.isInternal();
        hasURL = !artifact.isInternal();
        value = artifact.getUri();
        kind = "Artifact";
    }

    private void setTagParameters() {
        name = ((Tag) entity).getName();
        kind = "Tag";
        value = "-";
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getValue() {
        return value;
    }

    public String getKind() {
        return kind;
    }

    public Object getEntity() {
        return entity;
    }

    public boolean getHasFile() {
        return hasFile;
    }

    public boolean getHasURL() {
        return hasURL;
    }
}
