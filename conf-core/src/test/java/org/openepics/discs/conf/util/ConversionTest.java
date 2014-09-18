package org.openepics.discs.conf.util;


import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
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

public class ConversionTest {

    private final DataType intDataType = new DataType(PropertyDataType.INT_NAME, "", true, null);
    private final DataType dblDataType = new DataType(PropertyDataType.DBL_NAME, "", true, null);
    private final DataType strDataType = new DataType(PropertyDataType.STR_NAME, "", true, null);
    private final DataType timestampDataType = new DataType(PropertyDataType.TIMESTAMP_NAME, "", true, null);
    private final DataType urlDataType = new DataType(PropertyDataType.URL_NAME, "", true, null);

    private final DataType dblVectorDataType = new DataType(PropertyDataType.DBL_VECTOR_NAME, "", false, null);
    private final DataType intVectorDataType = new DataType(PropertyDataType.INT_VECTOR_NAME, "", false, null);
    private final DataType strVectorDataType = new DataType(PropertyDataType.STRING_LIST_NAME, "", false, null);

    private final DataType dblTableDataType = new DataType(PropertyDataType.DBL_TABLE_NAME, "", false, null);
    private final DataType enumDataType = new DataType("AnyRandomName", "", true,
            "{\"meta\":{\"type\":\"SedsEnum\",\"protocol\":\"SEDSv1\",\"version\":\"1.0.0\"},"
            + "\"data\":{\"selected\":\"TEST1\"},\"type\":{\"elements\":[\"TEST1\",\"TEST2\",\"TEST3\",\"TEST4\"]}}");

    private final Property intProperty = new Property("IntProperty", "", PropertyAssociation.ALL);
    private final Property dblProperty = new Property("DblProperty", "", PropertyAssociation.ALL);
    private final Property strProperty = new Property("StrProperty", "", PropertyAssociation.ALL);
    private final Property timestampProperty = new Property("TimestampProperty", "", PropertyAssociation.ALL);
    private final Property urlProperty = new Property("UrlProperty", "", PropertyAssociation.ALL);

    private final Property dblVectorProperty = new Property("DblVectorProperty", "", PropertyAssociation.ALL);
    private final Property intVectorProperty = new Property("IntVectorProperty", "", PropertyAssociation.ALL);
    private final Property strVectorProperty = new Property("StrVectorProperty", "", PropertyAssociation.ALL);

    private final Property dblTableProperty = new Property("DblTableProperty", "", PropertyAssociation.ALL);

    private final Property enumProperty = new Property("EnumProperty", "", PropertyAssociation.ALL);

    @Before
    public void initProperties() {
        intProperty.setDataType(intDataType);
        dblProperty.setDataType(dblDataType);
        strProperty.setDataType(strDataType);
        timestampProperty.setDataType(timestampDataType);
        urlProperty.setDataType(urlDataType);
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
        assertEquals(PropertyDataType.INTEGER, Conversion.getDataType(intProperty));
    }

    @Test
    public void dataTypeDbl() {
        assertEquals(PropertyDataType.DOUBLE, Conversion.getDataType(dblProperty));
    }

    @Test
    public void dataTypeStr() {
        assertEquals(PropertyDataType.STRING, Conversion.getDataType(strProperty));
    }

    @Test
    public void dataTypeTimestamp() {
        assertEquals(PropertyDataType.TIMESTAMP, Conversion.getDataType(timestampProperty));
    }

    @Test
    public void dataTypeUrl() {
        assertEquals(PropertyDataType.URL, Conversion.getDataType(urlProperty));
    }

    @Test
    public void dataTypeDblVector() {
        assertEquals(PropertyDataType.DBL_VECTOR, Conversion.getDataType(dblVectorProperty));
    }

    @Test
    public void dataTypeIntVector() {
        assertEquals(PropertyDataType.INT_VECTOR, Conversion.getDataType(intVectorProperty));
    }

    @Test
    public void dataTypeStrVector() {
        assertEquals(PropertyDataType.STRING_LIST, Conversion.getDataType(strVectorProperty));
    }

    @Test
    public void dataTypeDblTable() {
        assertEquals(PropertyDataType.DBL_TABLE, Conversion.getDataType(dblTableProperty));
    }

