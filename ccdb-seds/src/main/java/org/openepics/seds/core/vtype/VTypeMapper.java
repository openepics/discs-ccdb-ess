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
package org.openepics.seds.core.vtype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import org.openepics.seds.api.AbstractMapper;
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.AlarmType;
import org.openepics.seds.util.ArrayUtil;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;

import static org.openepics.seds.util.SedsException.assertNotNull;

import org.openepics.seds.util.TypeUtil;

/**
 * A SEDS processor to provide a bidirectional mapping from <i>VType</i>s to
 * SEDS types to provide Json processing capabilities.
 *
 * <p>
 * A {@code VTypeMapper} can map:
 * <ul>
 * <li><b>from</b> a <i>VType</i> <b>to</b> a SEDS type.
 * <li><b>to</b> a <i>VType</i> <b>from</b> a SEDS type.
 * </ul>
 *
 * <p>
 * A {@code VTypeMapper} can build a {@code JsonObject} from the <i>VType</i>
 * type using a SEDS object as an intermediary. A {@code VTypeMapper} can build
 * a
 * <i>VType</i> type from a {@code JsonObject} using a SEDS object as an
 * intermediary.
 *
 * <p>
 * <b>Special Cases:</b>
 * <ul>
 * <li>A VEnum can map to a SedsEnum or SedsScalar_Enum. Using the default
 * mapping, a VEnum maps to a SedsScalar_Enum.
 * <li>A (VType) Display can map to a SedsDisplay or a SedsControl. Using the
 * default mapping, a (VType) Display maps to a SedsDisplay.
 * </ul>
 *
 * @author Aaron Barber
 */
public class VTypeMapper extends AbstractMapper<Alarm, Display, Display, VEnum, Time, VBoolean, VEnum, VInt, VNumber, VString, Object, VEnumArray, VIntArray, VNumberArray, VStringArray, VTable> {

    private final SedsFactory factory;

    /**
     * Creates a mapper that maps VTypes to and from SEDS types.
     *
     * @param factory factory to create SEDS types (used in the "toSeds"
     * methods)
     */
    public VTypeMapper(SedsFactory factory) {
        assertNotNull(factory, SedsFactory.class, "Factory for the VType mapper");
        this.factory = factory;
    }

    //VType --> NameType
    //--------------------------------------------------------------------------
    @Override
    public SedsAlarm toSedsAlarm(Alarm value) {
        if (value == null) {
            return null;
        }

        AlarmSeverity severity = null;
        String status = null;
        String message = null;

        try {
            severity = value.getAlarmSeverity();
        } catch (NullPointerException e) {
        }

        try {
            message = value.getAlarmName();
        } catch (NullPointerException e) {
        }

        if (status == null && severity == null && message == null) {
            return null;
        }

        return factory.newAlarm(
                AlarmType.fromVSeverity(severity),
                status,
                message
        );
    }

    @Override
    public SedsDisplay toSedsDisplay(Display value) {
        if (value == null) {
            return null;
        }

        Number lowAlarm = null;
        Number highAlarm = null;
        Number lowDisplay = null;
        Number highDisplay = null;
        Number lowWarning = null;
        Number highWarning = null;
        String message = null;
        String units = null;

        try {
            lowAlarm = value.getLowerAlarmLimit();
        } catch (NullPointerException e) {
        }

        try {
            highAlarm = value.getUpperAlarmLimit();
        } catch (NullPointerException e) {
        }

        try {
            lowDisplay = value.getLowerDisplayLimit();
        } catch (NullPointerException e) {
        }

        try {
            highDisplay = value.getUpperDisplayLimit();
        } catch (NullPointerException e) {
        }

        try {
            lowWarning = value.getLowerWarningLimit();
        } catch (NullPointerException e) {
        }

        try {
            highWarning = value.getUpperWarningLimit();
        } catch (NullPointerException e) {
        }

        try {
            units = value.getUnits();
        } catch (NullPointerException e) {
        }

        if (lowDisplay == null && highDisplay == null && message == null && units == null) {
            return null;
        }

        return factory.newDisplay(
                lowAlarm,
                highAlarm,
                lowDisplay,
                highDisplay,
                lowWarning,
                highWarning,
                message,
                units
        );
    }

