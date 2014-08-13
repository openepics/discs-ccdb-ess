package org.openepics.discs.conf.auditlog;

import java.util.HashMap;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import com.google.common.collect.Sets;

/**
 * Serializes {@link ComponentType} contents for auditing.
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
    public AuditRecord auditEntry(Object value, EntityTypeOperation operation, String user) {
        final ComponentType compType = (ComponentType) value;

        final HashMap<String, String> propertiesMap = new HashMap<>();
        if (compType.getComptypePropertyList() != null) {
            for (ComptypePropertyValue propValue : compType.getComptypePropertyList()) {
                propertiesMap.put(propValue.getProperty().getName(), propValue.getPropValue());
            }
        }

        final HashMap<String, String> artifactsMap = new HashMap<>();
        if (compType.getComptypeArtifactList() != null) {
            for (ComptypeArtifact artifact : compType.getComptypeArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        return (new AuditLogUtil(compType).
            removeTopProperties(Sets.newHashSet("id", "modifiedAt", "modifiedBy", "version", "name")).
            addArrayOfProperties("comptypePropertyList", propertiesMap).
            addArrayOfProperties("comptypeArtifactList", artifactsMap).
            auditEntry(operation, EntityType.COMPONENT_TYPE, compType.getName(), compType.getId(), user));
    }
}
