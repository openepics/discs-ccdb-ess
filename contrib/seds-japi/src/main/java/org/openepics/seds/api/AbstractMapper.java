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

import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.SedsException;
import static org.openepics.seds.util.SedsException.assertNotNull;

/**
 * A SEDS processor to provide a bidirectional mapping from an unknown set of
 * types (referred to as the <i>client's</i> types) to SEDS types to provide
 * Json processing capabilities.
 * <p>
 * A {@code AbstractMapper} can map:
 * <ul>
 * <li><b>from</b> a <i>client's</i> type <b>to</b> a SEDS type.
 * <li><b>to</b> a <i>client's</i> type <b>from</b> a SEDS type.
 * </ul>
 * <p>
 * A {@code AbstractMapper} can build a {@code JsonObject} from the
 * <i>client's</i>
 * type using a SEDS object as an intermediary. A {@code AbstractMapper} can
 * build a
 * <i>client's</i> type from a {@code JsonObject} using a SEDS object as an
 * intermediary.
 *
 * <p>
 * The multiple generic parameters are required because a mapper can take any
 * type from the <i>client's</i> type <i>system</i> (multiple types) and map it
 * into a single type of the SEDS type <i>system</i> (multiple types).
 *
 * <p>
 * If a <i>client's</i> type system does not accommodate a specific type (for
 * example, if a Control abstraction is not represented by any <i>client</i>
 * type), an easy solution is to use the <code>Object</code> class as the
 * generic type and returning <b><code>null</code></b> when converting in either
 * direction of the mapping.
 *
 * <p>
 * Let XTypes represent the <i>client's</i> type system. Although the mapper
 * appears to be invertible (providing functions to map SedsTypes → XTypes and
 * XTypes → SedsTypes), data is necessarily lost in the mappings. For example:
 * <pre><blockquote>
 * Let SedsFoo contain the data members:<ul>
 * <li><code>String</code> message
 * <li><code>Integer</code> value
 * </ul>
 * Let XFoo contain the data members:<ul>
 * <li><code>Integer</code> value
 * </ul>
 * Mapping an instance of XFoo to a SedsFoo and back to a XFoo loses
 * the data for the member <code>message</code>.
 *
 * ie: XFoo{"myMsg", 1} → SedsFoo{1} → XFoo{<code>null</code>, 1}
 * </blockquote></pre>
 *
 * <p>
 * Essentially, a mapper converts a <i>client's</i> type into a SEDS type which
 * converts to Json (and vice versa). This provides a standard for storing data
 * of common data structures as JSON.
 *
 * <p>
 * See {@link org.openepics.seds.core.vtype.VTypeMapper} as an example of how to
 * implement a {@code AbstractMapper}.
 *
 * @author Aaron Barber
 * @param <ALARM> <i>client's</i> type representing a SEDS Alarm
 * @param <CONTROL> <i>client's</i> type representing a SEDS Control
 * @param <DISPLAY> <i>client's</i> type representing a SEDS Display
 * @param <ENUM> <i>client's</i> type representing a SEDS Enum
 * @param <TIME> <i>client's</i> type representing a SEDS Time
 * @param <SCALAR_BOOL><i>client's</i> type representing a SEDS Scalar (of a
 * Boolean)
 * @param <SCALAR_ENUM> <i>client's</i> type representing a SEDS Scalar (of a
 * SedsEnum)
 * @param <SCALAR_INT> <i>client's</i> type representing a SEDS Scalar (of a
 * Integer)
 * @param <SCALAR_NUM> <i>client's</i> type representing a SEDS Scalar (of a
 * Number)
 * @param <SCALAR_STR> <i>client's</i> type representing a SEDS Scalar (of a
 * String)
 * @param <SCALAR_ARR_BOOL> <i>client's</i> type representing a SEDS ScalarArray
 * (of Booleans)
 * @param <SCALAR_ARR_ENUM> <i>client's</i> type representing a SEDS ScalarArray
 * (of SedsEnums)
 * @param <SCALAR_ARR_INT> <i>client's</i> type representing a SEDS ScalarArray
 * (of Integers)
 * @param <SCALAR_ARR_NUM> <i>client's</i> type representing a SEDS ScalarArray
 * (of Numbers)
 * @param <SCALAR_ARR_STR> <i>client's</i> type representing a SEDS ScalarArray
 * (of Strings)
 * @param <TABLE> <i>client's</i> type representing a SEDS Table
 */
