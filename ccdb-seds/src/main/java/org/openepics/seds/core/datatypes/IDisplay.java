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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((highAlarm == null) ? 0 : highAlarm.hashCode());
        result = prime * result + ((highDisplay == null) ? 0 : highDisplay.hashCode());
        result = prime * result + ((highWarning == null) ? 0 : highWarning.hashCode());
        result = prime * result + ((lowAlarm == null) ? 0 : lowAlarm.hashCode());
        result = prime * result + ((lowDisplay == null) ? 0 : lowDisplay.hashCode());
        result = prime * result + ((lowWarning == null) ? 0 : lowWarning.hashCode());
        result = prime * result + ((units == null) ? 0 : units.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IDisplay other = (IDisplay) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (highAlarm == null) {
            if (other.highAlarm != null)
                return false;
        } else if (!highAlarm.equals(other.highAlarm))
            return false;
        if (highDisplay == null) {
            if (other.highDisplay != null)
                return false;
        } else if (!highDisplay.equals(other.highDisplay))
            return false;
        if (highWarning == null) {
            if (other.highWarning != null)
                return false;
        } else if (!highWarning.equals(other.highWarning))
            return false;
        if (lowAlarm == null) {
            if (other.lowAlarm != null)
                return false;
        } else if (!lowAlarm.equals(other.lowAlarm))
            return false;
        if (lowDisplay == null) {
            if (other.lowDisplay != null)
                return false;
        } else if (!lowDisplay.equals(other.lowDisplay))
            return false;
        if (lowWarning == null) {
            if (other.lowWarning != null)
                return false;
        } else if (!lowWarning.equals(other.lowWarning))
            return false;
        if (units == null) {
            if (other.units != null)
                return false;
        } else if (!units.equals(other.units))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("IDisplay [lowAlarm=").append(lowAlarm);
        sb.append(", highAlarm=").append(highAlarm);
        sb.append(", lowDisplay=").append(lowDisplay);
        sb.append(", highDisplay=").append(highDisplay);
        sb.append(", lowWarning=").append(lowWarning);
        sb.append(", highWarning=").append(highWarning);
        sb.append(", description=").append(description);
        sb.append(", units=").append(units).append(']');
        return  sb.toString();
    }

}
