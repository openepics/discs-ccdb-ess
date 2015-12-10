package org.openepics.discs.conf.views;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.NamedEntity;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.Utility;

public class EntityAttrPropertyValueView<E extends NamedEntity> extends EntityAttributeView<E> {
    private static final String MULTILINE_DELIMITER = "(\\r\\n)|\\r|\\n";

    private PropertyValue propertyValue;
    private boolean propertyNameChangeDisabled;
    private final boolean beingAdded;

    public EntityAttrPropertyValueView(PropertyValue propertyValue, EntityAttributeViewKind kind, E parent, String usedBy) {
        this(propertyValue, kind, parent, usedBy, false);
    }

    public EntityAttrPropertyValueView(PropertyValue propertyValue, EntityAttributeViewKind kind, E parent) {
        this(propertyValue, kind, parent, null, false);
    }

    public EntityAttrPropertyValueView(PropertyValue propertyValue, EntityAttributeViewKind kind, E parent, String usedBy, boolean beingAdded) {
        super(kind, parent, usedBy);
        this.propertyValue = propertyValue;
        this.beingAdded = beingAdded;
    }

    @Override
    public String getId() {
        return propertyValue.getId().toString();
    }

    @Override
    public String getName() {
        return propertyValue.getProperty().getName();
    }

    /** @return A String representation of the associated entity */
    @Override
    public String getValue() {
        return Conversion.valueToString(propertyValue.getPropValue());
    }

    @Override
    public DataType getType() {
        return propertyValue.getProperty().getDataType();
    }

    @Override
    public Unit getUnit() {
        return propertyValue.getUnit();
    }

    /** @return The list of values the user can select a value from if the {@link DataType} is an enumeration. */
    public List<String> getEnumSelections()
    {
        if (Conversion.getBuiltInDataType(propertyValue.getProperty().getDataType()).equals(BuiltInDataType.USER_DEFINED_ENUM)) {
            // if it is an enumeration, get the list of its options from the data type definition field
            return Conversion.prepareEnumSelections(propertyValue.getProperty().getDataType());
        }
        return null;
    }

    /** @return The type of the UI control to use depending on the {@link PropertyValue} {@link DataType} */
    public PropertyValueUIElement getPropertyValueUIElement() {
        return propertyValue.getProperty() != null ?
                Conversion.getUIElementFromProperty(propertyValue.getProperty()) :
                PropertyValueUIElement.NONE;
    }

    /**
     * @return the propertyValue
     */
    @Override
    public PropertyValue getEntity() {
        return propertyValue;
    }

    /** Called by the UI input control to set the value.
     * @param property The property
     */
    public void setProperty(Property property) {
        propertyValue.setProperty(property);
    }
    /** @return The property associated with the property value */
    public Property getProperty() {
        return propertyValue.getProperty();
    }

    /** The method called to convert user input into {@link Value} when the user presses "Save" button in the dialog.
     * Called by the UI input control to set the value.
     * @param propertyValue String representation of the property value.
     */
    public void setPropertyValue(String propertyValue) {
        //final DataType dataType = attributeToModify != null ? attributeToModify.getType() : property.getDataType();
        this.propertyValue.setPropValue(Conversion.stringToValue(propertyValue, getType()));
    }
    /** @return String representation of the property value. */
    public String getPropertyValue() {
        return Conversion.valueToString(propertyValue.getPropValue());
    }

