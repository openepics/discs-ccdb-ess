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
package org.openepics.seds.core.vtype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.epics.util.array.ArrayInt;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VEnum;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.displayNone;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.AlarmType;
import org.openepics.seds.util.VTypeEquals;

/**
 *
 * @author asbarber
 */
public class VTypeMapperTest {

    public static final int SEDS = 0;
    public static final int VTYPE = 1;

    private static final VTypeMapper instance = new VTypeMapper(Seds.newFactory());
    /**
     * The key is the SEDS type (ex - alarm, time, table). The value is the
     * entry Object[] = {SedsValue, VTypeValue}.
     */
    private static final Map<String, Object[]> mappings;

    static {
        mappings = new HashMap<>();
        SedsFactory f = Seds.newFactory();

        Object[] obj;

        obj = new Object[]{
            f.newAlarm(AlarmType.fromOrdinal(3), null, "sampleAlarm"),
            ValueFactory.newAlarm(AlarmSeverity.INVALID, "sampleAlarm")
        };
        mappings.put("alarm", obj);

        obj = new Object[]{
            f.newDisplay(1d, 2d, 10d, 100d, 11d, 12d, null, "units"),
            ValueFactory.newDisplay(10d, 1d, 11d, "units", null, 12d, 2d, 100d, null, null)
        };
        mappings.put("display", obj);

        obj = new Object[]{
            f.newEnum("B", new String[]{"A", "B", "C"}),
            ValueFactory.newVEnum(1, Arrays.asList("A", "B", "C"), null, null)
        };
        mappings.put("enum", obj);

        obj = new Object[]{
            f.newControl(10d, 100d),
            ValueFactory.newDisplay(Double.NaN, Double.NaN, Double.NaN, "", null, Double.NaN, Double.NaN, Double.NaN, 10d, 100d)
        };
        mappings.put("control", obj);

        obj = new Object[]{
            f.newTime(Timestamp.of(1354719441L, 521786982), -1),
            ValueFactory.newTime(Timestamp.of(1354719441L, 521786982), -1, true)
        };
        mappings.put("time", obj);

        obj = new Object[]{
            f.newScalar(true, "true", f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, null, f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVBoolean(true, alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)))
        };
        mappings.put("scalar_boolean", obj);

