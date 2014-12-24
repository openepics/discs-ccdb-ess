/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.auditlog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.util.CCDBRuntimeException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Helper class that is used to serialize to JSON removing unnecessary properties dynamically
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public class AuditLogUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // Configure Jackson to always order entries see, http://stackoverflow.com/a/18993481
        MAPPER.setSerializationInclusion(Include.NON_EMPTY);
        MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private ObjectNode node = null;

    /**
     * Constructs the helper class
     *
     * @param entity an Entity object to be serialized to JSon
     */
    public AuditLogUtil(Object entity) {
        node = MAPPER.valueToTree(entity);
    }


    /**
     * Given a set of property names, those will be removed from the serialized output
     *
     * @param fieldNames
     * @return This class, with updated state (removed properties)
     */
    public AuditLogUtil removeTopProperties(final Collection<String> fieldNames) {
        node.remove(fieldNames);
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
        // Only add non-null stuff
        if (value != null) {
            node.put(key, value);
        }
        return this;
    }

    /**
     * Adds a map of key-value pairs under the key tag of the JSON
     *
     * @param key the key
     * @param keyValuePairs map of pairs to be added
     * @return a reference to this instance of {@link AuditLogUtil}
     */
    public AuditLogUtil addArrayOfMappedProperties(String key, Map<String, ?> keyValuePairs) {
        // Don't add anything if empty
        if (keyValuePairs.isEmpty()) {
            return this;
        }

        final ArrayNode arrayNode = MAPPER.createArrayNode();

        for (Entry<String, ?> entry : keyValuePairs.entrySet()) {
            final ObjectNode arrayObjectNode = MAPPER.createObjectNode();
            arrayObjectNode.put(entry.getKey(), entry.getValue().toString());
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
        // Don't add anything if empty
        if (arrayValues.isEmpty()) {
            return this;
        }

        final ArrayNode arrayNode = MAPPER.createArrayNode();
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
            return MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            throw new CCDBRuntimeException("AuditLogUtil serialization to JSon failed", e);
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
