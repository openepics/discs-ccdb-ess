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

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * Utility for quickly building and parsing JsonObjects.
 *
 * @author Aaron Barber
 */
public class JsonUtil {

    /**
     * Builder object for creating a JsonObject without having to ensure type
     * correctness. <b>It is recommended that client's do NOT use this
     * object</b>, as it can cause puzzling issues when values are expected to
     * be added to a JsonObject but are not.
     */
    public static class ValueBuilder {

        /**
         * Factory creation of this builder.
         *
         * @return a builder for JsonObject values
         */
        public static ValueBuilder builder() {
            return new ValueBuilder();
        }

        //Datum
        //----------------------------------------------------------------------            
        private final JsonObjectBuilder builder;
        //----------------------------------------------------------------------            

        //Constructor
        //----------------------------------------------------------------------            
        ValueBuilder() {
            this.builder = Json.createObjectBuilder();
        }
        //----------------------------------------------------------------------            

        //Add
        //----------------------------------------------------------------------  
        /**
         * Does nothing, because it uses
         * {@link #put(java.lang.String, java.lang.Object)} with a value of
         * null, which does not get added to the builder.
         *
         * @param name name portion of JSON name-value mapping
         * @return this
         */
        public ValueBuilder put(String name) {
            return put(name, null);
        }

        /**
         * Will NOT put the name-value pair if the value is null, but will put
         * the pair if the value is NOT null.
         *
         * @param name name portion of JSON name-value mapping
         * @param value value portion of JSON name-value mapping
         * @return this
         */
        public ValueBuilder put(String name, Object value) {
            if (value == null) {
                //Do not serialize
                return this;
            } else {
                return forcePut(name, value);
            }
        }

        /**
         * Will always put the name-value pair, even if the value is null.
         *
         * @param name name portion of JSON name-value mapping
         * @param value value portion of JSON name-value mapping
         * @return this
         */
        public ValueBuilder forcePut(String name, Object value) {
            if (value == null) {
                builder.addNull(name);
            } else if (value instanceof JsonValue) {
                builder.add(name, (JsonValue) value);
            } else if (value instanceof BigInteger) {
                builder.add(name, (BigInteger) value);
            } else if (value instanceof BigDecimal) {
                builder.add(name, (BigDecimal) value);
            } else if (value instanceof Boolean) {
                builder.add(name, (Boolean) value);
            } else if (value instanceof Byte) {
                builder.add(name, (Byte) value);
            } else if (value instanceof Double) {
                if (Double.isNaN((double) value) || Double.isInfinite((double) value)) {
                    //Do not add NaNs
                } else {
                    builder.add(name, (Double) value);
                }
            } else if (value instanceof Float) {
                if (Float.isNaN((float) value) || Float.isInfinite((float) value)) {
                    //Do not add NaNs
                } else {
                    builder.add(name, (Float) value);
                }
            } else if (value instanceof Integer) {
                builder.add(name, (Integer) value);
            } else if (value instanceof Long) {
                builder.add(name, (Long) value);
            } else if (value instanceof Short) {
                builder.add(name, (Short) value);
            } else if (value instanceof String) {
                builder.add(name, (String) value);
            } else if (value instanceof ValueBuilder) {
                builder.add(name, ((ValueBuilder) value).build());
            } else if (value instanceof JsonObjectBuilder) {
                builder.add(name, (JsonObjectBuilder) value);
            } else if (value instanceof JsonArrayBuilder) {
                builder.add(name, (JsonArrayBuilder) value);
            } else {
                throw SedsException.buildIAE(
                        value,
                        JsonValue.class,
                        "Adding the name-value pair to the JSON object"
                );
            }

            return this;
        }
        //----------------------------------------------------------------------            

        //Finalize
        //----------------------------------------------------------------------   
        /**
         * Finalizes the builder into a JsonObject.
         *
         * @return JsonObject from the name-value pairs added
         */
        public JsonObject build() {
            return builder.build();
        }
        //----------------------------------------------------------------------            
    }

