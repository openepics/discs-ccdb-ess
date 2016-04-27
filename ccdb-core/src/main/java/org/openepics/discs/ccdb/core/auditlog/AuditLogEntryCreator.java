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
package org.openepics.discs.ccdb.core.auditlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.openepics.discs.ccdb.model.AuditRecord;
import org.openepics.discs.ccdb.model.EntityTypeOperation;

/**
 * Use this class to serialize Object contents to JSON for the Entry string in the AuditRecord
 * The class is EJB Singleton, initialized at startup and reused at Injection points.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Singleton
@Startup
public class AuditLogEntryCreator {
    private Map<Class<?>, EntityLogger<?>> loggers = new ConcurrentHashMap<Class<?>, EntityLogger<?>>();

    public AuditLogEntryCreator() {}

    /**
     * Constructs the item. Expects injected iterator of all EntityLogger implementations
     *
     * @param allLoggers CDI will inject all logger types in this constructor parameter
     */
    @Inject
    public AuditLogEntryCreator(@Any Instance<EntityLogger<?>> allLoggers) {
        for (EntityLogger<?> logger : allLoggers) {
            loggers.put(logger.getType(), logger);
        }
    }

    /**
     * Serialize a supported entity to a JSON String and creates {@link AuditRecord}
     *
     * @param entity Entity to be serialized. Supported Unit, DataType, Property etc
     * @param operation The {@link EntityTypeOperation} for which this audit record is being created
     * @return List of {@link AuditRecord}s for the entities that are supported / implemented.
     */
    public List<AuditRecord> auditRecords(Object entity, EntityTypeOperation operation) {
        // Resolve the EntityLogger by class and use it to serialize to String
        final EntityLogger<?> logger = loggers.get(entity.getClass());
        if (logger == null) {
            return new ArrayList<>();
        }

        return logger.auditEntries(entity, operation);
    }
}
