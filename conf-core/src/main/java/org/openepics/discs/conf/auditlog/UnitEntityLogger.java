package org.openepics.discs.conf.auditlog;

import org.codehaus.jackson.map.ObjectMapper;
import org.openepics.discs.conf.ent.Unit;

import com.google.common.collect.Sets;

/**
 * Serializes Unit contents for auditing.
 * 
 * @author mpavleski
 *
 */
public class UnitEntityLogger implements EntityLogger {
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public Class getType() {
        return Unit.class;
    }

    @Override
    public String serializeEntity(Object value) {
        return (new AuditLogUtil((Unit) value)).
            removeTopProperties(Sets.newHashSet(
                    "id", "modifiedAt", "modifiedBy", "version", "name")).
            serialize();
    }
}
