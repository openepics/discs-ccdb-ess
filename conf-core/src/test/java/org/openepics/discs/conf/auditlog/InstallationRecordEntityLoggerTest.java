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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class InstallationRecordEntityLoggerTest {
    private final InstallationRecord installationRecord = new InstallationRecord("record1", new Date(213123213));
    private final Slot slot = new Slot("slot1", true);
    private final Device device = new Device("device1");
    private final InstallationArtifact artifact1 = new InstallationArtifact("CAT Image", true, "Simple CAT image", "/var/usr/images/CAT");
    private final InstallationArtifact artifact2 = new InstallationArtifact("Manual", false, "Users manual", "www.deteriorator.com/user-manual");

    private final InstallationRecordEntityLogger installationRecordEntityLogger = new InstallationRecordEntityLogger();

    @Before
    public void setUp() {
        installationRecord.setSlot(slot);
        installationRecord.setDevice(device);
        installationRecord.getInstallationArtifactList().add(artifact1);
        installationRecord.getInstallationArtifactList().add(artifact2);
    }

    @Test
    public void testGetType() {
        assertTrue(InstallationRecord.class.equals(installationRecordEntityLogger.getType()));
    }

    @Test
    public void testSerializeEntity() {
        final String RESULT = "{\"installDate\":\"1970-01-03T11:12:03.213+0000\",\"slot\":\"slot1\",\"device\":\"device1\",\"installationArtifactList\":[{\"CAT Image\":\"/var/usr/images/CAT\"},{\"Manual\":\"www.deteriorator.com/user-manual\"}]}";

        assertEquals(RESULT, installationRecordEntityLogger.auditEntries(installationRecord, EntityTypeOperation.CREATE).get(0).getEntry());
    }
}
