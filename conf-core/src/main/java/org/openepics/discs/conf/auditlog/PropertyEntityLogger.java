package org.openepics.discs.conf.auditlog;

import java.util.List;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Serializes Property contents for auditing.
 *
 * @author mpavleski
 *
 */
public class PropertyEntityLogger implements EntityLogger {

    @Override
    public Class getType() {
        return Property.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation, String user) {
        final Property prop = (Property) value;

        return ImmutableList.of((new AuditLogUtil(prop).
            removeTopProperties(Sets.newHashSet(
                    "id", "modifiedAt", "modifiedBy", "version", "name", "dataType", "unit")).
            addStringProperty("dataType", (prop.getDataType() != null ? prop.getDataType().getName() : null)).
            addStringProperty("unit", (prop.getUnit() != null ? prop.getUnit().getName() : null)).
            auditEntry(operation, EntityType.PROPERTY, prop.getName(), prop.getId(), user)));
    }
}
