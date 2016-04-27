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

import java.util.Arrays;
import java.util.List;

import org.openepics.discs.ccdb.model.AuditRecord;
import org.openepics.discs.ccdb.model.EntityType;
import org.openepics.discs.ccdb.model.EntityTypeOperation;
import org.openepics.discs.ccdb.model.Property;

import com.google.common.collect.ImmutableList;

/**
 * {@link AuditRecord} maker for {@link Property}
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
public class PropertyEntityLogger implements EntityLogger<Property> {

    @Override
    public Class<Property> getType() {
        return Property.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation) {
        final Property prop = (Property) value;

        return ImmutableList.of(new AuditLogUtil(prop)
                                .removeTopProperties(Arrays.asList("id", "modifiedAt", "modifiedBy",
                                        "version", "name", "dataType", "unit", "associationAll", "associationNone"))
                                .addStringProperty("dataType",
                                        prop.getDataType() != null ? prop.getDataType().getName() : null)
                                .addStringProperty("unit", prop.getUnit() != null ? prop.getUnit().getName() : null)
                                .auditEntry(operation, EntityType.PROPERTY, prop.getName(), prop.getId()));
    }
}
