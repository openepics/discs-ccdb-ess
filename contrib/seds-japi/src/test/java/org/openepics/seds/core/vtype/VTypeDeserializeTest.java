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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.JsonObject;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VFloat;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.junit.Test;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SampleVFactory;
import org.openepics.seds.util.SedsException;
import org.openepics.seds.util.VTypeEquals;

/**
 *
 * @author Aaron Barber
 */
public class VTypeDeserializeTest {

    private Object read(String name) throws IOException, SedsException {
        //Message
        System.out.println("deserialize_" + name);

        //Files
        File data = FileUtil.get(FileUtil.CORE_VTYPE, name);

        try (InputStream in = new FileInputStream(data)) {

            //Reads
            JsonObject actual = Seds.newReader().read(in);

            //Converts
            return Seds.newVTypeConverter().toClientType(actual);
        }

    }

    @Test
    public void testSerializeVEnum1() throws Exception {
        VEnum actual = (VEnum) read("venum1");
        VEnum expected = new SampleVFactory().newVEnum1();

        VTypeEquals.enumerated(expected, actual);
    }

    public void testSerializeVEnumArray1() throws Exception {
        VEnumArray actual = (VEnumArray) read("venumrray1");
        VEnumArray expected = new SampleVFactory().newVEnumArray1();

        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
        VTypeEquals.enumArray(expected, actual);
    }

    @Test
    public void testSerializeVDouble1() throws Exception {
        VDouble actual = (VDouble) read("vdouble1");
        VDouble expected = new SampleVFactory().newVDouble1();

        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
        VTypeEquals.number(expected, actual);
    }

    @Test
    public void testSerializeVDouble2() throws Exception {
        VDouble actual = (VDouble) read("vdouble2");
        VDouble expected = new SampleVFactory().newVDouble2();

        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
        VTypeEquals.number(expected, actual);
    }

    @Test
    public void testSerializeVDoubleArray1() throws Exception {
        VDoubleArray actual = (VDoubleArray) read("vdoublearray1");
        VDoubleArray expected = new SampleVFactory().newVDoubleArray1();

        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
        VTypeEquals.numberArray(expected, actual);
    }

    @Test
    public void testSerializeVFloat1() throws Exception {
        VDouble actual = (VDouble) read("vfloat1");
        VFloat expected = new SampleVFactory().newVFloat1();

        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
        VTypeEquals.number(expected, actual);
    }

    @Test
    public void testSerializeVFloatArray1() throws Exception {
        VNumberArray actual = (VNumberArray) read("vfloatarray1");
        VNumberArray expected = new SampleVFactory().newVFloatArray1();

        VTypeEquals.alarm(expected, actual);
        VTypeEquals.time(expected, actual);
        VTypeEquals.numberArray(expected, actual);
    }

    @Test
    public void testSerializeVTable1() throws Exception {
        VTable actual = (VTable) read("vtable1");
        VTable expected = new SampleVFactory().newVTable1();

        VTypeEquals.table(expected, actual);
    }

    @Test
    public void testSerializeVTable2() throws Exception {
        VTable actual = (VTable) read("vtable2");
        VTable expected = new SampleVFactory().newVTable2();

        VTypeEquals.table(expected, actual);
    }

    @Test
    public void testSerializeVTable3() throws Exception {
        VTable actual = (VTable) read("vtable3");
        VTable expected = new SampleVFactory().newVTable3();

        VTypeEquals.table(expected, actual);
    }

    @Test
    public void testSerializeVTable4() throws Exception {
        VTable actual = (VTable) read("vtable4");
        VTable expected = new SampleVFactory().newVTable4();

        VTypeEquals.table(expected, actual);
    }

    @Test
    public void testSerializeVTable5() throws Exception {
        VTable actual = (VTable) read("vtable5");
        VTable expected = new SampleVFactory().newVTable5();

        VTypeEquals.table(expected, actual);
    }

    @Test
    public void testSerializeVTable6() throws Exception {
        VTable actual = (VTable) read("vtable6");
        VTable expected = new SampleVFactory().newVTable6();

        VTypeEquals.table(expected, actual);
    }

    @Test
    public void testSerializeVTable7() throws Exception {
        VTable actual = (VTable) read("vtable7");
        VTable expected = new SampleVFactory().newVTable7();

        VTypeEquals.table(expected, actual);
    }
}