public abstract class AbstractMapper<ALARM, CONTROL, DISPLAY, ENUM, TIME, SCALAR_BOOL, SCALAR_ENUM, SCALAR_INT, SCALAR_NUM, SCALAR_STR, SCALAR_ARR_BOOL, SCALAR_ARR_ENUM, SCALAR_ARR_INT, SCALAR_ARR_NUM, SCALAR_ARR_STR, TABLE> {

    //VType --> NameType
    //--------------------------------------------------------------------------
    /**
     * Converts the <i>client's</i> Alarm type into a standard SEDS Alarm type.
     *
     * @param value <i>client's</i> representation of a Alarm
     * @return SEDS Alarm from the <i>client's</i> data
     */
    public abstract SedsAlarm toSedsAlarm(ALARM value);

    /**
     * Converts the <i>client's</i> Control type into a standard SEDS Control
     * type.
     *
     * @param value <i>client's</i> representation of a Control
     * @return SEDS Control from the <i>client's</i> data
     */
    public abstract SedsControl toSedsControl(CONTROL value);

    /**
     * Converts the <i>client's</i> Display type into a standard SEDS Display
     * type.
     *
     * @param value <i>client's</i> representation of a Display
     * @return SEDS Display from the <i>client's</i> data
     */
    public abstract SedsDisplay toSedsDisplay(DISPLAY value);

    /**
     * Converts the <i>client's</i> Enum type into a standard SEDS Enum type.
     *
     * @param value <i>client's</i> representation of a Enum
     * @return SEDS Enum from the <i>client's</i> data
     */
    public abstract SedsEnum toSedsEnum(ENUM value);

    /**
     * Converts the <i>client's</i> Time type into a standard SEDS Time type.
     *
     * @param value <i>client's</i> representation of a Time
     * @return SEDS Time from the <i>client's</i> data
     */
    public abstract SedsTime toSedsTime(TIME value);

    /**
     * Converts the <i>client's</i> Scalar Boolean type into a standard SEDS
     * Scalar Boolean type.
     *
     * @param value <i>client's</i> representation of a Scalar Boolean
     * @return SEDS Scalar Boolean from the <i>client's</i> data
     */
    public abstract SedsScalar<Boolean> toSedsScalarBoolean(SCALAR_BOOL value);

    /**
     * Converts the <i>client's</i> Scalar SedsEnum type into a standard SEDS
     * Scalar SedsEnum type.
     *
     * @param value <i>client's</i> representation of a Scalar SedsEnum
     * @return SEDS Scalar SedsEnum from the <i>client's</i> data
     */
    public abstract SedsScalar<SedsEnum> toSedsScalarEnum(SCALAR_ENUM value);

    /**
     * Converts the <i>client's</i> Scalar Integer type into a standard SEDS
     * Scalar Integer type.
     *
     * @param value <i>client's</i> representation of a Scalar Integer
     * @return SEDS Scalar Integer from the <i>client's</i> data
     */
    public abstract SedsScalar<Integer> toSedsScalarInteger(SCALAR_INT value);

    /**
     * Converts the <i>client's</i> Scalar Number type into a standard SEDS
     * Scalar Number type.
     *
     * @param value <i>client's</i> representation of a Scalar Number
     * @return SEDS Scalar Number from the <i>client's</i> data
     */
    public abstract SedsScalar<Number> toSedsScalarNumber(SCALAR_NUM value);

    /**
     * Converts the <i>client's</i> Scalar String type into a standard SEDS
     * Scalar String type.
     *
     * @param value <i>client's</i> representation of a Scalar String
     * @return SEDS Scalar String from the <i>client's</i> data
     */
    public abstract SedsScalar<String> toSedsScalarString(SCALAR_STR value);

    /**
     * Converts the <i>client's</i> ScalarArray Boolean type into a standard
     * SEDS ScalarArray Boolean type.
     *
     * @param value <i>client's</i> representation of a ScalarArray Boolean
     * @return SEDS ScalarArray Boolean from the <i>client's</i> data
     */
    public abstract SedsScalarArray<Boolean> toSedsScalarArrayBoolean(SCALAR_ARR_BOOL value);

