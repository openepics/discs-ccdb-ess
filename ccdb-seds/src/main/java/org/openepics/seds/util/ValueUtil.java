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

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.JsonObject;

import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.core.Seds;

/**
 * Utility for useful constant values of the API.
 *
 * @author asbarber
 */
public class ValueUtil {

    //Constants
    //--------------------------------------------------------------------------
    /**
     * Protocol of the API.
     */
    public static final String PROTOCOL = "SEDSv1";

    /**
     * Current version of the API.
     */
    public static final String VERSION = "1.0.0";

    /**
     * Encoding method of the API.
     */
    public static final String ENCODING = "UTF-8";
    //--------------------------------------------------------------------------

    //Types
    //--------------------------------------------------------------------------
    /**
     * Mapping of names and types of the SEDS protocol.
     */
    public static final Map<String, Class> TYPENAMES;

    /**
     * Mapping of names and types of the SEDS protocol for structures that use a
     * generic.
     */
    public static final Map<String, Entry<Class, ScalarType>> TYPENAMES_GENERICS;

    /**
     * String combining {@link #TYPENAMES} and {@link #TYPENAMES_GENERICS}/
     */
    public static final String TYPENAMES_LIST;

    static {
        TYPENAMES = new LinkedHashMap<>();

        TYPENAMES.put("SedsAlarm", SedsAlarm.class);
        TYPENAMES.put("SedsControl", SedsControl.class);
        TYPENAMES.put("SedsDisplay", SedsDisplay.class);
        TYPENAMES.put("SedsEnum", SedsEnum.class);
        TYPENAMES.put("SedsMetadata", SedsMetadata.class);
        TYPENAMES.put("SedsScalar", SedsScalar.class);
        TYPENAMES.put("SedsScalarArray", SedsScalarArray.class);
        TYPENAMES.put("SedsTable", SedsTable.class);
        TYPENAMES.put("SedsTime", SedsTime.class);
        TYPENAMES.put("SedsType", SedsType.class);

        TYPENAMES_GENERICS = new LinkedHashMap<>();

        TYPENAMES_GENERICS.put("SedsScalar_Boolean", new IEntry(SedsScalar.class, ScalarType.BOOLEAN));
        TYPENAMES_GENERICS.put("SedsScalar_Enum", new IEntry(SedsScalar.class, ScalarType.ENUM));
        TYPENAMES_GENERICS.put("SedsScalar_Integer", new IEntry(SedsScalar.class, ScalarType.INTEGER));
        TYPENAMES_GENERICS.put("SedsScalar_Number", new IEntry(SedsScalar.class, ScalarType.NUMBER));
        TYPENAMES_GENERICS.put("SedsScalar_String", new IEntry(SedsScalar.class, ScalarType.STRING));

        TYPENAMES_GENERICS.put("SedsScalarArray_Boolean", new IEntry(SedsScalarArray.class, ScalarType.BOOLEAN));
        TYPENAMES_GENERICS.put("SedsScalarArray_Enum", new IEntry(SedsScalarArray.class, ScalarType.ENUM));
        TYPENAMES_GENERICS.put("SedsScalarArray_Integer", new IEntry(SedsScalarArray.class, ScalarType.INTEGER));
        TYPENAMES_GENERICS.put("SedsScalarArray_Number", new IEntry(SedsScalarArray.class, ScalarType.NUMBER));
        TYPENAMES_GENERICS.put("SedsScalarArray_String", new IEntry(SedsScalarArray.class, ScalarType.STRING));

        TYPENAMES_LIST = "{"
                + Arrays.deepToString(TYPENAMES.keySet().toArray())
                + Arrays.deepToString(TYPENAMES_GENERICS.keySet().toArray())
                + "}";
    }
    //--------------------------------------------------------------------------

    //Documentation package
    //--------------------------------------------------------------------------
    /**
     * Path specifying the location of the schema repository (JSON schema files
     * to use for validation).
     */
    public static final String SCHEMA_PATH = "schema/";
    //--------------------------------------------------------------------------

    //Schema Files
    //--------------------------------------------------------------------------
    /**
     * JSON schema file for a SEDS type JSON structure (containing meta data and
     * raw data).
     */
    public static final String SCHEMA_SEDS_TYPE;
    public static final String SCHEMA_SEDS_TYPE_PATH = SCHEMA_PATH + "SEDS_type.json";

    /**
     * JSON schema file for a SEDS raw JSON structure (containing raw data).
     */
    public static final String SCHEMA_SEDS_RAW;
    public static final String SCHEMA_SEDS_RAW_PATH = SCHEMA_PATH + "SEDS_raw.json";

    /**
     * JSON schema file for a SEDS meta JSON structure (containing meta data).
     */
    public static final String SCHEMA_SEDS_META;
    public static final String SCHEMA_SEDS_META_PATH = SCHEMA_PATH + "SEDS_meta.json";

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        SCHEMA_SEDS_TYPE = streamToString(loader.getResourceAsStream(SCHEMA_SEDS_TYPE_PATH));
        SCHEMA_SEDS_RAW = streamToString(loader.getResourceAsStream(SCHEMA_SEDS_RAW_PATH));
        SCHEMA_SEDS_META = streamToString(loader.getResourceAsStream(SCHEMA_SEDS_META_PATH));
    }

    private static String streamToString(InputStream jsonStream) {
        // This uses the scanner to read the entire stream (file) as one token. It is a "read all" operation.
        try(java.util.Scanner s = new java.util.Scanner(jsonStream)) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

    //--------------------------------------------------------------------------

    //Version Check
    //--------------------------------------------------------------------------
    private static final String KEY_VERSION = "version";

    private static void assertVersion() throws Exception {
        String[] files = {
            SCHEMA_SEDS_TYPE_PATH,
            SCHEMA_SEDS_RAW_PATH,
            SCHEMA_SEDS_META_PATH
        };

        JsonObject[] schemas = new JsonObject[]{
            Seds.newReader().read(SCHEMA_SEDS_TYPE),
            Seds.newReader().read(SCHEMA_SEDS_RAW),
            Seds.newReader().read(SCHEMA_SEDS_META)
        };

        for (int i = 0; i < schemas.length; ++i) {
            try {
                String key = schemas[i].getJsonString(KEY_VERSION).getString();

                if (!key.equals(VERSION)) {
                    throw new IllegalStateException(
                            "The name-value pair ["
                            + KEY_VERSION + ": "
                            + key
                            + "] in the schema file ["
                            + files[i]
                            + "] does not match the "
                            + "version of the Java API ["
                            + KEY_VERSION + ": "
                            + VERSION
                            + "]. "
                            + "It is required that "
                            + "the version of the schemas "
                            + "match the version of the API"
                    );
                }
            } catch (ClassCastException | NullPointerException e) {
                throw new IllegalStateException(
                        "The schema file ["
                        + files[i]
                        + "] is missing the name-value pair "
                        + "for the name ["
                        + KEY_VERSION
                        + "] of the JSON type [String]."
                        + "This property is required "
                        + "to ensure that the version "
                        + "of the schemas match the "
                        + "version of the API"
                );
            }
        }
    }

    static {
        try {
            assertVersion();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    private static class IEntry<K, V> implements Entry<K, V> {

        private final K key;
        private final V value;

        public IEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V v) {
            throw new UnsupportedOperationException("Not supported.");
        }

    }
    //--------------------------------------------------------------------------
}
