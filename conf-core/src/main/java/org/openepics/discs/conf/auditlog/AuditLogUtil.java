package org.openepics.discs.conf.auditlog;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * @author mpavleski
 *
 *
 * Helper class that is used to serialize to JSON removing unnecessary properties dynamically
 *
 */
public class AuditLogUtil {

    private static final ObjectMapper mapper = new ObjectMapper();


    static {
        // Configure Jackson to always order entries see, http://stackoverflow.com/a/18993481
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(Include.NON_NULL);

    }

    private ObjectNode node = null;

    /**
     * Constructs the helper class
     *
     * @param entity an Entity object to be serialized to JSon
     */
    public AuditLogUtil(Object entity) {
        //mapper.configure(Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        node = mapper.valueToTree(entity);
    }


    /**
     * Given a set of property names, those will be removed from the serialized output
     *
     * ToDo: Check if it exists and throw exception if it doesn't. Exception should not happen,
     * if it happens something is changed and the audit logger should be updated.
     *
     * @param propertyNames
     * @return This class, with updated state (removed properties)
     */
    public AuditLogUtil removeTopProperties(Collection<String> propertyNames) {
        node.remove(propertyNames);
        return this;
    }

    /**
     * Adds a String property to the JSON
     *
     * @param key key-name
     * @param value value-name
     * @return a reference to this instance of {@link AuditLogUtil}
     */
    public AuditLogUtil addStringProperty(String key, String value) {
        node.put(key, value);
        return this;
    }

    /**
     * Adds a map of key-value pairs under the key tag of the JSON
     *
     * @param key the key
     * @param keyValuePairs map of pairs to be added
     * @return a reference to this instance of {@link AuditLogUtil}
     */
    public AuditLogUtil addArrayOfMappedProperties(String key, Map<String, String> keyValuePairs) {
        final ArrayNode arrayNode = mapper.createArrayNode();
        final Iterator<String> it = keyValuePairs.keySet().iterator();
        while (it.hasNext()) {
            final ObjectNode arrayObjectNode = mapper.createObjectNode();
            final String keyValue = it.next();
            arrayObjectNode.put(keyValue, keyValuePairs.get(keyValue));
            arrayNode.add(arrayObjectNode);
        }
        node.set(key, arrayNode);
        return this;
    }

    /**
     * Adds an array of {@link String}s to the JSON under a key
     *
     * @param key the key
     * @param arrayValues they list of values
     * @return a reference to this instance of {@link AuditLogUtil}
     */
    public AuditLogUtil addArrayOfProperties(String key, List<String> arrayValues) {
        final ArrayNode arrayNode = mapper.createArrayNode();
        for (String value : arrayValues) {
           arrayNode.add(value);
        }
        node.set(key, arrayNode);
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

    /**
     * Creates audit record
     *
     * @param oper {@link EntityTypeOperation} that was performed
     * @param entityType {@link EntityType} on which operation was performed
     * @param key Natural key of the entity
     * @param id Database id of the entity
     *
     * @return {@link AuditRecord}
     */
    public AuditRecord auditEntry(EntityTypeOperation oper, EntityType entityType, String key, Long id) {
        final String serialized = serialize();
        final AuditRecord arec = new AuditRecord(oper, serialized, id);
        arec.setEntityType(entityType);
        arec.setEntityKey(key);
        return arec;
    }
}
