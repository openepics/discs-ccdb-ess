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

/**
 * Time information.
 *
 * @author Aaron Barber
 */
public interface SedsTime extends SedsType {

    /**
     * Unix seconds portion of the timestamp.
     * <p>
     * The timestamp of the value typically indicates when it was generated. If
     * never connected, the timestamp is the time when it was last determined
     * that no connection was made.
     *
     * @return unix seconds portion of the timestamp
     */
    public Long getUnixSec();

    /**
     * Nano seconds portion of the timestamp.
     * <p>
     * The timestamp of the value typically indicates when it was generated. If
     * never connected, the timestamp is the time when it was last determined
     * that no connection was made.
     *
     * @return nano seconds portion of the timestamp
     */
    public Integer getNanoSec();

    /**
     * Returns a user defined tag, that can be used to store extra time
     * information, such as beam shot.
     *
     * @return the user tag
     */
    public Integer getUserTag();

}
