package org.openepics.discs.conf.auditlog;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/*
 * @author mpavleski
 *
 *
 * Helper class that is used to serialize to JSON removing unnecessary properties dynamically
 *
 */
public class AuditLogUtil {

    final private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode node = null;


    /**
     * Constructs the helper class
     *
     * @param entity an Entity object to be serialized to JSon
     */
    public AuditLogUtil(Object entity) {
        mapper.configure(Feature.WRITE_DATES_AS_TIMESTAMPS, false);
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
     * Adds list of values
     */
    public AuditLogUtil addArrayOfProperties(String key, Map<String, String> arrayValuePairs) {
        final ArrayNode arrayNode = mapper.createArrayNode();
        final Iterator<String> it = arrayValuePairs.keySet().iterator();
        while (it.hasNext()) {
            final ObjectNode arrayObjectNode = mapper.createObjectNode();
            final String keyValue = it.next();
            arrayObjectNode.put(keyValue, arrayValuePairs.get(keyValue));
            arrayNode.add(arrayObjectNode);
        }
        node.put(key, arrayNode);
        return this;
    }

    /**
     * Dumps the internal object with optionally removed properties to a String
     *
     * @return Serialized JSON string
     */
    private String serialize() {
        try {
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            // ToDo: Add our Runtime Exception type here
            throw new RuntimeException("AuditLogUtil serialization to JSon failed", e);
        }
    }

    public AuditRecord auditEntry(EntityTypeOperation oper, EntityType entityType, String key, Long id, String user) {
        final String serialized = serialize();
        final AuditRecord arec = new AuditRecord(oper, user, serialized, id);
        arec.setEntityType(entityType);
        arec.setEntityKey(key);
        return arec;
    }
}
