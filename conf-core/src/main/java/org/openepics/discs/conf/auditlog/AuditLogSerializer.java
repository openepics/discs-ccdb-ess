package org.openepics.discs.conf.auditlog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Use this class to serialize Object contents to JSON for the Entry string in the AuditRecord
 * The class is EJB Singleton, initialized at startup and reused at Injection points.
 * 
 * @author mpavleski
 *
 */
@Singleton
@Startup
public class AuditLogSerializer {
    private Map<Class<?>, EntityLogger> loggers = new ConcurrentHashMap<Class<?>, EntityLogger>();
    
    /**
     * Constructs the item. Expects injected iterator of all EntityLogger implementations
     * 
     */
    @Inject
    public AuditLogSerializer(
            @Any
            Instance<EntityLogger> allLoggers) 
    {
        for (EntityLogger logger : allLoggers) 
        {
            loggers.put(logger.getType(), logger);
        }        
    }
    
    /**
     * Serialize a supported entity to a JSON String
     * 
     * @param entity Entity to be serialized. Supported Unit, DataType, Property etc
     * @return JSON String or null if serialization of the entity is not supported / implemented.
     */
    public String serialize(Object entity) {        
        // Resolve the EntityLogger by class and use it to serialize to String
        EntityLogger logger = loggers.get(entity.getClass());
        if (logger==null) return null;

        return logger.serializeEntity(entity);
    }
}
