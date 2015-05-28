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
package org.openepics.discs.conf.dl.common;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.EntityWithProperties;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Preconditions;

/**
 * Abstract data loader for entities that have properties.
 *
 * @param <S> The abstract data loader is used for all possible {@link PropertyValue} implementations.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
public abstract class AbstractEntityWithPropertiesDataLoader<S extends PropertyValue> extends AbstractDataLoader
                                                                                            implements DataLoader {
    @Inject protected PropertyEJB propertyEJB;

    /**
     * Used by sub-classes to update properties
     *
     * @param entity the database entity for which to update or insert the property.
     * @param propertyName the name of the property which value to update or add.
     * @param propertyValue the value of the property with which to update or add. The property value can be set to
     * <code>null</code>.
     * @return {@link ErrorMessage#ENTITY_NOT_FOUND} if there is no property of this name in the database,
     * {@link ErrorMessage#PROPERTY_NOT_FOUND} if the entity does not have such property value,
     * <code>null</code> if the property value vase successfully updated.
     */
    protected ErrorMessage addOrUpdateProperty(EntityWithProperties entity, String propertyName,
                                                                @Nullable String propertyValue) {
        Preconditions.checkNotNull(propertyName);
        final List<PropertyValue> propertyList = entity.getEntityPropertyList();

        final @Nullable Property property = propertyEJB.findByName(propertyName);
        if (property == null) {
            return ErrorMessage.ENTITY_NOT_FOUND;
        }

        PropertyValue entityPropertyToUpdate = null;
        for (PropertyValue value : propertyList) {
            if (value.getProperty().equals(property)) {
                entityPropertyToUpdate = value;
                break;
            }
        }

        if (entityPropertyToUpdate != null) {
            entityPropertyToUpdate.setPropValue(Conversion.stringToValue(propertyValue, property.getDataType()));
            getDAO().saveChild(entityPropertyToUpdate);
        } else {
            return ErrorMessage.PROPERTY_NOT_FOUND;
        }
        return null;
    }

    /**
     * To be implemented by sub-classes. Returns a DAO EJB for accessing properties
     * @param <T> configuration entity class
     * @return the DAO EJB
     */
    protected abstract <T> DAO<T> getDAO();
}
