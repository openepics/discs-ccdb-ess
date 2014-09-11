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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.values.DblTableValue;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.DblVectorValue;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.IntValue;
import org.openepics.discs.conf.ent.values.IntVectorValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.StrVectorValue;
import org.openepics.discs.conf.ent.values.TimestampValue;
import org.openepics.discs.conf.ent.values.UrlValue;
import org.openepics.discs.conf.ent.values.Value;

import com.google.common.base.Preconditions;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class Conversion {

    public static PropertyDataType getDataType(Property prop) {
        Preconditions.checkNotNull(prop);
        switch (prop.getDataType().getName()) {
        case PropertyDataTypeConstants.INT_NAME :
            return PropertyDataType.INTEGER;
        case PropertyDataTypeConstants.DBL_NAME :
            return PropertyDataType.DOUBLE;
        case PropertyDataTypeConstants.STR_NAME :
            return PropertyDataType.STRING;
        case PropertyDataTypeConstants.TIMESTAMP_NAME :
            return PropertyDataType.TIMESTAMP;
        case PropertyDataTypeConstants.URL_NAME :
            return PropertyDataType.URL;
        case PropertyDataTypeConstants.INT_VECTOR_NAME :
            return PropertyDataType.INT_VECTOR;
        case PropertyDataTypeConstants.DBL_VECTOR_NAME :
            return PropertyDataType.DBL_VECTOR;
        case PropertyDataTypeConstants.STRING_LIST_NAME :
            return PropertyDataType.STRING_LIST;
        case PropertyDataTypeConstants.DBL_TABLE_NAME :
            return PropertyDataType.DBL_TABLE;
        default:
            return PropertyDataType.ENUM;
        }
    }

    public static PropertyValueUIElement getUIElementFromPropertyDataType(PropertyDataType dataType) {
        switch (dataType) {
        case URL:
        case TIMESTAMP:
        case STRING:
        case INTEGER:
        case DOUBLE:
            return PropertyValueUIElement.INPUT;
        case ENUM:
            return PropertyValueUIElement.SELECT_ONE_MENU;
        case INT_VECTOR:
        case DBL_VECTOR:
        case DBL_TABLE:
        case STRING_LIST:
            return PropertyValueUIElement.TEXT_AREA;
        default:
            throw new IllegalArgumentException("Unknow data type: " + dataType.name());
        }
    }

    public static PropertyValueUIElement getUIElementFromProperty(Property property) {
        return getUIElementFromPropertyDataType(getDataType(property));
    }

    public static String toString(String str) { return str; }

    public static Double toDouble(String str) { return str == null ? null : Double.valueOf(str); }

    public static Integer toInteger(String str) { return str == null ? null : Integer.valueOf(str); }

    public static String toEnum(String str) { return str; }

    public static URL toURL(String str) {
        try {
            // TODO check for protocols, etc
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> toStrVector(String str) { return new ArrayList<String>(); } // TODO implement

    public static List<Integer> toIntVector(String str) { return new ArrayList<Integer>(); } // TODO implement

    public static List<Double> toDblVector(String str) { return new ArrayList<Double>(); } // TODO implement

    public static Date toTimestamp(String str) { return new Date(); } // TODO implement

    public static List<List<Double>> toDblTable(String str) { return new ArrayList<List<Double>>(); } // TODO implement

    public static String fromString(String value) { return value; }

    public static String fromDouble(Double value) { return value == null ? null : value.toString(); }

    public static String fromInteger(Integer value) { return value == null ? null : value.toString(); }

    public static String fromEnum(String value) { return value; }

    public static String fromURL(URL value) { return value.toString(); }

    public static String fromStrVector(List<String> value) { return value == null ? null : value.toString(); } // TODO implement

    public static String fromIntVector(List<Integer> value) { return value == null ? null : value.toString(); } // TODO implement

    public static String fromDblVector(List<Double> value) { return value == null ? null : value.toString(); } // TODO implement

    public static String fromTimestamp(Date value) { return value == null ? null : value.toString(); } // TODO implement

    public static String fromDblTable(List<List<Double>> value) { return value == null ? null : value.toString(); } // TODO implement

    public static Value stringToValue(String strValue, Property property) {
        Preconditions.checkNotNull(property);
        if (strValue == null) return null;
        switch (Conversion.getDataType(property)) {
        case DBL_TABLE:
            return new DblTableValue(Conversion.toDblTable(strValue));
        case DBL_VECTOR:
            return new DblVectorValue(Conversion.toDblVector(strValue));
        case DOUBLE:
            return new DblValue(Conversion.toDouble(strValue));
        case ENUM:
            return new EnumValue(Conversion.toEnum(strValue));
        case INTEGER:
            return new IntValue(Conversion.toInteger(strValue));
        case INT_VECTOR:
            return new IntVectorValue(Conversion.toIntVector(strValue));
        case STRING:
            return new StrValue(Conversion.toString(strValue));
        case STRING_LIST:
            return new StrVectorValue(Conversion.toStrVector(strValue));
        case TIMESTAMP:
            return new TimestampValue(Conversion.toTimestamp(strValue));
        case URL:
            return new UrlValue(Conversion.toURL(strValue));
        default:
            throw new IllegalStateException("Unknown property type.");
        }
    }

    public static String valueToString(Value value) {
        if (value == null) return null;

        if (value instanceof IntValue) {
            return Conversion.fromInteger(((IntValue)value).getIntValue());
        } else if (value instanceof DblValue) {
            return Conversion.fromDouble(((DblValue)value).getDblValue());
        } else if (value instanceof StrValue) {
            return Conversion.fromString(((StrValue)value).getStrValue());
        } else if (value instanceof TimestampValue) {
            return Conversion.fromTimestamp(((TimestampValue)value).getTimestampValue());
        } else if (value instanceof UrlValue) {
            return Conversion.fromURL(((UrlValue)value).getUrlValue());
        } else if (value instanceof IntVectorValue) {
            return Conversion.fromIntVector(((IntVectorValue)value).getIntVectorValue());
        } else if (value instanceof DblVectorValue) {
            return Conversion.fromDblVector(((DblVectorValue)value).getDblVectorValue());
        } else if (value instanceof StrVectorValue) {
            return Conversion.fromStrVector(((StrVectorValue)value).getStrVectorValue());
        } else if (value instanceof DblTableValue) {
            return Conversion.fromDblTable(((DblTableValue)value).getDblTableValue());
        } else if (value instanceof EnumValue) {
            return Conversion.fromEnum(((EnumValue)value).getEnumValue());
        } else
            throw new IllegalStateException("Unknown property type.");
     }

}
