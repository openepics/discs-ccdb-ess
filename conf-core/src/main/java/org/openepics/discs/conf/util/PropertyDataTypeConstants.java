package org.openepics.discs.conf.util;

/**
 * The predefined data types are all stored in the backing database storage. Each database entity representing a data type
 * has a predefined name. The predefined names are listed in this interface.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public interface PropertyDataTypeConstants {
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
}
