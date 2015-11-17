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

import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;

/**
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
public class UnitEJB extends DAO<Unit> {
    @Override
    protected Class<Unit> getEntityClass() {
        return Unit.class;
    }

    /**
     * @return a list of all {@link Unit}s ordered by name.
     */
    public List<Unit> findAllOrdered() {
        return em.createNamedQuery("Unit.findAllOrdered", Unit.class).getResultList();
    }

    /**
     * @param unit unit to check
     * @return <code>true</code> if the unit is used in some property definition, <code>false</code> otherwise.
     */
    public boolean isUnitUsed(Unit unit) {
        return !em.createNamedQuery("Property.findByUnit", Property.class).setParameter("unit", unit).getResultList()
                        .isEmpty();
    }

    /**
     * @param unit unit to check
     * @return the list of properties, where the unit is used
     */
    public List<Property> findProperties(Unit unit, int maxResults) {
        return em.createNamedQuery("Property.findByUnit", Property.class).setParameter("unit", unit).setMaxResults(maxResults).getResultList();
    }
}
