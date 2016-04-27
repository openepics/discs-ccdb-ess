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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonReader;

import org.epics.util.time.Timestamp;
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
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.api.io.DBConverter;
import org.openepics.seds.core.Seds;
import org.openepics.seds.core.datatypes.ImmutableSedsFactory;
import org.openepics.seds.core.datatypes.SimpleSedsFactory;
import org.openepics.seds.util.ScalarType;

/**
 * @param <T> the actual Value type this converter is for.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public abstract class ValueConverter<T extends Value> {
    // we only want the factories to be instantiated once and for all descendants.
    protected static final SimpleSedsFactory SEDS_FACTORY = new SimpleSedsFactory();
    protected static final DBConverter SEDS_DB_CONVERTER = Seds.newDBConverter();
    protected static final ImmutableSedsFactory I_SEDS_FACTORY = new ImmutableSedsFactory();

    /**
     * @return the class of the {@link Value} used in this converter.
     */
    public abstract Class<T> getType();

    /** The method converts the actual {@link Value} instance into a string representation (serialization) to store in
     * the database.
     * @param attribute The {@link Value} instance to serialize
     * @return the serialized String to store into the database.
     */
    public abstract String convertToDatabaseColumn(T attribute);

    /** Converts the DB data (SEDS encoded) into a CCDB {@link Value}
     * @param dbData the database data
     * @return the {@link Value} the is decoded from database
     */
    public static Value convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        if (!dbData.startsWith("{")) {
            throw new IllegalArgumentException("SEDS data expected. Found: " + dbData);
        }

        JsonReader reader = Json.createReader(new StringReader(dbData));
        final SedsType seds = Seds.newDBConverter().deserialize(reader.readObject());

        final Value convertedValue;

        if (seds instanceof SedsScalar<?>) {
            convertedValue = convertFromSedsScalar((SedsScalar<?>) seds, dbData);
        } else if (seds instanceof SedsTime) {
            convertedValue = convertFromSedsTime((SedsTime)seds);
        } else if (seds instanceof SedsScalarArray<?>) {
            convertedValue = convertFromSedsScalarArray((SedsScalarArray<?>)seds, dbData);
        } else if (seds instanceof SedsTable) {
            convertedValue = convertFromSedsTable((SedsTable) seds);
        } else if (seds instanceof SedsEnum) {
            convertedValue = convertFromSedsEnum((SedsEnum) seds);
        } else {
            throw new IllegalArgumentException("Unable to convert DB data: " + dbData);
        }
        return convertedValue;
    }

    private static Value convertFromSedsScalar(SedsScalar<?> sedsScalar, String dbData) {
        final Value returnValue;

        switch (sedsScalar.getType()) {
            case INTEGER:
                returnValue = new IntValue((Integer)sedsScalar.getValue());
                break;
            case NUMBER:
                final Number number = (Number) sedsScalar.getValue();
                if (number instanceof Double) {
                    if (sedsScalar.getRepresentation() != null) {
                        returnValue = new DblValue(sedsScalar.getRepresentation());
                    } else {
                        returnValue = new DblValue(Double.toString((Double)number));
                    }

                    break;
                } else if (number instanceof Integer) {
                    returnValue = new IntValue((Integer)number);
                    break;
                } else {
                    throw new IllegalArgumentException("Unable to convert DB data: " + dbData);
                }
            case STRING:
                returnValue = new StrValue((String)sedsScalar.getValue());
                break;
            default:
                throw new InvalidDataTypeException("Data type not supported: " + sedsScalar.getType().name());
        }
        return returnValue;
    }

    private static Value convertFromSedsTime(SedsTime sedsTime) {
        final Timestamp epicsTimestamp = Timestamp.of(sedsTime.getUnixSec(), sedsTime.getNanoSec());
        return new TimestampValue(epicsTimestamp);
    }

    private static Value convertFromSedsScalarArray(SedsScalarArray<?> sedsScalarArray, String dbData) {
        final IntVectorValue intVectorValue;
        final List<Integer> iValues;

        switch (sedsScalarArray.getType()) {
            case INTEGER:
                iValues = new ArrayList<Integer>(Arrays.asList((Integer[])sedsScalarArray.getValueArray()));
                intVectorValue = new IntVectorValue(iValues);
                return intVectorValue;
            case NUMBER:
                final Number[] numbers = (Number[]) sedsScalarArray.getValueArray();
                final List<Double> dblValues = new ArrayList<>(numbers.length);
                for (Number element : numbers) {
                    if (element instanceof Double) {
                        dblValues.add((Double)element);
                    } else {
                        throw new InvalidDataTypeException("Data type for table not Double.");
                    }
                }
                return new DblVectorValue(dblValues, Arrays.asList(sedsScalarArray.getRepresentationArray()));
            case STRING:
                final List<String> sValues = new ArrayList<String>(
                                                            Arrays.asList((String[])sedsScalarArray.getValueArray()));
                final StrVectorValue strVectorValue = new StrVectorValue(sValues);
                return strVectorValue;
            default:
                throw new InvalidDataTypeException("Data type not supported: " + sedsScalarArray.getType().name());
        }
    }

    private static Value convertFromSedsTable(SedsTable sedsTable) {
        List<List<Double>> tableValues = new ArrayList<List<Double>>();
        if (sedsTable.getNumColumns() > 0) {
            for (SedsScalarArray<?> col : sedsTable.getValues()) {
                if (col.getType() != ScalarType.NUMBER) {
                    throw new InvalidDataTypeException("Data type not supported for table: " + col.getType().name());
                }
                final Number[] colValues = (Number[]) col.getValueArray();
                final List<Double> dblColValues = new ArrayList<>(colValues.length);
                for (Number element : colValues) {
                    if (element instanceof Double) {
                        dblColValues.add((Double)element);
                    } else {
                        throw new InvalidDataTypeException("Data type for table not Double.");
                    }
                }
                tableValues.add(dblColValues);
            }
        }
        return new DblTableValue(tableValues);
    }

    private static Value convertFromSedsEnum(SedsEnum sedsEnum) {
        return new EnumValue(sedsEnum.getSelected());
    }

}
