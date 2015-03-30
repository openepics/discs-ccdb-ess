/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.seds.core.datatypes;

import java.util.Arrays;

import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;

/**
 * Factory to create SEDS types in memory using data that is primarily raw
 * values (no alarm metadata, no time metadata, etc.).
 *
 * <p>
 * The quickest way to build SEDS types in memory.
 *
 * @author Aaron Barber
 */
public class SimpleSedsFactory {

    private final ImmutableSedsFactory factory;

    /**
     * Creates a factory to build SEDS types in memory, where the data input is
     * primarily raw values and no meta data (no
     * {@link SedsAlarm}, {@link SedsDisplay}, etc.).
     */
    public SimpleSedsFactory() {
        factory = new ImmutableSedsFactory();
    }

    /**
     * Creates a SEDS Scalar from the data.
     *
     * @param value {@link SedsScalar#getValue()}, MUST be of a supported
     * primitive data type
     * @return scalar
     */
    public SedsScalar newScalar(
            Object value
    ) {
        if (value instanceof Boolean) {
            return factory.newScalar((Boolean) value, null, null, null, null);
        } else if (value instanceof SedsEnum) {
            return factory.newScalar((SedsEnum) value, null, null, null, null);
        } else if (value instanceof Integer) {
            return factory.newScalar((Integer) value, null, null, null, null);
        } else if (value instanceof Number) {
            return factory.newScalar((Number) value, null, null, null, null);
        } else if (value instanceof String) {
            return factory.newScalar((String) value, null, null, null, null);
        } else {
            throw SedsException.buildIAE(value, ScalarType.values(), "Building a scalar, the type of the value is unsupported");
        }
    }

    /**
     * Creates a SEDS Scalar Array from the data.
     *
     * @param value {@link SedsScalarArray#getValueArray()}, MUST be of a
     * supported primitive data type
     * @return scalar array
     */
    public SedsScalarArray newScalarArray(
            Object[] value
    ) {
        if (value instanceof Boolean[]) {
            return factory.newScalarArray((Boolean[]) value, null, null, null, null);
        } else if (value instanceof SedsEnum[]) {
            return factory.newScalarArray((SedsEnum[]) value, null, null, null, null);
        } else if (value instanceof Integer[]) {
            return factory.newScalarArray((Integer[]) value, null, null, null, null);
        } else if (value instanceof Number[]) {
            return factory.newScalarArray((Number[]) value, null, null, null, null);
        } else if (value instanceof String[]) {
            return factory.newScalarArray((String[]) value, null, null, null, null);
        } else {
            throw SedsException.buildIAE(
                    value,
                    "Array of " + Arrays.toString(ScalarType.values()),
                    "Building a scalar array, the type of the value is unsupported"
            );
        }
    }

    private SedsScalarArray newScalarArray(
            Object[] value,
            String unit
    ) {
        SedsDisplay display = factory.newDisplay(null, null, 0d, 0d, null, null, null, unit);

        if (value instanceof Boolean[]) {
            return factory.newScalarArray((Boolean[]) value, null, null, display, null);
        } else if (value instanceof SedsEnum[]) {
            return factory.newScalarArray((SedsEnum[]) value, null, null, display, null);
        } else if (value instanceof Integer[]) {
            return factory.newScalarArray((Integer[]) value, null, null, display, null);
        } else if (value instanceof Number[]) {
            return factory.newScalarArray((Number[]) value, null, null, display, null);
        } else if (value instanceof String[]) {
            return factory.newScalarArray((String[]) value, null, null, display, null);
        } else {
            throw SedsException.buildIAE(
                    value,
                    "Array of " + Arrays.toString(ScalarType.values()),
                    "Building a scalar, the type of the value is unsupported"
            );
        }
    }

    /**
     * Creates a SEDS Table from the data.
     *
     * @param names {@link SedsTable#getNames()}
     * @param values {@link SedsTable#getValues()}, columns of the table
     * @return table with given column names and values
     */
    public SedsTable newTable(
            String[] names,
            SedsScalarArray[] values
    ) {
        Integer numRows = (values.length == 0)
                ? 0
                : values[0].getValueArray().length;

        return factory.newTable(numRows, names.length, names, values);
    }

    /**
     * Creates a SEDS Table from the data.
     *
     * @param names {@link SedsTable#getNames()}
     * @param units {@link SedsDisplay#getUnits()}
     * @param values {@link SedsTable#getValues()}, MUST be an array of columns
     * (of primitive data types supported by scalars)
     * @return table with column names, units, and values
     */
    public SedsTable newTable(
            String[] names,
            String[] units,
            Object[][] values
    ) {
        SedsScalarArray[] columns = new SedsScalarArray[values.length];

        for (int i = 0; i < values.length; i++) {
            columns[i] = newScalarArray(values[i], units[i]);
        }

        return newTable(names, columns);
    }

    /**
     * @return An underlying implementation of the SEDS factory.
     */
    public SedsFactory getFactory() {
        return factory;
    }

}
