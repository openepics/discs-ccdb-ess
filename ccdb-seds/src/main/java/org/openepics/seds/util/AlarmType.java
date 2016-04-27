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

import java.util.Arrays;
import org.epics.vtype.AlarmSeverity;
import static org.openepics.seds.util.SedsException.assertNotNull;

/**
 * Utility to describe the severity of an alarm.
 *
 * @author Aaron Barber
 */
public enum AlarmType {

    /**
     * The current value is valid, and there is no alarm.
     */
    NONE,
    /**
     * There is a minor problem with the value: the exact meaning is defined by
     * the channel, but typically this means that the value is valid and is
     * outside some working range.
     */
    MINOR,
    /**
     * There is a major problem with the value: the exact meaning is defined by
     * the channel, but typically this means that the value is valid and is
     * outside some working range.
     */
    MAJOR,
    /**
     * There is a major problem with the value itself: the exact meaning is
     * defined by the channel, but typically this means that the returned value
     * is not a real representation of the actual value.
     */
    INVALID,
    /**
     * The channel cannot be read and its state is undefined: the exact meaning
     * is defined by the channel, but typically this means that the client is
     * either disconnected or connected with no read access. The value is either
     * stale or invalid.
     */
    UNDEFINED;

    /**
     * Obtains the severity of the alarm from the string (case insensitive).
     *
     * @param name string representing a severity
     * @return severity from the string
     */
    public static AlarmType fromName(String name) {
        assertNotNull(name, String.class, "Finding the alarm severity type (enum value) from the string");
        return AlarmType.valueOf(name.toUpperCase());
    }

    /**
     * Obtains the severity of the alarm from the ordinal index.
     *
     * @param index ordinal index of the severity
     * @return severity from the index
     */
    public static AlarmType fromOrdinal(int index) {
        if (index > AlarmType.values().length) {
            throw SedsException.buildIAE(
                    index,
                    "ordinal index in " + Arrays.toString(AlarmType.values()),
                    "Obtaining SEDS Alarm Severity from an index."
            );
        }

        return AlarmType.values()[index];
    }

    /**
     * Converts the VType Alarm severity to a SEDS Alarm severity.
     *
     * @param value VType Alarm severity
     * @return SEDS Alarm severity
     */
    public static AlarmType fromVSeverity(AlarmSeverity value) {
        switch (value) {
            case NONE:
                return AlarmType.NONE;
            case MINOR:
                return AlarmType.MINOR;
            case MAJOR:
                return AlarmType.MAJOR;
            case INVALID:
                return AlarmType.INVALID;
            case UNDEFINED:
                return AlarmType.UNDEFINED;
        }

        throw SedsException.buildIAE(
                value,
                "value in " + Arrays.toString(AlarmType.values()),
                "Converting VType Alarm Severity to SEDS Alarm Severity."
        );
    }

    /**
     * Converts the SEDS Alarm severity to a VType Alarm severity.
     *
     * @param value SEDS Alarm severity
     * @return VType Alarm severity
     */
    public static AlarmSeverity toVSeverity(AlarmType value) {
        switch (value) {
            case NONE:
                return AlarmSeverity.NONE;
            case MINOR:
                return AlarmSeverity.MINOR;
            case MAJOR:
                return AlarmSeverity.MAJOR;
            case INVALID:
                return AlarmSeverity.INVALID;
            case UNDEFINED:
                return AlarmSeverity.UNDEFINED;
        }

        throw SedsException.buildIAE(
                value,
                "value in " + Arrays.toString(AlarmType.values()),
                "Converting SEDS Alarm Severity to VType Alarm Severity."
        );
    }
}
