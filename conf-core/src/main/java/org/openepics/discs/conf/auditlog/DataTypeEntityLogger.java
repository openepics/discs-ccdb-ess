package org.openepics.discs.conf.auditlog;

import java.util.Arrays;
import java.util.List;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import com.google.common.collect.ImmutableList;

/**
 * Serializes DataType contents for auditing.
 *
 * @author mpavleski
 *
 */
public class DataTypeEntityLogger implements EntityLogger {

    @Override
    public Class<?> getType() {
        return DataType.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation) {
        DataType dt = (DataType) value;

        return ImmutableList.of((new AuditLogUtil(dt).
            removeTopProperties(Arrays.asList(
                    "id", "modifiedAt", "modifiedBy", "version", "name")).
                    auditEntry(operation, EntityType.DATA_TYPE, dt.getName(), dt.getId())));
    }
}
