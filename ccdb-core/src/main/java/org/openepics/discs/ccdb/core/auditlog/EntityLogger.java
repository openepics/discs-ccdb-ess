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

import java.util.List;

import org.openepics.discs.ccdb.model.AuditRecord;
import org.openepics.discs.ccdb.model.EntityTypeOperation;


/**
 * Creates list of {@link AuditRecord}s for entity
 *
 * @param <T> {@link Class} of the entity for which the logger is implemented
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
public interface EntityLogger<T> {

    /**
     * Returns the type of the handled logger
     * @return the type of the entity being logged by this logger
     */
    public Class<T> getType();

    /**
     * Creates audit log(s) for given entity
     *
     * @param entity Entity for which {@link AuditRecord} should be created
     * @param operation The {@link EntityTypeOperation} being logged for the entity
     * @return List of {@link AuditRecord}s for input entity
     */
    public List<AuditRecord> auditEntries(Object entity, EntityTypeOperation operation);
}