    @Test
    public void dataTypeEnum() {
        assertEquals(PropertyDataType.ENUM, Conversion.getDataType(enumProperty));
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
    public void uiElementFromPropertyURL() {
        assertEquals(PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(urlProperty));
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
        IntValue intValue = (IntValue) Conversion.stringToValue("  123   ", intProperty);
        assertEquals(new Integer(123), intValue.getIntValue());
    }

    @Test
    public void stringToValueDbl() {
        DblValue dblValue = (DblValue) Conversion.stringToValue("  123.456   ", dblProperty);
        assertEquals(new Double(123.456), dblValue.getDblValue());

        dblValue = (DblValue) Conversion.stringToValue("  456   ", dblProperty);
        assertEquals(new Double(456), dblValue.getDblValue());
    }


    @Test
    public void stringToValueStr() {
        StrValue strValue = (StrValue) Conversion.stringToValue("  This is a   string test. ", strProperty);
        assertEquals("  This is a   string test. ", strValue.getStrValue());
    }

    @Test
    public void stringToValueTimestamp() {
        TimestampValue tsValue = (TimestampValue) Conversion.stringToValue("1973-06-20", timestampProperty);
        assertEquals("Date to timestamp conversion failed", 109382400, tsValue.getTimestampValue().getSec());

        tsValue = (TimestampValue) Conversion.stringToValue("1973-06-20 13:50:00", timestampProperty);
        assertEquals("Date & time to timestamp conversion failed", 109432200, tsValue.getTimestampValue().getSec());

        tsValue = (TimestampValue) Conversion.stringToValue("1973-06-20 13:50:00.12345", timestampProperty);
        assertEquals("Date & time in nanos to timestamp conversion failed", 109432200, tsValue.getTimestampValue().getSec());
        assertEquals("Date & time in nanos to timestamp conversion failed (nanos)", 123450000, tsValue.getTimestampValue().getNanoSec());

        final long expectedTime = 13 * 60 * 60 + 50 * 60 + (today().getTime() / 1000);
        tsValue = (TimestampValue) Conversion.stringToValue("13:50:00", timestampProperty);
        assertEquals("Time to timestamp conversion failed", expectedTime, tsValue.getTimestampValue().getSec());
    }

    @Test
    public void stringToValueEnum() {
        EnumValue enumValue = (EnumValue) Conversion.stringToValue("TEST2", enumProperty);
        assertEquals("TEST2", enumValue.getEnumValue());
    }

    @Test
    public void stringToValueIntVector() {
        final String intVectorStr = "1\n  100 \n-123";
        IntVectorValue intVectorValue = (IntVectorValue) Conversion.stringToValue(intVectorStr, intVectorProperty);
        assertEquals(Arrays.asList(1, 100, -123), intVectorValue.getIntVectorValue());
    }

    @Test
    public void stringToValueDblVector() {
        final String dblVectorStr = "0.1\n  1.0006e12 \n-1.23";
        DblVectorValue dblVectorValue = (DblVectorValue) Conversion.stringToValue(dblVectorStr, dblVectorProperty);
        assertEquals("Dbl vector string to value conversion failed.", Arrays.asList(0.1, 1.0006e12, -1.23), dblVectorValue.getDblVectorValue());

        String intVectorStr = "1\n  100 \n-123";
        dblVectorValue = (DblVectorValue) Conversion.stringToValue(intVectorStr, dblVectorProperty);
        assertEquals("Dbl vector (integers) string to value conversion failed.", Arrays.asList(1.0, 100.0, -123.0), dblVectorValue.getDblVectorValue());
    }

    @Test
    public void stringToValueStrVector() {
        String strVectorStr = "0.1\n  1.0006e12 \n-1.23";
        StrVectorValue strVectorValue = (StrVectorValue) Conversion.stringToValue(strVectorStr, strVectorProperty);
        assertEquals(Arrays.asList("0.1", "  1.0006e12 ", "-1.23"), strVectorValue.getStrVectorValue());
    }

    @Test
    public void stringToValueDblTable() {
        String sblTableStr = "1,2,3,4\n0.1, 0.2, 0.3, 0.777\n123, 456, 789, 1234567";
        DblTableValue dblTableValue = (DblTableValue) Conversion.stringToValue(sblTableStr, dblTableProperty);
        assertEquals(Arrays.asList(Arrays.asList(1.0, 2.0, 3.0, 4.0),
                                        Arrays.asList(0.1, 0.2, 0.3, 0.777),
                                        Arrays.asList(123.0, 456.0, 789.0, 1234567.0)),
                        dblTableValue.getDblTableValue());
    }

    @Test
    public void stringToValueUrl() throws MalformedURLException {
        UrlValue urlValue = (UrlValue) Conversion.stringToValue("http://www.cosylab.com", urlProperty);
        assertEquals(new URL("http://www.cosylab.com"), urlValue.getUrlValue());

        urlValue = (UrlValue) Conversion.stringToValue("https://www.cosylab.com", urlProperty);
        assertEquals(new URL("https://www.cosylab.com"), urlValue.getUrlValue());

        urlValue = (UrlValue) Conversion.stringToValue("ftp://ftp.cosylab.com/some/resource/document.pdf", urlProperty);
        assertEquals(new URL("ftp://ftp.cosylab.com/some/resource/document.pdf"), urlValue.getUrlValue());
    }

    private Date today() {
        final long dayInMillis = 1000 * 60 * 60 * 24;
        final Date today = new Date();
        today.setTime((today.getTime() / dayInMillis) * dayInMillis); // remove the time
        return today;
    }
}
