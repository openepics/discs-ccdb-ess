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
package org.openepics.seds.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openepics.seds.api.SedsSerializer;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SampleSedsFactory;
import org.openepics.seds.util.SedsException;

public class SedsJsonBuilderTest {

    private void test(String name, SedsType obj) throws IOException, SedsException {
        //Data
        SedsSerializer instance = Seds.newSerializer();

        //Message
        System.out.println("serialize_" + name);

        //Files
        File expected = FileUtil.get(FileUtil.CORE_SEDS, name);
        File actual = FileUtil.get(FileUtil.CORE_SEDS, name + FileUtil.FAILED);
        actual.createNewFile();

        //Writes
        try (OutputStream out = new FileOutputStream(actual)) {

            //Writes
            Seds.newWriter().write(instance.serializeSEDS(obj), out);

            //Tests
            assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));
        }

        actual.delete();
    }

    /**
     * Test of serializeAlarm method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildAlarm() throws IOException, SedsException {
        test("alarm", new SampleSedsFactory().sampleSedsAlarm());
    }

    /**
     * Test of serializeControl method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildControl() throws IOException, SedsException {
        test("control", new SampleSedsFactory().sampleSedsControl());
    }

    /**
     * Test of serializeDisplay method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildDisplay() throws IOException, SedsException {
        test("display", new SampleSedsFactory().sampleSedsDisplay());
    }

    /**
     * Test of serializeEnum method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildEnum() throws IOException, SedsException {
        test("enum", new SampleSedsFactory().sampleSedsEnum());
    }

    /**
     * Test of serializeTime method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildTime() throws IOException, SedsException {
        test("time", new SampleSedsFactory().sampleSedsTime());
    }

    /**
     * Test of serializeScalarBoolean method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarBoolean() throws IOException, SedsException {
        test("scalar_boolean", new SampleSedsFactory().sampleSedsScalarBoolean());
    }

    /**
     * Test of serializeScalarEnum method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarEnum() throws IOException, SedsException {
        test("scalar_enum", new SampleSedsFactory().sampleSedsScalarEnum());
    }

    /**
     * Test of serializeScalarInteger method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarInteger() throws IOException, SedsException {
        test("scalar_integer", new SampleSedsFactory().sampleSedsScalarInteger());
    }

    /**
     * Test of serializeScalarNumber method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarNumber() throws IOException, SedsException {
        test("scalar_number", new SampleSedsFactory().sampleSedsScalarNumber());
    }

    /**
     * Test of serializeScalarString method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarString() throws IOException, SedsException {
        test("scalar_string", new SampleSedsFactory().sampleSedsScalarString());
    }

    /**
     * Test of serializeScalarArrayBoolean method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarArrayBoolean() throws IOException, SedsException {
        test("scalararray_boolean", new SampleSedsFactory().sampleSedsScalarArrayBoolean());
    }

    /**
     * Test of serializeScalarArrayEnum method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarArrayEnum() throws IOException, SedsException {
        test("scalararray_enum", new SampleSedsFactory().sampleSedsScalarArrayEnum());
    }

    /**
     * Test of serializeScalarArrayInteger method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarArrayInteger() throws IOException, SedsException {
        test("scalararray_integer", new SampleSedsFactory().sampleSedsScalarArrayInteger());
    }

    /**
     * Test of serializeScalarArrayNumber method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarArrayNumber() throws IOException, SedsException {
        test("scalararray_number", new SampleSedsFactory().sampleSedsScalarArrayNumber());
    }

    /**
     * Test of serializeScalarArrayString method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildScalarArrayString() throws IOException, SedsException {
        test("scalararray_string", new SampleSedsFactory().sampleSedsScalarArrayString());
    }

    /**
     * Test of serializeTable method, of class BaseSerializer.
     *
     * @throws java.io.IOException
     * @throws org.openepics.seds.util.SedsException
     */
    @Test
    public void testBuildTable() throws IOException, SedsException {
        test("table", new SampleSedsFactory().sampleSedsTable());
    }

}
