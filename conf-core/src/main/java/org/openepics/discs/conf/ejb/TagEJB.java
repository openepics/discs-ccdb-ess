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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.ent.Tag;

import com.google.common.base.Preconditions;

/**
 * @author ess-dev
 *
 */
@Stateless
public class TagEJB {

    @PersistenceContext private EntityManager em;

    /**
     * Find a single tag by its name (Id)
     *
     * @param id the id
     * @return the entity found or null
     */
    public Tag findById(String id) {
        return em.find(Tag.class, id);
    }

    /**
     * Returns all tags in the database
     *
     * @return the list of tags
     */
    public List<Tag> findAll() {
        final CriteriaQuery<Tag> cq = em.getCriteriaBuilder().createQuery(Tag.class);
        cq.from(Tag.class);

        final List<Tag> result = em.createQuery(cq).getResultList();
        return result != null ? result : new ArrayList<Tag>();
    }

    /**
     * Returns all tags in the database sorted in ascending order
     *
     * @return the list of tags
     */
    public List<Tag> findAllSorted() {
        return em.createNamedQuery( "Tag.findAllOrdered", Tag.class).getResultList();
    }

    /**
     * Find a single tag by its name (Id)
     *
     * @param name the name of the tag
     * @return the entity found or null
     */
    public Tag findByName(String name) {
        return em.find(Tag.class, name);
    }

    /**
     * Adds a new tag to the list of existing tags.
     *
     * @param tag the tag to add
     */
    public void add(Tag tag) {
        Preconditions.checkNotNull(tag);
        em.persist(tag);
    }

}
