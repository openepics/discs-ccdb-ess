package org.openepics.discs.conf.auditlog;

import java.util.List;
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
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
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
     * @param allLoggers CDI will inject all logger types in this constructor parameter
     */
    @Inject
    public AuditLogEntryCreator(@Any Instance<EntityLogger> allLoggers) {
        for (EntityLogger logger : allLoggers) {
            loggers.put(logger.getType(), logger);
        }
    }

    /**
     * Serialize a supported entity to a JSON String and creates {@link AuditRecord}
     *
     * @param entity Entity to be serialized. Supported Unit, DataType, Property etc
     * @param operation The {@link EntityTypeOperation} for which this audit record is being created
     * @return List of {@link AuditRecord}s for the entities that are supported / implemented.
     */
    public List<AuditRecord> auditRecords(Object entity, EntityTypeOperation operation) {
        // Resolve the EntityLogger by class and use it to serialize to String
        final EntityLogger logger = loggers.get(entity.getClass());
        if (logger == null) {
            return null;
        }

        return logger.auditEntries(entity, operation);
    }
}
