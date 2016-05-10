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
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ent.Unit_;
import org.openepics.discs.conf.ent.fields.UnitFields;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;
import org.openepics.discs.conf.util.SortOrder;
import org.openepics.discs.conf.util.Utility;

import com.google.common.collect.Lists;

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
        return em.createNamedQuery("Property.findByUnit", Property.class).setParameter("unit", unit).
                setMaxResults(maxResults).getResultList();
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

    /**
     * If the name does not exist, the {@link NoResultException} will get thrown.
     *
     * @param name the name MUST exist
     * @return the position of this entity if ordered b name
     */
    public long getNamedPosition(String name) {
        return em.createQuery("SELECT COUNT(*) FROM Unit u WHERE u.name <= :name", Long.class).
                setParameter("name", name).getSingleResult();
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
     * @param symbol the unit symbol
     * @return The required entities.
     */
    public List<Unit> findLazy(final int first, final int pageSize,
            final @Nullable UnitFields sortField, final @Nullable SortOrder sortOrder,
            final @Nullable String name, final @Nullable String description, final @Nullable String symbol) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Unit> cq = cb.createQuery(getEntityClass());
        final Root<Unit> auditRecord = cq.from(getEntityClass());

        addSortingOrder(sortField, sortOrder, cb, cq, auditRecord);

        final Predicate[] predicates = buildPredicateList(cb, auditRecord, name, description, symbol);
        cq.where(predicates);

        final TypedQuery<Unit> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    private void addSortingOrder(final UnitFields sortField, final SortOrder sortOrder, final CriteriaBuilder cb,
            final CriteriaQuery<Unit> cq, final Root<Unit> unitRecord) {
        if ((sortField != null) && (sortOrder != null) && (sortOrder != SortOrder.UNSORTED)) {
            switch (sortField) {
            case NAME:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Unit_.name))
                                : cb.desc(unitRecord.get(Unit_.name)));
            case DESCRIPTION:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Unit_.description))
                                : cb.desc(unitRecord.get(Unit_.description)));
                break;
            case SYMBOL:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(unitRecord.get(Unit_.symbol))
                                : cb.desc(unitRecord.get(Unit_.symbol)));
                break;
            default:
                break;
            }
        }
    }

    private Predicate[] buildPredicateList(final CriteriaBuilder cb, final Root<Unit> unitRecord,
            final @Nullable String name, final @Nullable String description, final @Nullable String symbol) {
        final List<Predicate> predicates = Lists.newArrayList();

        if (name != null) {
            predicates.add(cb.like(unitRecord.get(Unit_.name), "%" + escapeDbString(name) + "%", '\\'));
        }
        if (description != null) {
            predicates.add(cb.like(unitRecord.get(Unit_.description), "%" + escapeDbString(description) + "%", '\\'));
        }
        if (symbol != null) {
            predicates.add(cb.like(unitRecord.get(Unit_.symbol), "%" + escapeDbString(symbol) + "%", '\\'));
        }

        return predicates.toArray(new Predicate[] {});
    }
}
