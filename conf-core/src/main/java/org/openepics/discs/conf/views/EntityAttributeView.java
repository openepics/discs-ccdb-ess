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

import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.NamedEntity;
import org.openepics.discs.conf.ent.Unit;

/**
 * The UI view class. This is a helper class containing all the information that is used in the UI and is displayed
 * to the user. The objects of this class also contain a reference to the actual database entity the data is coming
 * from.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public abstract class EntityAttributeView<E extends ConfigurationEntity & NamedEntity> {
    private final E parentEntity;
    private final String usedBy;
    private EntityAttributeViewKind kind;

    /** Construct a new UI view object based on the database entity
     * @param viewParent the named entity view parent for this attribute
     * @param usedBy the name of the entity owning this attribute
     */
    protected EntityAttributeView(@Nullable E viewParent, String usedBy) {
        this.parentEntity = viewParent;
        this.kind = EntityAttributeViewKind.UNKNOWN_PROPERTY;
        this.usedBy = usedBy;
    }

    protected EntityAttributeView(@Nullable E parent) {
        this(parent, null);
    }

    public EntityAttributeViewKind getKind() {
        return kind;
    }
    protected void setKind(EntityAttributeViewKind kind) {
        this.kind = kind;
    }

    public abstract Object getEntity();

    public abstract String getId();
    public abstract String getName();
    public abstract String getValue();

    public String getParent() {
        return parentEntity != null ? parentEntity.getName() : null;
    }

    public Long getParentId() {
        return parentEntity != null ? parentEntity.getId() : null;
    }

    /** @return the parentEntity */
    public E getParentEntity() {
        return parentEntity;
    }

    /** @return the usedBy */
    public String getUsedBy() {
        return usedBy;
    }

    /** @return the {@link DataType} */
    public DataType getType() {
        return null;
    }

    /** @return the {@link Unit} */
    public Unit getUnit() {
        return null;
    }

    /** @return <code>true</code> if attribute has an URL (is an artifact), <code>false</code> otherwise */
    public boolean getHasURL() {
        return false;
    }

    /** @return <code>true</code> if attribute has a contains a file (is an artifact), <code>false</code> otherwise */
    public boolean getHasFile() {
        return false;
    }
}
