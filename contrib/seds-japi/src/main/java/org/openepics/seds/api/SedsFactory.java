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
package org.openepics.seds.api;

import org.epics.util.time.Timestamp;
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

/**
 * Factory to create {@code SedsType} objects based on raw data (primitive and
 * other {@code SedsType} values).
 *
 * @author Aaron Barber
 */
public interface SedsFactory {

    /**
     * Creates a SEDS Alarm from the data.
     *
     * @param severity {@link SedsAlarm#getSeverity() }
     * @param status {@link SedsAlarm#getStatus() }
     * @param message {@link SedsAlarm#getMessage() }
     * @return Alarm
     */
    public SedsAlarm newAlarm(
            AlarmType severity,
            String status,
            String message
    );

    /**
     * Creates a SEDS Control from the data.
     *
     * @param lowLimit {@link SedsControl#getLowLimit() }
     * @param highLimit {@link SedsControl#getHighLimit() }
     * @return Control
     */
    public SedsControl newControl(
            Number lowLimit,
            Number highLimit
    );

    /**
     * Creates a SEDS Display from the data.
     *
     * @param lowAlarm {@link SedsDisplay#getLowAlarm() }
     * @param highAlarm {@link SedsDisplay#getHighAlarm() }
     * @param lowDisplay {@link SedsDisplay#getLowDisplay() }
     * @param highDisplay {@link SedsDisplay#getHighDisplay() }
     * @param lowWarning {@link SedsDisplay#getLowWarning() }
     * @param highWarning {@link SedsDisplay#getHighWarning() }
     * @param description {@link SedsDisplay#getDescription() }
     * @param units {@link SedsDisplay#getUnits() }
     * @return Display
     */
    public SedsDisplay newDisplay(
            Number lowAlarm,
            Number highAlarm,
            Number lowDisplay,
            Number highDisplay,
            Number lowWarning,
            Number highWarning,
            String description,
            String units
    );

    /**
     * Creates a SEDS Enum from the data.
     *
     * @param selected {@link SedsEnum#getIndex() }
     * @param elements {@link SedsEnum#getChoices() }
     * @return Enum
     */
    public SedsEnum newEnum(
            String selected,
            String[] elements
    );

    /**
     * Creates a SEDS Time from the data.
     *
     * @param time {@link SedsTime#getUnixSec() } and
     * {@link SedsTime#getNanoSec()}
     * @param userTag {@link SedsTime#getUserTag() }
     * @return Time
     */
    public SedsTime newTime(
            Timestamp time,
            Integer userTag
    );

