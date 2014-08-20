package org.openepics.discs.conf.auditlog;

import java.util.Arrays;
import java.util.List;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;

import com.google.common.collect.ImmutableList;

/**
 * {@link AuditRecord} maker for {@link Unit}
 *
 * @author mpavleski
 *
 */
public class UnitEntityLogger implements EntityLogger {

    @Override
    public Class<?> getType() {
        return Unit.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation) {
        final Unit unit = (Unit) value;
        return ImmutableList.of((new AuditLogUtil(value)).
            removeTopProperties(Arrays.asList(
                    "id", "modifiedAt", "modifiedBy", "version", "name")).
                    auditEntry(operation, EntityType.UNIT, unit.getName(), unit.getId()));
    }
}
