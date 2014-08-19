package org.openepics.discs.conf.auditlog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * {@link AuditRecord} maker for {@link ComponentType}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class ComponentTypeEntityLogger implements EntityLogger {

    @Override
    public Class getType() {
        return ComponentType.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation, String user) {
        final ComponentType compType = (ComponentType) value;

        final Map<String, String> propertiesMap = new HashMap<>();
        if (compType.getComptypePropertyList() != null) {
            for (ComptypePropertyValue propValue : compType.getComptypePropertyList()) {
                propertiesMap.put(propValue.getProperty().getName(), propValue.getPropValue());
            }
        }

        final Map<String, String> artifactsMap = new HashMap<>();
        if (compType.getComptypeArtifactList() != null) {
            for (ComptypeArtifact artifact : compType.getComptypeArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        return ImmutableList.of((new AuditLogUtil(compType).
            removeTopProperties(Sets.newHashSet("id", "modifiedAt", "modifiedBy", "version", "name")).
            addArrayOfMappedProperties("comptypePropertyList", propertiesMap).
            addArrayOfMappedProperties("comptypeArtifactList", artifactsMap).
            auditEntry(operation, EntityType.COMPONENT_TYPE, compType.getName(), compType.getId(), user)));
    }
}
