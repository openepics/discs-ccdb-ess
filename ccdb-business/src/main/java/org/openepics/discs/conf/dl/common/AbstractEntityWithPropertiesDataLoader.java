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
import org.openepics.discs.conf.ent.EntityWithId;
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
public abstract class AbstractEntityWithPropertiesDataLoader<S extends PropertyValue> extends AbstractDataLoader {
    @Inject protected PropertyEJB propertyEJB;

    /**
     * Used by sub-classes to update properties.
     * Updates the {@link DataLoaderResult} in case of {@link ErrorMessage#ENTITY_NOT_FOUND} error
     * (if there is no property of this name in the database), or {@link ErrorMessage#PROPERTY_NOT_FOUND} error
     * (if the entity does not have such property value)
     *
     * @param entity the database entity for which to update or insert the property.
     * @param propertyName the name of the property which value to update or add.
     * @param propertyValue the value of the property with which to update or add. The property value can be set to
     * <code>null</code>.
     * @param propNameHeader the name of the column producing the error. This is added to the error message.
     */
    protected void addOrUpdateProperty(EntityWithProperties entity, String propertyName,
                                                    @Nullable String propertyValue, String propNameHeader) {
        PropertyValue entityPropertyToUpdate = getPropertyValue(entity, propertyName, propNameHeader);

        if (entityPropertyToUpdate != null) {
            entityPropertyToUpdate.setPropValue(Conversion.stringToValue(propertyValue,
                    entityPropertyToUpdate.getProperty().getDataType()));
            getDAO().saveChild(entityPropertyToUpdate);
        }
    }

    /**
     * Updates the {@link DataLoaderResult} in case of {@link ErrorMessage#ENTITY_NOT_FOUND} error
     * (if there is no property of this name in the database), or {@link ErrorMessage#PROPERTY_NOT_FOUND} error
     * (if the entity does not have such property value)
     *
     * @param entity the database entity for which to update or insert the property.
     * @param propertyName the name of the property which value to update or add.
     * @param propNameHeader the name of the column producing the error. This is added to the error message.
     * @return the {@link PropertyValue} if found, <code>null</code> otherwise.
     */
    protected PropertyValue getPropertyValue(EntityWithProperties entity, String propertyName,
                                                    String propNameHeader) {
        Preconditions.checkNotNull(propertyName);
        Preconditions.checkNotNull(propNameHeader);
        // TODO push this search for property value into EntityWithProperties
        final List<PropertyValue> propertyList = entity.getEntityPropertyList();

        final @Nullable Property property = propertyEJB.findByName(propertyName);
        if (property == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, propNameHeader, propertyName);
            return null;
        }

        for (final PropertyValue value : propertyList) {
            if (value.getProperty().equals(property)) {
                return value;
            }
        }

        // property value was not found
        result.addRowMessage(ErrorMessage.PROPERTY_NOT_FOUND, propNameHeader, propertyName);
        return null;
    }

    /**
     * To be implemented by sub-classes. Returns a DAO EJB for accessing properties
     * @param <T> configuration entity class
     * @return the DAO EJB
     */
    protected abstract <T extends EntityWithId> DAO<T> getDAO();
}
