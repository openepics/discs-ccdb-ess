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
package org.openepics.discs.ccdb.core.util;

import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonReader;

import org.epics.util.time.Timestamp;
import org.openepics.discs.ccdb.model.DataType;
import org.openepics.discs.ccdb.model.Property;
import org.openepics.discs.ccdb.model.values.DblTableValue;
import org.openepics.discs.ccdb.model.values.DblValue;
import org.openepics.discs.ccdb.model.values.DblVectorValue;
import org.openepics.discs.ccdb.model.values.EnumValue;
import org.openepics.discs.ccdb.model.values.IntValue;
import org.openepics.discs.ccdb.model.values.IntVectorValue;
import org.openepics.discs.ccdb.model.values.StrValue;
import org.openepics.discs.ccdb.model.values.StrVectorValue;
import org.openepics.discs.ccdb.model.values.TimestampValue;
import org.openepics.discs.ccdb.model.values.Value;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.core.Seds;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * A class holding utility methods for UI value conversion. All utility conversion methods are static.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class Conversion {

    private static final String NEW_LINE_REGEX = "(\\r\\n)|\\r|\\n";
    private static final String UNICODE_NON_BREAKING_SPACE = "\\u00A0";
    private static final String TIMESTAMP_PARSE_ERROR;

    /** Format string for acceptable date format. Read from properties file. Default is ISO 8601: yyyy-MM-dd. */
    public static final String DATE_ONLY_FORMAT;

    /** Format string for acceptable time format. Read from properties file.
     * Default is HH:mm:ss, HH is a value 00-23.
     */
    public static final String TIME_ONLY_FORMAT;

    /** Format string for acceptable date time format. Read from properties file.
     * Default is yyyy-MM-dd HH:mm:ss, HH is a value 00-23.
     */
    public static final String DATE_TIME_FORMAT;

    /** Format string for displaying date time as a timestamp. Read from properties file.
     * Default is yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static final String TIMESTAMP_FORMAT;

    private static final Logger LOGGER = Logger.getLogger(Conversion.class.getCanonicalName());

    private static Properties messagesProperties = new Properties();

    private static final String MESSAGES_PROPERTIES_FILE = "/messages.properties";

    static {
        try (final InputStream msgProperties = Conversion.class.getResourceAsStream(MESSAGES_PROPERTIES_FILE)) {
            messagesProperties.load(msgProperties);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not load " + MESSAGES_PROPERTIES_FILE, e);
        }
        DATE_ONLY_FORMAT = messagesProperties.getProperty("isoDateFormat", "yyyy-MM-dd");
        TIME_ONLY_FORMAT = messagesProperties.getProperty("isoTimeFormat", "HH:mm:ss");
        DATE_TIME_FORMAT = messagesProperties.getProperty("isoDateTimeFormat", DATE_ONLY_FORMAT + " " + TIME_ONLY_FORMAT);
        TIMESTAMP_FORMAT = messagesProperties.getProperty("timestampFormat" , "yyyy-MM-dd HH:mm:ss.SSS");
        TIMESTAMP_PARSE_ERROR = messagesProperties.getProperty("timeStampParseError", "Cannot parse timestamp.");
    }


    private Conversion() {
        // utility class
    }

    /**
     * Returns a built-in data type enumeration (description) based on the DataType entity.
     *
     * @see BuiltInDataType
     * @param dataType The property to check data type on.
     * @return The data type that has been selected for this property.
     */
    public static BuiltInDataType getBuiltInDataType(DataType dataType) {
        Preconditions.checkNotNull(dataType);
        final BuiltInDataType builtInDataType;
        switch (dataType.getName()) {
            case BuiltInDataType.INT_NAME :
                builtInDataType = BuiltInDataType.INTEGER;
                break;
            case BuiltInDataType.DBL_NAME :
                builtInDataType = BuiltInDataType.DOUBLE;
                break;
            case BuiltInDataType.STR_NAME :
                builtInDataType = BuiltInDataType.STRING;
                break;
            case BuiltInDataType.TIMESTAMP_NAME :
                builtInDataType = BuiltInDataType.TIMESTAMP;
                break;
            case BuiltInDataType.INT_VECTOR_NAME :
                builtInDataType = BuiltInDataType.INT_VECTOR;
                break;
            case BuiltInDataType.DBL_VECTOR_NAME :
                builtInDataType = BuiltInDataType.DBL_VECTOR;
                break;
            case BuiltInDataType.STRING_LIST_NAME :
                builtInDataType = BuiltInDataType.STRING_LIST;
                break;
            case BuiltInDataType.DBL_TABLE_NAME :
                builtInDataType = BuiltInDataType.DBL_TABLE;
                break;
            default:
                builtInDataType = BuiltInDataType.USER_DEFINED_ENUM;
                break;
        }
        return builtInDataType;
    }

    /**
     * Determines what UI control to use for editing a property value of a certain data type.
     *
     * @see PropertyValueUIElement
     *
     * @param dataType the data type
     * @return the UI element to use
     */
    public static PropertyValueUIElement getUIElementFromBuiltInDataType(BuiltInDataType dataType) {
        switch (dataType) {
            case TIMESTAMP:
            case STRING:
            case INTEGER:
            case DOUBLE:
                return PropertyValueUIElement.INPUT;
            case USER_DEFINED_ENUM:
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
     *
     * @see PropertyValueUIElement
     *
     * @param property the property to check for data type
     * @return the UI element to use
     */
    public static PropertyValueUIElement getUIElementFromProperty(Property property) {
        return getUIElementFromBuiltInDataType(getBuiltInDataType(property.getDataType()));
    }

    /**
     * This method takes a string containing a value (the string is returned by the UI elements) and converts it into a
     * property value of appropriate type.
     *
     * @see Value
     *
     * @param strValue the string containing a property value
     * @param dataType the {@link DataType} of the property for which to return a value
     * @return The {@link Value} of the property.
     */
    public static Value stringToValue(String strValue, DataType dataType) {
        Preconditions.checkNotNull(dataType);
        if (strValue == null) {
            return null;
        }
        final Value convertedValue;
        switch (Conversion.getBuiltInDataType(dataType)) {
            case DBL_TABLE:
                convertedValue = new DblTableValue(Conversion.toDblTable(strValue));
                break;
            case DBL_VECTOR:
                convertedValue = new DblVectorValue(Conversion.toDblVector(strValue),
                                        Conversion.toDblVectorRep(strValue));
                break;
            case DOUBLE:
                convertedValue = new DblValue(strValue);
                break;
            case USER_DEFINED_ENUM:
                convertedValue = new EnumValue(Conversion.toEnum(strValue, dataType));
                break;
            case INTEGER:
                convertedValue = new IntValue(Conversion.toInteger(strValue));
                break;
            case INT_VECTOR:
                return new IntVectorValue(Conversion.toIntVector(strValue));
            case STRING:
                convertedValue = new StrValue(Conversion.toString(strValue));
                break;
            case STRING_LIST:
                convertedValue = new StrVectorValue(Conversion.toStrVector(strValue));
                break;
            case TIMESTAMP:
                convertedValue = new TimestampValue(Conversion.toTimestamp(strValue));
                break;
            default:
                throw new IllegalStateException("Unknown property type.");
        }
        return convertedValue;
    }

    /**
     * The method takes a value returned by the persistence layer
     * {@link org.openepics.discs.ccdb.model.PropertyValue} and turns it into a string
     * representation to be used by UI component.
     * @param value the value to convert into a string
     * @return the string representation of the value
     */
    public static String valueToString(Value value) {
        if (value == null) {
            return null;
        }
        final String stringValue;
        if (value instanceof IntValue) {
            stringValue = Conversion.fromInteger(((IntValue)value).getIntValue());
        } else if (value instanceof DblValue) {
            stringValue = ((DblValue)value).toString();
        } else if (value instanceof StrValue) {
            stringValue = Conversion.fromString(((StrValue)value).getStrValue());
        } else if (value instanceof TimestampValue) {
            stringValue = Conversion.fromTimestamp((TimestampValue)value);
        } else if (value instanceof IntVectorValue) {
            stringValue = Conversion.fromIntVector(((IntVectorValue)value).getIntVectorValue());
        } else if (value instanceof DblVectorValue) {
            stringValue = Conversion.fromStrVector(((DblVectorValue)value).getRepresentations());
        } else if (value instanceof StrVectorValue) {
            stringValue = Conversion.fromStrVector(((StrVectorValue)value).getStrVectorValue());
        } else if (value instanceof DblTableValue) {
            stringValue = Conversion.fromDblTable(((DblTableValue)value).getDblTableValue());
        } else if (value instanceof EnumValue) {
            stringValue = Conversion.fromEnum(((EnumValue)value).getEnumValue());
        } else
            throw new IllegalStateException("Unknown property type.");
        return stringValue;
    }

    private static String toString(String str) {
        return str;
    }

    private static Integer toInteger(String str) {
        return Integer.valueOf(str.trim());
    }

    private static String toEnum(String str, DataType dataType) {
        JsonReader reader = Json.createReader(new StringReader(dataType.getDefinition()));
        final SedsEnum sedsEnum = (SedsEnum) Seds.newDBConverter().deserialize(reader.readObject());
        boolean foundValue = false;
        for (String selectItem : sedsEnum.getElements()) {
            if (selectItem.equals(str)) {
                foundValue = true;
                break;
            }
        }
        if (!foundValue) {
            throw new ConversionException("Enum selected value from the selection list.");
        }
        return str;
    }

    private static List<String> toStrVector(String str) {
        final List<String> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(str)) {
            scanner.useDelimiter(Pattern.compile(NEW_LINE_REGEX));

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
            scanner.useDelimiter(Pattern.compile(NEW_LINE_REGEX));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                list.add(Integer.parseInt(scanner.next().replaceAll(UNICODE_NON_BREAKING_SPACE, " ").trim()));
            }
        }
        return list;
    }

    private static List<Double> toDblVector(String str) {
        final List<Double> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(str)) {
            scanner.useDelimiter(Pattern.compile(NEW_LINE_REGEX));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                list.add(Double.parseDouble(scanner.next().replaceAll(UNICODE_NON_BREAKING_SPACE, " ").trim()));
            }
        }
        return list;
    }

    private static List<String> toDblVectorRep(String str) {
        final List<String> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(str)) {
            scanner.useDelimiter(Pattern.compile(NEW_LINE_REGEX));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                list.add(scanner.next().replaceAll(UNICODE_NON_BREAKING_SPACE, "").trim());
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
     * @param str the timestamp in string format
     * @return The Timestamp to store in the database
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
            if (trimmedValue.substring(dotPos + 1).length() < 9) {
                // .1 stands for 100000000 ns
                nanosStr = (trimmedValue.substring(dotPos + 1) + "000000000").substring(0, 9);
            } else {
                nanosStr = trimmedValue.substring(dotPos + 1);
            }
            if (nanosStr.length() > 9) {
                throw new ConversionException("Maximum accuracy allowed is nanoseconds.");
            }
        } else {
            dateStr = trimmedValue;
            nanosStr = "";
        }
        if (dateStr.charAt(dateStr.length() - 1) < '0' || dateStr.charAt(dateStr.length() - 1) > '9')
            throw new ConversionException("Timestamp contains invalid characters.");

        Date parsedDate;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        simpleDateFormat.setLenient(false);
        if (nanosStr.isEmpty()) {
            ParsePosition parsePos = new ParsePosition(0);
            simpleDateFormat.applyPattern(DATE_TIME_FORMAT);
            parsedDate = simpleDateFormat.parse(dateStr, parsePos);
            if (parsePos.getErrorIndex() >= 0) {
                // clear error for new parser
                parsePos.setErrorIndex(-1);
                simpleDateFormat.applyPattern(DATE_ONLY_FORMAT);
                parsedDate = simpleDateFormat.parse(dateStr, parsePos);
                if (parsePos.getErrorIndex() >= 0) {
                    // clear error for new parser
                    parsePos.setErrorIndex(-1);
                    simpleDateFormat.applyPattern(TIME_ONLY_FORMAT);
                    parsedDate = simpleDateFormat.parse(dateStr, parsePos);
                    if (parsePos.getErrorIndex() >= 0) {
                        throw new ConversionException(TIMESTAMP_PARSE_ERROR);
                    } else {
                        if (parsePos.getIndex() < dateStr.length()) {
                            throw new ConversionException(TIMESTAMP_PARSE_ERROR);
                        }
                        // in unix time
                        final long todayDate = ((new Date()).getTime() / dayMillis) * daySeconds;
                        unixtime = (parsedDate.getTime() / 1000) + todayDate;
                    }
                } else {
                    if (parsePos.getIndex() < dateStr.length()) {
                        throw new ConversionException(TIMESTAMP_PARSE_ERROR);
                    }
                    unixtime = parsedDate.getTime() / 1000;
                }
            } else {
                if (parsePos.getIndex() < dateStr.length()) {
                    throw new ConversionException(TIMESTAMP_PARSE_ERROR);
                }
                unixtime = parsedDate.getTime() / 1000;
            }
        } else {
            try {
                simpleDateFormat.applyPattern(DATE_TIME_FORMAT);
                parsedDate = simpleDateFormat.parse(dateStr);
                unixtime = parsedDate.getTime() / 1000;
                nanos = Integer.parseInt(nanosStr);
            } catch (ParseException e) {
                throw new ConversionException(TIMESTAMP_PARSE_ERROR, e);
            } catch (NumberFormatException e1) {
                throw new ConversionException("Cannot parse timestamp nanoseconds.", e1);
            }
        }

        return Timestamp.of(unixtime, nanos);
    }

    private static List<List<Double>> toDblTable(String str) {
        final List<List<Double>> table = new ArrayList<>();

        try (Scanner lineScanner = new Scanner(str)) {
            lineScanner.useDelimiter(Pattern.compile(NEW_LINE_REGEX));

            int rowLength = -1;

            while (lineScanner.hasNext()) {
                // replace unicode no-break spaces with normal ones
                final String lineStr = lineScanner.next().replaceAll(UNICODE_NON_BREAKING_SPACE, " ");
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
                        throw new ConversionException("All rows must contain the same number of elements.");
                    }
                    table.add(tableRow);
                }
            }
        }
        return table;
    }

    private static String fromString(String value) {
        return value;
    }

    private static String fromInteger(Integer value) {
        return value.toString();
    }

    private static String fromEnum(String value) {
        return value;
    }

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

    private static String fromTimestamp(TimestampValue value) {
        final Timestamp timestamp = value.getTimestampValue();
        final Date dateTime = new Date();
        dateTime.setTime(timestamp.getSec() * 1000);

        final int dayInSeconds = 60 * 60 * 24;
        if ((timestamp.getSec() % dayInSeconds == 0) && (timestamp.getNanoSec() <= 0)) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_ONLY_FORMAT);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(dateTime);
        }

        return value.toString();
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

    /** This method returns the list of strings that defined the user defined enumeration.
     * @param dataType the data type holding the enumeration definition
     * @return a list of strings defining the enumeration
     */
    public static List<String> prepareEnumSelections(DataType dataType) {
        // dataType must not be null and dataType.definition must not be empty or null
        Preconditions.checkArgument(!Strings.isNullOrEmpty(Preconditions.checkNotNull(dataType).getDefinition()));

        JsonReader reader = Json.createReader(new StringReader(dataType.getDefinition()));
        final SedsType seds = Seds.newDBConverter().deserialize(reader.readObject());
        final SedsEnum sedsEnum = (SedsEnum) seds;
        return Arrays.asList(sedsEnum.getElements());
    }
}
