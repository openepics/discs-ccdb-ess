package org.openepics.discs.conf.auditlog;

import org.codehaus.jackson.map.ObjectMapper;
import org.openepics.discs.conf.ent.Property;

import com.google.common.collect.Sets;

/**
 * Serializes Property contents for auditing.
 * 
 * @author mpavleski
 *
 */
public class PropertyEntityLogger implements EntityLogger {
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public Class getType() {
        return Property.class;
    }

    @Override
    public String serializeEntity(Object value) {
        Property prop = (Property) value;
        
        return (new AuditLogUtil(prop).
            removeTopProperties(Sets.newHashSet(
                    "id", "modifiedAt", "modifiedBy", "version", "name", "dataType", "unit")).
            addStringProperty("dataType", (prop.getDataType() != null ? prop.getDataType().getName() : null)).
            addStringProperty("unit", (prop.getUnit() != null ? prop.getUnit().getName() : null)).
            serialize() );
    }
}
