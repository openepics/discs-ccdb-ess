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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SampleVFactory;
import org.openepics.seds.util.SedsException;

/**
 *
 * @author asbarber
 */
public class VTypeSerializeTest {

    private void test(String name, Object obj) throws IOException, SedsException {
        //Message
        System.out.println("serialize_" + name);

        //Files
        File expected = FileUtil.get(FileUtil.CORE_VTYPE, name);
        File actual = FileUtil.get(FileUtil.CORE_VTYPE, name + FileUtil.FAILED);
        actual.createNewFile();
        OutputStream out = new FileOutputStream(actual);

        try {
            //Writes
            Seds.newWriter().write(Seds.newVTypeConverter().toJSON(obj), out);
        } catch (SedsException ex) {
            if (ex.getJson() != null) {
                Seds.newWriter().write(ex.getJson(), out);
            } else {
                throw ex;
            }
        }

        //Tests
        assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));

        //Cleans-up
        out.close();
        actual.delete();
    }

    @Test
    public void testSerializeVEnum1() throws IOException, SedsException {
        test("venum1", new SampleVFactory().newVEnum1());
    }

    @Test
    public void testSerializeVEnumArray1() throws IOException, SedsException {
        test("venumarray1", new SampleVFactory().newVEnumArray1());
    }

    @Test
    public void testSerializeVDouble1() throws IOException, SedsException {
        test("vdouble1", new SampleVFactory().newVDouble1());
    }

    @Test
    public void testSerializeVDouble2() throws IOException, SedsException {
        test("vdouble2", new SampleVFactory().newVDouble2());
    }

    @Test
    public void testSerializeVDoubleArray1() throws IOException, SedsException {
        test("vdoublearray1", new SampleVFactory().newVDoubleArray1());
    }

    @Test
    public void testSerializeVFloat1() throws IOException, SedsException {
        test("vfloat1", new SampleVFactory().newVFloat1());
    }

    @Test
    public void testSerializeVFloatArray1() throws IOException, SedsException {
        test("vfloatarray1", new SampleVFactory().newVFloatArray1());
    }

    @Test
    public void testSerializeVTable1() throws IOException, SedsException {
        test("vtable1", new SampleVFactory().newVTable1());
    }

    @Test
    public void testSerializeVTable2() throws IOException, SedsException {
        test("vtable2", new SampleVFactory().newVTable2());
    }

    @Test
    public void testSerializeVTable3() throws IOException, SedsException {
        test("vtable3", new SampleVFactory().newVTable3());
    }

    @Test
    public void testSerializeVTable4() throws IOException, SedsException {
        test("vtable4", new SampleVFactory().newVTable4());
    }

    @Test
    public void testSerializeVTable5() throws IOException, SedsException {
        test("vtable5", new SampleVFactory().newVTable5());
    }

    @Test
    public void testSerializeVTable6() throws IOException, SedsException {
        test("vtable6", new SampleVFactory().newVTable6());
    }

    @Test
    public void testSerializeVTable7() throws IOException, SedsException {
        test("vtable7", new SampleVFactory().newVTable7());
    }
}
