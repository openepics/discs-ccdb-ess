/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.util;

/**
 *
 * An enumeration of built-in data types in the CCDB application. The list has been predefined in the design phase
 * and all property values can only contain a value of one of these types.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
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

    private BuiltInDataType(String dbName) {
        this.dbName = dbName;
    }

    @Override public String toString() {
        return dbName;
    }
}
