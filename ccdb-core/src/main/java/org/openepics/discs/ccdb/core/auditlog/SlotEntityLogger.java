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
package org.openepics.discs.ccdb.core.auditlog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openepics.discs.ccdb.model.AuditRecord;
import org.openepics.discs.ccdb.model.EntityType;
import org.openepics.discs.ccdb.model.EntityTypeOperation;
import org.openepics.discs.ccdb.model.InstallationRecord;
import org.openepics.discs.ccdb.model.Slot;
import org.openepics.discs.ccdb.model.SlotArtifact;
import org.openepics.discs.ccdb.model.SlotPair;
import org.openepics.discs.ccdb.model.SlotPropertyValue;
import org.openepics.discs.ccdb.core.util.Conversion;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
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

        /*
         * TODO This is a hack to correctly generate logs when creating new slot through GUI or data loaders.
         * This MUST be removed and DAO layer should be separated to business logic layer and DAO layer where DAO
         * layer ONLY communicates with the database and business logic layer controls the work flow and other
         * business rules related activities.
         */
        final List<AuditRecord> auditRecords = new ArrayList<>();
        auditRecords.addAll(createAuditRecords(slot, operation));
        if (operation == EntityTypeOperation.CREATE && slot.getPairsInWhichThisSlotIsAChildList().size() == 1) {
            // Create an audit record for the parent slot as well.
            // For parent slot adding a new child is an UPDATE operation.
            auditRecords.addAll(createAuditRecords(slot.getPairsInWhichThisSlotIsAChildList().get(0).getParentSlot(),
                                                        EntityTypeOperation.UPDATE));
        }
        return auditRecords;
    }

    private List<AuditRecord> createAuditRecords(Slot slot, EntityTypeOperation operation) {
        final Map<String, String> propertiesMap = new TreeMap<>();
        if (slot.getSlotPropertyList() != null) {
            for (SlotPropertyValue propValue : slot.getSlotPropertyList()) {
                final String entryValue = propValue.getPropValue() == null ? null
                                                : propValue.getPropValue().auditLogString(100, 50);
                propertiesMap.put(propValue.getProperty().getName(), entryValue);
            }
        }

        final Map<String, String> artifactsMap = new TreeMap<>();
        if (slot.getSlotArtifactList() != null) {
            for (SlotArtifact artifact : slot.getSlotArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        final Multimap<String, String> childrenMap = ArrayListMultimap.create();
        if (slot.getPairsInWhichThisSlotIsAParentList() != null) {
            for (SlotPair slotPair : slot.getPairsInWhichThisSlotIsAParentList()) {
                childrenMap.put(slotPair.getChildSlot().getName(), slotPair.getSlotRelation().getName().toString());
            }
        }

        final Multimap<String, String> parentsMap = ArrayListMultimap.create();
        if (slot.getPairsInWhichThisSlotIsAChildList() != null) {
            for (SlotPair slotPair : slot.getPairsInWhichThisSlotIsAChildList()) {
                parentsMap.put(slotPair.getParentSlot().getName(), slotPair.getSlotRelation().getIname());
            }
        }

        final Map<String, String> installationDeviceMap = new TreeMap<>();
        InstallationRecord lastInstallationRecord = null;
        for (InstallationRecord installationRecord : slot.getInstallationRecordList()) {
            if (lastInstallationRecord == null
                    || installationRecord.getModifiedAt().after(lastInstallationRecord.getModifiedAt())) {
                lastInstallationRecord = installationRecord;
            }
        }

        if (lastInstallationRecord != null) {
            final SimpleDateFormat timestampFormat = new SimpleDateFormat(Conversion.DATE_TIME_FORMAT);
            final String installationDeviceSerial = lastInstallationRecord.getDevice().getSerialNumber();
            installationDeviceMap.put("inventoryID", installationDeviceSerial);
            installationDeviceMap.put("installationDate",
                        timestampFormat.format(lastInstallationRecord.getInstallDate()));
            if (lastInstallationRecord.getUninstallDate() != null) {
                installationDeviceMap.put("uninstallationDate",
                        timestampFormat.format(lastInstallationRecord.getUninstallDate()));
            }
        }

        final AuditLogUtil logUtil = new AuditLogUtil(slot)
                        .removeTopProperties(Arrays.asList("id", "modifiedAt", "modifiedBy", "version",
                                "name", "componentType"))
                        .addStringProperty("componentType", slot.getComponentType().getName())
                        .addArrayOfMappedProperties("slotPropertyList", propertiesMap)
                        .addArrayOfMappedProperties("slotArtifactList", artifactsMap)
                        .addArrayOfMappedProperties("childrenSlots", childrenMap.asMap())
                        .addArrayOfMappedProperties("parentSlots", parentsMap.asMap())
                        .addArrayOfMappedProperties("installation", installationDeviceMap)
                        .addArrayOfProperties("tagsList", EntityLoggerUtil.getTagNamesFromTagsSet(slot.getTags()));

        return ImmutableList.of(logUtil.auditEntry(operation, EntityType.SLOT, slot.getName(), slot.getId()));
    }
}
