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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.openepics.discs.ccdb.core.auditlog.Audit;
import org.openepics.discs.ccdb.core.auditlog.AuditLogEntryCreator;
import org.openepics.discs.ccdb.model.Artifact;
import org.openepics.discs.ccdb.model.AuditRecord;
import org.openepics.discs.ccdb.model.EntityTypeOperation;
import org.openepics.discs.ccdb.model.EntityWithArtifacts;
import org.openepics.discs.ccdb.model.EntityWithProperties;
import org.openepics.discs.ccdb.model.Property;
import org.openepics.discs.ccdb.model.PropertyValue;
import org.openepics.discs.ccdb.model.values.Value;
import org.openepics.discs.ccdb.core.security.Authorized;
import org.openepics.discs.ccdb.core.util.CRUDOperation;
import org.openepics.discs.ccdb.core.util.ParentEntityResolver;
import org.openepics.discs.ccdb.core.util.PropertyValueNotUniqueException;
import org.openepics.discs.ccdb.core.util.UnhandledCaseException;

import com.google.common.base.Preconditions;

/**
 * Abstract generic DAO used for all entities.
 *
 * It uses the concept of Parent and optional Child entities in {@link List} collections.
 * This one extends the read-only {@link ReadOnlyDAO}.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 * @param <T> The entity type for which this DAO is defined.
 */
public abstract class DAO<T> extends ReadOnlyDAO<T> {
    private static final String PROPERTY_PARAM = "property";
    private static final String PROPERTY_VALUE_PARAM = "propValue";

    @Inject private AuditLogEntryCreator auditLogEntryCreator;

    /**
     * Adds a new entity to the database
     *
     * @param entity the entity to be added
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void add(T entity) {
        Preconditions.checkNotNull(entity);
        entityUtility.setModified(entity);
        em.persist(entity);
    }

    /**
     * Updates an entity in the database
     *
     * @param entity the entity to be updated
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void save(T entity) {
        Preconditions.checkNotNull(entity);
        entityUtility.setModified(entity);
        em.merge(entity);
    }

    /**
     * Deletes entity from the database
     *
     * @param entity the entity to be deleted
     */
    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void delete(T entity) {
        Preconditions.checkNotNull(entity);
        em.remove( em.merge(entity) );
    }

    /**
     * Adds a child entity to a parent and the database
     *
     * @param <S> the child entity type
     * @param child the child entity to be added
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public <S> void addChild(S child) {
        Preconditions.checkNotNull(child);
        final T parent = getParent(child);

        uniquePropertyValueCheck(child, parent);

        entityUtility.setModified(parent, child);

        getChildrenFromParent(child).add(child);
        em.merge(parent);
    }

    /**
     * Updates a child entity of a parent and in the database
     *
     * @param <S> the child entity type
     * @param child the child entity to be updated
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public <S> void saveChild(S child) {
        Preconditions.checkNotNull(child);

        uniquePropertyValueCheck(child, getParent(child));

        final S mergedChild = em.merge(child);
        entityUtility.setModified(getParent(mergedChild), mergedChild);
    }

    protected <S> void uniquePropertyValueCheck(final S child, final T parent) {
        if (child instanceof PropertyValue) {
            final PropertyValue propVal = (PropertyValue) child;
            switch (propVal.getProperty().getValueUniqueness()) {
                case NONE:
                    break;
                case TYPE:
                    if (!isPropertyValueTypeUnique(propVal, parent)) {
                        throw new PropertyValueNotUniqueException();
                    }
                    break;
                case UNIVERSAL:
                    if (!isPropertyValueUniversallyUnique(propVal)) {
                        throw new PropertyValueNotUniqueException();
                    }
                    break;
                default:
                    throw new UnhandledCaseException();
            }
        }
    }

    /**
     * Deletes a child entity from parent collection and removes from the database
     *
     * @param <S> the child entity type
     * @param child the child entity to be removed
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public <S> void deleteChild(S child) {
        Preconditions.checkNotNull(child);
        getChildrenFromParent(child).remove(child);

        final S mergedChild = em.merge(child) ;
        em.remove(mergedChild);
    }

    @SuppressWarnings("unchecked")
    private <S> T getParent(S child) {
        Preconditions.checkNotNull(child);
        if (child instanceof PropertyValue) {
            return (T) ((PropertyValue)child).getPropertiesParent();
        } else if (child instanceof Artifact) {
            return (T) ((Artifact)child).getArtifactsParent();
        } else {
            throw new IllegalStateException("getParent called on entity that has neither properties nor artifacts.");
        }
    }

    @SuppressWarnings("unchecked")
    private <S> List<S> getChildrenFromParent(S child) {
        Preconditions.checkNotNull(child);
        if (child instanceof PropertyValue) {
            final EntityWithProperties parent = ((PropertyValue)child).getPropertiesParent();
            return (List<S>) parent.getEntityPropertyList();
        } else if (child instanceof Artifact) {
            final EntityWithArtifacts parent = ((Artifact)child).getArtifactsParent();
            return (List<S>) parent.getEntityArtifactList();
        } else {
            throw new IllegalStateException("getParent called on entity that has neither properties nor artifacts.");
        }
    }

    /** <p>
     * Default implementation of the of the property value uniqueness check. This implementation only throws an
     * exception, since default functionality is only intended to provide implementation to entities without a
     * property value child.
     * </p>
     * <p>
     * Every EJB that supports property value children must override this method.
     * </p>
     * @param child the child entity @link {@link PropertyValue}
     * @param parent the parent entity
     * @return <code>true</code> if the property values is unique or <code>null</code>, <code>false</code> otherwise.
     * <code>null</code> value can only be achieved through adding a property definition.
     */
    protected boolean isPropertyValueTypeUnique(PropertyValue child, T parent) {
        throw new UnhandledCaseException();
    }

