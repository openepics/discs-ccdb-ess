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

package org.openepics.discs.ccdb.model.values;

import org.openepics.discs.ccdb.model.values.DblValue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class DblValueTest {

    @Test(expected = NullPointerException.class)
    public void dblValue() {
        DblValue dblValue = new DblValue(null);
    }

    @Test
    public void displayToString() {
        DblValue dblValue = new DblValue("1.0");
        assertEquals("1.0", dblValue.toString());
    }

    @Test
    public void displayAuditLogString() {
        DblValue dblValue = new DblValue("1.0");
        assertEquals("1.0", dblValue.auditLogString());
    }
}
