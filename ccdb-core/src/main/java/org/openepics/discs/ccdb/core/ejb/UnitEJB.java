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

import org.openepics.discs.ccdb.model.EntityTypeOperation;
import org.openepics.discs.ccdb.model.Property;
import org.openepics.discs.ccdb.model.Unit;
import org.openepics.discs.ccdb.core.security.Authorized;
import org.openepics.discs.ccdb.core.util.CRUDOperation;
import org.openepics.discs.ccdb.core.util.Utility;

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
     * @param maxResults the maximum number of entities returned by the database
     * @return the list of properties, where the unit is used
     */
    public List<Property> findProperties(Unit unit, int maxResults) {
        return em.createNamedQuery("Property.findByUnit", Property.class).setParameter("unit", unit).setMaxResults(maxResults).getResultList();
    }

    /**
     * The method creates a new copy of the selected {@link Unit}s
     * @param unitsToDuplicate a {@link List} of {@link Unit}s to create a copy of
     * @return the number of copies created
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Authorized
    public int duplicate(final List<Unit> unitsToDuplicate) {
        int duplicated = 0;
        if (Utility.isNullOrEmpty(unitsToDuplicate)) return 0;

        for (final Unit unitToCopy : unitsToDuplicate) {
            final String newUnitName = Utility.findFreeName(unitToCopy.getName(), this);
            final Unit newUnit = new Unit(newUnitName, unitToCopy.getSymbol(), unitToCopy.getDescription());
            add(newUnit);
            explicitAuditLog(newUnit, EntityTypeOperation.CREATE);
            ++duplicated;
        }
        return duplicated;
    }
}
