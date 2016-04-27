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

import java.util.Map.Entry;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsType;
import static org.openepics.seds.util.SedsException.assertNotNull;

/**
 * Utility for working with Types in memory (as a {@code Class} or an
 * <i>instance</i>) and Types as strings (standard SEDS names).
 *
 * @author Aaron Barber
 */
public class TypeUtil {

    //Cases
    //--------------------------------------------------------------------------
    private static class CornerCases {

        //Class
        //----------------------------------------------------------------------
        private static boolean isCornerCase(String typename) {
            return ValueUtil.TYPENAMES_GENERICS.containsKey(typename);
        }

        private static Class classOf(String name) {
            for (String type : ValueUtil.TYPENAMES_GENERICS.keySet()) {
                if (type.equals(name)) {
                    return ValueUtil.TYPENAMES_GENERICS.get(name).getKey();
                }
            }
            return null;
        }
        //----------------------------------------------------------------------

        //Name
        //----------------------------------------------------------------------
        private static boolean isCornerCase(SedsType value) {
            if (value instanceof SedsScalar) {
                return true;
            }

            if (value instanceof SedsScalarArray) {
                return true;
            }

            return false;
        }

        private static String nameOf(SedsType value) {
            if (value instanceof SedsScalar) {
                return nameOfScalar((SedsScalar) value);
            }

            if (value instanceof SedsScalarArray) {
                return nameOfScalarArray((SedsScalarArray) value);
            }

            return null;
        }

        private static String nameOfScalar(SedsScalar value) {
            for (String type : ValueUtil.TYPENAMES_GENERICS.keySet()) {
                Entry<Class, ScalarType> entry = ValueUtil.TYPENAMES_GENERICS.get(type);
                Class c = entry.getKey();
                ScalarType scalarType = entry.getValue();

                if (c.isAssignableFrom(value.getClass()) && scalarType.equals(value.getType())) {
                    return type;
                }
            }

            return null;
        }

        private static String nameOfScalarArray(SedsScalarArray value) {
            for (String type : ValueUtil.TYPENAMES_GENERICS.keySet()) {
                Entry<Class, ScalarType> entry = ValueUtil.TYPENAMES_GENERICS.get(type);
                Class c = entry.getKey();
                ScalarType scalarType = entry.getValue();

                if (c.isAssignableFrom(value.getClass()) && scalarType.equals(value.getType())) {
                    return type;
                }
            }

            return null;
        }
        //----------------------------------------------------------------------
    }

    private static class StandardCases {

        private static Class classOf(String name) {
            for (String type : ValueUtil.TYPENAMES.keySet()) {
                if (type.equals(name)) {
                    return ValueUtil.TYPENAMES.get(name);
                }
            }

            return null;
        }

        private static String nameOf(Class type) {
            if (type == null) {
                return null;
            }

            for (String name : ValueUtil.TYPENAMES.keySet()) {
                if (ValueUtil.TYPENAMES.get(name).isAssignableFrom(type)) {
                    return name;
                }
            }

            return null;
        }

    }
    //--------------------------------------------------------------------------

    //Name (String) <--> Type (Class)
    //--------------------------------------------------------------------------
    /**
     * Returns the {@code ScalarType} from the standard SEDS name.
     *
     * <p>
     * For example,
     * <blockquote>
     * <pre>
     * "{@code SedsScalar_Boolean}" maps to {@link ScalarType#BOOLEAN}
     * </pre>
     * </blockquote>
     *
     * @param name standard SEDS name associated with a type
     * @return {@code ScalarType} of the standard SEDS name
     * @throws IllegalArgumentException if the type name is not a standard SEDS
     * name with scalar data
     */
    public static ScalarType scalarTypeOf(String name) {
        for (String type : ValueUtil.TYPENAMES_GENERICS.keySet()) {
            if (type.equals(name)) {
                return ValueUtil.TYPENAMES_GENERICS.get(name).getValue();
            }
        }
        return ScalarType.UNKNOWN;
    }

    /**
     * Returns the {@code Class} from the standard SEDS name.
     *
     * <p>
     * For example,
     * <blockquote>
     * <pre>
     * "{@code SedsScalar_Boolean}" maps to {@link SedsScalar#getClass()}
     * </pre>
     * </blockquote>
     *
     * @param typename standard SEDS name associated with a type
     * @return {@code Class} of the standard SEDS name, returns null if the name
     * is not a standard SEDS name
     */
    public static Class classOf(String typename) {
        assertNotNull(typename, String.class, "Finding the class of the string");

        Class type;
        if (CornerCases.isCornerCase(typename)) {
            type = CornerCases.classOf(typename);
        } else {
            type = StandardCases.classOf(typename);
        }

        //Illegal Argument
        if (type == null) {
            throw SedsException.buildIAE(
                    typename,
                    ValueUtil.TYPENAMES_LIST,
                    "Finding the class of the string"
            );
        }

        return type;
    }

    /**
     * Returns the standard SEDS name associated with the type.
     *
     * <p>
     * This is not necessarily the name of the class of the object. For example,
     * the name includes metadata for the type of a value for Scalar objects
     * (ex, name: SedsScalar_Boolean).
     *
     * @param value {@code SedsType} instance to get the type name of
     * @return standard SEDS name of the object type, returns null if the type
     * is not a supported standard SEDS type
     */
    public static String nameOf(SedsType value) {
        assertNotNull(value, SedsType.class, "Finding the class name of the value");

        String type;
        if (CornerCases.isCornerCase(value)) {
            type = CornerCases.nameOf(value);
        } else {
            type = StandardCases.nameOf(value.getClass());
        }

        //Illegal Argument
        if (type == null) {
            throw SedsException.buildIAE(
                    value,
                    "Instance of one of..." + ValueUtil.TYPENAMES_LIST,
                    "Finding the typename of the value"
            );
        }

        return type;
    }
    //--------------------------------------------------------------------------

}
