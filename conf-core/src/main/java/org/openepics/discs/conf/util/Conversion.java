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
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.epics.util.time.Timestamp;
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
 * A class holding utility methods for UI value conversion. All utility conversion methods are static.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
/**
 * @author ess-dev
 *
 */
public class Conversion {

    /**
     * Returns a data type used for this property.
     * <br/>
     * See: {@link PropertyDataType}
     * @param prop The property to check data type on.
     * @return The data type that has been selected for this property.
     */
    public static PropertyDataType getDataType(Property prop) {
        Preconditions.checkNotNull(prop);
        switch (prop.getDataType().getName()) {
        case PropertyDataType.INT_NAME :
            return PropertyDataType.INTEGER;
        case PropertyDataType.DBL_NAME :
            return PropertyDataType.DOUBLE;
        case PropertyDataType.STR_NAME :
            return PropertyDataType.STRING;
        case PropertyDataType.TIMESTAMP_NAME :
            return PropertyDataType.TIMESTAMP;
        case PropertyDataType.URL_NAME :
            return PropertyDataType.URL;
        case PropertyDataType.INT_VECTOR_NAME :
            return PropertyDataType.INT_VECTOR;
        case PropertyDataType.DBL_VECTOR_NAME :
            return PropertyDataType.DBL_VECTOR;
        case PropertyDataType.STRING_LIST_NAME :
            return PropertyDataType.STRING_LIST;
        case PropertyDataType.DBL_TABLE_NAME :
            return PropertyDataType.DBL_TABLE;
        default:
            return PropertyDataType.ENUM;
        }
    }

    /**
     * Determines what UI control to use for editing a property value of a certain data type.
     * <br/>
     * See: {@link PropertyValueUIElement}
     *
     * @param dataType the data type
     * @return the UI element to use
     */
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

    /**
     * Determines what UI control to use for editing a property value of a certain data type.
     * <br/>
     * See: {@link PropertyValueUIElement}
     *
     * @param property the property to chekc for data type
     * @return the UI element to use
     */
    public static PropertyValueUIElement getUIElementFromProperty(Property property) {
        return getUIElementFromPropertyDataType(getDataType(property));
    }

