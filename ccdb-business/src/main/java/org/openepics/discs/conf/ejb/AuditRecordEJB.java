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
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.AuditRecord_;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.fields.AuditRecordFields;
import org.openepics.discs.conf.util.SortOrder;

import com.google.common.collect.Lists;

/**
 * DAO Service for accessing {@link AuditRecord}s
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
public class AuditRecordEJB extends ReadOnlyDAO<AuditRecord> {
    /**
     * Queries for list of {@link AuditRecord}s by Entity Id and Entity Type
     *
     * @param entityId the id of the audited entity
     * @param entityType the {@link EntityType} of the audited entity
     * @return the list of {@link AuditRecord}s
     */
    public List<AuditRecord> findByEntityIdAndType(Long entityId, EntityType entityType) {
        final List<AuditRecord> auditRecords = em.createNamedQuery("AuditRecord.findByEntityIdAndType",
                AuditRecord.class)
                .setParameter("entityId", entityId)
                .setParameter("entityType", entityType).getResultList();

        return auditRecords == null ? new ArrayList<AuditRecord>() : auditRecords;
    }

    @Override
    public AuditRecord findByName(String name) {
        throw new UnsupportedOperationException("findByName method not aplicable to AuditRecord class");
    }

    @Override
    protected Class<AuditRecord> getEntityClass() {
        return AuditRecord.class;
    }

    /** @return A list of all {@link AuditRecord}s in the database ordered by creation date */
    public List<AuditRecord> findAllOrdered() {
        final List<AuditRecord> auditRecords = em.createNamedQuery("AuditRecord.findAll", AuditRecord.class)
                .getResultList();

        return auditRecords == null ? new ArrayList<AuditRecord>() : auditRecords;
    }

    public List<AuditRecord> findLazy(final int first, final int pageSize, final @Nullable AuditRecordFields sortField,
            final @Nullable SortOrder sortOrder, final @Nullable Date logTime, final @Nullable String user,
            final @Nullable EntityTypeOperation oper, final @Nullable String entityName,
            final @Nullable EntityType entityType, final @Nullable Long entityId, final @Nullable String entry) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<AuditRecord> cq = cb.createQuery(AuditRecord.class);
        final Root<AuditRecord> auditRecord = cq.from(AuditRecord.class);

        addSortingOrder(sortField, sortOrder, cb, cq, auditRecord);

        final Predicate[] predicates = buildPredicateList(cb, auditRecord, logTime, user, oper, entityName, entityType,
                entityId, entry);
        cq.where(predicates);

        final TypedQuery<AuditRecord> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    private void addSortingOrder(final AuditRecordFields sortField, final SortOrder sortOrder, final CriteriaBuilder cb,
            final CriteriaQuery<AuditRecord> cq, final Root<AuditRecord> auditRecord) {
        if ((sortField != null) && (sortOrder != null) && (sortOrder != SortOrder.UNSORTED)) {
            switch (sortField) {
            case LOG_TIME:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.logTime))
                                : cb.desc(auditRecord.get(AuditRecord_.logTime)));
                break;
            case USER:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.user))
                                : cb.desc(auditRecord.get(AuditRecord_.user)));
                break;
            case OPER:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.oper))
                                : cb.desc(auditRecord.get(AuditRecord_.oper)));
                break;
            case ENTITY_ID:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.entityId))
                                : cb.desc(auditRecord.get(AuditRecord_.entityId)));
                break;
            case ENTITY_TYPE:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.entityType))
                                : cb.desc(auditRecord.get(AuditRecord_.entityType)));
                break;
            case ENTITY_KEY:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.entityKey))
                                : cb.desc(auditRecord.get(AuditRecord_.entityKey)));
                break;
            case ENTRY:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(auditRecord.get(AuditRecord_.entry))
                                : cb.desc(auditRecord.get(AuditRecord_.entry)));
                break;
            default:
                break;
            }
        }
    }

    private Predicate[] buildPredicateList(final CriteriaBuilder cb, final Root<AuditRecord> auditRecord,
            final Date logTime, final String user, final EntityTypeOperation oper,
            final String entityName, final EntityType entityType, final Long entityId, final String entry) {
        final List<Predicate> predicates = Lists.newArrayList();

        if (logTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(auditRecord.get(AuditRecord_.logTime), logTime));
        }
        if (user != null) {
            predicates.add(cb.like(auditRecord.get(AuditRecord_.user), "%" + escapeDbString(user) + "%", '\\'));
        }
        if (oper != null) {
            predicates.add(cb.equal(auditRecord.get(AuditRecord_.oper), oper));
        }
        if (entityName != null) {
            predicates.add(cb.like(auditRecord.get(AuditRecord_.entityKey), "%" + escapeDbString(entityName) + "%", '\\'));
        }
        if (entityType != null) {
            predicates.add(cb.equal(auditRecord.get(AuditRecord_.entityType), entityType));
        }
        if (entityId != null) {
            predicates.add(cb.equal(auditRecord.get(AuditRecord_.entityId), entityId));
        }
        if (entry != null) {
            predicates.add(cb.like(auditRecord.get(AuditRecord_.entry), "%" + escapeDbString(entry) + "%", '\\'));
        }

        return predicates.toArray(new Predicate[] {});
    }

    private String escapeDbString(final String dbString) {
        return dbString.replace("%", "\\%").replace("_", "\\_");
    }
}