    /**
     * Converts the <i>client's</i> ScalarArray SedsEnum type into a standard
     * SEDS ScalarArray SedsEnum type.
     *
     * @param value <i>client's</i> representation of a ScalarArray SedsEnum
     * @return SEDS ScalarArray SedsEnum from the <i>client's</i> data
     */
    public abstract SedsScalarArray<SedsEnum> toSedsScalarArrayEnum(SCALAR_ARR_ENUM value);

    /**
     * Converts the <i>client's</i> ScalarArray Integer type into a standard
     * SEDS ScalarArray Integer type.
     *
     * @param value <i>client's</i> representation of a ScalarArray Integer
     * @return SEDS ScalarArray Integer from the <i>client's</i> data
     */
    public abstract SedsScalarArray<Integer> toSedsScalarArrayInteger(SCALAR_ARR_INT value);

    /**
     * Converts the <i>client's</i> ScalarArray Number type into a standard SEDS
     * ScalarArray Number type.
     *
     * @param value <i>client's</i> representation of a ScalarArray Number
     * @return SEDS ScalarArray Number from the <i>client's</i> data
     */
    public abstract SedsScalarArray<Number> toSedsScalarArrayNumber(SCALAR_ARR_NUM value);

    /**
     * Converts the <i>client's</i> ScalarArray String type into a standard SEDS
     * ScalarArray String type.
     *
     * @param value <i>client's</i> representation of a ScalarArray String
     * @return SEDS ScalarArray String from the <i>client's</i> data
     */
    public abstract SedsScalarArray<String> toSedsScalarArrayString(SCALAR_ARR_STR value);

    /**
     * Converts the <i>client's</i> Table type into a standard SEDS Table type.
     *
     * @param value <i>client's</i> representation of a Table
     * @return SEDS Table from the <i>client's</i> data
     */
    public abstract SedsTable toSedsTable(TABLE value);

    /**
     * Converts a general object (ideally of the <i>client's</i>) type into a
     * standard SEDS type.
     *
     * <p>
     * The implementation SHOULD return {@code null} if the <i>value</i> is NOT
     * an instance of a <i>client's</i> type represented in the
     * {@code AbstractMapper}.
     *
     * <p>
     * For example,
     * <pre>
     * Client Type System: Foo
     *
     * class FooMapper implements AbstractMapper {
     * + toSedsAlarm(FooAlarm){ ... }
     * + toSedsDisplay(FooDisplay){ ... }
     *
     * ...
     *
     * + toSedsType(Object value){ ... }
     * }
     *
     * FooMapper mapper = ...
     * mapper.toSedsType(null)                  //returns null
     * mapper.toSedsType(new FooAlarm())        //returns SedsAlarm object
     * mapper.toSedsType(new BarAlarm())        //returns null
     * </pre>
     *
     * @param value <i>client's</i> representation of a SEDS Type
     * @return SEDS Type from the <i>client's</i>data
     */
    public abstract SedsType toSedsType(Object value);
    //--------------------------------------------------------------------------

    //NameType --> VType
    //--------------------------------------------------------------------------
    /**
     * Converts a standard SEDS Alarm value into the <i>client's</i> Alarm type.
     *
     * @param value SEDS Alarm data to map
     * @return <i>client's</i> Alarm type containing the data of the SEDS Alarm
     * value
     */
    public abstract ALARM fromSedsAlarm(SedsAlarm value);

    /**
     * Converts a standard SEDS Control value into the <i>client's</i> Control
     * type.
     *
     * @param value SEDS Control data to map
     * @return <i>client's</i> Control type containing the data of the SEDS
     * Control value
     */
    public abstract CONTROL fromSedsControl(SedsControl value);

    /**
     * Converts a standard SEDS Display value into the <i>client's</i> Display
     * type.
     *
     * @param value SEDS Display data to map
     * @return <i>client's</i> Display type containing the data of the SEDS
     * Display value
     */
    public abstract DISPLAY fromSedsDisplay(SedsDisplay value);

