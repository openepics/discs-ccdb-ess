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
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;

class IScalarArray<T> implements SedsScalarArray<T> {

    private final T[] valueArray;
    private final String[] representationArray;
    private final SedsAlarm alarm;
    private final SedsDisplay display;
    private final SedsControl control;
    private final SedsTime time;
    private final ScalarType type;

    IScalarArray(Class<T> type, T[] valueArray, String[] representationArray, SedsAlarm alarm, SedsDisplay display,
            SedsControl control, SedsTime time) {
        this.valueArray = valueArray;
        this.alarm = alarm;
        this.control = control;
        this.display = display;
        this.time = time;
        this.type = ScalarType.typeOf(type);

        if (this.type == ScalarType.UNKNOWN) {
            throw SedsException.buildIAE(
                    valueArray,
                    "Element of a known scalarArray type, " + Arrays.toString(ScalarType.values()),
                    "Creating a scalarArray"
            );
        }

        if (representationArray != null && valueArray != null
                && representationArray.length != valueArray.length) {
            throw SedsException.buildIAE(representationArray.length,
                    "The length of the representation array does not match the length of values array: "
                            + this.valueArray.length,
                    "Creating a scalarArray"
            );
        }

        if (representationArray == null && valueArray != null && !(SedsEnum.class.isInstance(this))) {
            String[] reps = new String[valueArray.length];
            for (int i = 0; i < valueArray.length; ++i) {
                reps[i] = valueArray[i].toString();
            }
            this.representationArray = reps;
        } else {
            this.representationArray = representationArray;
        }
    }

    @Override
    public T[] getValueArray() {
        return valueArray;
    }

    @Override
    public String[] getRepresentationArray() {
        return representationArray;
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

    private static final double EPSILON = 0.000000000000001d;

    private static boolean isEqual(Number[] a, Number[] b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            //If not the same
            if (Math.abs(a[i].doubleValue() - b[i].doubleValue()) > EPSILON) {
                return false;
            }
        }

        return true;
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
        result = prime * result + Arrays.hashCode(representationArray);
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + Arrays.hashCode(valueArray);
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
        final IScalarArray<?> other = (IScalarArray<?>) obj;

        if (this.valueArray instanceof Number[] && other.valueArray instanceof Number[]) {
            if (!isEqual((Number[]) this.valueArray, (Number[]) other.valueArray)) {
                return false;
            }
        } else if (!Arrays.deepEquals(this.valueArray, other.valueArray)) {
            return false;
        }
        if (!Objects.equals(this.alarm, other.alarm)) {
            return false;
        }
        if (!Objects.equals(this.display, other.display)) {
            return false;
        }
        if (!Objects.equals(this.control, other.control)) {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IScalarArray [valueArray=" + Arrays.toString(valueArray) + ", representationArray="
                + Arrays.toString(representationArray) + ", alarm=" + alarm + ", display=" + display + ", control="
                + control + ", time=" + time + ", type=" + type + "]";
    }
}
