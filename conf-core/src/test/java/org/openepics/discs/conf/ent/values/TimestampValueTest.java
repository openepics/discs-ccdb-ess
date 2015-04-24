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

package org.openepics.discs.conf.ent.values;

import static org.junit.Assert.assertEquals;

import org.epics.util.time.Timestamp;
import org.junit.Test;

/**
 * @author Miha Vitorovič &lt;miha.vitorovic@cosylab.com&gt;
 *
 */
public class TimestampValueTest {

    @Test(expected = NullPointerException.class)
    public void timestampValue() {
        TimestampValue timestampValue = new TimestampValue(null);
    }

    /*
     * TimestampValue.auditLogString() tests toString as well.
     */
    @Test
    public void displayTimestampNoNanoseconds() {
        TimestampValue timestampValue = new TimestampValue(Timestamp.of(109432210, 0));
        assertEquals("1973-06-20 13:50:10", timestampValue.auditLogString());
    }

    @Test
    public void displayTimestampWithNanoseconds() {
        TimestampValue timestampValue = new TimestampValue(Timestamp.of(109432210, 123000000));
        assertEquals("1973-06-20 13:50:10.123", timestampValue.auditLogString());
    }
}
