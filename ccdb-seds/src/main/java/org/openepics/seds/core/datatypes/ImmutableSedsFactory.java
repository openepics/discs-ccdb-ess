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

import org.epics.util.time.Timestamp;
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.AlarmType;
import org.openepics.seds.util.SedsException;
import org.openepics.seds.util.TypeUtil;
import org.openepics.seds.util.ValueUtil;

/**
 * Factory to create immutable {@code SedsType} objects based on raw data
 * (primitive and other {@code SedsType} values).
 *
 * @author Aaron Barber
 */
public class ImmutableSedsFactory implements SedsFactory {

    @Override
    public SedsAlarm newAlarm(
            AlarmType severity,
            String status,
            String message
    ) {
        return new IAlarm(
                severity,
                status,
                message
        );
    }

    @Override
    public SedsDisplay newDisplay(
            Number lowAlarm,
            Number highAlarm,
            Number lowDisplay,
            Number highDisplay,
            Number lowWarning,
            Number highWarning,
            String description,
            String units
    ) {
        return new IDisplay(
                lowAlarm,
                highAlarm,
                lowDisplay,
                highDisplay,
                lowWarning,
                highWarning,
                description,
                units
        );
    }

    @Override
    public SedsEnum newEnum(
            String selected,
            String[] elements
    ) {
        return new IEnum(
                selected,
                elements
        );
    }

    @Override
    public SedsControl newControl(
            Number lowLimit,
            Number highLimit
    ) {
        return new IControl(
                lowLimit,
                highLimit
        );
    }

    @Override
    public SedsTime newTime(
            Timestamp time,
            Integer userTag
    ) {
        return new ITime(
                time == null ? null : time.getSec(),
                time == null ? null : time.getNanoSec(),
                userTag
        );
    }

    @Override
    public SedsScalar<Boolean> newScalar(
            Boolean value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalar<>(
                Boolean.class,
                value,
                representation,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalar<SedsEnum> newScalar(
            SedsEnum value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalar<>(
                SedsEnum.class,
                value,
                representation,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalar<Integer> newScalar(
            Integer value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalar<>(
                Integer.class,
                value,
                representation,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalar<Number> newScalar(
            Number value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalar<>(
                Number.class,
                value,
                representation,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalar<String> newScalar(
            String value,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalar<>(
                String.class,
                value,
                value,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalarArray<Boolean> newScalarArray(
            Boolean[] value,
            String[] representations,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalarArray<>(
                Boolean.class,
                value,
                representations,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalarArray<SedsEnum> newScalarArray(
            SedsEnum[] value,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalarArray<>(
                SedsEnum.class,
                value,
                null,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalarArray<Integer> newScalarArray(
            Integer[] value,
            String[] representations,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalarArray<>(
                Integer.class,
                value,
                representations,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalarArray<Number> newScalarArray(
            Number[] value,
            String[] representations,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalarArray<>(
                Number.class,
                value,
                representations,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsScalarArray<String> newScalarArray(
            String[] value,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    ) {
        return new IScalarArray<>(
                String.class,
                value,
                value,
                alarm,
                display,
                control,
                time
        );
    }

    @Override
    public SedsTable newTable(
            Integer numRows,
            Integer numColumns,
            String[] names,
            SedsScalarArray[] values
    ) {
        String[] columnTypes;

        //Null Checks
        if (names == null) {
            throw SedsException.buildNPE(String[].class, "names of columns of the table");
        }
        if (values == null) {
            throw SedsException.buildNPE(SedsScalarArray[].class, "value array of columns of the table");
        }

        //Verifies Column Sizes
        if (numColumns != names.length) {
            throw SedsException.buildIAE(names.length, numColumns, "numColumns vs length of name array");
        }
        if (numColumns != values.length) {
            throw SedsException.buildIAE(values.length, numColumns, "numColumns vs length of valueArray");
        }

        columnTypes = new String[numColumns];

        for (int i = 0; i < numColumns; ++i) {
            columnTypes[i] = TypeUtil.nameOf(values[i]);

            if (values[i] == null) {
                throw SedsException.buildNPE(SedsScalarArray.class, "column data (value array, alarm, etc...) of the table");
            }
            if (values[i].getValueArray() == null) {
                throw SedsException.buildNPE("Scalars[]", "values of the column of the table");
            }
            if (values[i].getValueArray().length != numRows) {
                throw SedsException.buildIAE(values[i], numRows, "Row sizes of the table");
            }
        }

        return new ITable(
                numRows,
                numColumns,
                names,
                columnTypes,
                values
        );
    }

    @Override
    public SedsMetadata newMetadata(
            SedsType value
    ) {
        return new IMetadata(
                TypeUtil.nameOf(value),
                ValueUtil.PROTOCOL,
                ValueUtil.VERSION
        );
    }

    @Override
    public SedsMetadata newMetadata(
            String type,
            String protocol,
            String version
    ) {
        return new IMetadata(
                type,
                protocol,
                version
        );
    }
}
