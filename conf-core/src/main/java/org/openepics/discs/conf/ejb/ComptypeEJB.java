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

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;

import com.google.common.base.Preconditions;

/**
 * DAO Service for accessing Component Types ( {@link ComponentType} )
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */

@Stateless
public class ComptypeEJB extends DAO<ComponentType> {
    /**
     * @return A list of all device types ordered by name.
     */
    public List<ComponentType> findComponentTypeOrderedByName() {
        return em.createNamedQuery("ComponentType.findAllOrdered", ComponentType.class).getResultList();
    }

    /**
     * @param componentType - the device type
     * @return A list of all property definitions for the selected device type.
     */
    public List<ComptypePropertyValue> findPropertyDefinitions(ComponentType componentType) {
        return em.createNamedQuery("ComptypePropertyValue.findPropertyDefs", ComptypePropertyValue.class)
               .setParameter("componentType", componentType).getResultList();
    }

    @Override
    public List<ComponentType> findAll() {
        return em.createNamedQuery("ComponentType.findUserTypesOnly", ComponentType.class)
                .setParameter("internalType1", SlotEJB.ROOT_COMPONENT_TYPE)
                .setParameter("internalType2", SlotEJB.GRP_COMPONENT_TYPE).getResultList();
    }

    @Override
    protected Class<ComponentType> getEntityClass() {
        return ComponentType.class;
    }

    @Override
    protected boolean isPropertyValueTypeUnique(PropertyValue child, ComponentType parent) {
        // each component type can have only one property value for some property. This is true by definition.
        return true;
    }

    /** If the property value fails the uniqueness check, this method throws an unchecked
     * {@link PropertyValueNotUniqueException}
     * @param pv the property value to check for
     */
    public void checkPropertyValueUnique(final ComptypePropertyValue pv) {
        Preconditions.checkNotNull(pv);
        uniquePropertyValueCheck(pv, (ComponentType)pv.getPropertiesParent());
    }
}
