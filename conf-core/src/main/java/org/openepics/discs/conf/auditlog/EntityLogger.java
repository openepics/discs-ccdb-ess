package org.openepics.discs.conf.auditlog;

import java.util.List;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityTypeOperation;


/**
 * Creates list of {@link AuditRecord}s for entity
 *
 * @author mpavleski
 *
 */
public interface EntityLogger {

    /*
     * Returns the type of the handled logger
     */
    public Class getType();

    /**
     * Creates audit logs for given entity
     *
     * @param entity Entity for which {@link AuditRecord} should be created
     * @return List of {@link AuditRecord}s for input entity
     */
    public List<AuditRecord> auditEntries(Object entity, EntityTypeOperation operation, String user);

}
