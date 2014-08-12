package org.openepics.discs.conf.auditlog;

import org.codehaus.jackson.map.ObjectMapper;
import org.openepics.discs.conf.ent.DataType;

import com.google.common.collect.Sets;

/**
 * Serializes DataType contents for auditing.
 * 
 * @author mpavleski
 *
 */
public class DataTypeEntityLogger implements EntityLogger {
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public Class getType() {
        return DataType.class;
    }

    @Override
    public String serializeEntity(Object value) {
        DataType dt = (DataType) value;
        
        return (new AuditLogUtil(dt).
            removeTopProperties(Sets.newHashSet(
                    "id", "modifiedAt", "modifiedBy", "version", "name")).
            serialize() );
    }
}
