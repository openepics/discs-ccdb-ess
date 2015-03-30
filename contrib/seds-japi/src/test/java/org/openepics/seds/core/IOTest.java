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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openepics.seds.util.FileUtil;

/**
 *
 * @author asbarber
 */
public class IOTest {

    @Test
    public void testWrite() throws IOException {
        //Data
        String name = "IO_write";
        System.out.println(name);

        //Files
        File expected = FileUtil.get(FileUtil.CORE, name);
        File actual = FileUtil.get(FileUtil.CORE, name + FileUtil.FAILED);
        actual.createNewFile();

        //Writes
        try (OutputStream out = new FileOutputStream(actual)) {

            //Writes
            JsonObject obj = Json.createObjectBuilder()
                    .add("id", 5001)
                    .add("name", "Acme_Dynamite")
                    .add("price", 12.50)
                    .build();
            Seds.newWriter().write(obj, out);

            //Tests
            assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));
        }
        actual.delete();
    }

    /**
     * Test of read method, of class SedsUtil.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testRead() throws IOException {
        //Data
        String name = "IO_read";
        System.out.println(name);

        //Files
        File data = FileUtil.get(FileUtil.CORE, "IO_write");

        //Reads
        try (InputStream in = new FileInputStream(data)) {
            //Reads
            JsonObject actual = Seds.newReader().read(in);

            //Expected
            JsonObject expected = Json.createObjectBuilder()
                    .add("id", 5001)
                    .add("name", "Acme_Dynamite")
                    .add("price", 12.50)
                    .build();

            //Tests
            assertThat(expected, equalTo(actual));
        }
    }

}
