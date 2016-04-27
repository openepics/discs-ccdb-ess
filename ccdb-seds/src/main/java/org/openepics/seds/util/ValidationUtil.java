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

import static org.openepics.seds.util.SedsException.assertNotNull;

import java.io.IOException;

import javax.json.JsonObject;

import org.openepics.seds.api.SedsValidator;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * Utility for validation of JSON schemas.
 *
 * @author Aaron Barber
 */
public class ValidationUtil {

    //Type of check
    //--------------------------------------------------------------------------
    /**
     * General type describing what sort of SEDS data is represented (the meta
     * data, the raw data, or both).
     */
    public static enum DataType {

        /**
         * SEDS with meta data and raw data.
         */
        SEDS_TYPE,
        /**
         * SEDS with meta data only.
         */
        SEDS_META,
        /**
         * SEDS with raw data only.
         */
        SEDS_RAW
    }
    //--------------------------------------------------------------------------

    //Checks
    //--------------------------------------------------------------------------
    /**
     * Fails with an exception if the JSON structure is invalid.
     *
     * @param v validating tool
     * @param type indicator for what validity method to call
     * @param value structure to verify
     * @return value (unmodified)
     * @throws SedsException if the structure is invalid
     */
    @SuppressWarnings({"ThrowableResultIgnored", "ThrowableInstanceNotThrown", "ThrowableInstanceNeverThrown"})
    public static JsonObject assertValid(
            SedsValidator v,
            DataType type,
            JsonObject value
    ) throws SedsException {
        assertNotNull(v, SedsValidator.class, "Validating the SEDS Json data");
        assertNotNull(type, DataType.class, "Type of SEDS (full object, meta data, or raw data) being validated");
        assertNotNull(value, JsonObject.class, "JSON data being validated");

        switch (type) {
            case SEDS_TYPE:
                if (!v.isValidSEDS(value)) {
                    throw new SedsException("Error in validating the SEDS (data and metadata).")
                            .attachLog(v.validateSEDS(value))
                            .attachJson(value);
                }
                break;
            case SEDS_META:
                if (!v.isValidSEDSMeta(value)) {
                    throw new SedsException("Error in validating the SEDS metadata.")
                            .attachLog(v.validateSEDSMeta(value))
                            .attachJson(value);
                }
                break;
            case SEDS_RAW:
                if (!v.isValidSEDSRaw(value)) {
                    throw new SedsException("Error in validating the SEDS raw data.")
                            .attachLog(v.validateSEDSRaw(value))
                            .attachJson(value);
                }
                break;
        }

        return value;
    }

    /**
     * Checks whether the JSON structure is valid according to the schema.
     * <p>
     * An exception WILL NOT be thrown if the JSON structure is invalid. Rather,
     * the reason for invalidity is provided in the returned report.
     *
     * @param schema ".json" file containing JSON schema information, not null
     * @param instance JSON structure to check validity of
     * @return report about validity
     * @throws SedsException if schema could not be loaded or JSON data from the
     * schema is corrupt
     */
    @SuppressWarnings({"ThrowableResultIgnored", "ThrowableInstanceNotThrown", "ThrowableInstanceNeverThrown"})
    public static ProcessingReport validate(
            String schema,
            String schemaPath,
            JsonObject instance
    ) throws SedsException {
        assertNotNull(schema, String.class, "JSON schema");
        assertNotNull(schemaPath, String.class, "a path to JSON schema");
        assertNotNull(instance, JsonObject.class, "JSON data being validated");

        try {
            return schemaOf(schema).validate(
                    JsonLoader.fromString(instance.toString())
            );
        } catch (IOException | ProcessingException ex) {
            throw new SedsException("Error in reading/parsing the JSON Schema", ex)
                    .attachSchemaPath(schemaPath)
                    .attachJson(instance);
        }
    }
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    private static JsonSchema schemaOf(
            String schema
    ) throws ProcessingException, IOException {
        assertNotNull(schema, String.class, "JSON schema");

        return JsonSchemaFactory
                .byDefault()
                .getJsonSchema(JsonLoader.fromString(schema));
    }
    //--------------------------------------------------------------------------

}
