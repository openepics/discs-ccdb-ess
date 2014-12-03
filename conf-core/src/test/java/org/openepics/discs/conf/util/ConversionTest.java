package org.openepics.discs.conf.util;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
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

public class ConversionTest {

    private final DataType intDataType = new DataType(PropertyDataType.INT_NAME, "", true, null);
    private final DataType dblDataType = new DataType(PropertyDataType.DBL_NAME, "", true, null);
    private final DataType strDataType = new DataType(PropertyDataType.STR_NAME, "", true, null);
    private final DataType timestampDataType = new DataType(PropertyDataType.TIMESTAMP_NAME, "", true, null);

    private final DataType dblVectorDataType = new DataType(PropertyDataType.DBL_VECTOR_NAME, "", false, null);
    private final DataType intVectorDataType = new DataType(PropertyDataType.INT_VECTOR_NAME, "", false, null);
    private final DataType strVectorDataType = new DataType(PropertyDataType.STRING_LIST_NAME, "", false, null);

    private final DataType dblTableDataType = new DataType(PropertyDataType.DBL_TABLE_NAME, "", false, null);
    private final DataType enumDataType = new DataType("AnyRandomName", "", true,
            "{\"meta\":{\"type\":\"SedsEnum\",\"protocol\":\"SEDSv1\",\"version\":\"1.0.0\"},"
            + "\"data\":{\"selected\":\"TEST1\"},\"type\":{\"elements\":[\"TEST1\",\"TEST2\",\"TEST3\",\"TEST4\"]}}");
    private static final String INVALID_ENUMERATION_ITEM = "invalid_item";
    private final Property intProperty = new Property("IntProperty", "");
    private final Property dblProperty = new Property("DblProperty", "");
    private final Property strProperty = new Property("StrProperty", "");
    private final Property timestampProperty = new Property("TimestampProperty", "");

    private final Property dblVectorProperty = new Property("DblVectorProperty", "");
    private final Property intVectorProperty = new Property("IntVectorProperty", "");
    private final Property strVectorProperty = new Property("StrVectorProperty", "");

    private final Property dblTableProperty = new Property("DblTableProperty", "");

    private final Property enumProperty = new Property("EnumProperty", "");

    @Before
    public void initProperties() {
        intProperty.setDataType(intDataType);
        dblProperty.setDataType(dblDataType);
        strProperty.setDataType(strDataType);
        timestampProperty.setDataType(timestampDataType);
        dblVectorProperty.setDataType(dblVectorDataType);
        intVectorProperty.setDataType(intVectorDataType);
        strVectorProperty.setDataType(strVectorDataType);
        dblTableProperty.setDataType(dblTableDataType);
        enumProperty.setDataType(enumDataType);
    }

    @Test(expected = NullPointerException.class)
    public void dataTypeNotNull() {
        Conversion.getDataType(null);
    }

    @Test
    public void dataTypeInt() {
        assertEquals(PropertyDataType.INTEGER, Conversion.getDataType(intProperty.getDataType()));
    }

    @Test
    public void dataTypeDbl() {
        assertEquals(PropertyDataType.DOUBLE, Conversion.getDataType(dblProperty.getDataType()));
    }

    @Test
    public void dataTypeStr() {
        assertEquals(PropertyDataType.STRING, Conversion.getDataType(strProperty.getDataType()));
    }

    @Test
    public void dataTypeTimestamp() {
        assertEquals(PropertyDataType.TIMESTAMP, Conversion.getDataType(timestampProperty.getDataType()));
    }

    @Test
    public void dataTypeDblVector() {
        assertEquals(PropertyDataType.DBL_VECTOR, Conversion.getDataType(dblVectorProperty.getDataType()));
    }

    @Test
    public void dataTypeIntVector() {
        assertEquals(PropertyDataType.INT_VECTOR, Conversion.getDataType(intVectorProperty.getDataType()));
    }

    @Test
    public void dataTypeStrVector() {
        assertEquals(PropertyDataType.STRING_LIST, Conversion.getDataType(strVectorProperty.getDataType()));
    }

    @Test
    public void dataTypeDblTable() {
        assertEquals(PropertyDataType.DBL_TABLE, Conversion.getDataType(dblTableProperty.getDataType()));
    }

    @Test
    public void dataTypeEnum() {
        assertEquals(PropertyDataType.ENUM, Conversion.getDataType(enumProperty.getDataType()));
    }

    @Test
    public void uiElementFromPropertyInt() {
        assertEquals(PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(intProperty));
    }

    @Test
    public void uiElementFromPropertyDbl() {
        assertEquals(PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(dblProperty));
    }

    @Test
    public void uiElementFromPropertyStr() {
        assertEquals(PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(strProperty));
    }

    @Test
    public void uiElementFromPropertyTimestamp() {
        assertEquals(PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(timestampProperty));
    }

    @Test
    public void uiElementFromPropertyIntVector() {
        assertEquals(PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(intVectorProperty));
    }

