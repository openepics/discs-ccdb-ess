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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class DblTableValueTest {

    @Test(expected = NullPointerException.class)
    public void dblTableValue() {
        DblTableValue dblTableValue = new DblTableValue(null);
    }

    /*
     * All display tests also test the Value.auditLogString(int... dimensions)
     */
    @Test
    public void dblTableDisplay1by1() {
        DblTableValue dblTableValue = new DblTableValue(prepareTableTestData(1, 1));
        final String expectedOutput = "[[0.0]]";
        assertEquals(expectedOutput, dblTableValue.toString());
    }

    @Test
    public void dblTableDisplay2by2() {
        DblTableValue dblTableValue = new DblTableValue(prepareTableTestData(2, 2));
        final String expectedOutput = "[[0.0, 0.1], [1.0, 1.1]]";
        assertEquals( expectedOutput, dblTableValue.toString());

        final List<List<Double>> table3 = prepareTableTestData(3, 5);
        dblTableValue = new DblTableValue(table3);
        final String expectedOutput5 = "[[0.0, 0.1, 0.2, 0.30000000000000004, 0.4], [1.0, 1.1, 1.2000000000000002, 1.3000000000000003, 1.4000000000000004], "
                + "[2.0, 2.1, 2.2, 2.3000000000000003, 2.4000000000000004]]";
        assertEquals("Outputs differ. Expected: " + expectedOutput5 + ", Actual: " + dblTableValue.toString(), expectedOutput5, dblTableValue.toString());

        final List<List<Double>> table4 = prepareTableTestData(5, 7);
        dblTableValue = new DblTableValue(table4);
        final String expectedOutput7 = "[[0.0, 0.1, 0.2, 0.30000000000000004, ..., 0.6], "
                + "[1.0, 1.1, 1.2000000000000002, 1.3000000000000003, ..., 1.6000000000000005], ..., "
                + "[4.0, 4.1, 4.199999999999999, 4.299999999999999, ..., 4.599999999999998]]";
        assertEquals("Outputs differ. Expected: " + expectedOutput7 + ", Actual: " + dblTableValue.toString(), expectedOutput7, dblTableValue.toString());
    }

    @Test
    public void dblTableDisplay3by5() {
        DblTableValue dblTableValue = new DblTableValue(prepareTableTestData(3, 5));
        final String expectedOutput = "[[0.0, 0.1, 0.2, 0.30000000000000004, 0.4], "
                + "[1.0, 1.1, 1.2000000000000002, 1.3000000000000003, 1.4000000000000004], "
                + "[2.0, 2.1, 2.2, 2.3000000000000003, 2.4000000000000004]]";
        assertEquals(expectedOutput, dblTableValue.toString());
    }


    @Test
    public void dblTableDisplay5by7() {
        DblTableValue dblTableValue = new DblTableValue(prepareTableTestData(5, 7));
        final String expectedOutput = "[[0.0, 0.1, 0.2, 0.30000000000000004, ..., 0.6], "
                + "[1.0, 1.1, 1.2000000000000002, 1.3000000000000003, ..., 1.6000000000000005], ..., "
                + "[4.0, 4.1, 4.199999999999999, 4.299999999999999, ..., 4.599999999999998]]";
        assertEquals(expectedOutput, dblTableValue.toString());
    }

    private List<List<Double>> prepareTableTestData(int columns, int rows) {
        final double step = 0.1;
        List<List<Double>> testValues = new ArrayList<>();
        for (int col = 0; col < columns; col++) {
            final List<Double> column = new ArrayList<Double>();
            double value = col;
            for (int row = 0; row < rows; row++) {
                column.add(value);
                value += step;
            }
            testValues.add(column);
        }
        return testValues;
    }
}
