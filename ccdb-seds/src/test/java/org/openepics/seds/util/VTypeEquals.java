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
package org.openepics.seds.util;

import java.util.Arrays;
import java.util.List;
import org.epics.util.array.IteratorNumber;
import org.epics.vtype.Alarm;
import org.epics.vtype.Display;
import org.epics.vtype.Scalar;
import org.epics.vtype.Time;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Aaron Barber
 */
public class VTypeEquals {

    //Scalar Arrays
    //--------------------------------------------------------------------------    
    public static void intArray(VIntArray expected, VIntArray actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getData(), actual.getData());
    }

    public static void stringArray(VStringArray expected, VStringArray actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getData(), actual.getData());
    }

    public static void enumArray(VEnumArray expected, VEnumArray actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getData(), actual.getData());
        assertEquals(expected.getIndexes(), actual.getIndexes());
        assertEquals(expected.getLabels(), actual.getLabels());
    }

    public static void numberArray(VNumberArray expected, VNumberArray actual) {
        if (expected == actual) {
            return;
        }

        IteratorNumber e = expected.getData().iterator();
        IteratorNumber a = actual.getData().iterator();

        assertThat(expected.getData().size(), equalTo(actual.getData().size()));
        while (e.hasNext() && a.hasNext()) {
            assertThat(e.nextDouble(), equalTo(a.nextDouble()));
        }
    }

    public static void booleanArray(Object object, Object object0) {
        //No Boolean Arrays exist in VType
    }
    //--------------------------------------------------------------------------

    //Simple VTypes
    //--------------------------------------------------------------------------    
    public static void alarm(Alarm expected, Alarm actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getAlarmSeverity(), actual.getAlarmSeverity());
        assertEquals(expected.getAlarmName(), actual.getAlarmName());
    }

    public static void display(Display expected, Display actual) {
        if (expected == actual) {
            return;
        }

        primitive(expected.getLowerDisplayLimit(), actual.getLowerDisplayLimit());
        primitive(expected.getUpperDisplayLimit(), actual.getUpperDisplayLimit());
        assertEquals(expected.getUnits(), actual.getUnits());
    }

    public static void enumerated(VEnum expected, VEnum actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getIndex(), actual.getIndex());
        assertEquals(expected.getLabels(), actual.getLabels());
    }

    public static void control(Object expected, Object actual) {
        //No Control exist in VType        
    }

    public static void time(Time expected, Time actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getTimeUserTag(), actual.getTimeUserTag());
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
    }
    //--------------------------------------------------------------------------    

    //Advanced VTypes
    //--------------------------------------------------------------------------    
    public static void number(VNumber expected, VNumber actual) {
        if (expected == actual) {
            return;
        }

        Assert.assertTrue(expected.getValue().doubleValue() == actual.getValue().doubleValue());
    }

    public static void scalar(Scalar expected, Scalar actual) {
        if (expected == actual) {
            return;
        }

        assertEquals(expected.getValue(), actual.getValue());
    }

    public static void table(VTable expected, VTable actual) {
        if (expected == actual) {
            return;
        }

        Assert.assertEquals(expected.getRowCount(), actual.getRowCount());
        Assert.assertEquals(expected.getColumnCount(), actual.getColumnCount());

        for (int i = 0; i < expected.getColumnCount(); ++i) {
            object(expected.getColumnType(i), actual.getColumnType(i));
            Assert.assertEquals(expected.getColumnName(i), actual.getColumnName(i));
            Assert.assertEquals(expected.getColumnData(i), actual.getColumnData(i));
        }
    }
    //--------------------------------------------------------------------------    

    //Primitives
    //--------------------------------------------------------------------------        
    public static void primitive(Boolean e, Boolean a) {
        assertEquals(e, a);
    }

    public static void primitive(String e, String a) {
        assertEquals(e, a);
    }

    public static void primitive(Integer e, Integer a) {
        assertEquals(e, a);
    }

    public static void primitive(Number e, Number a) {
        primitive(e.doubleValue(), a.doubleValue());
    }

    public static void primitive(Double e, Double a) {
        //Checks empty
        boolean emptyE = e == null || Double.isNaN(e) || Double.isInfinite(e);
        boolean emptyA = a == null || Double.isNaN(a) || Double.isInfinite(a);

        //One is empty
        if (emptyE != emptyA) {
            fail(e, a);
        }

        //Both not empty
        if (emptyE == true) {
            assertEquals(e, a);
        }
    }

    private static void object(Class e, Class a) {
        List<Class> num = Arrays.asList(new Class[]{Number.class, byte.class, double.class, long.class, short.class});

        if (int.class.equals(e) && int.class.equals(a)) {
            return;
        }

        if (num.contains(e) && num.contains(a)) {
            return;
        }

        if (String.class.equals(e) && String.class.equals(a)) {
            return;
        }

        if (boolean.class.equals(e) && boolean.class.equals(a)) {
            return;
        }

        assertEquals(e, a);
    }
    //--------------------------------------------------------------------------    

    //Helper
    //--------------------------------------------------------------------------    
    private static void fail(Object e, Object a) {
        Assert.fail("Expected: " + e + ", does not equal" + a);
    }
    //--------------------------------------------------------------------------    

}