    @Test
    public void uiElementFromPropertyDblVector() {
        assertEquals(PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(dblVectorProperty));
    }

    @Test
    public void uiElementFromPropertyStrVector() {
        assertEquals(PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(strVectorProperty));
    }

    @Test
    public void uiElementFromPropertyDblTable() {
        assertEquals(PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(dblTableProperty));
    }

    @Test
    public void uiElementFromPropertyEnum() {
        assertEquals(PropertyValueUIElement.SELECT_ONE_MENU, Conversion.getUIElementFromProperty(enumProperty));
    }

    @Test
    public void stringToValueInt() {
        IntValue intValue = (IntValue) Conversion.stringToValue("  123   ", intProperty.getDataType());
        assertEquals(new Integer(123), intValue.getIntValue());
    }

    @Test
    public void stringToValueDbl() {
        DblValue dblValue = (DblValue) Conversion.stringToValue("  123.456   ", dblProperty.getDataType());
        assertEquals(new Double(123.456), dblValue.getDblValue());

        dblValue = (DblValue) Conversion.stringToValue("  456   ", dblProperty.getDataType());
        assertEquals(new Double(456), dblValue.getDblValue());
    }

    @Test
    public void stringToValueStr() {
        StrValue strValue = (StrValue) Conversion.stringToValue("  This is a   string test. ", strProperty.getDataType());
        assertEquals("  This is a   string test. ", strValue.getStrValue());
    }

    @Test
    public void stringToValueTimestamp() {
        TimestampValue tsValue = (TimestampValue) Conversion.stringToValue("1973-06-20", timestampProperty.getDataType());
        assertEquals("Date to timestamp conversion failed", 109382400, tsValue.getTimestampValue().getSec());

        tsValue = (TimestampValue) Conversion.stringToValue("1973-06-20 13:50:00", timestampProperty.getDataType());
        assertEquals("Date & time to timestamp conversion failed", 109432200, tsValue.getTimestampValue().getSec());

        tsValue = (TimestampValue) Conversion.stringToValue("1973-06-20 13:50:00.12345", timestampProperty.getDataType());
        assertEquals("Date & time in nanos to timestamp conversion failed", 109432200, tsValue.getTimestampValue().getSec());
        assertEquals("Date & time in nanos to timestamp conversion failed (nanos)", 123450000, tsValue.getTimestampValue().getNanoSec());

        final long expectedTime = 13 * 60 * 60 + 50 * 60 + (today().getTime() / 1000);
        tsValue = (TimestampValue) Conversion.stringToValue("13:50:00", timestampProperty.getDataType());
        assertEquals("Time to timestamp conversion failed", expectedTime, tsValue.getTimestampValue().getSec());
    }

    @Test
    public void stringToValueEnum() {
        EnumValue enumValue = (EnumValue) Conversion.stringToValue("TEST2", enumProperty.getDataType());
        assertEquals("TEST2", enumValue.getEnumValue());
    }

    @Test
    public void stringToValueIntVector() {
        final String intVectorStr = "1\n  100 \n-123";
        IntVectorValue intVectorValue = (IntVectorValue) Conversion.stringToValue(intVectorStr, intVectorProperty.getDataType());
        assertEquals(Arrays.asList(1, 100, -123), intVectorValue.getIntVectorValue());
    }

    @Test
    public void stringToValueDblVector() {
        final String dblVectorStr = "0.1\n  1.0006e12 \n-1.23";
        DblVectorValue dblVectorValue = (DblVectorValue) Conversion.stringToValue(dblVectorStr, dblVectorProperty.getDataType());
        assertEquals("Dbl vector string to value conversion failed.", Arrays.asList(0.1, 1.0006e12, -1.23), dblVectorValue.getDblVectorValue());

        String intVectorStr = "1\n  100 \n-123";
        dblVectorValue = (DblVectorValue) Conversion.stringToValue(intVectorStr, dblVectorProperty.getDataType());
        assertEquals("Dbl vector (integers) string to value conversion failed.", Arrays.asList(1.0, 100.0, -123.0), dblVectorValue.getDblVectorValue());
    }

    @Test
    public void stringToValueStrVector() {
        String strVectorStr = "0.1\n  1.0006e12 \n-1.23";
        StrVectorValue strVectorValue = (StrVectorValue) Conversion.stringToValue(strVectorStr, strVectorProperty.getDataType());
        assertEquals(Arrays.asList("0.1", "  1.0006e12 ", "-1.23"), strVectorValue.getStrVectorValue());
    }

    @Test
    public void stringToValueDblTable() {
        String sblTableStr = "1,2,3,4\n0.1, 0.2, 0.3, 0.777\n123, 456, 789, 1234567";
        DblTableValue dblTableValue = (DblTableValue) Conversion.stringToValue(sblTableStr, dblTableProperty.getDataType());
        assertEquals(Arrays.asList(Arrays.asList(1.0, 2.0, 3.0, 4.0),
                                        Arrays.asList(0.1, 0.2, 0.3, 0.777),
                                        Arrays.asList(123.0, 456.0, 789.0, 1234567.0)),
                        dblTableValue.getDblTableValue());
    }

