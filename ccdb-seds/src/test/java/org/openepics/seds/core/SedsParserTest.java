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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SampleSedsFactory;
import org.openepics.seds.util.SedsException;

/**
 *
 * @author asbarber
 */
public class SedsParserTest {

    private JsonObject read(String name) throws IOException {
        //Message
        System.out.println("deserialize_" + name);

        //Files
        File data = FileUtil.get(FileUtil.CORE_SEDS, name);

        //Reads
        try (InputStream in = new FileInputStream(data)) {
            //Reads
            return Seds.newReader().read(in);
        }
    }

    private void test(String name, SedsType expected) throws IOException, SedsException {
        SedsType actual = Seds.newDeserializer().deserializeSEDS(read(name));
        assertThat(actual, equalTo(expected));
    }

    /**
     * Test of deserializeAlarm method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseAlarm() throws Exception {
        test("alarm", new SampleSedsFactory().sampleSedsAlarm());
    }

    /**
     * Test of deserializeControl method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseControl() throws Exception {
        test("control", new SampleSedsFactory().sampleSedsControl());
    }

    /**
     * Test of deserializeDisplay method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseDisplay() throws Exception {
        test("display", new SampleSedsFactory().sampleSedsDisplay());
    }

    /**
     * Test of deserializeEnum method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseEnum() throws Exception {
        test("enum", new SampleSedsFactory().sampleSedsEnum());
    }

    /**
     * Test of deserializeTime method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseTime() throws Exception {
        test("time", new SampleSedsFactory().sampleSedsTime());
    }

    /**
     * Test of deserializeScalarBoolean method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarBoolean() throws Exception {
        test("scalar_boolean", new SampleSedsFactory().sampleSedsScalarBoolean());
    }

    /**
     * Test of deserializeScalarEnum method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarEnum() throws Exception {
        test("scalar_enum", new SampleSedsFactory().sampleSedsScalarEnum());
    }

    /**
     * Test of deserializeScalarInteger method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarInteger() throws Exception {
        test("scalar_integer", new SampleSedsFactory().sampleSedsScalarInteger());
    }

    /**
     * Test of deserializeScalarNumber method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarNumber() throws Exception {
        test("scalar_number", new SampleSedsFactory().sampleSedsScalarNumber());
    }

    /**
     * Test of deserializeScalarString method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarString() throws Exception {
        test("scalar_string", new SampleSedsFactory().sampleSedsScalarString());
    }

    /**
     * Test of deserializeScalarArrayBoolean method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarArrayBoolean() throws Exception {
        test("scalararray_boolean", new SampleSedsFactory().sampleSedsScalarArrayBoolean());
    }

    /**
     * Test of deserializeScalarArrayEnum method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarArrayEnum() throws Exception {
        test("scalararray_enum", new SampleSedsFactory().sampleSedsScalarArrayEnum());
    }

    /**
     * Test of deserializeScalarArrayInteger method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarArrayInteger() throws Exception {
        test("scalararray_integer", new SampleSedsFactory().sampleSedsScalarArrayInteger());
    }

    /**
     * Test of deserializeScalarArrayNumber method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarArrayNumber() throws Exception {
        test("scalararray_number", new SampleSedsFactory().sampleSedsScalarArrayNumber());
    }

    /**
     * Test of deserializeScalarArrayString method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseScalarArrayString() throws Exception {
        test("scalararray_string", new SampleSedsFactory().sampleSedsScalarArrayString());
    }

    /**
     * Test of deserializeTable method, of class BaseDeserializer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseTable() throws Exception {
        test("table", new SampleSedsFactory().sampleSedsTable());
    }

}
