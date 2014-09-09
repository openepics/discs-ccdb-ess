package org.openepics.discs.conf.util;

public enum PropertyDataType {
    INTEGER(PropertyDataTypeConstants.INT_NAME),
    DOUBLE(PropertyDataTypeConstants.DBL_NAME),
    STRING(PropertyDataTypeConstants.STR_NAME),
    TIMESTAMP(PropertyDataTypeConstants.TIMESTAMP_NAME),
    URL(PropertyDataTypeConstants.URL_NAME),
    INT_VECTOR(PropertyDataTypeConstants.INT_VECTOR_NAME),
    DBL_VECTOR(PropertyDataTypeConstants.DBL_VECTOR_NAME),
    STRING_LIST(PropertyDataTypeConstants.STRING_LIST_NAME),
    DBL_TABLE(PropertyDataTypeConstants.DBL_TABLE_NAME),
    ENUM(PropertyDataTypeConstants.ENUM_NAME);

    private String dbName;

    private PropertyDataType(String dbName) { this.dbName = dbName; }

    @Override public String toString() { return dbName; }
}