    @Override
    public SedsEnum toSedsEnum(VEnum value) {
        if (value == null) {
            return null;
        }

        Integer index = null;
        String[] elements = null;

        try {
            index = value.getIndex();
        } catch (NullPointerException e) {
        }

        try {
            elements = ArrayUtil.AsBoxedArray.typeString(value.getLabels());
        } catch (NullPointerException e) {
        }

        if (index == null && elements == null) {
            return null;
        }

        return factory.newEnum(
                elements[index],
                elements
        );
    }

    @Override
    public SedsControl toSedsControl(Display value) {

        if (value == null) {
            return null;
        }

        Number lowControl = null;
        Number highControl = null;

        try {
            lowControl = value.getLowerCtrlLimit();
        } catch (NullPointerException e) {
        }

        try {
            highControl = value.getUpperCtrlLimit();
        } catch (NullPointerException e) {
        }

        boolean lowNull = lowControl == null || lowControl.equals(Double.NaN);
        boolean highNull = highControl == null || highControl.equals(Double.NaN);
        if (lowNull && highNull) {
            return null;
        }

        return factory.newControl(
                lowControl,
                highControl
        );
    }

    @Override
    public SedsTime toSedsTime(Time value) {
        if (value == null) {
            return null;
        }

        Long unixSec = null;
        Integer nanoSec = null;
        Integer userTag = null;

        try {
            unixSec = value.getTimestamp().getSec();
        } catch (NullPointerException e) {
        }

        try {
            nanoSec = value.getTimestamp().getNanoSec();
        } catch (NullPointerException e) {
        }

        try {
            userTag = value.getTimeUserTag();
        } catch (NullPointerException e) {
        }

        if (unixSec == null && nanoSec == null && userTag == null) {
            return null;
        }

        return factory.newTime(
                Timestamp.of(unixSec, nanoSec),
                userTag
        );
    }

