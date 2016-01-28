/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.seds.api;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.core.Seds;

/**
 *
 * @author Aaron Barber
 */
public class SedsFactoryTest {

    @Test
    public void testTable_illegal_dimensions() {
        System.out.println("table_illegal_dimensions1");
        SedsFactory f = Seds.newFactory();
        int index;

        //Incorrect Column Length
        index = 1;
        try {
            f.newTable(
                    2,
                    10,
                    new String[]{
                        "A",
                        "B",
                        "C",
                        "D"
                    },
                    new SedsScalarArray[]{
                        f.newScalarArray(new Boolean[]{true, false}, null, null, null, null, null),
                        f.newScalarArray(new Integer[]{1, 2}, null, null, null, null, null)
                    }
            );
            fail("Expected an illegal argument exception (" + index + ").");
        } catch (IllegalArgumentException e) {
        }

        //Incorrect Row Length
        index = 2;
        try {
            f.newTable(
                    10,
                    2,
                    new String[]{
                        "A",
                        "B"
                    },
                    new SedsScalarArray[]{
                        f.newScalarArray(new Boolean[]{true, false}, null, null, null, null, null),
                        f.newScalarArray(new Integer[]{1, 2}, null, null, null, null, null)
                    }
            );
            fail("Expected an illegal argument exception (" + index + ").");
        } catch (IllegalArgumentException e) {
        }

        //Incorrect Row Length
        index = 3;
        try {
            f.newTable(
                    2,
                    2,
                    new String[]{
                        "A",
                        "B",},
                    new SedsScalarArray[]{
                        f.newScalarArray(new Boolean[]{}, null, null, null, null, null),
                        f.newScalarArray(new Integer[]{1, 2}, null, null, null, null, null)
                    }
            );
            fail("Expected an illegal argument exception (" + index + ").");
        } catch (IllegalArgumentException e) {
        }

        //Correct Row Length
        index = 3;
        try {
            f.newTable(
                    2,
                    2,
                    new String[]{
                        "A",
                        "B",},
                    new SedsScalarArray[]{
                        f.newScalarArray(new Boolean[]{true, false}, null, null, null, null, null),
                        f.newScalarArray(new Integer[]{1, 2}, null, null, null, null, null)
                    }
            );
        } catch (IllegalArgumentException e) {
            fail("Did not expect an illegal argument exception (" + index + ").");
        }
    }

}
