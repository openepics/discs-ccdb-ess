package org.openepics.discs.conf.auditlog;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import com.google.common.collect.Sets;

/**
 * Serializes DataType contents for auditing.
 *
 * @author mpavleski
 *
 */
public class DataTypeEntityLogger implements EntityLogger {

    @Override
    public Class getType() {
        return DataType.class;
    }

    @Override
    public AuditRecord auditEntry(Object value, EntityTypeOperation operation, String user) {
        DataType dt = (DataType) value;

        return (new AuditLogUtil(dt).
            removeTopProperties(Sets.newHashSet(
                    "id", "modifiedAt", "modifiedBy", "version", "name")).
                    auditEntry(operation, EntityType.DATA_TYPE, dt.getName(), dt.getId(), user));
    }
}
