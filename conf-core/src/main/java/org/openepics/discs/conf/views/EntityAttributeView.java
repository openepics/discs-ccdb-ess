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
package org.openepics.discs.conf.views;

import javax.annotation.Nullable;

import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.UnhandledCaseException;

import com.google.common.base.Preconditions;

/**
 * The UI view class. This is a helper class containing all the information that is used in the UI and is displayed
 * to the user. The objects of this class also contain a reference to the actual database entity the data is coming
 * from.
 *
 * @author Andraž Požar &lt;andraz.pozar@cosylab.com&gt;
 * @author Miha Vitorovič &lt;miha.vitorovic@cosylab.com&gt;
 *
 */
public class EntityAttributeView {
    private String id;
    private String name;
    @Nullable private DataType type;
    @Nullable private Unit unit;
    @Nullable private Value value;
    private EntityAttributeViewKind kind;
    private boolean hasFile;
    private boolean hasURL;
    private boolean isBuiltIn;
    private Object entity;

    /** Construct a new UI view object based on the database entity
     * @param entity the database entity
     * @param kind database entity kind {@link EntityAttributeViewKind}
     */
    public EntityAttributeView(Object entity, EntityAttributeViewKind kind) {
        Preconditions.checkNotNull(kind);
        this.entity = entity;
        setParameters();
        this.kind = kind;
    }

    /** Construct a new UI view object based on the database entity. The entity kind {@link EntityAttributeViewKind} is
     * determined automatically based on the type of the <code>entity</code>.
     * @param entity the database entity
     */
    public EntityAttributeView(Object entity) {
        this.entity = entity;
        setParameters();
    }

    private void setParameters() {
        if (entity instanceof BuiltInProperty) {
            setBuiltInProperty();
        } else  if (entity instanceof ComptypePropertyValue) {
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
        final ComptypePropertyValue comptypePropertyValue = (ComptypePropertyValue) entity;
        if (!comptypePropertyValue.isPropertyDefinition()) {
            kind = EntityAttributeViewKind.DEVICE_TYPE_PROPERTY;
        } else {
            if (comptypePropertyValue.isDefinitionTargetSlot()) {
                kind = EntityAttributeViewKind.INSTALL_SLOT_PROPERTY;
            } else if (comptypePropertyValue.isDefinitionTargetDevice()) {
                kind = EntityAttributeViewKind.DEVICE_PROPERTY;
            } else {
                kind = EntityAttributeViewKind.UNKNOWN_PROPERTY;
            }
        }
        id = comptypePropertyValue.getId().toString();
    }

    private void setPropValueParameters() {
        final PropertyValue propertyValue = (PropertyValue) entity;
        name = propertyValue.getProperty().getName();
        type = propertyValue.getProperty().getDataType();
        unit = propertyValue.getProperty().getUnit();
        value = propertyValue.getPropValue();
        kind =  EntityAttributeViewKind.PROPERTY;
        id = propertyValue.getId().toString();
    }

    private void setArtifactParameters() {
        final Artifact artifact = (Artifact) entity;
        name = artifact.getName();
        hasFile = artifact.isInternal();
        hasURL = !artifact.isInternal();
        value = hasURL ? new StrValue(artifact.getUri()) : new StrValue("Download attachment");
        kind =  EntityAttributeViewKind.ARTIFACT;
        id = artifact.getId().toString();
    }

    private void setTagParameters() {
        name = ((Tag) entity).getName();
        kind =  EntityAttributeViewKind.TAG;
        value = null;
        id = "TAG_" + name;
    }

    private void setBuiltInProperty() {
        final BuiltInProperty builtInProperty = (BuiltInProperty) entity;
        name = builtInProperty.getName().toString();
        value = builtInProperty.getValue();
        type = builtInProperty.getDataType();
        kind = EntityAttributeViewKind.BUILT_IN_PROPERTY;
        isBuiltIn = true;
        id = "BIP_" + name;
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

    /** @return A String representation of the associated entity */
    public String getValue() {
        return Conversion.valueToString(value);
    }

    public EntityAttributeViewKind getKind() {
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

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public String getId() {
        return id;
    }
}
