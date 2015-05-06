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

package org.openepics.discs.conf.valueconverters;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.epics.util.time.Timestamp;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openepics.discs.conf.ent.values.DblTableValue;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.DblVectorValue;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.IntValue;
import org.openepics.discs.conf.ent.values.IntVectorValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.StrVectorValue;
import org.openepics.discs.conf.ent.values.TimestampValue;
import org.openepics.discs.conf.ent.values.Value;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class SedsConverterTest {

    private static final String SEDS_INT = "{\"meta\":{\"type\":\"SedsScalar_Integer\","
            + "\"protocol\":\"SEDSv1\",\"version\":\"1.0.0\"},\"data\":{\"value\":123}}";
    private static final String SEDS_INT_VECTOR = "{\"meta\":{\"type\":\"SedsScalarArray_Integer\","
            + "\"protocol\":\"SEDSv1\",\"version\":\"1.0.0\"},\"data\":{\"valueArray\":[1,2,3]}}";
    private static final String SEDS_DBL_TABLE = "{\"meta\":{\"type\":\"SedsTable\",\"protocol\":\"SEDSv1\","
            + "\"version\":\"1.0.0\"},\"data\":{\"numRows\":3,\"values\":"
            + "[{\"valueArray\":[0.1,0.2,0.3]},{\"valueArray\":[1.1,1.2,1.3]},{\"valueArray\":[2.1,2.2,2.3]}]},"
            + "\"type\":{\"numColumns\":3,\"names\":[null,null,null],"
            + "\"columnTypes\":[\"SedsScalarArray_Number\",\"SedsScalarArray_Number\",\"SedsScalarArray_Number\"]}}";
    private static final String SEDS_DBL_VECTOR = "{\"meta\":{\"type\":\"SedsScalarArray_Number\","
            + "\"protocol\":\"SEDSv1\",\"version\":\"1.0.0\"},\"data\":{\"valueArray\":[1.0,2.0,3.0]}}";
    private static final String SEDS_DBL = "{\"meta\":{\"type\":\"SedsScalar_Number\",\"protocol\":\"SEDSv1\","
            + "\"version\":\"1.0.0\"},\"data\":{\"value\":123.456}}";
    private static final String SEDS_ENUM = "{\"meta\":{\"type\":\"SedsEnum\",\"protocol\":\"SEDSv1\","
            + "\"version\":\"1.0.0\"},\"data\":{\"selected\":\"TEST2\"},\"type\":{\"elements\":[\"TEST2\"]}}";
    private static final String SEDS_STR = "{\"meta\":{\"type\":\"SedsScalar_String\",\"protocol\":\"SEDSv1\","
            + "\"version\":\"1.0.0\"},\"data\":{\"value\":\"TEST2\"}}";
    private static final String SEDS_STR_VECTOR = "{\"meta\":{\"type\":\"SedsScalarArray_String\","
            + "\"protocol\":\"SEDSv1\",\"version\":\"1.0.0\"},\"data\":{\"valueArray\":[\"TEST1\",\"TEST2\",\"TEST3\"]}}";
    private static final String SEDS_TIMESTAMP = "{\"meta\":{\"type\":\"SedsTime\",\"protocol\":\"SEDSv1\","
            + "\"version\":\"1.0.0\"},\"data\":{\"unixSec\":109432210,\"nanoSec\":123000000}}";

    private static final DblTableValue VAL_DBL_TABLE = new DblTableValue(Arrays.asList(Arrays.asList(0.1, 0.2, 0.3),
                    Arrays.asList(1.1, 1.2, 1.3),Arrays.asList(2.1, 2.2, 2.3)));
    private static final DblVectorValue VAL_DBL_VECTOR = new DblVectorValue(Arrays.asList(1.0, 2.0, 3.0));
    private static final DblValue VAL_DBL = new DblValue(123.456);
    private static final EnumValue VAL_ENUM = new EnumValue("TEST2");
    private static final IntValue VAL_INT = new IntValue(123);
    private static final IntVectorValue VAL_INT_VECTOR = new IntVectorValue(Arrays.asList(1, 2, 3));
    private static final StrValue VAL_STR = new StrValue("TEST2");
    private static final StrVectorValue VAL_STR_VECTOR = new StrVectorValue(Arrays.asList("TEST1", "TEST2", "TEST3"));
    private static final TimestampValue VAL_TIMESTAMP = new TimestampValue(Timestamp.of(109432210, 123000000));

    private SedsConverter sedsConverter;

    @BeforeClass
    public static void initSedsConverters() {
        final Map<Class<? extends Value>, ValueConverter<? extends Value>> converters = new HashMap<>();
        converters.put(DblTableValue.class, new DblTableValueConverter());
        converters.put(DblValue.class, new DblValueConverter());
        converters.put(DblVectorValue.class, new DblVectorValueConverter());
        converters.put(EnumValue.class, new EnumValueConverter());
        converters.put(IntValue.class, new IntValueConverter());
        converters.put(IntVectorValue.class, new IntVectorValueConverter());
        converters.put(StrValue.class, new StrValueConverter());
        converters.put(StrVectorValue.class, new StrVectorValueConverter());
        converters.put(TimestampValue.class, new TimestampValueConverter());

        SedsConverters.setConverters(converters);
    }

    @Before
    public void initilizeConverter() {
        sedsConverter = new SedsConverter();
    }

    @Test
    public void intValueToSeds() {
        assertEquals(SEDS_INT, sedsConverter.convertToDatabaseColumn(VAL_INT));
    }

    @Test
    public void intVectorValueToSeds() {
        assertEquals(SEDS_INT_VECTOR, sedsConverter.convertToDatabaseColumn(VAL_INT_VECTOR));
    }

    @Test
    public void dblTableValueToSeds() {
        assertEquals(SEDS_DBL_TABLE, sedsConverter.convertToDatabaseColumn(VAL_DBL_TABLE));
    }

    @Test
    public void dblVectorValueToSeds() {
        assertEquals(SEDS_DBL_VECTOR,sedsConverter.convertToDatabaseColumn(VAL_DBL_VECTOR));
    }

    @Test
    public void dblValueToSeds() {
        assertEquals(SEDS_DBL, sedsConverter.convertToDatabaseColumn(VAL_DBL));
    }

    @Test
    public void enumValueToSeds() {
        assertEquals(SEDS_ENUM, sedsConverter.convertToDatabaseColumn(VAL_ENUM));
    }

    @Test
    public void strValueToSeds() {
        assertEquals(SEDS_STR, sedsConverter.convertToDatabaseColumn(VAL_STR));
    }

    @Test
    public void strVectorValueToSeds() {
        assertEquals(SEDS_STR_VECTOR, sedsConverter.convertToDatabaseColumn(VAL_STR_VECTOR));
    }

    @Test
    public void timestampValueToSeds() {
        assertEquals(SEDS_TIMESTAMP, sedsConverter.convertToDatabaseColumn(VAL_TIMESTAMP));
    }

    @Test
    public void dblTableValueFromSeds() {
        assertEquals(VAL_DBL_TABLE, sedsConverter.convertToEntityAttribute(SEDS_DBL_TABLE));
    }

    @Test
    public void dblVectorValueFromSeds() {
        assertEquals(VAL_DBL_VECTOR, sedsConverter.convertToEntityAttribute(SEDS_DBL_VECTOR));
    }

    @Test
    public void dblValueFromSeds() {
        assertEquals(VAL_DBL, sedsConverter.convertToEntityAttribute(SEDS_DBL));
    }

    @Test
    public void enumValueFromSeds() {
        assertEquals(VAL_ENUM, sedsConverter.convertToEntityAttribute(SEDS_ENUM));
    }

    @Test
    public void intValueFromSeds() {
        assertEquals(VAL_INT, sedsConverter.convertToEntityAttribute(SEDS_INT));
    }

    @Test
    public void intVectorValueFromSeds() {
        assertEquals(VAL_INT_VECTOR, sedsConverter.convertToEntityAttribute(SEDS_INT_VECTOR));
    }

    @Test
    public void strValueFromSeds() {
        assertEquals(VAL_STR, sedsConverter.convertToEntityAttribute(SEDS_STR));
    }

    @Test
    public void strVectorValueFromSeds() {
        assertEquals(VAL_STR_VECTOR, sedsConverter.convertToEntityAttribute(SEDS_STR_VECTOR));
    }

    @Test
    public void timestampValueFromSeds() {
        assertEquals(VAL_TIMESTAMP, sedsConverter.convertToEntityAttribute(SEDS_TIMESTAMP));
    }
}
