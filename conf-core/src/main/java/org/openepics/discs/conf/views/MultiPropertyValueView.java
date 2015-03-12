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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.UnhandledCaseException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class MultiPropertyValueView {

    private final Property property;
    private final List<String> enumValues;
    private final PropertyValueUIElement propertyValueUIElement;
    private Value value;
    private String uiValue;

    public MultiPropertyValueView(Property property) {
        this.property = property;
        propertyValueUIElement = Conversion.getUIElementFromProperty(property);
        if (Strings.isNullOrEmpty(property.getDataType().getDefinition())) {
            enumValues = Lists.newArrayList(new String[] {"Not an enum"} );
        } else {
            enumValues = Conversion.prepareEnumSelections(property.getDataType());
        }
    }

    public Long getId() {
        return property.getId();
    }

    public String getName() {
        return property.getName();
    }

    public String getDescription() {
        return property.getDescription();
    }

    public DataType getDataType() {
        return property.getDataType();
    }

    public Unit getUnit() {
        return property.getUnit();
    }

    public Property getProperty() {
        return property;
    }

    public List<String> getEnumOptions() {
        return enumValues;
    }

    /** @return the value */
    public Value getValue() {
        return value;
    }

    /** @param value the value to set */
    public void setValue(Value value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enumValues == null) ? 0 : enumValues.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other == this) {
            return true;
        }

        if (other instanceof MultiPropertyValueView) {
            return property.equals(((MultiPropertyValueView)other).property);
        } else {
            return false;
        }
    }

    private Value defaultPropertyValue() {
        Value returnValue;
        switch (Conversion.getBuiltInDataType(property.getDataType())) {
            case DOUBLE:
            case INTEGER:
            case INT_VECTOR:
            case DBL_VECTOR:
            case DBL_TABLE:
                returnValue = Conversion.stringToValue("0", property.getDataType());
                break;
            case STRING:
            case STRING_LIST:
                returnValue = Conversion.stringToValue("", property.getDataType());
                break;
            case TIMESTAMP:
                final SimpleDateFormat sdf = new SimpleDateFormat(Conversion.DATE_ONLY_FORMAT);
                returnValue = Conversion.stringToValue(sdf.format(new Date()), property.getDataType());
                break;
            case USER_DEFINED_ENUM:
                returnValue = new EnumValue("");
                break;
            default:
                throw new UnhandledCaseException();
        }
        return returnValue;
    }

    /** @return the propertyValueUIElement */
    public PropertyValueUIElement getPropertyValueUIElement() {
        return propertyValueUIElement;
    }

    /** @return the uiValue */
    public String getUiValue() {
        return uiValue;
    }

    /** @param uiValue the uiValue to set */
    public void setUiValue(String uiValue) {
        this.uiValue = uiValue;
    }
}
