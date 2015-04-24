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
 * @author Andraž Požar &lt;andraz.pozar@cosylab.com&gt;
 *
 */
public class ComponentTypeEntityLogger implements EntityLogger<ComponentType> {

    @Override
    public Class<ComponentType> getType() {
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

        return ImmutableList.of(new AuditLogUtil(compType)
                                .removeTopProperties(Arrays.asList("id", "modifiedAt", "modifiedBy", "version", "name"))
                                .addArrayOfMappedProperties("comptypePropertyList", propertiesMap)
                                .addArrayOfMappedProperties("comptypeArtifactList", artifactsMap)
                                .addArrayOfProperties("tagsList", EntityLoggerUtil.getTagNamesFromTagsSet(compType.getTags()))
                                .auditEntry(operation, EntityType.COMPONENT_TYPE, compType.getName(),
                                        compType.getId()));
    }
}
