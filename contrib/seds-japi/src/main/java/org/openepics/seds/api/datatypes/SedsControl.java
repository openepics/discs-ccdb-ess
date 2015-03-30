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
 * Limit information specifying a numeric range (also known as a control).
 *
 * @author Aaron Barber
 */
public interface SedsControl extends SedsType {

    /**
     * The lower limit of the range for a value field.
     *
     * @return lower limit
     */
    public Number getLowLimit();

    /**
     * The upper limit of the range for a value field.
     *
     * @return upper limit
     */
    public Number getHighLimit();

}
