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
 * A single string value.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class StrValue implements Value {
    private final String strValue;

    public StrValue(String strValue) {
        this.strValue = Preconditions.checkNotNull(strValue);
    }

    public String getStrValue() {
        return strValue;
    }

    @Override
    public String toString() {
        return strValue;
    }

    @Override
    public String auditLogString(int... dimensions) {
        if (dimensions.length > 2) {
            throw new IllegalArgumentException("Invalid number of parameter.");
        }
        return strValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((strValue == null) ? 0 : strValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StrValue)) {
            return false;
        }
        StrValue other = (StrValue) obj;
        if (strValue == null) {
            return other.strValue == null;
        }

        return strValue.equals(other.strValue);
    }
}
