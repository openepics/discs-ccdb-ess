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

import java.util.HashMap;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;

import com.google.common.collect.Sets;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class InstallationRecordEntityLogger implements EntityLogger{

    @Override
    public Class getType() {
        return InstallationRecord.class;
    }

    @Override
    public AuditRecord auditEntry(Object value, EntityTypeOperation operation, String user) {
        final InstallationRecord installationRecord = (InstallationRecord) value;

        final HashMap<String, String> artifactsMap = new HashMap<>();
        if (installationRecord.getInstallationArtifactList() != null) {
            for (InstallationArtifact artifact : installationRecord.getInstallationArtifactList()) {
                artifactsMap.put(artifact.getName(), artifact.getUri());
            }
        }

        return (new AuditLogUtil(installationRecord).
                removeTopProperties(Sets.newHashSet("id", "modifiedAt", "modifiedBy", "version", "recordNumber", "slot", "device")).
                addStringProperty("slot", installationRecord.getSlot().getName()).
                addStringProperty("device", installationRecord.getDevice().getSerialNumber()).
                addArrayOfProperties("installationArtifactList", artifactsMap).
                auditEntry(operation, EntityType.INSTALLATION_RECORD, installationRecord.getRecordNumber(), installationRecord.getId(), user));
    }
}
