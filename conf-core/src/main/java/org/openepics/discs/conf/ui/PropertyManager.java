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
package org.openepics.discs.conf.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.dl.PropertiesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.BatchIterator;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;

import com.google.common.collect.ImmutableList;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class PropertyManager extends AbstractExcelSingleFileImportUI implements Serializable {
    private static final long serialVersionUID = 1056645993595744719L;

    @Inject private transient PropertyEJB propertyEJB;

    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @PropertiesLoaderQualifier private transient DataLoader propertiesDataLoader;

    private List<Property> properties;
    private List<Property> filteredProperties;

    private String name;
    private String description;
    private DataType dataType;
    private Unit unit;
    private Property selectedProperty;
    private boolean unitComboEnabled;
    private boolean isPropertyUsed;
    private PropertyValueUniqueness valueUniqueness;

    // ---- batch property creation
    private boolean isBatchCreation;
    private int batchStartIndex;
    private int batchEndIndex;
    private int batchLeadingZeros;

    /** Creates a new instance of PropertyManager */
    public PropertyManager() {
    }

    private void init() {
        properties = propertyEJB.findAllOrderedByName();
        selectedProperty = null;
        resetFields();
    }

    /** Called when the user presses the "Save" button in the "Add new property" dialog */
    public void onAdd() {
        if (isBatchCreation) {
            multiPropertyAdd();
        } else {
            singlePropertyAdd();
        }
        init();
    }

    private void singlePropertyAdd() {
        final Property propertyToAdd = createNewProperty(name);
        propertyEJB.add(propertyToAdd);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                "New property has been created");
    }

    private void multiPropertyAdd() {
        for (final BatchIterator bi = new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
                bi.hasNext();) {
            final String propertyName = name.replace("{i}", bi.next());
            if (propertyEJB.findByName(propertyName) != null) {
                FacesContext.getCurrentInstance().addMessage("propertyNameMsg",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                "The property \"" + propertyName + "\" already exists."));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        // validation complete. Batch creation of all the properties.
        int propertiesCreated = 0;
        for (final BatchIterator bi = new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
                bi.hasNext();) {
            final String propertyName = name.replace("{i}", bi.next());
            final Property propertyToAdd = createNewProperty(propertyName);
            propertyEJB.add(propertyToAdd);
            propertiesCreated++;
        }
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                "Created " + propertiesCreated + " new properties.");
    }

    private Property createNewProperty(String propertyName) {
        final Property newProperty = new Property(propertyName, description);
        newProperty.setDataType(dataType);
        newProperty.setUnit(unit);
        newProperty.setValueUniqueness(valueUniqueness);
        return newProperty;
    }

    /** Called when the user presses the "Save" button in the "Modify a property" dialog */
    public void onModify() {
        selectedProperty.setName(name);
        selectedProperty.setDescription(description);
        selectedProperty.setDataType(dataType);
        selectedProperty.setUnit(unit);
        selectedProperty.setValueUniqueness(valueUniqueness);

        propertyEJB.save(selectedProperty);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Property was modified");
        init();
    }

    /** Prepares the data to be used in the "Add new property" dialog */
    public void prepareAddPopup() {
        resetFields();
        isPropertyUsed = false;
        RequestContext.getCurrentInstance().update("addPropertyForm:addProperty");
    }

    /** Prepares the data to be used in the "Modify a property" dialog */
    public void prepareModifyPopup() {
        name = selectedProperty.getName();
        description = selectedProperty.getDescription();
        dataType = selectedProperty.getDataType();
        unit = selectedProperty.getUnit();
        valueUniqueness = selectedProperty.getValueUniqueness();
        isPropertyUsed = propertyEJB.isPropertyUsed(selectedProperty);
        setIsUnitComboEnabled();
        RequestContext.getCurrentInstance().update("modifyPropertyForm:modifyProperty");
    }

    /** Called when the user clicks the "trash can" icon in the UI */
    public void onDelete() {
        try {
            propertyEJB.delete(selectedProperty);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Property was deleted");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_DELETE_FAIL,
                                                        "The property could not be deleted because it is used.");
            } else {
                throw e;
            }
        } finally {
            init();
        }
    }

    /** @return The list of filtered properties used by the PrimeFaces filter field */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    /** @param filteredObjects The list of filtered properties used by the PrimeFaces filter field */
    public void setFilteredProperties(List<Property> filteredObjects) {
        this.filteredProperties = filteredObjects;
    }

    /** @return The list of all properties in the database ordered by name */
    public List<Property> getProperties() {
        if (properties == null) {
            properties = propertyEJB.findAllOrderedByName();
        }
        return properties;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            loaderResult = dataLoaderHandler.loadData(inputStream, propertiesDataLoader);
            init();
            RequestContext.getCurrentInstance().update("propertiesForm");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return The name of the property the user is working on. Used by UI */
    public String getName() {
        return name;
    }
    /** @param name The name of the property the user is working on. Used by UI */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The description of the property the user is working on. Used by UI */
    public String getDescription() {
        return description;
    }
    /** @param description The description of the property the user is working on. Used by UI */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return The {@link DataType} of the property the user is working on. Used by UI */
    public DataType getDataType() {
        return dataType;
    }
    /** @param dataType The {@link DataType} of the property the user is working on. Used by UI */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /** @return The {@link Unit} of the property the user is working on. Used by UI */
    public Unit getUnit() {
        return unit;
    }
    /** @param unit The {@link Unit} of the property the user is working on. Used by UI */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Determines whether the {@link Unit} combo box in the property dialog should be enabled
     * (the user can change the {@link Unit}, or not. <br />
     * The {@link Unit} can be set only for some {@link DataType}s:
     * <ul>
     * <li>Integer</li>
     * <li>Double</li>
     * <li>Integer vector</li>
     * <li>Double vector</li>
     * <li>Double table</li>
     * </ul>
     */
    public void setIsUnitComboEnabled() {
        final List<String> possibleTypes = Arrays.asList(new String[] {BuiltInDataType.INT_NAME,
                                                BuiltInDataType.DBL_NAME, BuiltInDataType.INT_VECTOR_NAME,
                                                BuiltInDataType.DBL_VECTOR_NAME, BuiltInDataType.DBL_TABLE_NAME } );
        unitComboEnabled = possibleTypes.contains(dataType.getName());
    }

    /**
     * @return <code>true</code> if the combo box selection is enabled, <code>false</code> otherwise.
     * Used by the UI drop-down element.
     */
    public boolean isUnitComboEnabled() {
        return unitComboEnabled;
    }

    /** @return The {@link Property} selected in the dialog */
    public Property getSelectedProperty() {
        return selectedProperty;
    }
    /** @param selectedProperty The {@link Property} selected in the dialog */
    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    /** @return The {@link Property} selected in the dialog (modify property dialog) */
    public Property getSelectedPropertyToModify() {
        return selectedProperty;
    }
    /** @param selectedProperty The {@link Property} selected in the dialog (modify property dialog) */
    public void setSelectedPropertyToModify(Property selectedProperty) {
        this.selectedProperty = propertyEJB.findById(selectedProperty.getId());
        prepareModifyPopup();
    }

    private void resetFields() {
        name = null;
        description = null;
        dataType = null;
        unit = null;
        unitComboEnabled = true;
        valueUniqueness = PropertyValueUniqueness.NONE;
        isBatchCreation = false;
        batchStartIndex = 0;
        batchEndIndex = 0;
        batchLeadingZeros = 0;
    }

    /**
     * @return <code>true</code> if the property is already used in some {@link PropertyValue},
     * <code>false</code> otherwise.
     */
    public boolean isPropertyUsed() {
        return isPropertyUsed;
    }

    /** @return the valueUniqueness */
    public PropertyValueUniqueness getValueUniqueness() {
        return valueUniqueness;
    }
    /** @param valueUniqueness the valueUniqueness to set */
    public void setValueUniqueness(PropertyValueUniqueness valueUniqueness) {
        this.valueUniqueness = valueUniqueness;
    }
    /** @return the set of possible uniqueness values to show in the drop-down control */
    public List<PropertyValueUniqueness> getUniqunessValues() {
        return ImmutableList.copyOf(PropertyValueUniqueness.values());
    }

    /** Validates the {@link Property} name for uniqueness.
     * @param ctx
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void nameValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "Please enter a name"));
        }

        final String propertyName = value.toString();

        if (isBatchCreation && !propertyName.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Batch creation selected, but index position \"{i}\" not set"));
        }
        if (!isBatchCreation && propertyName.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Error in name: \"{i}\""));
        }

        final Property existingProperty = propertyEJB.findByName(propertyName);
        if ((selectedProperty == null && existingProperty != null)
                || (selectedProperty != null &&  existingProperty != null
                        && !selectedProperty.equals(existingProperty))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "The property with this name already exists."));
        }
    }

    /** @return the isBatchCreation */
    public boolean isBatchCreation() {
        return isBatchCreation;
    }
    /** @param isBatchCreation the isBatchCreation to set */
    public void setBatchCreation(boolean isBatchCreation) {
        this.isBatchCreation = isBatchCreation;
    }

    /** @return the batchStartIndex */
    public int getBatchStartIndex() {
        return batchStartIndex;
    }
    /** @param batchStartIndex the batchStartIndex to set */
    public void setBatchStartIndex(int batchStartIndex) {
        this.batchStartIndex = batchStartIndex;
    }

    /** @return the batchEndIndex */
    public int getBatchEndIndex() {
        return batchEndIndex;
    }
    /** @param batchEndIndex the batchEndIndex to set */
    public void setBatchEndIndex(int batchEndIndex) {
        this.batchEndIndex = batchEndIndex;
    }

    /** @return the batchLeadingZeros */
    public int getBatchLeadingZeros() {
        return batchLeadingZeros;
    }
    /** @param batchLeadingZeros the batchLeadingZeros to set */
    public void setBatchLeadingZeros(int batchLeadingZeros) {
        this.batchLeadingZeros = batchLeadingZeros;
    }

    /** The validator for the end index field
     * @param ctx
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void batchEndValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (batchStartIndex >= (Integer)value) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "End index must be greater than start index."));
        }
    }

    /** The validator for the start index field
     * @param ctx
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void batchStartValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if ((Integer)value >= batchEndIndex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Start index must be less than end index."));
        }
    }
}