        obj = new Object[]{
            f.newScalar(10, "10", f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, f.newDisplay(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, null, ""), f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVInt(10, alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)), displayNone())
        };
        mappings.put("scalar_integer", obj);

        obj = new Object[]{
            f.newScalar(10L, "10", f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, f.newDisplay(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, null, ""), f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVNumber(10L, alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)), ValueFactory.displayNone())
        };
        mappings.put("scalar_number", obj);

        obj = new Object[]{
            f.newScalar("a", f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, null, f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVString("a", alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)))
        };
        mappings.put("scalar_string", obj);

        obj = new Object[]{
            null,
            null
        };
        mappings.put("scalar_array_boolean", obj);

        obj = new Object[]{
            f.newScalarArray(new Integer[]{1, 2}, null, f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, null, f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVIntArray(new ArrayInt(new int[]{1, 2}), alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)), null)
        };
        mappings.put("scalar_array_integer", obj);

        obj = new Object[]{
            f.newScalarArray(new Number[]{1, 2}, null, f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, null, f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVNumberArray(new ArrayInt(new int[]{1, 2}), alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)), null)
        };
        mappings.put("scalar_array_number", obj);

        obj = new Object[]{
            f.newScalarArray(new String[]{"A", "B"}, f.newAlarm(AlarmType.fromOrdinal(0), null, "NONE"), null, null, f.newTime(Timestamp.of(1354719441L, 521786982), null)),
            ValueFactory.newVStringArray(Arrays.asList("A", "B"), alarmNone(), ValueFactory.newTime(Timestamp.of(1354719441L, 521786982)))
        };
        mappings.put("scalar_array_string", obj);

        ArrayList<Class<?>> colTypes = new ArrayList<>();
        colTypes.add(int.class);
        colTypes.add(String.class);

        obj = new Object[]{
            f.newTable(2, 2, new String[]{"numbers", "names"}, new SedsScalarArray[]{f.newScalarArray(new Integer[]{1, 2}, null, null, null, null, null), f.newScalarArray(new String[]{"A", "B"}, null, null, null, null)}),
            ValueFactory.newVTable(colTypes, Arrays.asList("numbers", "names"), Arrays.asList(new ArrayInt(1, 2), Arrays.asList("A", "B")))
        };
        mappings.put("table", obj);
    }

    //To
    //--------------------------------------------------------------------------
    /**
     * Test of toSedsAlarm method, of class VTypeMapper.
     */
    @Test
    public void testToSedsAlarm() {
        System.out.println("toSedsAlarm");

        SedsAlarm expected = (SedsAlarm) mappings.get("alarm")[SEDS];
        SedsAlarm actual = instance.toSedsAlarm((Alarm) mappings.get("alarm")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsDisplay method, of class VTypeMapper.
     */
    @Test
    public void testToSedsDisplay() {
        System.out.println("toSedsDisplay");

        SedsDisplay expected = (SedsDisplay) mappings.get("display")[SEDS];
        SedsDisplay actual = instance.toSedsDisplay((Display) mappings.get("display")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsEnum method, of class VTypeMapper.
     */
    @Test
    public void testToSedsEnum() {
        System.out.println("toSedsEnum");

        SedsEnum expected = (SedsEnum) mappings.get("enum")[SEDS];
        SedsEnum actual = instance.toSedsEnum((VEnum) mappings.get("enum")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsControl method, of class VTypeMapper.
     */
    @Test
    public void testToSedsControl() {
        System.out.println("toSedsControl");

        SedsControl expected = (SedsControl) mappings.get("control")[SEDS];
        SedsControl actual = instance.toSedsControl((Display) mappings.get("control")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsTime method, of class VTypeMapper.
     */
    @Test
    public void testToSedsTime() {
        System.out.println("toSedsTime");

        SedsTime expected = (SedsTime) mappings.get("time")[SEDS];
        SedsTime actual = instance.toSedsTime((Time) mappings.get("time")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarBoolean method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarBoolean() {
        System.out.println("toSedsScalar<Boolean>");

        SedsScalar<Boolean> expected = (SedsScalar<Boolean>) mappings.get("scalar_boolean")[SEDS];
        SedsScalar<Boolean> actual = instance.toSedsScalarBoolean((VBoolean) mappings.get("scalar_boolean")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarInteger method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarInteger() {
        System.out.println("toSedsScalar<Integer>");

        SedsScalar<Integer> expected = (SedsScalar<Integer>) mappings.get("scalar_integer")[SEDS];
        SedsScalar<Integer> actual = instance.toSedsScalarInteger((VInt) mappings.get("scalar_integer")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarNumber method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarNumber() {
        System.out.println("toSedsScalar<Number>");

        SedsScalar<Number> expected = (SedsScalar<Number>) mappings.get("scalar_number")[SEDS];
        SedsScalar<Number> actual = instance.toSedsScalarNumber((VNumber) mappings.get("scalar_number")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarString method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarString() {
        System.out.println("toSedsScalar<String>");

        SedsScalar<String> expected = (SedsScalar<String>) mappings.get("scalar_string")[SEDS];
        SedsScalar<String> actual = instance.toSedsScalarString((VString) mappings.get("scalar_string")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarArrayBoolean method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarArrayBoolean() {
        System.out.println("toSedsScalarArray<Boolean>");

        SedsScalarArray<Boolean> expected = (SedsScalarArray<Boolean>) mappings.get("scalar_array_boolean")[SEDS];
        SedsScalarArray<Boolean> actual = instance.toSedsScalarArrayBoolean(mappings.get("scalar_array_boolean")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarArrayInteger method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarArrayInteger() {
        System.out.println("toSedsScalarArray<Integer>");

        SedsScalarArray<Integer> expected = (SedsScalarArray<Integer>) mappings.get("scalar_array_integer")[SEDS];
        SedsScalarArray<Integer> actual = instance.toSedsScalarArrayInteger((VIntArray) mappings.get("scalar_array_integer")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarArrayNumber method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarArrayNumber() {
        System.out.println("toSedsScalarArray<Number>");

        SedsScalarArray<Number> expected = (SedsScalarArray<Number>) mappings.get("scalar_array_number")[SEDS];
        SedsScalarArray<Number> actual = instance.toSedsScalarArrayNumber((VNumberArray) mappings.get("scalar_array_number")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsScalarArrayString method, of class VTypeMapper.
     */
    @Test
    public void testToSedsScalarArrayString() {
        System.out.println("toSedsScalarArray<String>");

        SedsScalarArray<String> expected = (SedsScalarArray<String>) mappings.get("scalar_array_string")[SEDS];
        SedsScalarArray<String> actual = instance.toSedsScalarArrayString((VStringArray) mappings.get("scalar_array_string")[VTYPE]);

        assertEquals(expected, actual);
    }

    /**
     * Test of toSedsTable method, of class VTypeMapper.
     */
    @Test
    public void testToSedsTable() {
        System.out.println("toSedsTable");

        SedsTable expected = (SedsTable) mappings.get("table")[SEDS];
        SedsTable actual = instance.toSedsTable((VTable) mappings.get("table")[VTYPE]);

        assertEquals(expected, actual);
    }
    //--------------------------------------------------------------------------

    //From
    //--------------------------------------------------------------------------
    /**
     * Test of fromSedsAlarm method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsAlarm() {
        System.out.println("toSedsAlarm");

        Alarm expected = (Alarm) mappings.get("alarm")[VTYPE];
        Alarm actual = instance.fromSedsAlarm((SedsAlarm) mappings.get("alarm")[SEDS]);

        VTypeEquals.alarm(expected, actual);
    }

    /**
     * Test of fromSedsDisplay method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsDisplay() {
        System.out.println("toSedsDisplay");

        Display expected = (Display) mappings.get("display")[VTYPE];
        Display actual = instance.fromSedsDisplay((SedsDisplay) mappings.get("display")[SEDS]);

        VTypeEquals.display(expected, actual);
    }

    /**
     * Test of fromSedsEnum method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsEnum() {
        System.out.println("toSedsEnum");

        VEnum expected = (VEnum) mappings.get("enum")[VTYPE];
        VEnum actual = instance.fromSedsEnum((SedsEnum) mappings.get("enum")[SEDS]);

        VTypeEquals.enumerated(expected, actual);
    }

    /**
     * Test of fromSedsControl method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsControl() {
        System.out.println("toSedsControl");

        VTypeEquals.control(null, null);
    }

    /**
     * Test of fromSedsTime method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsTime() {
        System.out.println("toSedsTime");

        Time expected = (Time) mappings.get("time")[VTYPE];
        Time actual = instance.fromSedsTime((SedsTime) mappings.get("time")[SEDS]);

        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarBoolean method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarBoolean() {
        System.out.println("toSedsScalarBoolean");

        VBoolean expected = (VBoolean) mappings.get("scalar_boolean")[VTYPE];
        VBoolean actual = instance.fromSedsScalarBoolean((SedsScalar<Boolean>) mappings.get("scalar_boolean")[SEDS]);

        VTypeEquals.scalar(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarInteger method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarInteger() {
        System.out.println("toSedsScalarInteger");

        VInt expected = (VInt) mappings.get("scalar_integer")[VTYPE];
        VInt actual = instance.fromSedsScalarInteger((SedsScalar<Integer>) mappings.get("scalar_integer")[SEDS]);

        VTypeEquals.scalar(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarNumber method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarNumber() {
        System.out.println("toSedsScalarNumber");

        VNumber expected = (VNumber) mappings.get("scalar_number")[VTYPE];
        VNumber actual = instance.fromSedsScalarNumber((SedsScalar<Number>) mappings.get("scalar_number")[SEDS]);

        VTypeEquals.scalar(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarString method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarString() {
        System.out.println("toSedsScalarString");

        VString expected = (VString) mappings.get("scalar_string")[VTYPE];
        VString actual = instance.fromSedsScalarString((SedsScalar<String>) mappings.get("scalar_string")[SEDS]);

        VTypeEquals.scalar(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarArrayBoolean method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarArrayBoolean() {
        System.out.println("toSedsScalarArrayBoolean");

        VTypeEquals.booleanArray(null, null);
    }

    /**
     * Test of fromSedsScalarArrayInteger method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarArrayInteger() {
        System.out.println("toSedsScalarArrayInteger");

        VIntArray expected = (VIntArray) mappings.get("scalar_array_integer")[VTYPE];
        VIntArray actual = instance.fromSedsScalarArrayInteger((SedsScalarArray<Integer>) mappings.get("scalar_array_integer")[SEDS]);

        VTypeEquals.intArray(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarArrayNumber method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarArrayNumber() {
        System.out.println("toSedsScalarArrayNumber");

        VNumberArray expected = (VNumberArray) mappings.get("scalar_array_number")[VTYPE];
        VNumberArray actual = instance.fromSedsScalarArrayNumber((SedsScalarArray<Number>) mappings.get("scalar_array_number")[SEDS]);

        VTypeEquals.numberArray(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsScalarArrayString method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsScalarArrayString() {
        System.out.println("toSedsScalarArrayString");

        VStringArray expected = (VStringArray) mappings.get("scalar_array_string")[VTYPE];
        VStringArray actual = instance.fromSedsScalarArrayString((SedsScalarArray<String>) mappings.get("scalar_array_string")[SEDS]);

        VTypeEquals.stringArray(expected, actual);
        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
    }

    /**
     * Test of fromSedsTable method, of class VTypeMapper.
     */
    @Test
    public void testFromSedsTable() {
        System.out.println("toSedsTable");

        VTable expected = (VTable) mappings.get("table")[VTYPE];
        VTable actual = instance.fromSedsTable((SedsTable) mappings.get("table")[SEDS]);

        VTypeEquals.table(expected, actual);
    }
    //--------------------------------------------------------------------------
}
