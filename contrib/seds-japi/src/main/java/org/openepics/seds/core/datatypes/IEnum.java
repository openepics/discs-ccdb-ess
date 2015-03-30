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
package org.openepics.seds.core.datatypes;

import java.util.Arrays;
import java.util.Objects;
import org.openepics.seds.api.datatypes.SedsEnum;

/**
 *
 * @author asbarber
 */
class IEnum implements SedsEnum {

    private final String selected;
    private final String[] elements;

    IEnum(String index, String[] elements) {
        this.selected = index;
        this.elements = elements;
    }

    @Override
    public String getSelected() {
        return selected;
    }

    @Override
    public String[] getElements() {
        return elements;
    }

    @Override
    public Integer getIndex() {
        if (elements == null) {
            return -1;
        }

        return Arrays.asList(elements).indexOf(selected);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IEnum other = (IEnum) obj;
        if (!Objects.equals(this.selected, other.selected)) {
            return false;
        }
        if (!Arrays.deepEquals(this.elements, other.elements)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Enum{" + "selected=" + selected + ", elements=" + Arrays.toString(elements) + '}';
    }

}
