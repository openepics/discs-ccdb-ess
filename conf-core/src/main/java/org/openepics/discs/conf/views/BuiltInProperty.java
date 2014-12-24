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

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.Value;

import com.google.common.base.Preconditions;

public class BuiltInProperty {

    private BuiltInPropertyName name;
    private Value value;
    private DataType dataType;

    public BuiltInProperty(BuiltInPropertyName name, Double value, DataType dataType) {
        this.name = name;
        if (value == null) {
            this.value = null;
        } else {
            this.value = new DblValue(value);
        }
        this.dataType = dataType;
    }

    public BuiltInProperty(BuiltInPropertyName name, String value, DataType dataType) {
        this.name = name;
        if (value == null) {
            this.value = null;
        } else {
            this.value = new StrValue(value);
        }
        this.dataType = dataType;
    }

    public BuiltInProperty(BuiltInPropertyName name, EnumValue value, DataType dataType) {
        this.name = name;
        this.value = value;
        this.dataType = dataType;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        Preconditions.checkArgument(value.getClass().equals(this.value.getClass()));
        this.value = value;
    }

    public BuiltInPropertyName getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }
}
