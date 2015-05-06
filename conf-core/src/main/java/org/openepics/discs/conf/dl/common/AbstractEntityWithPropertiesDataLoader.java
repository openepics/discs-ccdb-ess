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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.EntityWithProperties;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.util.Conversion;

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

    private Class<S> propertyValueClass;

    @Override
    protected boolean indexPropertyColumns() {
        return true;
    }

    @Override
    protected boolean checkPropertyHeader(String propertyName) {
        final @Nullable Property property = propertyEJB.findByName(propertyName);
        if (property == null) {
            result.addRowMessage(ErrorMessage.PROPERTY_NOT_FOUND, propertyName);
        }
        return !result.isError();
    }

    /**
     * Used by sub-classes to update properties
     *
     * @param entity the database entity for which to update or insert the properties
     */
    protected void addOrUpdateProperties(EntityWithProperties entity) {
        final List<PropertyValue> propertyList = entity.getEntityPropertyList();
        final List<PropertyValue> entityProperties = new ArrayList<>();
        if (propertyList != null) {
            entityProperties.addAll(propertyList);
        }

        final Map<Property, PropertyValue> entityPropertyByProperty = new HashMap<>();
        for (PropertyValue entityProperty : entityProperties) {
            entityPropertyByProperty.put(entityProperty.getProperty(), entityProperty);
        }

        for (String propertyName : getProperties()) {
            final @Nullable Property property = propertyEJB.findByName(propertyName);
            final @Nullable String propertyValue = readCurrentRowCellForProperty(propertyName);
            if (entityPropertyByProperty.containsKey(property)) {
                final PropertyValue entityPropertyToUpdate = entityPropertyByProperty.get(property);

                if (propertyValue == null) {
                    getDAO().deleteChild(entityPropertyToUpdate);
                } else {
                    entityPropertyToUpdate.setPropValue(Conversion.stringToValue(propertyValue, property.getDataType()));
                    getDAO().saveChild(entityPropertyToUpdate);
                }

            } else if (propertyValue != null) {
                try {
                    final S propertyValueToAdd = propertyValueClass.newInstance();
                    propertyValueToAdd.setProperty(property);
                    propertyValueToAdd.setPropValue(Conversion.stringToValue(propertyValue, property.getDataType()));
                    propertyValueToAdd.setPropertiesParent(entity);
                    getDAO().addChild(propertyValueToAdd);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Sets the class of a {@link PropertyValue} sub-class from which new instance of
     * that class can be created
     *
     * @param propertyValueClass the @link {@link PropertyValue} class
     */
    protected void setPropertyValueClass(Class<S> propertyValueClass) {
        this.propertyValueClass = propertyValueClass;
    }

    /**
     * To be implemented by sub-classes. Returns a DAO EJB for accessing properties
     * @param <T> configuration entity class
     * @return the DAO EJB
     */
    protected abstract <T> DAO<T> getDAO();
}
