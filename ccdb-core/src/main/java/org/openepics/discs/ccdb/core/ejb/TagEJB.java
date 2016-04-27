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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.ccdb.model.Tag;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Service that is used to query tags in the database
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Stateless
public class TagEJB {
    @PersistenceContext private EntityManager em;

    /**
     * Returns all tags in the database sorted in ascending order
     *
     * @return the list of tags
     */
    public List<Tag> findAllSorted() {
        return em.createNamedQuery( "Tag.findAllOrdered", Tag.class).getResultList();
    }

    /**
     * @param tag the {@link Tag} (as a String) to search for
     * @return a {@link Tag} entity if it is already defined, <code>null</code> otherwise
     */
    public Tag findById(String tag) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(tag));
        return em.find(Tag.class, tag);
    }

}
