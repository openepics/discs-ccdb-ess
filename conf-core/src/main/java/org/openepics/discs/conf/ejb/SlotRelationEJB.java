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

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;

/**
 * DAO Service for accessing (querying) for available {@link SlotRelation}s
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
public class SlotRelationEJB extends ReadOnlyDAO<SlotRelation> {
    /**
     * {@link ReadOnlyDAO#findByName(String)} not applicable for {@link SlotRelation} entities.
     *
     * Calling this method causes {@link UnsupportedOperationException}.
     */
    @Override
    public SlotRelation findByName(String name) {
        throw new UnsupportedOperationException("findByName with String parametar method not aplicable to "
                + "SlotRelation class");
    }

    /**
     * Queries for a {@link SlotRelation} given {@link SlotRelationName}
     * @param name the type of slot relation
     * @return the resulting {@link SlotRelation} or <code>null</code> if none exists in the database
     */
    public SlotRelation findBySlotRelationName(SlotRelationName name) {
        try {
            return em.createNamedQuery("SlotRelation.findByName", SlotRelation.class)
                    .setParameter("name", name).getSingleResult();
        } catch (NoResultException e) { // NOSONAR
            // no result is not an exception
            return null;
        }
    }

    @Override
    protected Class<SlotRelation> getEntityClass() {
        return SlotRelation.class;
    }
}
