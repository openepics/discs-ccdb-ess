package org.openepics.discs.conf.views;

import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Unit;

public class PropertyView {
    private final Long id;
    private final String name;
    private final String description;
    private final Unit unit;
    private final DataType dataType;
    private final PropertyValueUniqueness valueUniqueness;
    private String usedBy;

    public PropertyView(Property prop)
    {
        id = prop.getId();
        name = prop.getName();
        description = prop.getDescription();
        unit = prop.getUnit();
        dataType = prop.getDataType();
        valueUniqueness = prop.getValueUniqueness();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * @return the dataType
     */
    public DataType getDataType() {
        return dataType;
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

    /**
     * @return the property
     */
    public Property findProperty(PropertyEJB ejb) {
        return ejb.findById(id);
    }

    /**
     * @return the valueUniqueness
     */
    public PropertyValueUniqueness getValueUniqueness() {
        return valueUniqueness;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

}
