package org.openepics.discs.conf.auditlog;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import com.google.common.collect.ImmutableList;

/**
 * {@link AuditRecord} maker for {@link ComponentType}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class ComponentTypeEntityLogger implements EntityLogger {

    @Override
    public Class<?> getType() {
        return ComponentType.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation) {
        final ComponentType compType = (ComponentType) value;

        final Map<String, String> propertiesMap = new TreeMap<>();
        if (compType.getComptypePropertyList() != null) {
            for (ComptypePropertyValue propValue : compType.getComptypePropertyList()) {
                final String entryValue = propValue.getPropValue() == null ? null : propValue.getPropValue().auditLogString(100, 50);
                propertiesMap.put(propValue.getProperty().getName(), entryValue);
            }
        }

        final Map<String, String> artifactsMap = new TreeMap<>();
        if (compType.getComptypeArtifactList() != null) {
            for (ComptypeArtifact artifact : compType.getComptypeArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }


        return ImmutableList.of((new AuditLogUtil(compType).
            removeTopProperties(Arrays.asList("id", "modifiedAt", "modifiedBy", "version", "name")).
            addArrayOfMappedProperties("comptypePropertyList", propertiesMap).
            addArrayOfMappedProperties("comptypeArtifactList", artifactsMap).
            auditEntry(operation, EntityType.COMPONENT_TYPE, compType.getName(), compType.getId())));
    }
}
