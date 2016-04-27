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
import java.util.Collections;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VFloat;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.newAlarm;
import static org.epics.vtype.ValueFactory.newDisplay;
import static org.epics.vtype.ValueFactory.newTime;
import static org.epics.vtype.ValueFactory.newVDouble;
import static org.epics.vtype.ValueFactory.newVDoubleArray;
import static org.epics.vtype.ValueFactory.newVEnum;
import static org.epics.vtype.ValueFactory.newVFloat;
import static org.epics.vtype.ValueFactory.newVFloatArray;
import static org.epics.vtype.ValueFactory.newVStringArray;
import static org.epics.vtype.ValueFactory.timeNow;
import org.epics.vtype.table.VTableFactory;
import static org.epics.vtype.table.VTableFactory.column;
import static org.epics.vtype.table.VTableFactory.newVTable;
import static org.epics.vtype.table.VTableFactory.valueTable;

public class SampleVFactory {

    //Data Samples
    //-------------------------------------------------------------------------- 
    private Display display(double lowLimit, double highLimit, String units, String message) {
        return newDisplay(
                lowLimit,
                Double.NaN,
                Double.NaN,
                units,
                NumberFormats.toStringFormat(),
                Double.NaN,
                Double.NaN,
                highLimit,
                Double.NaN,
                Double.NaN
        );
    }

    public VEnum newVEnum1() {
        return newVEnum(
                1,
                Arrays.asList("ONE", "TWO", "THREE"),
                alarmNone(),
                newTime(Timestamp.of(1354719441, 521786982))
        );
    }

    public VEnumArray newVEnumArray1() {
        return ValueFactory.newVEnumArray(
                new ArrayInt(2, 4, 1, 3),
                Arrays.asList("ACE", "TWO", "THREE", "FOUR", "FIVE"),
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982))
        );
    }

    public VDouble newVDouble1() {
        return newVDouble(
                1.0,
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982)),
                display(10, 100, "meters", null)
        );
    }

    public VDouble newVDouble2() {
        return newVDouble(
                -1.0,
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982)),
                newDisplay(
                        10d,
                        5d,
                        0d,
                        "unit",
                        NumberFormats.toStringFormat(),
                        1000d,
                        500d,
                        100d,
                        50d,
                        60d
                )
        );
    }

    public VDoubleArray newVDoubleArray1() {
        return newVDoubleArray(
                new ArrayDouble(3.14, 6.28, 1.41, 0.0, 1.0),
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982)),
                display(10, 100, "meters", null)
        );
    }

    public VFloat newVFloat1() {
        return newVFloat(
                1.0f,
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982)),
                display(10, 100, "meters", null)
        );
    }

    public VFloatArray newVFloatArray1() {
        return newVFloatArray(
                new ArrayFloat(3.125f, 6.25f, 1.375f, 0.0f, 1.0f),
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982)),
                display(10, 100, "meters", null)
        );
    }

    public VTable newVTable1() {
        return newVTable(
                column("Rack", newVStringArray(Arrays.asList("A", "A", "B"), alarmNone(), timeNow())),
                column("Slot", newVDoubleArray(new ArrayDouble(1, 2, 3), alarmNone(), timeNow(), display(10, 100, "meters", null))),
                column("CPU", newVStringArray(Arrays.asList("286", "286", "386"), alarmNone(), timeNow()))
        );

    }

    public VTable newVTable2() {
        return VTableFactory.newVTable(
                VTableFactory.column("colA", newVDoubleArray1()),
                VTableFactory.column("colB", newVFloatArray1())
        );
    }

    public VTable newVTable3() {
        return valueTable(Collections.<VType>emptyList());
    }

    public VTable newVTable4() {
        return valueTable(Arrays.asList((VType) null));
    }

    public VTable newVTable5() {
        VDouble value1 = newVDouble(3.1);
        VDouble value2 = newVDouble(3.2, newAlarm(AlarmSeverity.MINOR, "HI"), timeNow(), display(10, 100, "meters", null));
        VDouble value3 = newVDouble(3.3);
        return valueTable(Arrays.asList("A", "B", "C"), Arrays.asList(value1, value2, value3));
    }

    public VTable newVTable6() {
        return VTableFactory.valueTable(Arrays.asList("A"), Arrays.asList((VType) null));
    }

    public VTable newVTable7() {
        return VTableFactory.valueTable(Arrays.asList("A", "B", "C", "D"), Arrays.asList((VType) null, null, null, null));
    }
    //--------------------------------------------------------------------------    
}
