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

import java.util.Objects;
import org.openepics.seds.api.datatypes.SedsControl;

class IControl implements SedsControl {

    private final Number lowLimit;
    private final Number highLimit;

    IControl(Number lowLimit, Number highLimit) {
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
    }

    @Override
    public Number getLowLimit() {
        return lowLimit;
    }

    @Override
    public Number getHighLimit() {
        return highLimit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IControl other = (IControl) obj;
        if (!Objects.equals(this.lowLimit, other.lowLimit)) {
            return false;
        }
        if (!Objects.equals(this.highLimit, other.highLimit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Control{" + "lowLimit=" + lowLimit + ", highLimit=" + highLimit + '}';
    }

}
