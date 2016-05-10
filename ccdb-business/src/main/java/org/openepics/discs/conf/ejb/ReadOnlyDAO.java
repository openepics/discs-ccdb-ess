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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.EntityWithId;

/**
 * Abstract generic DAO used for all entities.
 * This only provides querying (read-only) access. See {@link DAO} for full CRUD version.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 * @param <T> The entity type for which this DAO is defined.
 */
public abstract class ReadOnlyDAO<T extends EntityWithId> {
    @Inject protected ConfigurationEntityUtility entityUtility;
    @PersistenceContext protected EntityManager em;

    /** Default no-parameters constructor */
    public ReadOnlyDAO() {}

    /**
     * Find a single entity by its Id (usually numeric)
     *
     * @param id the id
     * @return the entity found or null
     */
    public T findById(Object id) {
        return em.find(getEntityClass(), id);
    }

    /**
     * @param entity the entity to get a fresh copy of
     * @return returns a fresh entity read directly from the database
     */
    public T refreshEntity(T entity) {
        return findById(entity.getId());
    }

    /**
     * Finds an entity by name, by assuming SimpleClassName.findByName named query is defined for the entity.
     *
     * @param name the name of the searched entity
     * @return the entity found or null
     */
    public T findByName(String name) {
        try {
            return em.createNamedQuery(getEntityClass().getSimpleName() + ".findByName", getEntityClass()).
                    setParameter("name", name).getSingleResult();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("findByName query has not been defined for the entity " +
                    getEntityClass().getSimpleName(), e);
        } catch (NoResultException e) { // NOSONAR
            return null;
        }
    }

    /**
     * Returns all entities of the type in the database
     *
     * @return the list of entities
     */
    public List<T> findAll() {
        final CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(getEntityClass());
        cq.from(getEntityClass());

        final List<T> result = em.createQuery(cq).getResultList();
        return result != null ? result : new ArrayList<T>();
    }

    /**
     * @return the number of elements in the table
     */
    public long getRowCount() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<T> root = cq.from(getEntityClass());

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Implementation sub-classes should override this to return the encapsulated entity class.
     * @return the {@link Class} of the entity this DAO is for
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Escapes the characters that have a special meaning in the database LIKE query, '%' and '_'.
     *
     * @param dbString the string to escape
     * @return the escaped string
     */
    protected String escapeDbString(final String dbString) {
        return dbString.replaceAll("%", "\\%").replaceAll("_", "\\_");
    }

}
