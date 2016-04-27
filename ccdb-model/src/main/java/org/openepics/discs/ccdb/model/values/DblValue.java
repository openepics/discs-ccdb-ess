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
 * A single double precision value.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class DblValue implements Value {
    private final Double dblValue;
    private final String representation;

    /** Constructs a new double precision value
     * @param dblValue a double precision number in string representation
     */
    public DblValue(String dblValue) {
        Preconditions.checkNotNull(dblValue);
        this.dblValue = Double.valueOf(dblValue);
        this.representation = dblValue;
    }

    public Double getDblValue() {
        return dblValue;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public String auditLogString(int... dimensions) {
        if (dimensions.length > 2) {
            throw new IllegalArgumentException("Invalid number of parameter.");
        }
        return representation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dblValue == null) ? 0 : dblValue.hashCode());
        result = prime * result + ((representation == null) ? 0 : representation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DblValue)) {
            return false;
        }
        DblValue other = (DblValue) obj;
        // dblValue cannot be null

        return dblValue.equals(other.dblValue) && representation.equals(other.representation);
    }
}
