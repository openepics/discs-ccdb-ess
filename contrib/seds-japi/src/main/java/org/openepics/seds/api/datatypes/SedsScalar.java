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
package org.openepics.seds.api.datatypes;

import org.openepics.seds.util.ScalarType;

/**
 * Basic type definition for all scalar types, containing a value and metadata.
 * One <b>must always look</b> at the alarm severity to be able to correctly
 * interpret the value.
 * <p>
 * The generic type of the scalar is not enforced, although it SHOULD represent
 * a scalar primitive type of the list: boolean, number, string.
 *
 * @author Aaron Barber
 * @param <T> type of the scalar (SHOULD be a boolean, number, or string)
 */
public interface SedsScalar<T> extends SedsType {

    /**
     * Returns the primary data associated with the scalar.
     *
     * @return the value
     */
    public T getValue();

    /**
     * Returns a string representation of the scalar value.
     *
     * @return the string representation
     */
    public String getRepresentation();

    /**
     * Alarm metadata linked with the value.
     *
     * @return alarm data
     */
    public SedsAlarm getAlarm();

    /**
     * Control metadata linked with the value.
     *
     * @return control data
     */
    public SedsControl getControl();

    /**
     * Display metadata linked with the value.
     *
     * @return display data
     */
    public SedsDisplay getDisplay();

    /**
     * Time metadata linked with the value.
     *
     * @return time data
     */
    public SedsTime getTime();

    /**
     * Type metadata linked with the value (indicating the type of the Scalar,
     * ie - Boolean, Number, etc.).
     *
     * <p>
     * The type is NOT directly serialized with the scalar (instead it is
     * appended to the metadata).
     *
     * @return type data
     */
    public ScalarType getType();
}
