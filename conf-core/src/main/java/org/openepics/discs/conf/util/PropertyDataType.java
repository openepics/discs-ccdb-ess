package org.openepics.discs.conf.util;

/**
 *
 * An enumeration of possible (supported) data types in the CCDB application. The list has been predefined in the design phase
 * and all property values can only contain a value of one of these types.
 * <br/>
 * See: {@link PropertyDataTypeConstants}
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public enum PropertyDataType {
    /** The property value can be a single signed 32-bit integer number */
    INTEGER(PropertyDataTypeConstants.INT_NAME),
    /** The property value can be a single signed double precision number */
    DOUBLE(PropertyDataTypeConstants.DBL_NAME),
    /** The property value can be any string of characters */
    STRING(PropertyDataTypeConstants.STR_NAME),
    /** The property value can be a date and time timestamp */
    TIMESTAMP(PropertyDataTypeConstants.TIMESTAMP_NAME),
    /** The property value can be an URL. Possible protocols are http, https and ftp. */
    URL(PropertyDataTypeConstants.URL_NAME),
    /** The property value can be a 1-D vector of signed 32-bit integer numbers */
    INT_VECTOR(PropertyDataTypeConstants.INT_VECTOR_NAME),
    /** The property value can be a 1-D vector of double precision numbers */
    DBL_VECTOR(PropertyDataTypeConstants.DBL_VECTOR_NAME),
    /** the property value can be a list of strings. */
    STRING_LIST(PropertyDataTypeConstants.STRING_LIST_NAME),
    /** The property value can be a table (matrix) of double precision numbers. The rows of the table must all be of equal length. */
    DBL_TABLE(PropertyDataTypeConstants.DBL_TABLE_NAME),
    /** The property value can be a string value selected out of a predefined list of possible values (enumeration). */
    ENUM(PropertyDataTypeConstants.ENUM_NAME);

    private String dbName;

    private PropertyDataType(String dbName) { this.dbName = dbName; }

    @Override public String toString() { return dbName; }
}
