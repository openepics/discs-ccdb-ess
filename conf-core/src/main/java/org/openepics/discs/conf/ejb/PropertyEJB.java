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
package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.SlotPropertyValue;

/**
 * DAO Service for accesing {@link Property} entities
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class PropertyEJB extends DAO<Property> {

    /** @return a {@link List} of all {@link Property Properties} in the database ordered by the property name */
    public List<Property> findAllOrderedByName() {
        return em.createNamedQuery("Property.findAllOrderedByName", Property.class).getResultList();
    }

    /**
     * @param property <code>true</code> if the property is used in some {@link PropertyValue} instance,
     * <code>false</code> otherwise.
     * @return
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

    @Override
    protected Class<Property> getEntityClass() {
        return Property.class;
    }
}