    @Override
    public SedsScalar<Boolean> toSedsScalarBoolean(VBoolean value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                value.getValue(),
                value.getValue().toString(),
                toSedsAlarm(value),
                toSedsControl(null),
                toSedsDisplay(null),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalar<SedsEnum> toSedsScalarEnum(VEnum value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                toSedsEnum(value),
                value.getValue(),
                toSedsAlarm(value),
                toSedsControl(null),
                toSedsDisplay(null),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalar<Integer> toSedsScalarInteger(VInt value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                value.getValue(),
                value.getValue().toString(),
                toSedsAlarm(value),
                toSedsControl(value),
                toSedsDisplay(value),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalar<Number> toSedsScalarNumber(VNumber value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                value.getValue(),
                value.getValue().toString(),
                toSedsAlarm(value),
                toSedsControl(value),
                toSedsDisplay(value),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalar<String> toSedsScalarString(VString value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                value.getValue(),
                toSedsAlarm(value),
                toSedsControl(null),
                toSedsDisplay(null),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalarArray<Boolean> toSedsScalarArrayBoolean(Object value) {
        return null;
    }

    @Override
    public SedsScalarArray<SedsEnum> toSedsScalarArrayEnum(VEnumArray value) {
        if (value == null || value.getData() == null) {
            return null;
        }

        SedsEnum[] data = new SedsEnum[value.getData().size()];

        for (int i = 0; i < data.length; ++i) {
            data[i] = factory.newEnum(
                    value.getData().get(i),
                    ArrayUtil.AsBoxedArray.typeString(value.getLabels())
            );
        }

        return factory.newScalarArray(
                data,
                toSedsAlarm(value),
                toSedsControl(null),
                toSedsDisplay(null),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalarArray<Integer> toSedsScalarArrayInteger(VIntArray value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeInteger(value.getData()),
                null,
                toSedsAlarm(value),
                toSedsControl(value),
                toSedsDisplay(value),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalarArray<Number> toSedsScalarArrayNumber(VNumberArray value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeNumber(value.getData()),
                null,
                toSedsAlarm(value),
                toSedsControl(value),
                toSedsDisplay(value),
                toSedsTime(value)
        );
    }

    @Override
    public SedsScalarArray<String> toSedsScalarArrayString(VStringArray value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeString(value.getData()),
                toSedsAlarm(value),
                toSedsControl(null),
                toSedsDisplay(null),
                toSedsTime(value)
        );
    }

    @Override
    public SedsTable toSedsTable(VTable value) {
        if (value == null) {
            return null;
        }

        Integer numRows = value.getRowCount();
        Integer numColumns = value.getColumnCount();

        String[] names = new String[value.getColumnCount()];
        SedsScalarArray[] values = new SedsScalarArray[value.getColumnCount()];

        for (int i = 0; i < value.getColumnCount(); i++) {
            names[i] = value.getColumnName(i);
            values[i] = toSedsColumn(value.getColumnData(i));
        }

        return factory.newTable(
                numRows,
                numColumns,
                names,
                values
        );
    }

    @Override
    public SedsType toSedsType(Object value) {

        //Scalars
        if (value instanceof VInt) {
            return toSedsScalarInteger((VInt) value);
        }
        if (value instanceof VEnum) {
            return toSedsScalarEnum((VEnum) value);
        }
        if (value instanceof VNumber) {
            return toSedsScalarNumber((VNumber) value);
        }
        if (value instanceof VBoolean) {
            return toSedsScalarBoolean((VBoolean) value);
        }
        if (value instanceof VString) {
            return toSedsScalarString((VString) value);
        }

        //Arrays
        if (value instanceof VIntArray) {
            return toSedsScalarArrayInteger((VIntArray) value);
        }
        if (value instanceof VEnumArray) {
            return toSedsScalarArrayEnum((VEnumArray) value);
        }
        if (value instanceof VNumberArray) {
            return toSedsScalarArrayNumber((VNumberArray) value);
        }
        if (value instanceof VStringArray) {
            return toSedsScalarArrayString((VStringArray) value);
        }
        if (value instanceof VTable) {
            return toSedsTable((VTable) value);
        }

        //Standards
        if (value instanceof Alarm) {
            return toSedsAlarm((Alarm) value);
        }
        if (value instanceof Display) {
            return toSedsDisplay((Display) value);
        }
        if (value instanceof Time) {
            return toSedsTime((Time) value);
        }

        throw SedsException.buildIAE(
                value,
                "Supported VType",
                "Mapping VType to SEDS Type"
        );
    }
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    private SedsScalarArray toSedsColumn(Object col) {
        if (col == null) {
            return null;
        }

        //List<?>
        if (col instanceof List) {
            //ScalarArray<String>
            try {
                return factory.newScalarArray(
                        ArrayUtil.AsBoxedArray.typeString((List<String>) col),
                        null, null, null, null
                );
            } catch (ClassCastException e) {
            }

            //ScalarArray<Boolean>
            try {
                return factory.newScalarArray(
                        ArrayUtil.AsBoxedArray.typeBoolean((List<Boolean>) col),
                        null, null, null, null, null
                );
            } catch (ClassCastException e) {
            }

            //ScalarArray<Integer>
            try {
                return factory.newScalarArray(
                        (Integer[]) ((List<Integer>) col).toArray(),
                        null, null, null, null, null
                );
            } catch (ClassCastException e) {
            }

            //ScalarArray<Number>
            try {
                return factory.newScalarArray(
                        (Number[]) ((List<Number>) col).toArray(),
                        null, null, null, null, null
                );
            } catch (ClassCastException e) {
            }
        }

        //ListNumber
        if (col instanceof ListNumber) {
            //ListInt
            try {
                return factory.newScalarArray(
                        ArrayUtil.AsBoxedArray.typeInteger((ListInt) col),
                        null, null, null, null, null
                );
            } catch (ClassCastException e) {
            }

            //ListNumber
            try {
                return factory.newScalarArray(
                        ArrayUtil.AsBoxedArray.typeNumber((ListNumber) col),
                        null, null, null, null, null
                );
            } catch (ClassCastException e) {
            }
        }

        //SedsScalarArray
        if (col instanceof SedsScalarArray) {
            return (SedsScalarArray) col;
        }

        //Fails
        throw SedsException.buildIAE(
                col,
                "Valid column type "
                + "(List<Boolean>, List<Integer>, List<Number>, List<String>, "
                + "ListInt, ListNumber, SedsScalarArray, null)",
                "Mapping a column (Object) into a SedsScalarArray"
        );
    }

    private Object fromSedsColumn(SedsScalarArray col) {
        if (col == null || col.getValueArray() == null) {
            return null;
        }

        Object[] arr = col.getValueArray();

        if (arr instanceof Boolean[]) {
            return ArrayUtil.AsList.typeBoolean((Boolean[]) arr);
        } else if (arr instanceof Integer[]) {
            return ArrayUtil.AsListNumber.typeInteger((Integer[]) arr);
        } else if (arr instanceof Number[]) {
            return ArrayUtil.AsListNumber.typeNumber((Number[]) arr);
        } else if (arr instanceof String[]) {
            return ArrayUtil.AsList.typeString((String[]) arr);
        } else {
            return (Arrays.asList(arr));
        }
    }

    /**
     * Merges SEDS control and SEDS display information into a VType Display
     * data type.
     *
     * @param c SEDS control information
     * @param d SEDS display information
     * @return VType Display populated with data from a SEDS control and SEDS
     * display
     */
    public Display mergeSedsToDisplay(SedsControl c, SedsDisplay d) {
        //Have neither Control or Display
        if (c == null && d == null) {
            return null;
        }

        //Have only Display
        if (c == null) {
            return fromSedsDisplay(d);
        }

        //Have only Control
        if (d == null) {
            return fromSedsControl(c);
        }

        return ValueFactory.newDisplay(
                d.getLowDisplay() == null ? Double.NaN : d.getLowDisplay().doubleValue(),
                d.getLowAlarm() == null ? Double.NaN : d.getLowAlarm().doubleValue(),
                d.getLowWarning() == null ? Double.NaN : d.getLowWarning().doubleValue(),
                d.getUnits() == null ? "" : d.getUnits(),
                null,
                d.getHighWarning() == null ? Double.NaN : d.getHighWarning().doubleValue(),
                d.getHighAlarm() == null ? Double.NaN : d.getHighAlarm().doubleValue(),
                d.getHighDisplay() == null ? Double.NaN : d.getHighDisplay().doubleValue(),
                c.getLowLimit() == null ? Double.NaN : c.getLowLimit().doubleValue(),
                c.getHighLimit() == null ? Double.NaN : c.getHighLimit().doubleValue()
        );
    }
    //--------------------------------------------------------------------------

    //NameType --> VType
    //--------------------------------------------------------------------------
    @Override
    public Alarm fromSedsAlarm(SedsAlarm value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newAlarm(
                AlarmType.toVSeverity(value.getSeverity()),
                value.getMessage()
        );
    }

    @Override
    public Display fromSedsDisplay(SedsDisplay value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newDisplay(
                value.getLowDisplay() == null ? Double.NaN : value.getLowDisplay().doubleValue(),
                value.getLowAlarm() == null ? Double.NaN : value.getLowAlarm().doubleValue(),
                value.getLowWarning() == null ? Double.NaN : value.getLowWarning().doubleValue(),
                value.getUnits() == null ? "" : value.getUnits(),
                null,
                value.getHighWarning() == null ? Double.NaN : value.getHighWarning().doubleValue(),
                value.getHighAlarm() == null ? Double.NaN : value.getHighAlarm().doubleValue(),
                value.getHighDisplay() == null ? Double.NaN : value.getHighDisplay().doubleValue(),
                Double.NaN,
                Double.NaN
        );
    }

    @Override
    public VEnum fromSedsEnum(SedsEnum value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVEnum(
                value.getIndex(),
                ArrayUtil.AsList.typeString(value.getElements()),
                null,
                null
        );
    }

    @Override
    public Display fromSedsControl(SedsControl value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newDisplay(
                Double.NaN,
                Double.NaN,
                Double.NaN,
                "",
                null,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                value.getLowLimit() == null ? Double.NaN : value.getLowLimit().doubleValue(),
                value.getHighLimit() == null ? Double.NaN : value.getHighLimit().doubleValue()
        );
    }

    @Override
    public Time fromSedsTime(SedsTime value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newTime(
                Timestamp.of(
                        value.getUnixSec(),
                        value.getNanoSec()
                ),
                value.getUserTag(),
                true
        );
    }

    @Override
    public VBoolean fromSedsScalarBoolean(SedsScalar<Boolean> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVBoolean(
                value.getValue(),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime())
        );
    }

    @Override
    public VEnum fromSedsScalarEnum(SedsScalar<SedsEnum> value) {
        if (value == null || value.getValue() == null) {
            return null;
        }

        return ValueFactory.newVEnum(
                value.getValue().getIndex(),
                ArrayUtil.AsList.typeString(value.getValue().getElements()),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime())
        );
    }

    @Override
    public VInt fromSedsScalarInteger(SedsScalar<Integer> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVInt(
                value.getValue(),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime()),
                mergeSedsToDisplay(value.getControl(), value.getDisplay())
        );
    }

    @Override
    public VNumber fromSedsScalarNumber(SedsScalar<Number> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVNumber(
                value.getValue(),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime()),
                mergeSedsToDisplay(value.getControl(), value.getDisplay())
        );
    }

    @Override
    public VString fromSedsScalarString(SedsScalar<String> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVString(
                value.getValue(),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime())
        );
    }

    @Override
    public Object fromSedsScalarArrayBoolean(SedsScalarArray<Boolean> value) {
        return null;
    }

    @Override
    public VEnumArray fromSedsScalarArrayEnum(SedsScalarArray<SedsEnum> value) {
        if (value == null || value.getValueArray() == null) {
            return null;
        }

        int[] indices = new int[value.getValueArray().length];
        String[] labels = null;

        //First case (labels remains constant)
        if (indices.length != 0) {
            indices[0] = value.getValueArray()[0].getIndex();
            labels = value.getValueArray()[0].getElements();
        }

        //Other values in the array
        for (int i = 1; i < indices.length; ++i) {
            SedsEnum element = value.getValueArray()[i];

            indices[i] = element.getIndex();

            if (!Arrays.deepEquals(labels, element.getElements())) {
                throw SedsException.buildIAE(
                        element.getElements(),
                        labels,
                        "Converting SedsScalarArray_Enum to VEnumArray. It was"
                        + "expected that the labels (enumeration constants)"
                        + "were the same for the SedsScalarArray_Enum, but this was not true"
                );
            }
        }

        return ValueFactory.newVEnumArray(
                new ArrayInt(indices),
                ArrayUtil.AsList.typeString(labels),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime())
        );
    }

