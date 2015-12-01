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

import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;

class IScalar<T> implements SedsScalar<T> {

    private final T value;
    private final String representation;
    private final SedsAlarm alarm;
    private final SedsDisplay display;
    private final SedsControl control;
    private final SedsTime time;
    private final ScalarType type;

    IScalar(Class<T> type, T value, String representation, SedsAlarm alarm, SedsDisplay display, SedsControl control,
            SedsTime time) {
        this.value = value;
        this.representation = representation;
        this.alarm = alarm;
        this.control = control;
        this.display = display;
        this.time = time;
        this.type = ScalarType.typeOf(type);

        if (this.type == ScalarType.UNKNOWN) {
            throw SedsException.buildIAE(
                    value,
                    "Element of a known scalar type, " + Arrays.toString(ScalarType.values()),
                    "Creating a scalar"
            );
        }
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getRepresentation() {
        return representation != null ? representation : (value != null ? value.toString() : null);
    }

    @Override
    public SedsAlarm getAlarm() {
        return alarm;
    }

    @Override
    public SedsControl getControl() {
        return control;
    }

    @Override
    public SedsDisplay getDisplay() {
        return display;
    }

    @Override
    public SedsTime getTime() {
        return time;
    }

    @Override
    public ScalarType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alarm == null) ? 0 : alarm.hashCode());
        result = prime * result + ((control == null) ? 0 : control.hashCode());
        result = prime * result + ((display == null) ? 0 : display.hashCode());
        result = prime * result + ((representation == null) ? 0 : representation.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IScalar<?> other = (IScalar<?>) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.representation, other.representation)) {
            return false;
        }
        if (!Objects.equals(this.alarm, other.alarm)) {
            return false;
        }
        if (!Objects.equals(this.control, other.control)) {
            return false;
        }
        if (!Objects.equals(this.display, other.display)) {
            return false;
        }
        if (!Objects.equals(this.time, other.time)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append("IScalar [value=").append(value);
        sb.append(", representation=").append(representation);
        sb.append(", alarm=").append(alarm);
        sb.append(", control=").append(control);
        sb.append(", display=").append(display);
        sb.append(", time=").append(time).append(']');
        return sb.toString();
    }

}
