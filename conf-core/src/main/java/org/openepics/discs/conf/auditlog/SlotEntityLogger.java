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
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;

import com.google.common.collect.ImmutableList;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class SlotEntityLogger implements EntityLogger<Slot> {
    @Override
    public Class<Slot> getType() {
        return Slot.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object entity, EntityTypeOperation operation) {
        final Slot slot = (Slot) entity;

        final Map<String, String> propertiesMap = new TreeMap<>();
        if (slot.getSlotPropertyList() != null) {
            for (SlotPropertyValue propValue : slot.getSlotPropertyList()) {
                propertiesMap.put(propValue.getProperty().getName(), propValue.getPropValue());
            }
        }

        final Map<String, String> artifactsMap = new TreeMap<>();
        if (slot.getSlotArtifactList() != null) {
            for (SlotArtifact artifact : slot.getSlotArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        final Map<String, String> childrenMap = new TreeMap<>();
        if (slot.getChildrenSlotsPairList() != null) {
            for (SlotPair slotPair : slot.getChildrenSlotsPairList()) {
                childrenMap.put(slotPair.getChildSlot().getName(), slotPair.getSlotRelation().getName().toString());
            }
        }

        final Map<String, String> parentsMap = new TreeMap<>();
        if (slot.getParentSlotsPairList() != null) {
            for (SlotPair slotPair : slot.getParentSlotsPairList()) {
                parentsMap.put(slotPair.getParentSlot().getName(), slotPair.getSlotRelation().getName().toString());
            }
        }

        return ImmutableList.of(new AuditLogUtil(slot)
                                .removeTopProperties(Arrays.asList("id", "modifiedAt", "modifiedBy", "version",
                                        "name", "componentType"))
                                .addStringProperty("componentType", slot.getComponentType().getName())
                                .addArrayOfMappedProperties("slotPropertyList", propertiesMap)
                                .addArrayOfMappedProperties("slotArtifactList", artifactsMap)
                                .addArrayOfMappedProperties("childrenSlots", childrenMap)
                                .addArrayOfMappedProperties("parentSlots", parentsMap)
                                .auditEntry(operation, EntityType.SLOT, slot.getName(), slot.getId()));
    }
}
