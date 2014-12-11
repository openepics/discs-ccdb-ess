package org.openepics.discs.conf.util;

/**
 *
 * An enumeration of built-in data types in the CCDB application. The list has been predefined in the design phase
 * and all property values can only contain a value of one of these types.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public enum BuiltInDataType {
    /** The property value can be a single signed 32-bit integer number */
    INTEGER(BuiltInDataType.INT_NAME),
    /** The property value can be a single signed double precision number */
    DOUBLE(BuiltInDataType.DBL_NAME),
    /** The property value can be any string of characters */
    STRING(BuiltInDataType.STR_NAME),
    /** The property value can be a date and time timestamp */
    TIMESTAMP(BuiltInDataType.TIMESTAMP_NAME),
    /** The property value can be a 1-D vector of signed 32-bit integer numbers */
    INT_VECTOR(BuiltInDataType.INT_VECTOR_NAME),
    /** The property value can be a 1-D vector of double precision numbers */
    DBL_VECTOR(BuiltInDataType.DBL_VECTOR_NAME),
    /** the property value can be a list of strings. */
    STRING_LIST(BuiltInDataType.STRING_LIST_NAME),
    /** The property value can be a table (matrix) of double precision numbers. The rows of the table must all be of equal length. */
    DBL_TABLE(BuiltInDataType.DBL_TABLE_NAME),
    /** The property value can be a string value selected out of a user-defined list of possible values (enumeration). */
    USER_DEFINED_ENUM(BuiltInDataType.ENUM_NAME);

    public static final String INT_NAME = "Integer";
    public static final String DBL_NAME = "Double";
    public static final String STR_NAME = "String";
    public static final String TIMESTAMP_NAME = "Timestamp";
    public static final String INT_VECTOR_NAME = "Integers Vector";
    public static final String DBL_VECTOR_NAME = "Doubles Vector";
    public static final String STRING_LIST_NAME = "Strings List";
    public static final String DBL_TABLE_NAME = "Doubles Table";
    public static final String ENUM_NAME = "Enumeration";

    private String dbName;

    private BuiltInDataType(String dbName) { this.dbName = dbName; }

    @Override public String toString() { return dbName; }
}
