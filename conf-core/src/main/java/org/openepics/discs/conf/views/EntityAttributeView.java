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
public abstract class EntityAttributeView<E extends NamedEntity> {
    private final E parentEntity;
    final private EntityAttributeViewKind kind;
    private String usedBy;

//    private boolean isBuiltIn;
//    private Object entity;
  //  final private ReadOnlyDAO<? extends ConfigurationEntity> dao;
  //  private final String usedBy;

    /** Construct a new UI view object based on the database entity
     * @param entity the database entity
     * @param kind database entity kind {@link EntityAttributeViewKind}
     * @param parent the named entity parent for this attribute
     * @param dao the EJB to handle the entities associated with this view object
     */
/*    public EntityAttributeView(E entity, EntityAttributeViewKind kind, @Nullable NamedEntity parent,
            ReadOnlyDAO<? extends ConfigurationEntity> dao) {
        this(entity, kind, parent, dao, "");
    }*/

    /** Construct a new UI view object based on the database entity
     * @param entity the database entity
     * @param kind database entity kind {@link EntityAttributeViewKind}
     * @param parent the named entity parent for this attribute
     * @param dao the EJB to handle the entities associated with this view object
     * @param usedBy the name of the entity owning this attribute
     */
    protected EntityAttributeView(EntityAttributeViewKind kind, @Nullable E parent, String usedBy) {
        //Preconditions.checkNotNull(kind);
        this.kind = kind;
        this.parentEntity = parent;
        /*if (parent != null) {
            this.parentName = parent.getName();
            this.parentId = parent.getId();
        }*/
        this.usedBy = usedBy;
    }

    protected EntityAttributeView(EntityAttributeViewKind kind, @Nullable E parent) {
        this(kind, parent, null);
    }

    public EntityAttributeViewKind getKind() {
        return kind;
    }

    public abstract Object getEntity();

  /*  public boolean isBuiltIn() {
        return isBuiltIn;
    }*/

    public abstract String getId();
    public abstract String getName();
    public abstract String getValue();

    public String getParent() {
 //       return parentName;
        return parentEntity != null ? parentEntity.getName() : null;
    }

    public Long getParentId() {
//        return parentId;
        return parentEntity != null ? parentEntity.getId() : null;
    }

    /**
     * @return the parentEntity
     */
    public E getParentEntity() {
        return parentEntity;
    }

    /**
     * @return the usedBy
     */
    public String getUsedBy() {
        return usedBy;
    }

    public DataType getType() {
        return null;
    }

    public Unit getUnit() {
        return null;
    }

    public boolean getHasURL()
    {
        return false;
    }

    public boolean getHasFile() {
        return false;
    }
}
