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

import javax.json.JsonObject;

import com.github.fge.jsonschema.core.report.ProcessingReport;

/**
 * An exception that may contain data about when an exception occurred due to
 * some sort-of JSON related error.
 * <p>
 * The attached data can be inspected to help determine the cause of the error.
 * <p>
 * Common JSON related errors are malformed JSON structures, a file that is
 * referenced to contain a schema but does not exist, or a JSON structure that
 * does not conform to a given schema.
 * <p>
 * Also contains useful standardized builders for exceptions.
 *
 * @author Aaron Barber
 */
public class SedsException extends Exception {

    private ProcessingReport report = null;
    private JsonObject json = null;
    private String schemaPath = null;

    //Constructors
    //--------------------------------------------------------------------------
    /**
     * Creates the exception with no cause and no message.
     */
    public SedsException() {
        super();
    }

    /**
     * Creates the exception with no cause and the given message about the
     * details of the exception.
     *
     * @param message specified detail message
     */
    public SedsException(String message) {
        super(message);
    }

    /**
     * Creates the exception with the given cause and no message.
     *
     * @param cause reason why the exception was created
     */
    public SedsException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates the exception with the given cause and the given message about
     * the details of the exception.
     *
     * @param message specified detail message
     * @param cause reason why the exception was created
     */
    public SedsException(String message, Throwable cause) {
        super(message, cause);
    }
    //--------------------------------------------------------------------------

    //Attach
    //--------------------------------------------------------------------------
    /**
     * Sets the validity report (from a JSON Schema) of the exception.
     *
     * @param report report generated at the time of the exception
     * @return this
     */
    public SedsException attachLog(ProcessingReport report) {
        this.report = report;
        return this;
    }

    /**
     * Sets the JSON object data of the exception.
     *
     * @param json JSON data at the time of the exception
     * @return this
     */
    public SedsException attachJson(JsonObject json) {
        this.json = json;
        return this;
    }

    /**
     * Sets the JSON schema of the exception.
     *
     * @param schemaPath '.json' file containing a JSON schema, the schema being
     * processed at the time of the exception
     * @return this
     */
    public SedsException attachSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
        return this;
    }
    //--------------------------------------------------------------------------

    //Get
    //--------------------------------------------------------------------------
    /**
     * Gets the processing report that occurred at the time of the exception.
     *
     * @return validity report
     */
    public ProcessingReport getReport() {
        return report;
    }

    /**
     * Gets the JSON object that was being processed at the time of the
     * exception.
     *
     * @return JSON data
     */
    public JsonObject getJson() {
        return json;
    }

    /**
     * Gets the JSON schema that was being processed at the time of the
     * exception.
     * <p>
     * Note that an error could have occurred if the file does not exist, or
     * does not contain a valid JSON schema.
     *
     * @return a path to '.json' file containing a JSON schema
     */
    public String getSchemaPath() {
        return schemaPath;
    }
    //--------------------------------------------------------------------------

    //Has
    //--------------------------------------------------------------------------
    /**
     * Checks if the exception contains a processing report.
     *
     * @return true if there is an attached report, otherwise false
     */
    public boolean hasReport() {
        return report != null;
    }

    /**
     * Checks if the exception contains a JSON object.
     *
     * @return true if there is an attached JSON object, otherwise false
     */
    public boolean hasJson() {
        return json != null;
    }

    /**
     * Checks if the exception contains a JSON schema.
     *
     * @return true if there is an attached JSON schema, otherwise false
     */
    public boolean hasSchema() {
        return schemaPath != null;
    }
    //--------------------------------------------------------------------------

    //Output
    //--------------------------------------------------------------------------
    /**
     * Builds the message that contains all the attached information (if
     * attached).
     *
     * @return message of why the exception occurred
     */
    @Override
    public final String getMessage() {
        return new StringBuilder()
                .append("\n----------\n")
                .append("[Message]\n")
                .append(super.getMessage())
                .append("\n[Log]\n")
                .append(report == null ? null : report.toString())
                .append("\n[JSON]\n")
                .append(json == null ? null : json.toString())
                .append("[\nSchemaFile]\n")
                .append(schemaPath)
                .append("\n----------")
                .toString();
    }
    //--------------------------------------------------------------------------

    //Builders
    //--------------------------------------------------------------------------
    /**
     * Builds a standardized illegal argument exception that occurred from a
     * null argument.
     *
     * @param expected the type expected from the null-argument
     * @param context where or what was happening when the illegal null argument
     * was received
     * @return illegal argument exception containing a standardized message
     * about a null-error
     */
    public static IllegalArgumentException buildNPE(
            Class expected,
            String context
    ) {
        return buildNPE((expected == null ? "?" : expected.getSimpleName()), context);
    }

    /**
     * Builds a standardized illegal argument exception that occurred from a
     * null argument.
     *
     * @param expected text representation of what was expected from the
     * null-argument
     * @param context where or what was happening when the illegal null argument
     * was received
     * @return illegal argument exception containing a standardized message
     * about a null-error
     */
    public static IllegalArgumentException buildNPE(
            String expected,
            String context
    ) {
        return new IllegalArgumentException(
                "[Illegal Null]"
                + "[Expected Type: " + expected + "]"
                + "[Actual: null]"
                + "[Context: " + context + "]"
        );
    }

    /**
     * Builds a standardized illegal argument exception.
     *
     * @param actual value received (which was an invalid value)
     * @param expected value to indicate what was expected (commonly a string or
     * a primitive value)
     * @param context where or what was happening when the illegal null argument
     * was received
     * @return illegal argument exception containing a standardized message
     * about an illegal argument
     */
    public static IllegalArgumentException buildIAE(
            Object actual,
            Object expected,
            String context
    ) {
        return new IllegalArgumentException(
                "[Illegal Argument]"
                + "[Expected: " + expected + "]"
                + "[Actual: " + actual + "]"
                + "[Context: " + context + "]"
        );
    }

    /**
     * Fails if the value is not null, with a helpful illegal argument
     * exception.
     *
     * @param value value to check if null
     * @param expected type to indicate what was expected, to be used in the
     * exception if the value is null
     * @param context where or what was happening, to be used in the exception
     * if the value is null
     * @throws IllegalArgumentException if the value is null
     */
    public static void assertNotNull(
            Object value,
            Class expected,
            String context
    ) {
        if (value == null) {
            throw buildNPE(expected, context);
        }
    }
    //--------------------------------------------------------------------------

}
