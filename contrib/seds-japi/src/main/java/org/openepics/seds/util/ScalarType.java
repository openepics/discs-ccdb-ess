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
package org.openepics.seds.util;

import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import static org.openepics.seds.util.SedsException.assertNotNull;

/**
 * Type of a scalar value (the value of a {@link SedsScalar} or the value of a
 * {@link SedsScalarArray}).
 *
 * <p>
 * The types are the simplistic types that a Seds Scalar value SHOULD be, and
 * are designed to match the types supported in JSON.
 *
 * @author Aaron Barber
 */
public enum ScalarType {

    /**
     * A boolean scalar type (the value is either true or false).
     */
    BOOLEAN,
    /**
     * An enumeration scalar type (the value is a {@link SedsEnum enumeration}).
     */
    ENUM,
    /**
     * An integer scalar type (the value belongs to the integer domain, a number
     * without a fractional component).
     */
    INTEGER,
    /**
     * A number scalar type (the value belongs to the number domain, and
     * specifically indicates a value is an instance of the Number class). This
     * means a number can be a byte, float, double, long, short, etc.
     */
    NUMBER,
    /**
     * A string scalar type (the value is a string primitive type, or a set of
     * characters).
     */
    STRING,
    /**
     * An unknown scalar type (the value most likely belongs to the domain of
     * objects or arrays).
     */
    UNKNOWN;

    /**
     * Returns the <i>scalar</i> type from the enumeration representing the type
     * of the value.
     *
     * <p>
     * For example, this will return <code>BOOLEAN</code> for the value of
     * <code>true</code> (a boolean value).
     *
     * @param value value to determine the <i>scalar</i> type of
     * @return <i>scalar</i> type from the enumeration matching the type of the
     * value
     */
    public static ScalarType typeOf(Class value) {
        if (Boolean.class.equals(value)) {
            return BOOLEAN;
        } else if (boolean.class.equals(value)) {
            return BOOLEAN;
        } else if (SedsEnum.class.equals(value)) {
            return ENUM;
        } else if (Integer.class.equals(value)) {
            return INTEGER;
        } else if (int.class.equals(value)) {
            return INTEGER;
        } else if (Number.class.equals(value)) {
            return NUMBER;
        } else if (String.class.equals(value)) {
            return STRING;
        } else {
            return UNKNOWN;
        }
    }

    /**
     * Links the {@code ScalarType} to a primitive {@code Class}.
     *
     * <p>
     * The linking is as follows:
     * <ul>
     * <li>{@link #BOOLEAN} := boolean.class
     * <li>{@link #ENUM} := SedsEnum.class
     * <li>{@link #INTEGER} := int.class
     * <li>{@link #NUMBER} := Number.class
     * <li>{@link #STRING} := String.class
     * <li>{@link #UNKNOWN} := Object.class
     * </ul>
     *
     * @param value type of a scalar
     * @return {@code Class} representing the {@code ScalarType}
     */
    public static Class classOf(ScalarType value) {
        switch (value) {
            case BOOLEAN:
                return boolean.class;
            case ENUM:
                return SedsEnum.class;
            case INTEGER:
                return int.class;
            case NUMBER:
                return Number.class;
            case STRING:
                return String.class;
            case UNKNOWN:
            default:
                throw SedsException.buildIAE(
                        value,
                        "Supported type (boolean, enum, integer, number, string)",
                        "Class of the scalar type"
                );
        }
    }

    static String fromType(ScalarType value) {
        switch (value) {
            case BOOLEAN:
                return "Boolean";
            case ENUM:
                return "Enum";
            case INTEGER:
                return "Integer";
            case NUMBER:
                return "Number";
            case STRING:
                return "String";
            case UNKNOWN:
            default:
                throw SedsException.buildIAE(
                        value,
                        "Supported type (boolean, eunm, integer, number, string)",
                        "Typename of the scalar type"
                );
        }
    }

    static ScalarType fromName(String name) {
        assertNotNull(name, String.class, "Finding the scalar type (enum value) from the string");
        return ScalarType.valueOf(name.toUpperCase());
    }
}
