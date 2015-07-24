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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.dl.annotations.PropertiesLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.util.BatchIterator;
import org.openepics.discs.conf.util.BatchSaveStage;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Named
@ViewScoped
public class PropertyManager extends AbstractExcelSingleFileImportUI implements
                                    Serializable, SimpleTableExporter {
    private static final long serialVersionUID = 1056645993595744719L;
    private static final Logger LOGGER = Logger.getLogger(PropertyManager.class.getCanonicalName());
    private static final String CRLF = "\r\n";
    private static final List<String> UNIT_CAPABLE_DATA_TYPES =  Arrays.asList( new String[] {BuiltInDataType.INT_NAME,
                                        BuiltInDataType.DBL_NAME, BuiltInDataType.INT_VECTOR_NAME,
                                        BuiltInDataType.DBL_VECTOR_NAME, BuiltInDataType.DBL_TABLE_NAME});

    @Inject private transient PropertyEJB propertyEJB;

    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @PropertiesLoader private transient DataLoader propertiesDataLoader;

    private List<Property> properties;
    private List<Property> filteredProperties;
    private List<Property> selectedProperties;
    private List<Property> usedProperties;

    private String name;
    private String description;
    private DataType dataType;
    private Unit unit;
    private Property propertyToModify;
    private boolean unitComboEnabled;
    private boolean isPropertyUsed;
    private PropertyValueUniqueness valueUniqueness;

    // ---- batch property creation
    private boolean isBatchCreation;
    private int batchStartIndex;
    private int batchEndIndex;
    private int batchLeadingZeros;
    private String batchPropertyConflicts;
    private BatchSaveStage batchSaveStage;
    private boolean batchSkipExisting;

    private ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimplePropertyTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Properties";
        }

        @Override
        protected String getFileName() {
            return "properties";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description", "Unit", "Data Type");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<Property> exportData = filteredProperties == null || filteredProperties.isEmpty() ? properties
                    : filteredProperties;
            for (final Property prop : exportData) {
                final String unitName = prop.getUnit() != null ? prop.getUnit().getName() : null;
                exportTable.addDataRow(prop.getName(), prop.getDescription(), unitName, prop.getDataType().getName());
            }
        }
    }

    /** Creates a new instance of PropertyManager */
    public PropertyManager() {
    }

    /** Java EE post-construct life-cycle callback */
    @Override
    @PostConstruct
    public void init() {
        super.init();
        try {
            simpleTableExporterDialog = new ExportSimplePropertyTableDialog();
            properties = propertyEJB.findAllOrderedByName();
            selectedProperties = null;
            resetFields();
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    @Override
    public void setDataLoader() {
        dataLoader = propertiesDataLoader;
    }

    /** Called when the user presses the "Save" button in the "Add new property" dialog */
    public void onAdd() {
        if (isBatchCreation) {
            if (!multiPropertyAdd()) {
                return;
            }
            RequestContext.getCurrentInstance().execute("PF('addProperty').hide();");
        } else {
            singlePropertyAdd();
        }
        init();
    }

    private void singlePropertyAdd() {
        propertyEJB.add(createNewProperty(name));
        RequestContext.getCurrentInstance().execute("PF('addProperty').hide();");
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                "New property has been created");
    }

    private boolean multiPropertyAdd() {
        if (batchSaveStage == BatchSaveStage.VALIDATION) {
            batchPropertyConflicts = "";
            for (final BatchIterator bi = new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
                    bi.hasNext();) {
                final String propertyName = name.replace("{i}", bi.next());
                if (propertyEJB.findByName(propertyName) != null) {
                    batchPropertyConflicts += propertyName + CRLF;
                }
            }
            if (batchPropertyConflicts.isEmpty()) {
                batchSaveStage = BatchSaveStage.CREATION;
                batchSkipExisting = true;
            } else {
                RequestContext.getCurrentInstance().update("batchConflictForm");
                RequestContext.getCurrentInstance().execute("PF('batchConflict').show();");
                return false;
            }
        }

        // validation complete. Batch creation of all the properties.
        if (batchSaveStage == BatchSaveStage.CREATION) {
            if (!batchSkipExisting) {
                LOGGER.log(Level.SEVERE,
                    "Incorrect interal state: Batch property creation triggered with 'skip existing' set to false.");
                return false;
            }
            int propertiesCreated = 0;
            for (final BatchIterator bi = new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
                    bi.hasNext();) {
                final String propertyName = name.replace("{i}", bi.next());
                if (propertyEJB.findByName(propertyName) == null) {
                    final Property propertyToAdd = createNewProperty(propertyName);
                    propertyEJB.add(propertyToAdd);
                    propertiesCreated++;
                }
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Created " + propertiesCreated + " new properties.");
        }
        return true;
    }

    /** Called when the user confirms the batch property creation if there were some conflicts */
    public void creationProceed() {
        batchSaveStage = BatchSaveStage.CREATION;
        batchSkipExisting = true;
        multiPropertyAdd();
        init();
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
        Preconditions.checkNotNull(propertyToModify);
        propertyToModify.setName(name);
        propertyToModify.setDescription(description);
        propertyToModify.setDataType(dataType);
        propertyToModify.setUnit(UNIT_CAPABLE_DATA_TYPES.contains(dataType.getName()) ? unit : null);
        propertyToModify.setValueUniqueness(valueUniqueness);

        propertyEJB.save(propertyToModify);
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
        Preconditions.checkState(isSinglePropertySelected());

        propertyToModify = selectedProperties.get(0);
        name = propertyToModify.getName();
        description = propertyToModify.getDescription();
        dataType = propertyToModify.getDataType();
        unit = propertyToModify.getUnit();
        valueUniqueness = propertyToModify.getValueUniqueness();
        isPropertyUsed = propertyEJB.isPropertyUsed(propertyToModify);
        setIsUnitComboEnabled();
        RequestContext.getCurrentInstance().update("modifyPropertyForm:modifyProperty");
    }

    /** @return <code>true</code> if only a single property is selected in the table, <code>false</code> otherwise */
    public boolean isSinglePropertySelected() {
        return (selectedProperties != null) && (selectedProperties.size() == 1);
    }

    /**
     * The method builds a list of properties that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkPropertiesForDeletion() {
        Preconditions.checkNotNull(selectedProperties);
        Preconditions.checkState(!selectedProperties.isEmpty());

        usedProperties = Lists.newArrayList();
        for (final Property propToDelete : selectedProperties) {
            if (propertyEJB.isPropertyUsed(propToDelete)) {
                usedProperties.add(propToDelete);
            }
        }
    }

    /** Called when the user clicks the "trash can" icon in the UI */
    public void onDelete() {
        Preconditions.checkNotNull(selectedProperties);
        Preconditions.checkState(!selectedProperties.isEmpty());
        Preconditions.checkNotNull(usedProperties);
        Preconditions.checkState(usedProperties.isEmpty());

        int deletedProperties = 0;
        for (Property propertyToDelete : selectedProperties) {
            propertyEJB.delete(propertyToDelete);
            ++deletedProperties;
        }

        selectedProperties = null;
        usedProperties = null;
        init();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Deleted " + deletedProperties + " properties.");
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
            setLoaderResult(dataLoaderHandler.loadData(inputStream, propertiesDataLoader));
            init();
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
     */
    public void setIsUnitComboEnabled() {
        LOGGER.log(Level.FINE, "Selected data type: " + dataType.getName());
        unitComboEnabled = UNIT_CAPABLE_DATA_TYPES.contains(dataType.getName());
        if (!unitComboEnabled) {
            unit = null;
        }
    }

    /**
     * Used by the UI drop-down element.
     *
     * @return <code>true</code> if the combo box selection is enabled, <code>false</code> otherwise.
     */
    public boolean isUnitComboEnabled() {
        return unitComboEnabled;
    }

    /** @return The {@link List} of {@link Property} selected in the table */
    public List<Property> getSelectedProperties() {
        return selectedProperties;
    }
    /** @param selectedProperties The {@link List} of {@link Property} selected in the table */
    public void setSelectedProperties(List<Property> selectedProperties) {
        this.selectedProperties = selectedProperties;
    }

    /** @return the {@link List} of {@link Property} definitions that are used in some property value */
    public List<Property> getUsedProperties() {
        return usedProperties;
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
        batchSaveStage = BatchSaveStage.VALIDATION;
        batchSkipExisting = false;
        propertyToModify = null;
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
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
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
        if ((propertyToModify == null && existingProperty != null)
                || (propertyToModify != null &&  existingProperty != null
                        && !propertyToModify.equals(existingProperty))) {
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
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
     */
    public void batchEndValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (batchStartIndex >= (Integer)value) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "End index must be greater than start index."));
        }
    }

    /** The validator for the start index field
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
     */
    public void batchStartValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if ((Integer)value >= batchEndIndex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Start index must be less than end index."));
        }
    }

    /** @return a new line separated list of all properties in conflict */
    public String getBatchPropertyConflicts() {
        return batchPropertyConflicts;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }
}
