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

import org.openepics.discs.ccdb.core.auditlog.DataTypeEntityLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openepics.discs.ccdb.model.DataType;
import org.openepics.discs.ccdb.model.EntityTypeOperation;

public class DataTypeEntityLoggerTest {

    private final DataTypeEntityLogger entLogger = new DataTypeEntityLogger();

    @Test
    public void testGetType() {
        assertTrue(DataType.class.equals(entLogger.getType()));
    }

    @Test
    public void testSerializeEntity() {
        final DataType dt = new DataType("Float", "Float", true, "Well.. a scalar float");
        final String RESULT = "{\"description\":\"Float\",\"scalar\":true,\"definition\":\"Well.. a scalar float\"}";

        assertEquals(RESULT, entLogger.auditEntries(dt, EntityTypeOperation.CREATE).get(0).getEntry());
    }

}
