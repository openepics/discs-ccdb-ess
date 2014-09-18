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

import java.util.List;

import com.google.common.base.Preconditions;

/**
 * A list of strings.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class StrVectorValue implements Value {
    private static final int MAX_ELEMENTS = 5;

    private final List<String> strVectorValue;

    public StrVectorValue(List<String> strVectorValue) {
        this.strVectorValue = Preconditions.checkNotNull(strVectorValue);
    }

    /**
     * @return the strVectorValues
     */
    public List<String> getStrVectorValue() { return strVectorValue; }

    @Override
    public String toString() {
        final StringBuilder retStr = new StringBuilder();
        final int vectorSize = strVectorValue.size();
        int rowIndex = 0;
        retStr.append('[');

        for (String item : strVectorValue) {
            retStr.append('"').append(item).append('"');
            rowIndex++;
            if (rowIndex < vectorSize) {
                retStr.append(", ");
            }
            if ((vectorSize > MAX_ELEMENTS) && (rowIndex >= MAX_ELEMENTS - 1)) {
                retStr.append("..., \"").append(strVectorValue.get(vectorSize - 1)).append('"');
                break;
            }
        }
        retStr.append(']');
        return retStr.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((strVectorValue == null) ? 0 : strVectorValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StrVectorValue)) {
            return false;
        }
        StrVectorValue other = (StrVectorValue) obj;
        if (strVectorValue == null) {
            return other.strVectorValue == null;
        }

        return strVectorValue.equals(other.strVectorValue);
    }
}