    private boolean isPropertyValueUniversallyUnique(PropertyValue child) {
        Preconditions.checkNotNull(child);
        final Property property = Preconditions.checkNotNull(child.getProperty());
        final Value value = child.getPropValue();
        if (value == null) {
            return true;
        }

        List<PropertyValue> results = em.createNamedQuery("ComptypePropertyValue.findSamePropertyValue",
                                                        PropertyValue.class)
                    .setParameter(PROPERTY_PARAM, property)
                    .setParameter(PROPERTY_VALUE_PARAM, value).setMaxResults(2).getResultList();
        // value is unique if there is no property value with the same value, or the only one found us the entity itself
        boolean valueUnique = (results.size() < 2) && (results.isEmpty() || results.get(0).equals(child));
        if (valueUnique) {
            results = em.createNamedQuery("SlotPropertyValue.findSamePropertyValue", PropertyValue.class)
                        .setParameter(PROPERTY_PARAM, property)
                        .setParameter(PROPERTY_VALUE_PARAM, value).setMaxResults(2).getResultList();
            // value is unique if there is no same property value, or the only one found us the entity itself
            valueUnique = (results.size() < 2) && (results.isEmpty() || results.get(0).equals(child));
        }
        if (valueUnique) {
            results = em.createNamedQuery("DevicePropertyValue.findSamePropertyValue", PropertyValue.class)
                        .setParameter(PROPERTY_PARAM, property)
                        .setParameter(PROPERTY_VALUE_PARAM, value).setMaxResults(2).getResultList();
            // value is unique if there is no same property value, or the only one found us the entity itself
            valueUnique = (results.size() < 2) && (results.isEmpty() || results.get(0).equals(child));
        }
        return valueUnique;
    }

    /** Create an explicit audit log entry for the database entity.
     * @param entity the entity to create the audit log for
     * @param operation the type of database operation
     */
    protected void explicitAuditLog(final T entity, final EntityTypeOperation operation) {
        final List<AuditRecord> auditRecords = auditLogEntryCreator.auditRecords(
                ParentEntityResolver.resolveParentEntity(entity), operation);

        for (AuditRecord auditRecord : auditRecords) {
            auditRecord.setUser(entityUtility.getUserId());
            auditRecord.setLogTime(new Date());

            em.persist(auditRecord);
        }
    }
}
