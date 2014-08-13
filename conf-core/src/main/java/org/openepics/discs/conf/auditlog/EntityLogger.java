package org.openepics.discs.conf.auditlog;

import javax.inject.Inject;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ui.LoginManager;


/**
 * Converts entity to String dump, with standard format and stripped ID, Name etc.
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
     * Serializes entity value to a String
     *
     * @param value
     * @return
     */
    public AuditRecord auditEntry(Object value, EntityTypeOperation operation, String user);

}