    private Date today() {
        final long dayInMillis = 1000 * 60 * 60 * 24;
        final Date today = new Date();
        today.setTime((today.getTime() / dayInMillis) * dayInMillis); // remove the time
        return today;
    }

    /*
     * negSTV stands for negative (expected to fail) "S"tring "T"o "V"alue
     */
    @Test(expected = NumberFormatException.class)
    public void negSTVDblForInt() {
        Conversion.stringToValue("23.45", intProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVAlphaForInt() {
        Conversion.stringToValue("23a", intProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVInvalidCharForDbl() {
        Conversion.stringToValue("23,45", dblProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVEnum() {
        Conversion.stringToValue(INVALID_ENUMERATION_ITEM, enumProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVIntVectorEmptyLine() {
        final String intVectorStr = "1\n \n-123";
        Conversion.stringToValue(intVectorStr, intVectorProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVIntVectorInvalidCharacters() {
        final String intVectorStr = "1, 2, 3";
        Conversion.stringToValue(intVectorStr, intVectorProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVIntVectorDouble() {
        final String intVectorStr = "1\n 3.14 \n-123";
        Conversion.stringToValue(intVectorStr, intVectorProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVDblVectorEmptyLine() {
        final String dblVectorStr = "1.0\n \n-123.0";
        Conversion.stringToValue(dblVectorStr, dblVectorProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVDblVectorInvalidCharacters() {
        final String dblVectorStr = "1.0, 2.0, 3.0";
        Conversion.stringToValue(dblVectorStr, dblVectorProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVDblTableNonMatrix() {
        final String dblTableStr = "1.0, 2.0, 3.0\n1.0, 2.0, 3.0, 4.0\n1.0, 2.0, 3.0";
        Conversion.stringToValue(dblTableStr, dblTableProperty.getDataType());
    }

    @Test(expected = Exception.class)
    public void negSTVDblTableEmptyLine() {
        final String dblTableStr = "1.0, 2.0, 3.0\n \n1.0, 2.0, 3.0";
        Conversion.stringToValue(dblTableStr, dblTableProperty.getDataType());
    }

    @Test(expected = NumberFormatException.class)
    public void negSTVDblTableInvalidChar() {
        final String dblTableStr = "1.0, 2.0, 3.0\na, b, c\n1.0, 2.0, 3.0";
        Conversion.stringToValue(dblTableStr, dblTableProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullInvalid() {
        Conversion.stringToValue("1973-06-20 13:50:00,12345", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullSpaceAfterDot() {
        Conversion.stringToValue("1973-06-20 13:50:00. 12345", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullSpaceBeforeDot() {
        Conversion.stringToValue("1973-06-20 13:50:00 .12345", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullPicosec() {
        Conversion.stringToValue("1973-06-20 13:50:00.1234567890", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullTime() {
        Conversion.stringToValue("1973-06-20 13:50:62.1234567", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullTime2() {
        Conversion.stringToValue("1973-06-20 13:50:OO.1234567", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullDate() {
        Conversion.stringToValue("2000-02-30 12:00:00.13", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampFullDate2() {
        Conversion.stringToValue("2000-02-40 12:00:00.13", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDateTimeInvalid() {
        Conversion.stringToValue("1973-06-20 13:5O:62", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDateTimeTime() {
        Conversion.stringToValue("1973-06-20 13:50:62", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDateTimeDate() {
        Conversion.stringToValue("2000-02-30 12:00:00", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDateTimeDate2() {
        Conversion.stringToValue("2000-02-40 12:00:00", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDateInvalid() {
        Conversion.stringToValue("1973-06-2O", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDate() {
        Conversion.stringToValue("2000-02-30", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampDate2() {
        Conversion.stringToValue("2000-02-40", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampTime1() {
        Conversion.stringToValue("13:50:62", timestampProperty.getDataType());
    }

    @Test(expected = ConversionException.class)
    public void negSTVTimestampTime2() {
        Conversion.stringToValue("4:00:00 PM", timestampProperty.getDataType());
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * Value to string conversion tests
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Test
    public void valueToStringNull() {
        assertEquals(null, Conversion.valueToString(null));
    }

    @Test
    public void valueToStringInt() {
        assertEquals("123", Conversion.valueToString(new IntValue(123)));
    }

    @Test
    public void valueToStringDbl() {
        assertEquals("1.0", Conversion.valueToString(new DblValue(1.0)));
    }

    @Test
    public void valueToStringStr() {
        assertEquals("ESS", Conversion.valueToString(new StrValue("ESS")));
    }
}
