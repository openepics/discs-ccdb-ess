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

import org.openepics.seds.util.AlarmType;

/**
 * Alarm information. Represents the severity and name of the highest alarm
 * associated with the channel.
 *
 * @author Aaron Barber
 */
public interface SedsAlarm extends SedsType {

    /**
     * Returns the severity that MUST be one of the values in the enumeration
     * [NONE, MINOR, MAJOR, INVALID, UNDEFINED].
     *
     * @return severity interpreted as an index in the severity enumeration
     * @see org.epics.vtype.AlarmSeverity
     */
    public AlarmType getSeverity();

    /**
     * Returns the status that represents the status of the alarm. Common status
     * values are: [noStatus, deviceStatus, driverStatus, recordStatus,
     * dbStatus, confStatus, undefinedStatus, clientStatus].
     *
     * @return status of the alarm
     */
    public String getStatus();

    /**
     * Returns a brief text representation of the highest currently active
     * alarm.
     *
     * @return the alarm message, or name
     */
    public String getMessage();

}