    /**
     * This method takes a string containing a value (the string is returned by the UI elements) and converts it into a
     * property value of appropriate type.
     * <br/>
     * See: {@link Value}
     *
     * @param strValue the string containing a property value
     * @param property the property for which to return a value (what the user selects in the UI or referenced
     *                     from {@link PropertyValue})
     * @return The {@link Value} of the property.
     */
    public static Value stringToValue(String strValue, Property property) {
        Preconditions.checkNotNull(property);
        if (strValue == null) {
            return null;
        }
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

    /**
     * The method takes a value returned by the persistence layer {@link PropertyValue} and turns it into a string
     * representation to be used by UI component.
     * @param value the value to convert into a string
     * @return the string representation of the value
     */
    public static String valueToString(Value value) {
        if (value == null) {
            return null;
        }

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

    /**
     * @param str string to try and convert into an URL
     * @return the URL represented by a string.
     */
    public static URL toURL(String str) {
        try {
            final URL retUrl = new URL(str);
            if (!retUrl.getProtocol().startsWith("http") && !retUrl.getProtocol().equals("ftp")) {
                throw new RuntimeException("Protocol must be either http, https or ftp.");
            }
            return retUrl;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toString(String str) { return str; }

    private static Double toDouble(String str) { return Double.valueOf(str.trim()); }

    private static Integer toInteger(String str) { return Integer.valueOf(str.trim()); }

    private static String toEnum(String str) { return str; }

    private static List<String> toStrVector(String str) {
        final List<String> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(str)) {
            scanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            // replace unicode no-break spaces with normal does not work as expected for string list
            while (scanner.hasNext()) {
                list.add(scanner.next());
            }
        }
        return list;
    }

    private static List<Integer> toIntVector(String str) {
        final List<Integer> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(str)) {
            scanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                list.add(Integer.parseInt(scanner.next().replaceAll("\\u00A0", " ").trim()));
            }
        }
        return list;
    }

    private static List<Double> toDblVector(String str) {
        final List<Double> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(str)) {
            scanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                list.add(Double.parseDouble(scanner.next().replaceAll("\\u00A0", " ").trim()));
            }
        }
        return list;
    }

    /** This method parses the string into a timestamp. It accepts the following inputs:
     * <ul>
     * <li> yyyy-MM-dd : only the date in the ISO format.</li>
     * <li> HH:mm:ss : only the time in which case the current date is assumed. The time is in 24-hour format.</li>
     * <li> yyyy-MM-dd HH:mm:ss : the date and time. The time is in 24-hour format.</li>
     * <li> yyyy-MM-dd HH:mm:ss.nnnnnnnnn : the timestamp with nanosecond precision.
     *           The fractional part of the second can have any number of places up to 9.</li>
     * </ul>
     * In all cases the timestamp if without the time zone (or all timestamps are as UTC timestamps).
     *
     * @param str
     * @return The Timestamp to store in the database.
     */
    public static Timestamp toTimestamp(String str) {
        final String trimmedValue = str.trim();
        final String dateStr;
        final String nanosStr;

        final long daySeconds = 60 * 60 * 24;
        final long dayMillis = daySeconds * 1000;

        int nanos = 0;
        long unixtime = 0;

        final int dotPos = trimmedValue.indexOf('.');

        if (dotPos > -1) {
            dateStr = trimmedValue.substring(0, dotPos);
            nanosStr = (trimmedValue.substring(dotPos + 1) + "000000000").substring(0, 9); // .1 stands for 100000000 ns
        } else {
            dateStr = trimmedValue;
            nanosStr = "";
        }
        if (dateStr.charAt(dateStr.length() - 1) < '0' || dateStr.charAt(dateStr.length() - 1) > '9')
            throw new RuntimeException("Timestamp contains invalid characters.");

        Date parsedDate;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        simpleDateFormat.setLenient(false);
        if (nanosStr.isEmpty()) {
            ParsePosition parsePos = new ParsePosition(0);
            simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            parsedDate = simpleDateFormat.parse(dateStr, parsePos);
            if (parsePos.getErrorIndex() >= 0) {
                parsePos.setErrorIndex(-1); // clear error for new parser
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                parsedDate = simpleDateFormat.parse(dateStr, parsePos);
                if (parsePos.getErrorIndex() >= 0) {
                    parsePos.setErrorIndex(-1); // clear error for new parser
                    simpleDateFormat.applyPattern("HH:mm:ss");
                    parsedDate = simpleDateFormat.parse(dateStr, parsePos);
                    if (parsePos.getErrorIndex() >= 0) {
                        throw new RuntimeException("Cannot parse timestamp.");
                    } else {
                        if (parsePos.getIndex() < dateStr.length()) {
                            throw new RuntimeException("Cannot parse timestamp.");
                        }
                        final long todayDate = ((new Date()).getTime() / dayMillis) * daySeconds; // in unix time
                        unixtime = (parsedDate.getTime() / 1000) + todayDate;
                    }
                } else {
                    if (parsePos.getIndex() < dateStr.length()) {
                        throw new RuntimeException("Cannot parse timestamp.");
                    }
                    unixtime = parsedDate.getTime() / 1000;
                }
            } else {
                if (parsePos.getIndex() < dateStr.length()) {
                    throw new RuntimeException("Cannot parse timestamp.");
                }
                unixtime = parsedDate.getTime() / 1000;
            }
        } else {
            try {
                simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
                parsedDate = simpleDateFormat.parse(dateStr);
                unixtime = parsedDate.getTime() / 1000;
                nanos = Integer.parseInt(nanosStr);
            } catch (ParseException e) {
                throw new RuntimeException("Cannot parse timestamp.", e);
            } catch (NumberFormatException e1) {
                throw new RuntimeException("Cannot parse timestamp nanoseconds.", e1);
            }
        }

        return Timestamp.of(unixtime, nanos);
    }

    private static List<List<Double>> toDblTable(String str) {
        final List<List<Double>> table = new ArrayList<>();

        try (Scanner lineScanner = new Scanner(str)) {
            lineScanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            int rowLength = -1;

            while (lineScanner.hasNext()) {
                // replace unicode no-break spaces with normal ones
                final String lineStr = lineScanner.next().replaceAll("\\u00A0", " ");
                final List<Double> tableRow = new ArrayList<>();
                try (Scanner valueScanner = new Scanner(lineStr)) {
                    valueScanner.useDelimiter(Pattern.compile(",\\s*"));
                    while (valueScanner.hasNext()) {
                        final String dblValue = valueScanner.next().trim();
                        tableRow.add(Double.parseDouble(dblValue));
                    }
                }
                if (!tableRow.isEmpty()) {
                    if (rowLength < 0) {
                        rowLength = tableRow.size();
                    } else if (rowLength != tableRow.size()) {
                        throw new RuntimeException("All rows must contain the same number of elements.");
                    }
                    table.add(tableRow);
                }
            }
        }
        return table;
    }

    private static String fromString(String value) { return value; }

    private static String fromDouble(Double value) { return value.toString(); }

    private static String fromInteger(Integer value) { return value.toString(); }

    private static String fromEnum(String value) { return value; }

    private static String fromURL(URL value) { return value.toString(); }

    private static String fromStrVector(List<String> value) {
        StringBuilder retStr = new StringBuilder();
        for (String line : value) {
            retStr.append(line).append('\n');
        }
        return retStr.toString();
    }

    private static String fromIntVector(List<Integer> value) {
        StringBuilder retStr = new StringBuilder();
        for (Integer number : value) {
            retStr.append(number.intValue()).append('\n');
        }
        return retStr.toString();
    }

    private static String fromDblVector(List<Double> value)  {
        StringBuilder retStr = new StringBuilder();
        for (Double number : value) {
            retStr.append(number.doubleValue()).append('\n');
        }
        return retStr.toString();
    }

    private static String fromTimestamp(Timestamp value) {
        final Date dateTime = new Date();
        dateTime.setTime(value.getSec() * 1000);

        final int dayInSeconds = 60 * 60 * 24;
        if (value.getSec() % dayInSeconds == 0 && value.getNanoSec() <= 0) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(dateTime);
        }

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final StringBuilder returnString = new StringBuilder(simpleDateFormat.format(dateTime));
        if (value.getNanoSec() > 0) {
            returnString.append('.').append(Integer.toString(value.getNanoSec()).replaceAll("0*$", ""));
        }

        return returnString.toString();
    }

    private static String fromDblTable(List<List<Double>> value) {
        StringBuilder retStr = new StringBuilder();
        for (List<Double> column : value) {
            boolean firstNumber = true;
            for (Double number : column) {
                if (!firstNumber) {
                    retStr.append(", ");
                } else {
                    firstNumber = false;
                }
                retStr.append(number.doubleValue());
            }
            retStr.append('\n');
        }
        return retStr.toString();
    }
}
