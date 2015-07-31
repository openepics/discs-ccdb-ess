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

import org.openepics.discs.conf.ejb.ReadOnlyDAO;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.NamedEntity;
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
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class EntityAttributeView {
    private String id;
    private String parentName;
    private Long parentId;
    private String name;
    @Nullable private DataType type;
    @Nullable private Unit unit;
    @Nullable private Value value;
    final private EntityAttributeViewKind kind;
    private boolean hasFile;
    private boolean hasURL;
    private boolean isBuiltIn;
    private Object entity;
    final private ReadOnlyDAO<? extends ConfigurationEntity> dao;

    /** Construct a new UI view object based on the database entity
     * @param entity the database entity
     * @param kind database entity kind {@link EntityAttributeViewKind}
     * @param parent the named entity parent for this attribute
     */
    public EntityAttributeView(Object entity, EntityAttributeViewKind kind, @Nullable NamedEntity parent,
            ReadOnlyDAO<? extends ConfigurationEntity> dao) {
        Preconditions.checkNotNull(kind);
        this.entity = entity;
        this.kind = kind;
        setParameters();
        if (parent != null) {
            this.parentName = parent.getName();
            this.parentId = parent.getId();
        }
        this.dao = dao;
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
        final ComptypePropertyValue comptypePropertyValue = (ComptypePropertyValue) entity;
        id = comptypePropertyValue.getId().toString();
    }

    private void setPropValueParameters() {
        final PropertyValue propertyValue = (PropertyValue) entity;
        name = propertyValue.getProperty().getName();
        type = propertyValue.getProperty().getDataType();
        unit = propertyValue.getProperty().getUnit();
        value = propertyValue.getPropValue();
        id = propertyValue.getId().toString();
    }

    private void setArtifactParameters() {
        final Artifact artifact = (Artifact) entity;
        name = artifact.getName();
        hasFile = artifact.isInternal();
        hasURL = !artifact.isInternal();
        value = hasURL ? new StrValue(artifact.getUri()) : new StrValue("Download attachment");
        id = artifact.getId().toString();
    }

    private void setTagParameters() {
        name = ((Tag) entity).getName();
        value = null;
        id = "TAG_" + name;
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

    public String getParent() {
        return parentName;
    }

    public Long getParentId() {
        return parentId;
    }

    public ConfigurationEntity getParentEntity() {
        return (dao != null) && (parentId != null) ? dao.findById(parentId) : null;
    }
}
