package org.openepics.discs.conf.auditlog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * Use this class to serialize Object contents to JSON for the Entry string in the AuditRecord
 * The class is EJB Singleton, initialized at startup and reused at Injection points.
 *
 * @author mpavleski
 *
 */
@Singleton
@Startup
public class AuditLogEntryCreator {
    private Map<Class<?>, EntityLogger> loggers = new ConcurrentHashMap<Class<?>, EntityLogger>();

    public AuditLogEntryCreator(){}

    /**
     * Constructs the item. Expects injected iterator of all EntityLogger implementations
     *
     */
    @Inject
    public AuditLogEntryCreator(
            @Any
            Instance<EntityLogger> allLoggers)
    {
        for (EntityLogger logger : allLoggers)
        {
            loggers.put(logger.getType(), logger);
        }
    }

    /**
     * Serialize a supported entity to a JSON String and creates {@link AuditRecord}
     *
     * @param entity Entity to be serialized. Supported Unit, DataType, Property etc
     * @return {@link AuditRecord} for the entities that are supported / implemented.
     */
    public AuditRecord auditRecord(Object entity, EntityTypeOperation operation, String user) {
        // Resolve the EntityLogger by class and use it to serialize to String
        EntityLogger logger = loggers.get(entity.getClass());
        if (logger==null) return null;

        return logger.auditEntry(entity, operation, user);
    }
}
