package org.openepics.discs.conf.auditlog;

import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/*
 * @author mpavleski
 * 
 * 
 * Helper class that is used to serialize to JSON removing unnecessary properties dynamically
 * 
 */
public class AuditLogUtil {
    
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode node = null;
    
    
    /**
     * Constricts the heper class
     * 
     * @param entity an Entity object to be serialized to JSon
     */
    public AuditLogUtil(Object entity) {
        node = mapper.valueToTree(entity);
    }
    
    
    /**
     * Given a set of property names, those will be removed from the serialized output
     * 
     * @param propertyNames
     * @return This class, with updated state (removed properties)
     */
    public AuditLogUtil removeTopProperties(Set<String> propertyNames) {
        node.remove(propertyNames);
        return this;
    }
    
    /**
     * Adds simple property
     */
    public AuditLogUtil addStringProperty(String key, String value) {
        node.put(key, value);
        return this;
    }
    
    /**
     * Dumps the internal object with optionally removed properties to a String
     * 
     * @return Serialized JSON string
     */
    public String serialize() {
        try {
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            // ToDo: Add our Runtime Exception type here
            throw new RuntimeException("AuditLogUtil serialization to JSon failed", e);
        }
    }
}
