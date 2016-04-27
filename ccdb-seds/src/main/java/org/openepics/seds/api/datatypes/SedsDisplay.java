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
 * Limit and unit information needed for display.
 * <p>
 * The numeric limits are given in double precision no matter which numeric
 * type. The unit is a simple String, which can be empty if no unit information
 * is provided.
 *
 * @author Aaron Barber
 */
public interface SedsDisplay extends SedsType {

    /**
     * Lowest value before the alarm region.
     *
     * @return lower alarm limit
     */
    public Number getLowAlarm();

    /**
     * Highest value before the alarm region.
     *
     * @return upper alarm limit
     */
    public Number getHighAlarm();

    /**
     * Lowest possible value to be displayed.
     *
     * @return lower display limit
     */
    public Number getLowDisplay();

    /**
     * Highest possible value to be displayed.
     *
     * @return upper display limit
     */
    public Number getHighDisplay();

    /**
     * Lowest value before the warning region.
     *
     * @return lower warning limit
     */
    public Number getLowWarning();

    /**
     * Highest value before the warning region.
     *
     * @return upper warning limit
     */
    public Number getHighWarning();

    /**
     * Returns a brief text representation of the display.
     *
     * @return the display description, or name
     */
    public String getDescription();

    /**
     * String representation of the units used for values.
     *
     * @return units
     */
    public String getUnits();

}