    /** The validator for the UI input field when UI control accepts a double precision number, and integer number or a
     * string for input.
     * Called when saving {@link PropertyValue}
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException {@link javax.faces.validator.ValidatorException}
     */
    public void inputValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "No value to parse."));
        }

        if (propertyValue.getProperty() == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "You must select a property first."));
        }

        final DataType dataType = propertyValue.getProperty().getDataType();
        validateSingleLine(value.toString(), dataType);
    }

    public static void validateSingleLine(final String strValue, final DataType dataType) {
        switch (Conversion.getBuiltInDataType(dataType)) {
            case DOUBLE:
                try {
                    Double.parseDouble(strValue.trim());
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Not a double value."));
                }
                break;
            case INTEGER:
                try {
                    Integer.parseInt(strValue.trim());
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Not an integer number."));
                }
                break;
            case STRING:
                break;
            case TIMESTAMP:
                try {
                    Conversion.toTimestamp(strValue);
                } catch (RuntimeException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, e.getMessage()), e);
                }
                break;
            default:
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "Incorrect property data type."));
        }
    }

    /** The validator for the UI input area when the UI control accepts a matrix of double precision numbers or a list
     * of values for input.
     * Called when saving {@link PropertyValue}
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException {@link javax.faces.validator.ValidatorException}
     */
    public void areaValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "No value to parse."));
        }
        if (propertyValue.getProperty() == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "You must select a property first."));
        }

        final DataType dataType = propertyValue.getProperty().getDataType();
        validateMultiLine(value.toString(), dataType);
    }

    public static void validateMultiLine(final String strValue, final DataType dataType) {
        switch (Conversion.getBuiltInDataType(dataType)) {
            case DBL_TABLE:
                validateTable(strValue);
                break;
            case DBL_VECTOR:
                validateDblVector(strValue);
                break;
            case INT_VECTOR:
                validateIntVector(strValue);
                break;
            case STRING_LIST:
                break;
            default:
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "Incorrect property data type."));
        }
    }

    private static void validateTable(final String value) throws ValidatorException {
        try (Scanner lineScanner = new Scanner(value)) {
            lineScanner.useDelimiter(Pattern.compile(MULTILINE_DELIMITER));

            int lineLength = -1;
            while (lineScanner.hasNext()) {
                // replace unicode no-break spaces with normal ones
                final String line = lineScanner.next().replaceAll("\u00A0", " ");

                try (Scanner valueScanner = new Scanner(line)) {
                    valueScanner.useDelimiter(",\\s*");
                    int currentLineLength = 0;
                    while (valueScanner.hasNext()) {
                        final String dblValue = valueScanner.next().trim();
                        currentLineLength++;
                        try {
                            Double.valueOf(dblValue);
                        } catch (NumberFormatException e) {
                            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    Utility.MESSAGE_SUMMARY_ERROR, "Incorrect value: " + dblValue));
                        }
                    }
                    if (lineLength < 0) {
                        lineLength = currentLineLength;
                    } else if (currentLineLength != lineLength) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                Utility.MESSAGE_SUMMARY_ERROR, "All rows must contain the same number of elements."));
                    }
                }
            }
        }
    }

    private static void validateIntVector(final String value) throws ValidatorException {
        try (Scanner scanner = new Scanner(value)) {
            scanner.useDelimiter(Pattern.compile(MULTILINE_DELIMITER));

            while (scanner.hasNext()) {
                String intValue = "<error>";
                try {
                    // replace unicode no-break spaces with normal ones
                    intValue = scanner.next().replaceAll("\\u00A0", " ").trim();
                    Integer.parseInt(intValue);
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Incorrect value: " + intValue));
                }
            }
        }
    }

    private static void validateDblVector(final String value) throws ValidatorException {
        try (Scanner scanner = new Scanner(value)) {
            scanner.useDelimiter(Pattern.compile(MULTILINE_DELIMITER));

            while (scanner.hasNext()) {
                String dblValue = "<error>";
                try {
                    // replace unicode no-break spaces with normal ones
                    dblValue = scanner.next().replaceAll("\\u00A0", " ").trim();
                    Double.parseDouble(dblValue);
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Incorrect value: " + dblValue));
                }
            }
        }
    }


    /**
     * @return the propertyNameChangeDisabled
     */
    public boolean isPropertyNameChangeDisabled() {
        return propertyNameChangeDisabled;
    }

    /**
     * @param propertyNameChangeDisabled the propertyNameChangeDisabled to set
     */
    public void setPropertyNameChangeDisabled(boolean propertyNameChangeDisabled) {
        this.propertyNameChangeDisabled = propertyNameChangeDisabled;
    }

    /**
     * @return the beingAdded
     */
    public boolean isBeingAdded() {
        return beingAdded;
    }
}
