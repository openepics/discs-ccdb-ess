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
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;

class IScalarArray<T> implements SedsScalarArray<T> {

    private final T[] valueArray;
    private final SedsAlarm alarm;
    private final SedsDisplay display;
    private final SedsControl control;
    private final SedsTime time;
    private final ScalarType type;

    IScalarArray(Class<T> type, T[] valueArray, SedsAlarm alarm, SedsDisplay display, SedsControl control, SedsTime time) {
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
    }

    @Override
    public T[] getValueArray() {
        return valueArray;
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

    @Override
    public String toString() {
        return "ScalarArray{" + "value=" + Arrays.deepToString(valueArray) + ", alarm=" + alarm + ", control=" + control + ", display=" + display + ", time=" + time + '}';
    }

}
