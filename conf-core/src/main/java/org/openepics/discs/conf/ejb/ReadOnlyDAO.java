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
import javax.persistence.criteria.CriteriaQuery;

import com.google.common.base.Preconditions;

/**
 * Abstract generic DAO used for all entities.
 * This only provides querying (read-only) access. See {@link DAO} for full CRUD version.
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 * @param <T> The entity type for which this DAO is defined.
 */
public abstract class ReadOnlyDAO<T> {
    @Inject protected ConfigurationEntityUtility entityUtility;
    @PersistenceContext protected EntityManager em;

    private Class<T> entityClass;

    /**
     * Default no-parameters constructor
     */
    public ReadOnlyDAO() {
        defineEntity();
        Preconditions.checkNotNull(entityClass);
    }

    /**
     * Find a single entity by its Id (usually numeric)
     *
     * @param id the id
     * @return the entity found or null
     */
    public T findById(Object id) {
        return em.find(entityClass, id);
    }

    /**
     * Finds an entity by name, by assuming SimpleClassName.findByName named query is defined for the entity.
     *
     * @param name the name of the searched entity
     * @return the entity found or null
     */
    public T findByName(String name) {
        try {
            return em.createNamedQuery(entityClass.getSimpleName() + ".findByName", entityClass).
                    setParameter("name", name).getSingleResult();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("findByName query has not been defined for the entity " +
                    entityClass.getSimpleName(), e);
        } catch (NoResultException e) { // NOSONAR
            // no result is not an exception
            return null;
        }
    }

    /**
     * Returns all entities of the type in the database
     *
     * @return the list of entities
     */
    public List<T> findAll() {
        final CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
        cq.from(entityClass);

        final List<T> result = em.createQuery(cq).getResultList();
        return result != null ? result : new ArrayList<T>();
    }

    /**
     * Implementation classes must define this method to call the defineEntityClass and
     * optional defineParentChildInterface methods
     */
    protected abstract void defineEntity();

    /**
     * Defines the class of the entity managed by this DAO
     *
     * @param clazz
     */
    protected void defineEntityClass(Class<T> clazz) {
        this.entityClass = clazz;
    }

    /**
     * Getter for the Entity class, to be used in sub-classes
     *
     * @return
     */
    protected Class<T> getEntityClass() {
        return this.entityClass;
    }
}
