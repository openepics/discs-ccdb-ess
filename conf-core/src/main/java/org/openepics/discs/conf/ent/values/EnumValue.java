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
 * This class hold the selected value out of a predefined set of possible values (enumeration).
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class EnumValue implements Value {
    private final String enumValue;

    public EnumValue(String enumValue) {
        this.enumValue = Preconditions.checkNotNull(enumValue);
    }

    /**
     * @return the enumValue
     */
    public String getEnumValue() { return enumValue; }

    @Override
    public String toString() { return enumValue; }

    @Override
    public String auditLogString(int... dimensions) { return enumValue; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enumValue == null) ? 0 : enumValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EnumValue)) {
            return false;
        }
        EnumValue other = (EnumValue) obj;
        if (enumValue == null) {
            return other.enumValue == null;
        }

        return enumValue.equals(other.enumValue);
    }
}
