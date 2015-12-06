package org.openepics.discs.conf.views;

import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Utility;

public class PropertyView {
    private final Property prop;
    private final boolean beingAdded;
    private String usedBy;
    private static final List<String> UNIT_CAPABLE_DATA_TYPES =  Arrays.asList( new String[] {BuiltInDataType.INT_NAME,
            BuiltInDataType.DBL_NAME, BuiltInDataType.INT_VECTOR_NAME,
            BuiltInDataType.DBL_VECTOR_NAME, BuiltInDataType.DBL_TABLE_NAME});

    public PropertyView()
    {
        this.prop = new Property();
        beingAdded = true;
    }

    public PropertyView(Property prop)
    {
        this.prop = prop;
        beingAdded = false;
    }

    /* @return the id */
    public Long getId() {
        return prop.getId();
    }

    /** @return The name of the property the user is working on. Used by UI */
    @NotNull
    @Size(min = 1, max = 64, message = "Name can have at most 64 characters.")
    public String getName() {
        return prop.getName();
    }

    /** @return The description of the property the user is working on. Used by UI */
    @NotNull
    @Size(min = 1, max = 255, message = "Description can have at most 255 characters.")
    public String getDescription() {
        return prop.getDescription();
    }

    /** @return The {@link Unit} of the property the user is working on. Used by UI */
    public Unit getUnit() {
        return prop.getUnit();
    }

    /** @return The {@link DataType} of the property the user is working on. Used by UI */
    public DataType getDataType() {
        return prop.getDataType();
    }

    /** @return name of the places where the property is used or <code>null</code> otherwise. */
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
     * @return the valueUniqueness
     */
    public PropertyValueUniqueness getValueUniqueness() {
        return prop.getValueUniqueness();
    }

    public Property getProperty() {
        return prop;
    }

    /** @param name The name of the property the user is working on. Used by UI */
    public void setName(String name) {
        prop.setName(name);
    }

    /** @param description The description of the property the user is working on. Used by UI */
    public void setDescription(String description) {
        prop.setDescription(description);
    }

    /** @param dataType The {@link DataType} of the property the user is working on. Used by UI */
    public void setDataType(DataType dataType) {
        prop.setDataType(dataType);
    }

    /** @param unit The {@link Unit} of the property the user is working on. Used by UI */
    public void setUnit(Unit unit) {
        prop.setUnit(UNIT_CAPABLE_DATA_TYPES.contains(prop.getDataType().getName()) ? unit : null);
    }

    /** @param valueUniqueness the valueUniqueness to set */
    public void setValueUniqueness(PropertyValueUniqueness valueUniqueness) {
        prop.setValueUniqueness(valueUniqueness);
    }

    /** @return the beingAdded */
    public boolean isBeingAdded() {
        return beingAdded;
    }


    /** <p>
     * Determines whether the {@link Unit} combo box in the property dialog should be enabled
     * (the user can change the {@link Unit}, or not.
     * </p>
     * <p>
     * The {@link Unit} can be set only for some {@link DataType}s:
     * </p>
     * <ul>
     * <li>Integer</li>
     * <li>Double</li>
     * <li>Integer vector</li>
     * <li>Double vector</li>
     * <li>Double table</li>
     * </ul>
     *  Used by the UI drop-down element.
     * @return <code>true</code> if the combo box selection is enabled, <code>false</code> otherwise.
     */
    public boolean isUnitComboEnabled() {
        return prop.getDataType() != null ? UNIT_CAPABLE_DATA_TYPES.contains(prop.getDataType().getName()) : false;
    }

    public void nameValidator(String propertyName) {
        if (propertyName.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Error in name: \"{i}\""));
        }
    }
}
