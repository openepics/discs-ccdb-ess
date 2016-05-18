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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.collect.ImmutableList;

/**
 * {@link AuditRecord} maker for {@link Device}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
@Stateless
public class DeviceEntityLogger implements EntityLogger<Device> {

    @Override
    public Class<Device> getType() {
        return Device.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation) {
        final Device device = (Device) value;

        final Map<String, String> propertiesMap = new TreeMap<>();
        if (device.getDevicePropertyList() != null) {
            for (DevicePropertyValue propValue : device.getDevicePropertyList()) {
                final String entryValue = propValue.getPropValue() == null ? null
                        : propValue.getPropValue().auditLogString(EntityLoggerUtil.AUDIT_LOG_ROWS,
                                                                                EntityLoggerUtil.AUDIT_LOG_COLUMNS);
                propertiesMap.put(propValue.getProperty().getName(), entryValue);
            }
        }

        final Map<String, String> artifactsMap = new TreeMap<>();
        if (device.getDeviceArtifactList() != null) {
            for (DeviceArtifact artifact : device.getDeviceArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        final Map<String, String> installationSlotMap = new TreeMap<>();

        InstallationRecord lastInstallationRecord = null;
        for (InstallationRecord installationRecord : device.getInstallationRecordList()) {
            if (lastInstallationRecord == null || installationRecord.getModifiedAt().after(lastInstallationRecord.getModifiedAt())) {
                lastInstallationRecord = installationRecord;
            }
        }

        if (lastInstallationRecord != null) {
            final SimpleDateFormat timestampFormat = new SimpleDateFormat(Conversion.DATE_TIME_FORMAT);
            installationSlotMap.put("installationSlot", lastInstallationRecord.getSlot().getName());
            installationSlotMap.put("installationDate",
                                            timestampFormat.format(lastInstallationRecord.getInstallDate()));
            if (lastInstallationRecord.getUninstallDate() != null) {
                installationSlotMap.put("uninstallationDate",
                                            timestampFormat.format(lastInstallationRecord.getUninstallDate()));
            }
        }

        return ImmutableList.of(new AuditLogUtil(device)
                                .removeTopProperties(Arrays.asList("id", "modifiedAt", "modifiedBy",
                                        "version", "serialNumber", "componentType", "name"))
                                .addStringProperty("componentType",
                                        device.getComponentType() != null ? device.getComponentType().getName() : null)
                                .addArrayOfMappedProperties("installation", installationSlotMap)
                                .addArrayOfMappedProperties("devicePropertyList", propertiesMap)
                                .addArrayOfMappedProperties("deviceArtifactList", artifactsMap)
                                .addArrayOfProperties("tagsList", EntityLoggerUtil.getTagNamesFromTagsSet(device.getTags()))
                                .auditEntry(operation, EntityType.DEVICE, device.getSerialNumber(), device.getId()));
    }
}
