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

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.DataType_;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Property_;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ent.Unit_;
import org.openepics.discs.conf.ent.fields.PropertyFields;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;
import org.openepics.discs.conf.util.SortOrder;
import org.openepics.discs.conf.util.Utility;

import com.google.common.collect.Lists;

/**
 * DAO Service for accesing {@link Property} entities
 *
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
public class PropertyEJB extends DAO<Property> {

    /** @return a {@link List} of all {@link Property Properties} in the database ordered by the property name */
    public List<Property> findAllOrderedByName() {
        return em.createNamedQuery("Property.findAllOrderedByName", Property.class).getResultList();
    }

    /**
     * @param property the property to perform test for
     * @return <code>true</code> if the property is used in some {@link PropertyValue} instance,
     * <code>false</code> otherwise
     */
    public boolean isPropertyUsed(Property property) {
        List<SlotPropertyValue> slotPropertyValues = em.createQuery("SELECT pv FROM SlotPropertyValue pv "
                + "WHERE pv.property = :property", SlotPropertyValue.class).setParameter("property", property)
                .setMaxResults(1).getResultList();
        if (!slotPropertyValues.isEmpty()) {
            return true;
        }

        List<DevicePropertyValue> devicePropertyValues = em.createQuery("SELECT pv FROM DevicePropertyValue pv "
                + "WHERE pv.property = :property", DevicePropertyValue.class).setParameter("property", property)
                .setMaxResults(1).getResultList();
        if (!devicePropertyValues.isEmpty()) {
            return true;
        }

        List<ComptypePropertyValue> typePropertyValues = em.createQuery("SELECT pv FROM ComptypePropertyValue pv "
                + "WHERE pv.property = :property", ComptypePropertyValue.class).setParameter("property", property)
                .setMaxResults(1).getResultList();

        return !typePropertyValues.isEmpty();
    }

    /** Returns the {@link PropertyValue} of any kind  that match a given {@link Property}
     * @param property the {@link Property} to use for search
     * @param maxResults the maximum value of results to return for any of the three types
     * @return the {@link List} of {@link PropertyValue}s that match the {@link Property}
     */
    public List<? extends PropertyValue> findPropertyValues(Property property, int maxResults) {
        List<PropertyValue> propertyValues = Lists.newArrayList();
        propertyValues.addAll(em.createQuery("SELECT pv FROM ComptypePropertyValue pv "
                + "WHERE pv.property = :property", ComptypePropertyValue.class).setParameter("property", property)
                .setMaxResults(maxResults).getResultList());
        propertyValues.addAll(em.createQuery("SELECT pv FROM SlotPropertyValue pv "
                + "WHERE pv.property = :property", SlotPropertyValue.class).setParameter("property", property)
                .setMaxResults(maxResults).getResultList());
        propertyValues.addAll(em.createQuery("SELECT pv FROM DevicePropertyValue pv "
                + "WHERE pv.property = :property", DevicePropertyValue.class).setParameter("property", property)
                .setMaxResults(maxResults).getResultList());
        return propertyValues;

    }

    @Override
    protected Class<Property> getEntityClass() {
        return Property.class;
    }

    /**
     * The method creates a new copy of the selected {@link Property}s
     * @param propertiesToCopy a {@link List} of {@link Property}s to create a copy of
     * @return the number of copies created
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Authorized
    public int duplicate(final List<Property> propertiesToCopy) {
        if (Utility.isNullOrEmpty(propertiesToCopy)) return 0;

        int duplicated = 0;
        for (final Property propToCopy : propertiesToCopy) {
            final String newPropName = Utility.findFreeName(propToCopy.getName(), this);
            final Property newProp = new Property(newPropName, propToCopy.getDescription());
            newProp.setDataType(propToCopy.getDataType());
            newProp.setUnit(propToCopy.getUnit());
            newProp.setValueUniqueness(propToCopy.getValueUniqueness());
            add(newProp);
            explicitAuditLog(newProp, EntityTypeOperation.CREATE);
            ++duplicated;
        }
        return duplicated;
    }

    /**
     * Returns only a subset of data based on sort column, sort order and filtered by all the fields.
     *
     * @param first the index of the first result to return
     * @param pageSize the number of results
     * @param sortField the field by which to sort
     * @param sortOrder ascending/descending
     * @param name the unit name
     * @param description the unit description
     * @param unit the unit symbol
     * @return The required entities.
     */
    public List<Property> findLazy(final int first, final int pageSize,
            final @Nullable PropertyFields sortField, final @Nullable SortOrder sortOrder,
            final @Nullable String name, final @Nullable String description, final @Nullable String unit,
            final @Nullable String dataType) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Property> cq = cb.createQuery(getEntityClass());
        final Root<Property> auditRecord = cq.from(getEntityClass());

        addSortingOrder(sortField, sortOrder, cb, cq, auditRecord);

        final Predicate[] predicates = buildPredicateList(cb, cq, auditRecord, name, description, unit, dataType);
        cq.where(predicates);

        final TypedQuery<Property> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    private void addSortingOrder(final PropertyFields sortField, final SortOrder sortOrder, final CriteriaBuilder cb,
            final CriteriaQuery<Property> cq, final Root<Property> unitRecord) {
        if ((sortField != null) && (sortOrder != null) && (sortOrder != SortOrder.UNSORTED)) {
            switch (sortField) {
            case NAME:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Property_.name))
                                : cb.desc(unitRecord.get(Property_.name)));
            case DESCRIPTION:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Property_.description))
                                : cb.desc(unitRecord.get(Property_.description)));
                break;
            case UNIT:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Property_.unit))
                                : cb.desc(unitRecord.get(Property_.unit)));
                break;
            case DATA_TYPE:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Property_.dataType))
                                : cb.desc(unitRecord.get(Property_.dataType)));
                break;
            default:
                break;
            }
        }
    }

    private Predicate[] buildPredicateList(final CriteriaBuilder cb, final CriteriaQuery<Property> cq,
            final Root<Property> propertyRecord, final @Nullable String name, final @Nullable String description,
            final @Nullable String unit, final @Nullable String dataType) {
        final List<Predicate> predicates = Lists.newArrayList();

        if (name != null) {
            predicates.add(cb.like(propertyRecord.get(Property_.name), "%" + escapeDbString(name) + "%", '\\'));
        }
        if (description != null) {
            predicates.add(cb.like(propertyRecord.get(Property_.description),
                                                                "%" + escapeDbString(description) + "%", '\\'));
        }
        if (unit != null) {
            Subquery<Unit> unitQuery = cq.subquery(Unit.class);
            Root<Unit> unitRecord = unitQuery.from(Unit.class);
            unitQuery.select(unitRecord);
            unitQuery.where(cb.like(unitRecord.get(Unit_.name), "%" + escapeDbString(unit) + "%", '\\'));
            predicates.add(cb.equal(propertyRecord.get(Property_.unit), cb.any(unitQuery)));
        }
        if (dataType != null) {
            Subquery<DataType> enumQuery = cq.subquery(DataType.class);
            Root<DataType> enumRecord = enumQuery.from(DataType.class);
            enumQuery.select(enumRecord);
            enumQuery.where(cb.like(enumRecord.get(DataType_.name), "%" + escapeDbString(dataType) + "%", '\\'));
            predicates.add(cb.equal(propertyRecord.get(Property_.dataType), cb.any(enumQuery)));
        }

        return predicates.toArray(new Predicate[] {});
    }
}
