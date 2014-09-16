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
    INTEGER(PropertyDataType.INT_NAME),
    /** The property value can be a single signed double precision number */
    DOUBLE(PropertyDataType.DBL_NAME),
    /** The property value can be any string of characters */
    STRING(PropertyDataType.STR_NAME),
    /** The property value can be a date and time timestamp */
    TIMESTAMP(PropertyDataType.TIMESTAMP_NAME),
    /** The property value can be an URL. Possible protocols are http, https and ftp. */
    URL(PropertyDataType.URL_NAME),
    /** The property value can be a 1-D vector of signed 32-bit integer numbers */
    INT_VECTOR(PropertyDataType.INT_VECTOR_NAME),
    /** The property value can be a 1-D vector of double precision numbers */
    DBL_VECTOR(PropertyDataType.DBL_VECTOR_NAME),
    /** the property value can be a list of strings. */
    STRING_LIST(PropertyDataType.STRING_LIST_NAME),
    /** The property value can be a table (matrix) of double precision numbers. The rows of the table must all be of equal length. */
    DBL_TABLE(PropertyDataType.DBL_TABLE_NAME),
    /** The property value can be a string value selected out of a predefined list of possible values (enumeration). */
    ENUM(PropertyDataType.ENUM_NAME);

    public static final String INT_NAME = "Integer";
    public static final String DBL_NAME = "Double";
    public static final String STR_NAME = "String";
    public static final String TIMESTAMP_NAME = "Timestamp";
    public static final String URL_NAME = "URL";
    public static final String INT_VECTOR_NAME = "Integers Vector";
    public static final String DBL_VECTOR_NAME = "Doubles Vector";
    public static final String STRING_LIST_NAME = "Strings List";
    public static final String DBL_TABLE_NAME = "Doubles Table";
    public static final String ENUM_NAME = "Enumeration";

    private String dbName;

    private PropertyDataType(String dbName) { this.dbName = dbName; }

    @Override public String toString() { return dbName; }
}
