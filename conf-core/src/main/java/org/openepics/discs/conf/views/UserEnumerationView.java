/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.views;

import java.util.List;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Preconditions;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class UserEnumerationView {
    private final String name;
    private final String description;
    private final List<String> definition;
    private final String definitionString;
    private final DataType enumeration;
    private String usedBy;

    /**
     *  Creates an new view object that will expose the user enumeration to the UI layer.
     *
     * @param enumeration The user defined enumeration to base the view object on
     */
    public UserEnumerationView(DataType enumeration) {
        Preconditions.checkArgument(!Preconditions.checkNotNull(enumeration).isScalar());

        name = enumeration.getName();
        description = enumeration.getDescription();
        definition = Conversion.prepareEnumSelections(enumeration);
        definitionString = buildDefString();
        this.enumeration = enumeration;
    }

    /** @return the user enumeration name */
    public String getName() {
        return name;
    }

    /** @return the user enumeration description */
    public String getDescription() {
        return description;
    }

    /** @return the {@link List} of possible user enumeration values */
    public List<String> getDefinition() {
        return definition;
    }

    /** @return all possible enumeration values as a string - to display in the UI */
    public String getDefinitionAsString() {
        return definitionString;
    }

    /** @return the enumeration data type entity */
    public DataType getEnumeration() {
        return enumeration;
    }

    private String buildDefString() {
        final StringBuilder defString = new StringBuilder();
        defString.append('[');

        boolean first = true;
        for (String def : definition) {
            if (!first) {
                defString.append(", ");
            } else {
                first = false;
            }
            defString.append(def);
        }

        defString.append(']');
        return defString.toString();
    }

    /**
     * @return the usedBy
     */
    public String getUsedBy() {
        return usedBy;
    }

    /**
     * @param usedBy the usedBy to set
     */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }
}
