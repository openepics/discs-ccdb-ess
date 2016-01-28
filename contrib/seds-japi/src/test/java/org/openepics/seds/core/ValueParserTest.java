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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.openepics.seds.util.JsonUtil.ValueParser;
import static org.openepics.seds.util.JsonUtil.ValueParser.parser;

public class ValueParserTest {

    /**
     * Test of asNumber method, of class ValueParser.
     */
    @Test
    public void testAsNumber() {
        System.out.println("asNumber");

        Long initial = 10L;

        JsonObject value = Json.createObjectBuilder().add("key", initial).build();
        ValueParser instance = parser();

        Double expected = (double) 10;
        Number actual = instance.asNumber(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Number, equalTo(true));
        assertThat(actual instanceof Double, equalTo(true));
        assertThat(actual instanceof Long, equalTo(false));
    }

    /**
     * Test of asNumber method, of class ValueParser.
     */
    @Test
    public void testAsNumber2() {
        System.out.println("asNumber2");

        Long initial = 10L;

        JsonObject value = Json.createObjectBuilder().add("key", initial).build();
        ValueParser instance = parser();

        Byte expected = (byte) 10;
        Number actual = instance.asByte(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Number, equalTo(true));
        assertThat(actual instanceof Byte, equalTo(true));
        assertThat(actual instanceof Long, equalTo(false));
    }

    /**
     * Test of asNumber method, of class ValueParser.
     */
    @Test
    public void testAsNumber3() {
        System.out.println("asNumber3");

        Byte initial = (byte) 10;

        JsonObject value = Json.createObjectBuilder().add("key", initial).build();
        ValueParser instance = parser();

        Long expected = (long) 10;
        Number actual = instance.asLong(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Number, equalTo(true));
        assertThat(actual instanceof Byte, equalTo(false));
        assertThat(actual instanceof Long, equalTo(true));
    }

    /**
     * Test of asBoolean method, of class ValueParser.
     */
    @Test
    public void testAsBoolean() {
        System.out.println("asBoolean");

        JsonObject value = Json.createObjectBuilder().add("key", true).build();
        ValueParser instance = parser();

        Boolean expected = true;
        Boolean actual = instance.asBoolean(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Boolean, equalTo(true));
    }

    /**
     * Test of asByte method, of class ValueParser.
     */
    @Test
    public void testAsByte() {
        System.out.println("asByte");

        JsonObject value = Json.createObjectBuilder().add("key", 10).build();
        ValueParser instance = parser();

        Byte expected = (byte) 10;
        Byte actual = instance.asByte(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Byte, equalTo(true));
    }

    /**
     * Test of asDouble method, of class ValueParser.
     */
    @Test
    public void testAsDouble() {
        System.out.println("asDouble");

        JsonObject value = Json.createObjectBuilder().add("key", 10).build();
        ValueParser instance = parser();

        Double expected = (double) 10;
        Double actual = instance.asDouble(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Double, equalTo(true));
    }

    /**
     * Test of asFloat method, of class ValueParser.
     */
    @Test
    public void testAsFloat() {
        System.out.println("asFloat");

        JsonObject value = Json.createObjectBuilder().add("key", 10).build();
        ValueParser instance = parser();

        Float expected = (float) 10;
        Float actual = instance.asFloat(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Float, equalTo(true));
    }

    /**
     * Test of asInteger method, of class ValueParser.
     */
    @Test
    public void testAsInteger() {
        System.out.println("asInteger");

        JsonObject value = Json.createObjectBuilder().add("key", 10).build();
        ValueParser instance = parser();

        Integer expected = (int) 10;
        Integer actual = instance.asInteger(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Integer, equalTo(true));
    }

    /**
     * Test of asLong method, of class ValueParser.
     */
    @Test
    public void testAsLong() {
        System.out.println("asLong");

        JsonObject value = Json.createObjectBuilder().add("key", 10).build();
        ValueParser instance = parser();

        Long expected = (long) 10;
        Long actual = instance.asLong(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Long, equalTo(true));
    }

    /**
     * Test of asShort method, of class ValueParser.
     */
    @Test
    public void testAsShort() {
        System.out.println("asShort");

        JsonObject value = Json.createObjectBuilder().add("key", 10).build();
        ValueParser instance = parser();

        Short expected = (short) 10;
        Short actual = instance.asShort(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof Short, equalTo(true));
    }

    /**
     * Test of asString method, of class ValueParser.
     */
    @Test
    public void testAsString() {
        System.out.println("asString");

        JsonObject value = Json.createObjectBuilder().add("key", "10").build();
        ValueParser instance = parser();

        String expected = "10";
        String actual = instance.asString(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof String, equalTo(true));
    }

    /**
     * Test of asArray method, of class ValueParser.
     */
    @Test
    public void testAsArray() {
        System.out.println("asArray");

        JsonArray expected = Json.createArrayBuilder()
                .add((int) 1)
                .add((double) 2.5)
                .add((long) 10)
                .add((String) "A")
                .build();

        JsonObject value = Json.createObjectBuilder().add("key", expected).build();
        ValueParser instance = parser();

        JsonArray actual = instance.asArray(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof JsonArray, equalTo(true));
    }

    /**
     * Test of asObject method, of class ValueParser.
     */
    @Test
    public void testAsObject() {
        System.out.println("asObject");

        JsonObject expected = Json.createObjectBuilder()
                .add("first", (int) 1)
                .add("second", (double) 2.5)
                .add("third", (long) 10)
                .add("fourth", (String) "A")
                .addNull("fifth")
                .build();

        JsonObject value = Json.createObjectBuilder().add("key", expected).build();
        ValueParser instance = parser();

        JsonObject actual = instance.asObject(value, "key");

        assertEquals(expected, actual);
        assertThat(actual instanceof JsonObject, equalTo(true));
    }

}
