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

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;

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
}
