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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;

/**
 * {@link AuditRecord} maker for {@link InstallationRecord}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class InstallationRecordEntityLogger implements EntityLogger<InstallationRecord> {

    @Override
    public Class<InstallationRecord> getType() {
        return InstallationRecord.class;
    }

    @Override
    public List<AuditRecord> auditEntries(Object value, EntityTypeOperation operation) {
        final InstallationRecord installationRecord = (InstallationRecord) value;

        final Map<String, String> artifactsMap = new TreeMap<>();
        if (installationRecord.getInstallationArtifactList() != null) {
            for (InstallationArtifact artifact : installationRecord.getInstallationArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }
        
        final DeviceEntityLogger deviceEntityLogger = new DeviceEntityLogger();
        final SlotEntityLogger slotEntityLogger = new SlotEntityLogger();

        List<AuditRecord> installationRecordAuditRecords = new ArrayList<>();        
        installationRecordAuditRecords.addAll(deviceEntityLogger.auditEntries(installationRecord.getDevice(), operation));
        installationRecordAuditRecords.addAll(slotEntityLogger.auditEntries(installationRecord.getSlot(), operation));
        
        return installationRecordAuditRecords;
    }
}
