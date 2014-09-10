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
abstract public class ReadOnlyDAO<T> {
    @Inject protected ConfigurationEntityUtility entityUtility;
    @PersistenceContext protected EntityManager em;

    private Class<T> entityClass;

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
            return em.createNamedQuery(entityClass.getSimpleName()+".findByName", entityClass).setParameter("name", name).getSingleResult();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("findByName query has not been defined for the entity "+entityClass.getSimpleName(), e);
        } catch (NoResultException e) {
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
    abstract protected void defineEntity();

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
