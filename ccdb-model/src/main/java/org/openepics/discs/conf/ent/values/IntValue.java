/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ent.values;

import com.google.common.base.Preconditions;


/**
 * An integer value.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class IntValue implements Value {
    private final Integer intValue;

    /** Constructs a new integer value
     * @param intValue an integer number
     */
    public IntValue(Integer intValue) {
        this.intValue = Preconditions.checkNotNull(intValue);
    }

    public Integer getIntValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return intValue.toString();
    }

    @Override
    public String auditLogString(int... dimensions) {
        if (dimensions.length > 2) {
            throw new IllegalArgumentException("Invalid number of parameter.");
        }
        return intValue.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((intValue == null) ? 0 : intValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntValue)) {
            return false;
        }
        IntValue other = (IntValue) obj;
        if (intValue == null) {
            return other.intValue == null;
        }

        return intValue.equals(other.intValue);
    }
}