    @Override
    public VIntArray fromSedsScalarArrayInteger(SedsScalarArray<Integer> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVIntArray(
                ArrayUtil.AsListNumber.typeInteger(value.getValueArray()),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime()),
                mergeSedsToDisplay(value.getControl(), value.getDisplay())
        );
    }

    @Override
    public VNumberArray fromSedsScalarArrayNumber(SedsScalarArray<Number> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVNumberArray(
                ArrayUtil.AsListNumber.typeNumber(value.getValueArray()),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime()),
                mergeSedsToDisplay(value.getControl(), value.getDisplay())
        );
    }

    @Override
    public VStringArray fromSedsScalarArrayString(SedsScalarArray<String> value) {
        if (value == null) {
            return null;
        }

        return ValueFactory.newVStringArray(
                ArrayUtil.AsList.typeString(value.getValueArray()),
                fromSedsAlarm(value.getAlarm()),
                fromSedsTime(value.getTime())
        );
    }

    @Override
    public VTable fromSedsTable(SedsTable value) {
        if (value == null) {
            return null;
        }

        List<Class<?>> columnTypes = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<Object> columns = new ArrayList<>();

        for (int i = 0; i < value.getColumnTypes().length; i++) {
            columnTypes.add(ScalarType.classOf(TypeUtil.scalarTypeOf(value.getColumnTypes()[i])));
            names.add(value.getNames()[i]);
            columns.add(fromSedsColumn(value.getValues()[i]));
        }

        return ValueFactory.newVTable(
                columnTypes,
                names,
                columns
        );
    }
    //--------------------------------------------------------------------------

    @Override
    public boolean isClientType(Object value) {

        //Scalars
        if (value instanceof VNumber) {
            return true;
        }
        if (value instanceof VBoolean) {
            return true;
        }
        if (value instanceof VString) {
            return true;
        }

        //Arrays
        if (value instanceof VNumberArray) {
            return true;
        }
        if (value instanceof VStringArray) {
            return true;
        }
        if (value instanceof VTable) {
            return true;
        }

        //Standards
        if (value instanceof VEnum) {
            return true;
        }
        if (value instanceof Alarm) {
            return true;
        }
        if (value instanceof Display) {
            return true;
        }
        if (value instanceof Time) {
            return true;
        }

        return false;
    }

}
