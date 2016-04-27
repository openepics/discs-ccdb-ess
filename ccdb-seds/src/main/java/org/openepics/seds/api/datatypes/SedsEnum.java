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
 * Complete representation of an enumeration, or a structure describing a value
 * drawn from a given set of valid values also given.
 *
 * @author Aaron Barber
 */
public interface SedsEnum extends SedsType {

    /**
     * The value that MUST be taken from the enumeration elements.
     *
     * @return index of the choices
     */
    public String getSelected();

    /**
     * All the possible labels. MUST contain at least one element. MUST contain
     * unique elements.
     *
     * @return the possible values
     */
    public String[] getElements();

    /**
     * Calculates the index of the selected element from the set of elements.
     * Returns -1 if the selected element is not in the set of elements. Note
     * that this is a calculated (helper) method, and the index is not
     * stored/serialized.
     *
     * @return index of the selected element from the set of elements, returns
     * -1 if the selected element is not in the set of elements
     */
    public Integer getIndex();

}
