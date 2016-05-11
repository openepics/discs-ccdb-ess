/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.conf.ui.lazymodels;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openepics.discs.conf.ejb.AuditRecordEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.fields.AuditRecordFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.primefaces.model.SortOrder;

public class AuditLazyModel extends CCDBLazyModel<AuditRecord> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuditLazyModel.class.getCanonicalName());

    private static final String LOG_TIME_FORMATTED = "logTimeFormatted";
    private static final String USER = "user";
    private static final String OPER = "oper";
    private static final String ENTITY_KEY = "entityKey";
    private static final String ENTITY_TYPE = "entityType";
    private static final String ENTITY_ID = "entityId";
    private static final String ENTRY = "entry";

    private final AuditRecordEJB auditRecordEJB;

    public AuditLazyModel(AuditRecordEJB auditRecordEJB) {
        super(auditRecordEJB);
        this.auditRecordEJB = auditRecordEJB;
    }

    @Override
    public List<AuditRecord> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINEST, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINEST, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.FINER, "filter[" + filterKey + "] = " + filters.get(filterKey).toString());
        }

        setLatestLoadData(sortField, sortOrder, filters);

        final Date logDateTime = parseLogDateTime(filters);
        final String user = filters.containsKey(USER) ? filters.get(USER).toString() : null;
        final String entityName = filters.containsKey(ENTITY_KEY) ? filters.get(ENTITY_KEY).toString() : null;
        final String entry = filters.containsKey(ENTRY) ? filters.get(ENTRY).toString() : null;
        final Long entityId = filters.containsKey(ENTITY_ID) ?
                                    UiUtility.processUILong(filters.get(ENTITY_ID).toString()) : null;
        final EntityTypeOperation oper = filters.containsKey(OPER) ?
                UiUtility.parseIntoEnum(filters.get(OPER).toString(), EntityTypeOperation.class) : null;
        final EntityType entityType = filters.containsKey(ENTITY_TYPE) ?
                UiUtility.parseIntoEnum(filters.get(ENTITY_TYPE).toString(), EntityType.class) : null;

        final List<AuditRecord> results = auditRecordEJB.findLazy(first, pageSize,
                selectSortField(sortField), UiUtility.translateToCCDBSortOrder(sortOrder),
                logDateTime, user, oper, entityName, entityType, entityId, entry);

        setEmpty(first, results);

        return results;
    }

    private Date parseLogDateTime(final Map<String, Object> filters) {
        if (filters.containsKey(LOG_TIME_FORMATTED)) {
            final LocalDateTime filter = UiUtility.processUIDateTime(filters.get(LOG_TIME_FORMATTED).toString());
            return Date.from(filter.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    @Override
    public Object getRowKey(AuditRecord object) {
        return object.getId();
    }

    @Override
    public AuditRecord getRowData(String rowKey) {
        return auditRecordEJB.findById(Long.parseLong(rowKey));
    }

    private AuditRecordFields selectSortField(final String sortField) {
        if (sortField == null) return null;

        switch (sortField) {
        case LOG_TIME_FORMATTED:
            return AuditRecordFields.LOG_TIME;
        case USER:
            return AuditRecordFields.USER;
        case OPER:
            return AuditRecordFields.OPER;
        case ENTITY_ID:
            return AuditRecordFields.ENTITY_ID;
        case ENTITY_KEY:
            return AuditRecordFields.ENTITY_KEY;
        case ENTITY_TYPE:
            return AuditRecordFields.ENTITY_TYPE;
        case ENTRY:
            return AuditRecordFields.ENTRY;
        default:
            return null;
        }
    }
}
