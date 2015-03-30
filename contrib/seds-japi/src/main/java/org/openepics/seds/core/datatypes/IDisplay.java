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
import org.openepics.seds.api.datatypes.SedsDisplay;

class IDisplay implements SedsDisplay {

    private final Number lowAlarm;
    private final Number highAlarm;
    private final Number lowDisplay;
    private final Number highDisplay;
    private final Number lowWarning;
    private final Number highWarning;
    private final String description;
    private final String units;

    IDisplay(
            Number lowAlarm,
            Number highAlarm,
            Number lowDisplay,
            Number highDisplay,
            Number lowWarning,
            Number highWarning,
            String description,
            String units
    ) {
        this.lowAlarm = lowAlarm;
        this.highAlarm = highAlarm;
        this.lowDisplay = lowDisplay;
        this.highDisplay = highDisplay;
        this.lowWarning = lowWarning;
        this.highWarning = highWarning;
        this.description = description;
        this.units = units;
    }

    @Override
    public Number getLowDisplay() {
        return lowDisplay;
    }

    @Override
    public Number getHighDisplay() {
        return highDisplay;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public Number getLowAlarm() {
        return lowAlarm;
    }

    @Override
    public Number getHighAlarm() {
        return highAlarm;
    }

    @Override
    public Number getLowWarning() {
        return lowWarning;
    }

    @Override
    public Number getHighWarning() {
        return highWarning;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IDisplay other = (IDisplay) obj;
        if (!Objects.equals(this.lowAlarm, other.lowAlarm)) {
            return false;
        }
        if (!Objects.equals(this.highAlarm, other.highAlarm)) {
            return false;
        }
        if (!Objects.equals(this.lowDisplay, other.lowDisplay)) {
            return false;
        }
        if (!Objects.equals(this.highDisplay, other.highDisplay)) {
            return false;
        }
        if (!Objects.equals(this.lowWarning, other.lowWarning)) {
            return false;
        }
        if (!Objects.equals(this.highWarning, other.highWarning)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.units, other.units)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Display{" + "lowAlarm=" + lowAlarm + ", highAlarm=" + highAlarm + ", lowDisplay=" + lowDisplay + ", highDisplay=" + highDisplay + ", lowWarning=" + lowWarning + ", highWarning=" + highWarning + ", description=" + description + ", units=" + units + '}';
    }

}
