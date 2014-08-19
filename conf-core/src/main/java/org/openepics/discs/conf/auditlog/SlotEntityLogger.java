/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.auditlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class SlotEntityLogger implements EntityLogger {

    @Override
    public Class getType() {
        return Slot.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object entity, EntityTypeOperation operation, String user) {
        final Slot slot = (Slot) entity;

        final Map<String, String> propertiesMap = new HashMap<>();
        if (slot.getSlotPropertyList() != null) {
            for (SlotPropertyValue propValue : slot.getSlotPropertyList()) {
                propertiesMap.put(propValue.getProperty().getName(), propValue.getPropValue());
            }
        }

        final Map<String, String> artifactsMap = new HashMap<>();
        if (slot.getSlotArtifactList() != null) {
            for (SlotArtifact artifact : slot.getSlotArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        final Map<String, String> childrenMap = new HashMap<>();
        if (slot.getChildrenSlotsPairList() != null) {
            for (SlotPair slotPair : slot.getChildrenSlotsPairList()) {
                childrenMap.put(slotPair.getChildSlot().getName(), slotPair.getSlotRelation().getName().toString());
            }
        }

        final Map<String, String> parentsMap = new HashMap<>();
        if (slot.getParentSlotsPairList() != null) {
            for (SlotPair slotPair : slot.getParentSlotsPairList()) {
                parentsMap.put(slotPair.getParentSlot().getName(), slotPair.getSlotRelation().getName().toString());
            }
        }

        final List<String> installationRecordsList = new ArrayList<>();
        if (slot.getInstallationRecordList() != null) {
            for (InstallationRecord installationRecord : slot.getInstallationRecordList()) {
                installationRecordsList.add(installationRecord.getDevice().getSerialNumber());
            }
        }

        return ImmutableList.of((new AuditLogUtil(slot).
                removeTopProperties(Sets.newHashSet("id", "modifiedAt", "modifiedBy", "version", "name", "componentType")).
                addStringProperty("componentType", slot.getComponentType().getName()).
                addArrayOfMappedProperties("slotPropertyList", propertiesMap).
                addArrayOfMappedProperties("slotArtifactList", artifactsMap).
                addArrayOfMappedProperties("childrenSlots", childrenMap).
                addArrayOfMappedProperties("parentSlots", parentsMap).
                addArrayOfProperties("installationRecordList", installationRecordsList).
                auditEntry(operation, EntityType.SLOT, slot.getName(), slot.getId(), user)));
    }

}
