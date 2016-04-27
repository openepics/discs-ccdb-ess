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

import org.openepics.discs.ccdb.model.values.StrVectorValue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class StrVectorValueTest {

    private List<String> testValues;

    @Before
    public void testValuesInitialization() {
        testValues = new ArrayList<>();
        testValues.add("one");
        testValues.add("two");
        testValues.add("three");
        testValues.add("four");
        testValues.add("five");
        testValues.add("six");
        testValues.add("seven");
    }

    @Test(expected = NullPointerException.class)
    public void strVectorValue() {
        StrVectorValue strVectorValue = new StrVectorValue(null);
    }

    /*
     * All display tests also test the Value.auditLogString(int... dimensions)
     */
    @Test
    public void strVectorTest1() {
        StrVectorValue strVectorValue = new StrVectorValue(testValues.subList(0, 1));
        assertEquals("[\"one\"]", strVectorValue.toString());
    }

    @Test
    public void strVectorTest2() {
        StrVectorValue strVectorValue = new StrVectorValue(testValues.subList(0, 2));
        assertEquals("[\"one\", \"two\"]", strVectorValue.toString());
    }

    @Test
    public void strVectorTest5() {
        StrVectorValue strVectorValue = new StrVectorValue(testValues.subList(0, 5));
        assertEquals("[\"one\", \"two\", \"three\", \"four\", \"five\"]", strVectorValue.toString());
    }

    @Test
    public void strVectorTest7() {
        StrVectorValue strVectorValue = new StrVectorValue(testValues);
        assertEquals("[\"one\", \"two\", \"three\", \"four\", ..., \"seven\"]", strVectorValue.toString());
    }
}