    /**
     * Creates a SEDS Scalar (with Boolean data) from the data.
     *
     * @param value {@link SedsScalar#getValue() }
     * @param representation the string representation. E.g.: "0"/"1", "TRUE"/"FALSE"
     * @param alarm {@link SedsScalar#getAlarm() }
     * @param control {@link SedsScalar#getControl() }
     * @param display {@link SedsScalar#getDisplay() }
     * @param time {@link SedsScalar#getTime() }
     * @return Scalar (with Boolean data)
     */
    public SedsScalar<Boolean> newScalar(
            Boolean value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS Scalar (with SedsEnum data) from the data.
     *
     * @param value {@link SedsScalar#getValue() }
     * @param representation the string representation
     * @param alarm {@link SedsScalar#getAlarm() }
     * @param control {@link SedsScalar#getControl() }
     * @param display {@link SedsScalar#getDisplay() }
     * @param time {@link SedsScalar#getTime() }
     * @return Scalar (with SedsEnum data)
     */
    public SedsScalar<SedsEnum> newScalar(
            SedsEnum value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS Scalar (with Integer data) from the data.
     *
     * @param value {@link SedsScalar#getValue() }
     * @param representation the string representation. E.g.: A hex number
     * @param alarm {@link SedsScalar#getAlarm() }
     * @param control {@link SedsScalar#getControl() }
     * @param display {@link SedsScalar#getDisplay() }
     * @param time {@link SedsScalar#getTime() }
     * @return Scalar (with Integer data)
     */
    public SedsScalar<Integer> newScalar(
            Integer value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS Scalar (with Number data) from the data.
     *
     * @param value {@link SedsScalar#getValue() }
     * @param representation the string representation. E.g.: 12.3E-4, 9876543210L
     * @param alarm {@link SedsScalar#getAlarm() }
     * @param control {@link SedsScalar#getControl() }
     * @param display {@link SedsScalar#getDisplay() }
     * @param time {@link SedsScalar#getTime() }
     * @return Scalar (with Number data)
     */
    public SedsScalar<Number> newScalar(
            Number value,
            String representation,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS Scalar (with String data) from the data.
     *
     * @param value {@link SedsScalar#getValue() }
     * @param alarm {@link SedsScalar#getAlarm() }
     * @param control {@link SedsScalar#getControl() }
     * @param display {@link SedsScalar#getDisplay() }
     * @param time {@link SedsScalar#getTime() }
     * @return Scalar (with String data)
     */
    public SedsScalar<String> newScalar(
            String value,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS ScalarArray (with Boolean array data) from the data.
     *
     * @param value {@link SedsScalarArray#getValueArray() }
     * @param representations {@link SedsScalarArray#getRepresentationArray()}
     * @param alarm {@link SedsScalarArray#getAlarm() }
     * @param display {@link SedsScalarArray#getDisplay() }
     * @param control {@link SedsScalarArray#getControl() }
     * @param time {@link SedsScalarArray#getTime() }
     * @return ScalarArray (with Boolean array data)
     */
    public SedsScalarArray<Boolean> newScalarArray(
            Boolean[] value,
            String[] representations,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS ScalarArray (with SedsEnum array data) from the data.
     *
     * @param value {@link SedsScalarArray#getValueArray() }
     * @param alarm {@link SedsScalarArray#getAlarm() }
     * @param display {@link SedsScalarArray#getDisplay() }
     * @param control {@link SedsScalarArray#getControl() }
     * @param time {@link SedsScalarArray#getTime() }
     * @return ScalarArray (with SedsEnum array data)
     */
    public SedsScalarArray<SedsEnum> newScalarArray(
            SedsEnum[] value,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS ScalarArray (with Integer array data) from the data.
     *
     * @param value {@link SedsScalarArray#getValueArray() }
     * @param representations {@link SedsScalarArray#getRepresentationArray()}
     * @param alarm {@link SedsScalarArray#getAlarm() }
     * @param display {@link SedsScalarArray#getDisplay() }
     * @param control {@link SedsScalarArray#getControl() }
     * @param time {@link SedsScalarArray#getTime() }
     * @return ScalarArray (with Integer array data)
     */
    public SedsScalarArray<Integer> newScalarArray(
            Integer[] value,
            String[] representations,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS ScalarArray (with Number array data) from the data.
     *
     * @param value {@link SedsScalarArray#getValueArray() }
     * @param representations {@link SedsScalarArray#getRepresentationArray()}
     * @param alarm {@link SedsScalarArray#getAlarm() }
     * @param display {@link SedsScalarArray#getDisplay() }
     * @param control {@link SedsScalarArray#getControl() }
     * @param time {@link SedsScalarArray#getTime() }
     * @return ScalarArray (with Number array data)
     */
    public SedsScalarArray<Number> newScalarArray(
            Number[] value,
            String[] representations,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS ScalarArray (with String array data) from the data.
     *
     * @param value {@link SedsScalarArray#getValueArray() }
     * @param alarm {@link SedsScalarArray#getAlarm() }
     * @param display {@link SedsScalarArray#getDisplay() }
     * @param control {@link SedsScalarArray#getControl() }
     * @param time {@link SedsScalarArray#getTime() }
     * @return ScalarArray (with String array data)
     */
    public SedsScalarArray<String> newScalarArray(
            String[] value,
            SedsAlarm alarm,
            SedsControl control,
            SedsDisplay display,
            SedsTime time
    );

    /**
     * Creates a SEDS Table from the data.
     *
     * @param numRows {@link SedsTable#getNumRows() }
     * @param numColumns {@link SedsTable#getNumColumns() }
     * @param names {@link SedsTable#getNames() }
     * @param values {@link SedsTable#getValues() }
     * @return Table
     */
    public SedsTable newTable(
            Integer numRows,
            Integer numColumns,
            String[] names,
            SedsScalarArray[] values
    );

    /**
     * Creates SEDS Metadata from the value.
     *
     * @param value data to create Metadata for
     * @return Metadata
     */
    public SedsMetadata newMetadata(
            SedsType value
    );

    /**
     * Creates SEDS Metadata from the data.
     *
     * @param type {@link SedsMetadata#getType() }
     * @param protocol {@link SedsMetadata#getProtocol() }
     * @param version {@link SedsMetadata#getVersion() }
     * @return Metadata
     */
    public SedsMetadata newMetadata(
            String type,
            String protocol,
            String version
    );
}