    /**
     * Converts a standard SEDS Enum value into the <i>client's</i> Enum type.
     *
     * @param value SEDS Enum data to map
     * @return <i>client's</i> Enum type containing the data of the SEDS Enum
     * value
     */
    public abstract ENUM fromSedsEnum(SedsEnum value);

    /**
     * Converts a standard SEDS Time value into the <i>client's</i> Time type.
     *
     * @param value SEDS Time data to map
     * @return <i>client's</i> Time type containing the data of the SEDS Time
     * value
     */
    public abstract TIME fromSedsTime(SedsTime value);

    /**
     * Converts a standard SEDS Scalar Boolean value into the <i>client's</i>
     * Scalar Boolean type.
     *
     * @param value SEDS Scalar Boolean data to map
     * @return <i>client's</i> Scalar Boolean type containing the data of the
     * SEDS Scalar Boolean value
     */
    public abstract SCALAR_BOOL fromSedsScalarBoolean(SedsScalar<Boolean> value);

    /**
     * Converts a standard SEDS Scalar SedsEnum value into the <i>client's</i>
     * Scalar SedsEnum type.
     *
     * @param value SEDS Scalar SedsEnum data to map
     * @return <i>client's</i> Scalar SedsEnum type containing the data of the
     * SEDS Scalar SedsEnum value
     */
    public abstract SCALAR_ENUM fromSedsScalarEnum(SedsScalar<SedsEnum> value);

    /**
     * Converts a standard SEDS Scalar Integer value into the <i>client's</i>
     * Scalar Integer type.
     *
     * @param value SEDS Scalar Integer data to map
     * @return <i>client's</i> Scalar Integer type containing the data of the
     * SEDS Scalar Integer value
     */
    public abstract SCALAR_INT fromSedsScalarInteger(SedsScalar<Integer> value);

    /**
     * Converts a standard SEDS Scalar Number value into the <i>client's</i>
     * Scalar Number type.
     *
     * @param value SEDS Scalar Number data to map
     * @return <i>client's</i> Scalar Number type containing the data of the
     * SEDS Scalar Number value
     */
    public abstract SCALAR_NUM fromSedsScalarNumber(SedsScalar<Number> value);

    /**
     * Converts a standard SEDS Scalar String value into the <i>client's</i>
     * Scalar String type.
     *
     * @param value SEDS Scalar String data to map
     * @return <i>client's</i> Scalar String type containing the data of the
     * SEDS Scalar String value
     */
    public abstract SCALAR_STR fromSedsScalarString(SedsScalar<String> value);

    /**
     * Converts a standard SEDS ScalarArray Boolean value into the
     * <i>client's</i>
     * ScalarArray Boolean type.
     *
     * @param value SEDS ScalarArray Boolean data to map
     * @return <i>client's</i> ScalarArray Boolean type containing the data of
     * the SEDS ScalarArray Boolean value
     */
    public abstract SCALAR_ARR_BOOL fromSedsScalarArrayBoolean(SedsScalarArray<Boolean> value);

    /**
     * Converts a standard SEDS ScalarArray SedsEnum value into the
     * <i>client's</i>
     * ScalarArray SedsEnum type.
     *
     * @param value SEDS ScalarArray SedsEnum data to map
     * @return <i>client's</i> ScalarArray SedsEnum type containing the data of
     * the SEDS ScalarArray SedsEnum value
     */
    public abstract SCALAR_ARR_ENUM fromSedsScalarArrayEnum(SedsScalarArray<SedsEnum> value);

    /**
     * Converts a standard SEDS ScalarArray Integer value into the
     * <i>client's</i>
     * ScalarArray Integer type.
     *
     * @param value SEDS ScalarArray Integer data to map
     * @return <i>client's</i> ScalarArray Integer type containing the data of
     * the SEDS ScalarArray Integer value
     */
    public abstract SCALAR_ARR_INT fromSedsScalarArrayInteger(SedsScalarArray<Integer> value);

    /**
     * Converts a standard SEDS ScalarArray Number value into the
     * <i>client's</i>
     * ScalarArray Number type.
     *
     * @param value SEDS ScalarArray Number data to map
     * @return <i>client's</i> ScalarArray Number type containing the data of
     * the SEDS ScalarArray Number value
     */
    public abstract SCALAR_ARR_NUM fromSedsScalarArrayNumber(SedsScalarArray<Number> value);

