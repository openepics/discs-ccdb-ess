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
package org.openepics.discs.ccdb.core.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.ccdb.model.ComptypePropertyValue;
import org.openepics.discs.ccdb.model.DevicePropertyValue;
import org.openepics.discs.ccdb.model.EntityTypeOperation;
import org.openepics.discs.ccdb.model.Property;
import org.openepics.discs.ccdb.model.PropertyValue;
import org.openepics.discs.ccdb.model.SlotPropertyValue;
import org.openepics.discs.ccdb.core.security.Authorized;
import org.openepics.discs.ccdb.core.util.CRUDOperation;
import org.openepics.discs.ccdb.core.util.Utility;

import com.google.common.collect.Lists;

/**
 * DAO Service for accesing {@link Property} entities
 *
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
public class PropertyEJB extends DAO<Property> {

    /** @return a {@link List} of all {@link Property Properties} in the database ordered by the property name */
    public List<Property> findAllOrderedByName() {
        return em.createNamedQuery("Property.findAllOrderedByName", Property.class).getResultList();
    }

    /**
     * @param property the property to perform test for
     * @return <code>true</code> if the property is used in some {@link PropertyValue} instance,
     * <code>false</code> otherwise
     */
    public boolean isPropertyUsed(Property property) {
        List<SlotPropertyValue> slotPropertyValues = em.createQuery("SELECT pv FROM SlotPropertyValue pv "
                + "WHERE pv.property = :property", SlotPropertyValue.class).setParameter("property", property)
                .setMaxResults(1).getResultList();
        if (!slotPropertyValues.isEmpty()) {
            return true;
        }

        List<DevicePropertyValue> devicePropertyValues = em.createQuery("SELECT pv FROM DevicePropertyValue pv "
                + "WHERE pv.property = :property", DevicePropertyValue.class).setParameter("property", property)
                .setMaxResults(1).getResultList();
        if (!devicePropertyValues.isEmpty()) {
            return true;
        }

        List<ComptypePropertyValue> typePropertyValues = em.createQuery("SELECT pv FROM ComptypePropertyValue pv "
                + "WHERE pv.property = :property", ComptypePropertyValue.class).setParameter("property", property)
                .setMaxResults(1).getResultList();

        return !typePropertyValues.isEmpty();
    }

    /** Returns the {@link PropertyValue} of any kind  that match a given {@link Property}
     * @param property the {@link Property} to use for search
     * @param maxResults the maximum value of results to return for any of the three types
     * @return the {@link List} of {@link PropertyValue}s that match the {@link Property}
     */
    public List<? extends PropertyValue> findPropertyValues(Property property, int maxResults) {
        List<PropertyValue> propertyValues = Lists.newArrayList();
        propertyValues.addAll(em.createQuery("SELECT pv FROM ComptypePropertyValue pv "
                + "WHERE pv.property = :property", ComptypePropertyValue.class).setParameter("property", property)
                .setMaxResults(maxResults).getResultList());
        propertyValues.addAll(em.createQuery("SELECT pv FROM SlotPropertyValue pv "
                + "WHERE pv.property = :property", SlotPropertyValue.class).setParameter("property", property)
                .setMaxResults(maxResults).getResultList());
        propertyValues.addAll(em.createQuery("SELECT pv FROM DevicePropertyValue pv "
                + "WHERE pv.property = :property", DevicePropertyValue.class).setParameter("property", property)
                .setMaxResults(maxResults).getResultList());
        return propertyValues;

    }

    @Override
    protected Class<Property> getEntityClass() {
        return Property.class;
    }

    /**
     * The method creates a new copy of the selected {@link Property}s
     * @param propertiesToCopy a {@link List} of {@link Property}s to create a copy of
     * @return the number of copies created
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Authorized
    public int duplicate(final List<Property> propertiesToCopy) {
        if (Utility.isNullOrEmpty(propertiesToCopy)) return 0;

        int duplicated = 0;
        for (final Property propToCopy : propertiesToCopy) {
            final String newPropName = Utility.findFreeName(propToCopy.getName(), this);
            final Property newProp = new Property(newPropName, propToCopy.getDescription());
            newProp.setDataType(propToCopy.getDataType());
            newProp.setUnit(propToCopy.getUnit());
            newProp.setValueUniqueness(propToCopy.getValueUniqueness());
            add(newProp);
            explicitAuditLog(newProp, EntityTypeOperation.CREATE);
            ++duplicated;
        }
        return duplicated;
    }
}
