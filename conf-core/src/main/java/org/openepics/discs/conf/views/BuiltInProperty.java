package org.openepics.discs.conf.views;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.Value;

import com.google.common.base.Preconditions;

public class BuiltInProperty {

    private String name;
    private Value value;
    private DataType dataType;

    public BuiltInProperty(String name, Double value, DataType dataType) {
        this.name = name;
        if (value == null) {
            this.value = null;
        } else {
            this.value = new DblValue(value);
        }
        this.dataType = dataType;
    }

    public BuiltInProperty(String name, String value, DataType dataType) {
        this.name = name;
        if (value == null) {
            this.value = null;
        } else {
            this.value = new StrValue(value);
        }
        this.dataType = dataType;
    }

    public BuiltInProperty(String name, EnumValue value, DataType dataType) {
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

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }
}