    /**
     * Converts a standard SEDS ScalarArray String value into the
     * <i>client's</i>
     * ScalarArray String type.
     *
     * @param value SEDS ScalarArray String data to map
     * @return <i>client's</i> ScalarArray String type containing the data of
     * the SEDS ScalarArray String value
     */
    public abstract SCALAR_ARR_STR fromSedsScalarArrayString(SedsScalarArray<String> value);

    /**
     * Converts a standard SEDS Table value into the <i>client's</i> Table type.
     *
     * @param value SEDS Table data to map
     * @return <i>client's</i> Table type containing the data of the SEDS Table
     * value
     */
    public abstract TABLE fromSedsTable(SedsTable value);

    /**
     * Converts a standard SEDS Type value into the correct <i>client</i>
     * type.
     *
     * <p>
     * Returns null if the parameter value is null or if the parameter is not a
     * supported {@code SedsTypes}.
     *
     * <p>
     * This method determines the appropriate {@code SedsType} (ex -
     * {@code SedsAlarm}) and returns the conversion to the <i>client's</i> type
     * using the correct "from" method (ex -
     * {@link #fromSedsAlarm(org.openepics.seds.api.datatypes.SedsAlarm)}).
     *
     * @param value SEDS Type data to map
     * @return <i>client's</i> type containing the data of the SEDS Type value
     */
    public Object fromSedsType(SedsType value) {
        assertNotNull(value, SedsType.class, "SEDS Type value");

        if (value instanceof SedsAlarm) {
            return fromSedsAlarm((SedsAlarm) value);
        } else if (value instanceof SedsDisplay) {
            return fromSedsDisplay((SedsDisplay) value);
        } else if (value instanceof SedsEnum) {
            return fromSedsEnum((SedsEnum) value);
        } else if (value instanceof SedsControl) {
            return fromSedsControl((SedsControl) value);
        } else if (value instanceof SedsScalar) {
            switch (((SedsScalar) value).getType()) {
                case BOOLEAN:
                    return fromSedsScalarBoolean((SedsScalar<Boolean>) value);
                case ENUM:
                    return fromSedsScalarEnum((SedsScalar<SedsEnum>) value);
                case INTEGER:
                    return fromSedsScalarInteger((SedsScalar<Integer>) value);
                case NUMBER:
                    return fromSedsScalarNumber((SedsScalar<Number>) value);
                case STRING:
                    return fromSedsScalarString((SedsScalar<String>) value);
                case UNKNOWN:
                default: //Let exception be thrown
            }
        } else if (value instanceof SedsScalarArray) {
            switch (((SedsScalarArray) value).getType()) {
                case BOOLEAN:
                    return fromSedsScalarArrayBoolean((SedsScalarArray<Boolean>) value);
                case ENUM:
                    return fromSedsScalarArrayEnum((SedsScalarArray<SedsEnum>) value);
                case INTEGER:
                    return fromSedsScalarArrayInteger((SedsScalarArray<Integer>) value);
                case NUMBER:
                    return fromSedsScalarArrayNumber((SedsScalarArray<Number>) value);
                case STRING:
                    return fromSedsScalarArrayString((SedsScalarArray<String>) value);
                case UNKNOWN:
                default: //Let exception be thrown
            }
        } else if (value instanceof SedsTable) {
            return fromSedsTable((SedsTable) value);
        } else if (value instanceof SedsTime) {
            return fromSedsTime((SedsTime) value);
        }

        throw SedsException.buildIAE(
                value,
                "A supported SEDSType",
                "Converting the SEDS to a client type"
        );
    }
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    /**
     * Determines if the object represents a <i>client</i> type that IS able to
     * map into SEDS.
     * <p>
     * This method SHOULD NOT return true if the value is a <i>client</i> type
     * but is unable to map into SEDS.
     *
     * @param value object to check if a <i>client</i> type
     * @return true if a <i>client</i> type that is able to map into SEDS using
     * this mapper
     * @see #toSedsType(java.lang.Object)
     */
    public abstract boolean isClientType(Object value);
    //--------------------------------------------------------------------------

}
