package org.openepics.discs.conf.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

import com.google.common.base.Preconditions;

/**
 * Abstract generic DAO used for all entities.
 *
 * It uses the concept of Parent and optional Child entities in {@link List} collections.
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 * @param <T>
 */
abstract public class DAO<T> {
    @Inject private ConfigurationEntityUtility entityUtility;
    @PersistenceContext protected EntityManager em;

    @SuppressWarnings("rawtypes")
    private Map<Class, ParentChildInterface> interfaces = new HashMap<Class, ParentChildInterface>(3);
    private Class<T> entityClass;

    public DAO() {
        defineEntity();
        Preconditions.checkNotNull(entityClass);
    }

    public T findById(Object id) {
        return em.find(entityClass, id);
    }

    public T findByName(Object name) {
        try {
            return em.createNamedQuery(entityClass.getSimpleName()+".findByName", entityClass).setParameter("name", name).getSingleResult();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("findByName query has not been defined for the entity "+entityClass.getSimpleName(), e);
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findAll() {
        final CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
        cq.from(ComponentType.class);

        return em.createQuery(cq).getResultList();
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void add(T entity) {
        Preconditions.checkNotNull(entity);
        entityUtility.setModified(entity);
        em.persist(entity);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void save(T entity) {
        Preconditions.checkNotNull(entity);
        entityUtility.setModified(entity);
        em.merge(entity);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void delete(T entity) {
        Preconditions.checkNotNull(entity);
        em.remove( em.merge(entity) );
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public <S> void addChild(S child) {
        Preconditions.checkNotNull(child);
        final T parent = getParent(child);

        entityUtility.setModified(parent, child);

        getChildrenFromParent(child).add(child);
        em.merge(parent);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public <S> void saveChild(S child) {
        Preconditions.checkNotNull(child);
        final S mergedChild = em.merge( child );
        entityUtility.setModified(getParent(mergedChild), mergedChild);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public <S> void deleteChild(S child) {
        Preconditions.checkNotNull(child);

        final S mergedChild = em.merge(child) ;
        getChildrenFromParent(mergedChild).remove(mergedChild);
        em.remove(mergedChild);
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
     * Defines a calling interface between the parent and a child collection
     *
     * @param childClass the child collection
     * @param iface the interface
     */
    protected <S> void defineParentChildInterface(Class<S> childClass, ParentChildInterface<T, S> iface) {
        Preconditions.checkNotNull(childClass);

        this.interfaces.put(childClass, iface);
    }

    private <S> ParentChildInterface<T, S> getResolver(S child) {
        @SuppressWarnings("unchecked")
        final ParentChildInterface<T,S> resolver = interfaces.get(child.getClass());
        if (resolver == null) {
            throw new RuntimeException("No child interface defined for the class "+
                    child.getClass().getCanonicalName()+" in "+this.getClass().getName()+" DAO.");
        }
        return resolver;
    }

    private <S> T getParent(S child) {
        Preconditions.checkNotNull(child);
        return getResolver(child).getParentFromChild(child);
    }

    private <S> List<S> getChildrenFromParent(S child) {
        Preconditions.checkNotNull(child);
        return getResolver(child).getChildCollection(getParent(child));
    }
}