    /**
     * Processing object for parsing a JsonObject that performs type casting.
     * <b>It is recommended that client's do NOT use this object</b>, as it can
     * cause puzzling issues when values are expected to be parsed from the
     * JsonObject but are not. Additionally, NullExpectionPointers are caught
     * when a value does not contain a key.
     */
    public static class ValueParser {

        /**
         * Factory creation of this parser.
         *
         * @return a parser for JsonObject values
         */
        public static ValueParser parser() {
            return new ValueParser();
        }

        //Primitive JSON Values
        //----------------------------------------------------------------------   
        /**
         * Obtains the value as a number (double) that corresponds to the given
         * key (taken from the name-value pairs of the JsonObject parameter). If
         * the key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a number (double), null
         * if unable to find the key
         */
        public Number asNumber(JsonObject value, String key) {
            return asDouble(value, key);
        }

        /**
         * Obtains the value as a boolean that corresponds to the given key
         * (taken from the name-value pairs of the JsonObject parameter). If the
         * key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a boolean, null if
         * unable to find the key
         */
        public Boolean asBoolean(JsonObject value, String key) {
            try {
                return value.getBoolean(key);
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a byte that corresponds to the given key (taken
         * from the name-value pairs of the JsonObject parameter). If the key is
         * not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a byte, null if unable
         * to find the key
         */
        public Byte asByte(JsonObject value, String key) {
            try {
                return (byte) value.getJsonNumber(key).doubleValue();
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a double that corresponds to the given key
         * (taken from the name-value pairs of the JsonObject parameter). If the
         * key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a double, null if
         * unable to find the key
         */
        public Double asDouble(JsonObject value, String key) {
            try {
                return (double) value.getJsonNumber(key).doubleValue();
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a float that corresponds to the given key (taken
         * from the name-value pairs of the JsonObject parameter). If the key is
         * not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a float, null if unable
         * to find the key
         */
        public Float asFloat(JsonObject value, String key) {
            try {
                return (float) value.getJsonNumber(key).doubleValue();
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a integer that corresponds to the given key
         * (taken from the name-value pairs of the JsonObject parameter). If the
         * key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a integer, null if
         * unable to find the key
         */
        public Integer asInteger(JsonObject value, String key) {
            try {
                return value.getJsonNumber(key).intValue();
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a long that corresponds to the given key (taken
         * from the name-value pairs of the JsonObject parameter). If the key is
         * not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a long, null if unable
         * to find the key
         */
        public Long asLong(JsonObject value, String key) {
            try {
                return value.getJsonNumber(key).longValue();
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a short that corresponds to the given key (taken
         * from the name-value pairs of the JsonObject parameter). If the key is
         * not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a short, null if unable
         * to find the key
         */
        public Short asShort(JsonObject value, String key) {
            try {
                return (short) value.getJsonNumber(key).doubleValue();
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a string that corresponds to the given key
         * (taken from the name-value pairs of the JsonObject parameter). If the
         * key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a string, null if
         * unable to find the key
         */
        public String asString(JsonObject value, String key) {
            try {
                return value.getString(key);
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a JsonArray that corresponds to the given key
         * (taken from the name-value pairs of the JsonObject parameter). If the
         * key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a JsonArray, null if
         * unable to find the key
         */
        public JsonArray asArray(JsonObject value, String key) {
            try {
                return value.getJsonArray(key);
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Obtains the value as a JsonObject that corresponds to the given key
         * (taken from the name-value pairs of the JsonObject parameter). If the
         * key is not found or the JsonObject is null, returns null.
         *
         * @param value collection of JSON name-value pairs that contains the
         * key
         * @param key name portion of a JSON name-value pair matching, key to
         * find the value of
         * @return the value corresponding to the key as a JsonObject, null if
         * unable to find the key
         */
        public JsonObject asObject(JsonObject value, String key) {
            try {
                return value.getJsonObject(key);
            } catch (NullPointerException e) {
                return null;
            }
        }
        //----------------------------------------------------------------------   
    }
}
