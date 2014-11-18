package org.openepics.discs.conf.auditlog;

import java.util.Arrays;
import java.util.List;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;

import com.google.common.collect.ImmutableList;

/**
 * {@link AuditRecord} maker for {@link Property}
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
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
