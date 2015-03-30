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
package org.openepics.seds.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SampleSedsFactory;
import org.openepics.seds.util.SedsException;

/**
 *
 * @author Aaron Barber
 */
public class DBConverterTest {

    private JsonObject read(String name) throws IOException {
        //Message
        System.out.println("deserialize_" + name);

        //Files
        File data = FileUtil.get(FileUtil.CORE_IO, name);

        //Reads
        try (InputStream in = new FileInputStream(data)) {
            //Reads
            return Seds.newReader().read(in);
        }
    }

    private void serialize(String name, SedsType obj) throws IOException, SedsException {

        //Message
        System.out.println("serialize_" + name);

        //Files
        File expected = FileUtil.get(FileUtil.CORE_IO, name);
        File actual = FileUtil.get(FileUtil.CORE_IO, name + FileUtil.FAILED);
        actual.createNewFile();

        //Writes
        try (OutputStream out = new FileOutputStream(actual)) {

            //Writes
            Seds.newWriter().write(Seds.newDBConverter().serialize(obj), out);

            //Tests
            assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));
        }

        actual.delete();
    }

    private void deserialize(String name, SedsType expected) throws IOException, SedsException {
        SedsType actual = Seds.newDBConverter().deserialize(read(name));
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testSerializeNormal() throws Exception {
        serialize("db_normal", new SampleSedsFactory().sampleSedsScalarArrayInteger());
    }

    @Test
    public void testSerializeEnum() throws Exception {
        serialize("db_enum", new SampleSedsFactory().sampleSedsEnum());
    }

    @Test
    public void testSerializeScalarEnum() throws Exception {
        serialize("db_scalar", new SampleSedsFactory().sampleSedsScalarEnum());
    }

    @Test
    public void testSerializeScalarArrayEnum() throws Exception {
        serialize("db_scalar_array", new SampleSedsFactory().sampleDB_SedsScalarArrayEnum());
    }

    @Test
    public void testSerializeScalarArrayEnum_fails() throws Exception {
        String name = "db_scalar_array_expected_failure";
        SedsType actual = new SampleSedsFactory().sampleSedsScalarArrayEnum();

        //Message
        System.out.println("serialize_" + name);

        //Serializes
        try {
            String x = Seds.newWriter().write(Seds.newDBConverter().serialize(actual));
            fail("Expected an exception when serializing");
        } catch (IllegalArgumentException ex) {
            //success
        }
    }

    @Test
    public void testSerializeTable() throws Exception {
        serialize("db_table", new SampleSedsFactory().sampleSedsTable());
    }

    @Test
    public void testDeserializeNormal() throws Exception {
        deserialize("db_normal", new SampleSedsFactory().sampleSedsScalarArrayInteger());
    }

    @Test
    public void testDeserializeEnum() throws Exception {
        deserialize("db_enum", new SampleSedsFactory().sampleSedsEnum());
    }

    @Test
    public void testDeserializeScalarEnum() throws Exception {
        deserialize("db_scalar", new SampleSedsFactory().sampleSedsScalarEnum());
    }

    @Test
    public void testDeserializeScalarArrayEnum() throws Exception {
        deserialize("db_scalar_array", new SampleSedsFactory().sampleDB_SedsScalarArrayEnum());
    }

    @Test
    public void testDeserializeTable() throws Exception {
        deserialize("db_table", new SampleSedsFactory().sampleSedsTable());
    }

}
