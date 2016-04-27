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
package org.openepics.discs.ccdb.model.values;

import java.util.List;

import com.google.common.base.Preconditions;

/**
 * A list of strings.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class StrVectorValue implements Value {

    private final List<String> strVectorValue;

    /** Constructs a new list of {@link String} values
     * @param strVectorValue a {@link List} of {@link String} values
     */
    public StrVectorValue(List<String> strVectorValue) {
        this.strVectorValue = Preconditions.checkNotNull(strVectorValue);
    }

    /**
     * @return the strVectorValues
     */
    public List<String> getStrVectorValue() {
        return strVectorValue;
    }

    @Override
    public String toString() {
        return auditLogString(5);
    }

    @Override
    public String auditLogString(int... dimensions) {
        if (dimensions.length < 1 || dimensions.length > 2) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }

        final int maxElements = dimensions[0];
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
            if ((vectorSize > maxElements) && (rowIndex >= maxElements - 1)) {
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
