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

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.openepics.seds.util.AlarmType;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.JsonUtil.ValueBuilder;
import static org.openepics.seds.util.JsonUtil.ValueBuilder.builder;

/**
 *
 * @author asbarber
 */
public class ValueBuilderTest {

    /**
     * Test of put method, of class ValueBuilder.
     */
    @Test
    public void testPut_String() {
        System.out.println("put");
        String keyA = "";
        String keyB = "key";

        JsonObject instance = ValueBuilder.builder()
                .put(keyA)
                .put(keyB)
                .put(null)
                .build();

        assertThat(instance.containsKey(keyA), equalTo(false));
        assertThat(instance.containsKey(keyB), equalTo(false));
        assertThat(instance.containsKey(null), equalTo(false));
    }

    /**
     * Test of put method, of class ValueBuilder.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testPut_String_Object() throws IOException {
        System.out.println("put");

        int x = 5;

        Object valA = x;
        Object valB = "5";
        Object valC = 5.0;
        Object valD = new Float(5);
        Object valG = null;
        Object valH = Json.createArrayBuilder().build();
        Object valI = Json.createArrayBuilder().addNull().build();
        Object valJ = Json.createArrayBuilder().add(1).addNull().add(3).build();
        Object valK = builder().build();
        Object valL = builder().put("key").build();
        Object valM = builder().put("key", JsonValue.NULL).build();
        Object valN = builder().put("a", JsonValue.NULL).put("b", JsonValue.NULL).build();
        Object valO = Json.createArrayBuilder().add(1).add(2).add("3").build();
        Object valP = builder().put("a", "a").put("b", "b").build();
        Object valR = Double.NaN;
        Object valS = Double.POSITIVE_INFINITY;

        JsonObject instance = builder()
                .put("A", valA)
                .put("B", valB)
                .put("C", valC)
                .put("D", valD)
                .put("G", valG)
                .put("H", valH)
                .put("I", valI)
                .put("J", valJ)
                .put("K", valK)
                .put("L", valL)
                .put("M", valM)
                .put("N", valN)
                .put("O", valO)
                .put("P", valP)
                .put("R", valR)
                .put("S", valS)
                .put(null)
                .build();

        //Data
        String name = "ValueBuilder_put";

        //Files
        File expected = FileUtil.get(FileUtil.CORE, name);
        File actual = FileUtil.get(FileUtil.CORE, name + FileUtil.FAILED);
        actual.createNewFile();
        OutputStream out = new FileOutputStream(actual);

        //Writes
        Seds.newWriter().write(instance, out);

        //Tests
        assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));

        //Cleans-up
        out.close();
        actual.delete();
    }

    /**
     * Test of canPut method, of class ValueBuilder.
     */
    @Test
    public void testCanPut() {
        System.out.println("canPut");

        Object[] exception = new Object[]{
            new Point(1, 2),
            Seds.newFactory().newAlarm(AlarmType.MAJOR, "2", ""),
            int.class,
            new int[]{1, 2, 3},
            new Object()
        };

        Object[] cannotPut = new Object[]{
            null,
            Double.NaN,
            Float.NaN,
            Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY
        };

        Object[] canPut = new Object[]{
            1,
            "2",
            1d,
            1l,
            true,
            JsonValue.FALSE,
            JsonValue.NULL,
            builder(),
            Json.createArrayBuilder(),
            Json.createObjectBuilder(),
            Json.createArrayBuilder().build(),
            Json.createObjectBuilder().build()
        };

        //Tests
        ValueBuilder instance = builder();

        for (Object object : exception) {
            try {
                instance.put("test", object);
                fail("Expected that putting the object would cause an exception");
            } catch (IllegalArgumentException e) {
                //success
            }
        }

        for (Object object : cannotPut) {
            instance.put("test", object);
            assertThat(instance.build().containsKey("test"), equalTo(false));
            instance = builder();
        }
        for (Object object : canPut) {
            instance.put("test", object);
            assertThat(instance.build().containsKey("test"), equalTo(true));
            instance = builder();
        }
    }

    /**
     * Test of build method, of class ValueBuilder.
     */
    @Test
    public void testBuild() {
        System.out.println("build");

        JsonObject actual = builder()
                .put("A", 1)
                .put("B", true)
                .build();
        JsonObject expected = Json.createObjectBuilder()
                .add("A", 1)
                .add("B", true)
                .build();

        assertEquals(actual, expected);
    }

}
