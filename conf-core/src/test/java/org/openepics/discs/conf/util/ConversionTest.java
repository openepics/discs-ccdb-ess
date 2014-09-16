package org.openepics.discs.conf.util;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.DblVectorValue;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.IntValue;
import org.openepics.discs.conf.ent.values.IntVectorValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.StrVectorValue;

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
    public void dataType() {
        assertEquals("IntProperty getDataType failure.", PropertyDataType.INTEGER, Conversion.getDataType(intProperty));
        assertEquals("DblProperty getDataType failure.", PropertyDataType.DOUBLE, Conversion.getDataType(dblProperty));
        assertEquals("StrProperty getDataType failure.", PropertyDataType.STRING, Conversion.getDataType(strProperty));
        assertEquals("TimestampProperty getDataType failure.", PropertyDataType.TIMESTAMP, Conversion.getDataType(timestampProperty));
        assertEquals("UrlProperty getDataType failure.", PropertyDataType.URL, Conversion.getDataType(urlProperty));
        assertEquals("DblVectorProperty getDataType failure.", PropertyDataType.DBL_VECTOR, Conversion.getDataType(dblVectorProperty));
        assertEquals("IntVectorProperty getDataType failure.", PropertyDataType.INT_VECTOR, Conversion.getDataType(intVectorProperty));
        assertEquals("StrVectorProperty getDataType failure.", PropertyDataType.STRING_LIST, Conversion.getDataType(strVectorProperty));
        assertEquals("DblTableProperty getDataType failure.", PropertyDataType.DBL_TABLE, Conversion.getDataType(dblTableProperty));
        assertEquals("EnumProperty getDataType failure.", PropertyDataType.ENUM, Conversion.getDataType(enumProperty));
    }

    @Test
    public void uiElementFromProperty() {
        assertEquals("Integer UI element", PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(intProperty));
        assertEquals("Double UI element", PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(dblProperty));
        assertEquals("String UI element", PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(strProperty));
        assertEquals("Timestamp UI element", PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(timestampProperty));
        assertEquals("URL UI element", PropertyValueUIElement.INPUT, Conversion.getUIElementFromProperty(urlProperty));
        assertEquals("Integer Vector UI element", PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(intVectorProperty));
        assertEquals("Double Vector UI element", PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(dblVectorProperty));
        assertEquals("String List UI element", PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(strVectorProperty));
        assertEquals("Double Table UI element", PropertyValueUIElement.TEXT_AREA, Conversion.getUIElementFromProperty(dblTableProperty));
        assertEquals("Enumeration UI element", PropertyValueUIElement.SELECT_ONE_MENU, Conversion.getUIElementFromProperty(enumProperty));
    }

    @Test
    public void stringToValue() {
        IntValue intValue = (IntValue) Conversion.stringToValue("  123   ", intProperty);
        assertEquals("Integer string to value conversion failed.", new Integer(123), intValue.getIntValue());

        DblValue dblValue = (DblValue) Conversion.stringToValue("  123.456   ", dblProperty);
        assertEquals("Double string to value conversion failed.", new Double(123.456), dblValue.getDblValue());

        StrValue strValue = (StrValue) Conversion.stringToValue("  This is a   string test. ", strProperty);
        assertEquals("String string to value conversion failed.", "  This is a   string test. ", strValue.getStrValue());

        // TODO timestamp test

        EnumValue enumValue = (EnumValue) Conversion.stringToValue("TEST2", enumProperty);
        assertEquals("Enumeration string to value conversion failed.", "TEST2", enumValue.getEnumValue());

        String intVectorStr = "1\n  100 \n-123";
        IntVectorValue intVectorValue = (IntVectorValue) Conversion.stringToValue(intVectorStr, intVectorProperty);
        assertEquals("Int vector string to value conversion failed.", Arrays.asList(1, 100, -123), intVectorValue.getIntVectorValue());

        String dblVectorStr = "0.1\n  1.0006e12 \n-1.23";
        DblVectorValue dblVectorValue = (DblVectorValue) Conversion.stringToValue(dblVectorStr, dblVectorProperty);
        assertEquals("Dbl vector string to value conversion failed.", Arrays.asList(0.1, 1.0006e12, -1.23), dblVectorValue.getDblVectorValue());

        String strVectorStr = "0.1\n  1.0006e12 \n-1.23";
        StrVectorValue strVectorValue = (StrVectorValue) Conversion.stringToValue(strVectorStr, strVectorProperty);
        assertEquals("Str list string to value conversion failed.", Arrays.asList("0.1", "  1.0006e12 ", "-1.23"), strVectorValue.getStrVectorValue());
    }
}
