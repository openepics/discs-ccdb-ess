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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.PropertiesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.ExcelSingleFileImportUIHandlers;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

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
public class PropertyManager implements Serializable, ExcelSingleFileImportUIHandlers {
    @Inject transient private PropertyEJB propertyEJB;

    @Inject transient private DataLoaderHandler dataLoaderHandler;
    @Inject @PropertiesLoaderQualifier transient private DataLoader propertiesDataLoader;

    private List<Property> properties;
    private List<Property> filteredProperties;

    private byte[] importData;
    private String importFileName;
    transient private DataLoaderResult loaderResult;

    private String name;
    private String description;
    private DataType dataType;
    private Unit unit;
    private String[] association = new String[] {};
    private Property selectedProperty;
    private boolean unitComboEnabled;
    private boolean isPropertyUsed;

    /**
     * Creates a new instance of PropertyManager
     */
    public PropertyManager() {
    }

    private void init() {
        properties = propertyEJB.findAllOrderedByName();
        selectedProperty = null;
        resetFields();
    }

    /**
     * Called when the user presses the "Save" button in the "Add new property" dialog.
     */
    public void onAdd() {
        final Property propertyToAdd = new Property(name, description);
        setAssociationOnProperty(propertyToAdd);
        propertyToAdd.setDataType(dataType);
        propertyToAdd.setUnit(unit);
        try {
            propertyEJB.add(propertyToAdd);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New property has been created");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Property could not be added because a device instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            init();
        }
    }

    /**
     * Called when the user presses the "Save" button in the "Modify a property" dialog.
     */
    public void onModify() {
        selectedProperty.setName(name);
        selectedProperty.setDescription(description);
        selectedProperty.setDataType(dataType);
        setAssociationOnProperty(selectedProperty);
        selectedProperty.setUnit(unit);

        try {
            propertyEJB.save(selectedProperty);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Property was modified");
        } finally {
            init();
        }
    }

    /**
     * Prepares the data to be used in the "Add new property" dialog.
     */
    public void prepareAddPopup() {
        resetFields();
        isPropertyUsed = false;
        RequestContext.getCurrentInstance().update("addPropertyForm:addProperty");
    }

    /**
     * Prepares the data to be used in the "Modify a property" dialog.
     */
    public void prepareModifyPopup() {
        name = selectedProperty.getName();
        description = selectedProperty.getDescription();
        dataType = selectedProperty.getDataType();
        unit = selectedProperty.getUnit();
        association = constructSetAssociations(selectedProperty);
        isPropertyUsed = propertyEJB.isPropertyUsed(selectedProperty);
        setIsUnitComboEnabled();
        RequestContext.getCurrentInstance().update("modifyPropertyForm:modifyProperty");
    }

    /**
     * Called when the user clicks the "trash can" icon in the UI.
     */
    public void onDelete() {
        try {
            propertyEJB.delete(selectedProperty);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Property was deleted");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The property could not be deleted because it is used.");
            } else {
                throw e;
            }
        } finally {
            init();
        }
    }

    /**
     * @return The list of filtered properties used by the PrimeFaces filter field.
     */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    /**
     * @param filteredObjects The list of filtered properties used by the PrimeFaces filter field.
     */
    public void setFilteredProperties(List<Property> filteredObjects) {
        this.filteredProperties = filteredObjects;
    }

    /**
     * @return The list of all properties in the database ordered by name.
     */
    public List<Property> getProperties() {
        if (properties == null) {
            properties = propertyEJB.findAllOrderedByName();
        }
        return properties;
    }

    @Override
    public String getImportFileName() {
        return importFileName;
    }

    @Override
    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, propertiesDataLoader);
    }

    @Override
    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    @Override
    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The name of the property the user is working on. Used by UI.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name of the property the user is working on. Used by UI.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The description of the property the user is working on. Used by UI.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description of the property the user is working on. Used by UI.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The {@link DataType} of the property the user is working on. Used by UI.
     */
    public DataType getDataType() {
        return dataType;
    }
    /**
     * @param dataType The {@link DataType} of the property the user is working on. Used by UI.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return The {@link Unit} of the property the user is working on. Used by UI.
     */
    public Unit getUnit() {
        return unit;
    }
    /**
     * @param unit The {@link Unit} of the property the user is working on. Used by UI.
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * @see PropertyAssociation
     * @return The list of the possible property associations. Used by the PrimeFaces ManyCheckbox element.
     */
    public String[] getPropertyAssociation() {
        return association;
    }
    /**
     * @see PropertyAssociation
     * @param association The list of the possible property associations. Used by the PrimeFaces ManyCheckbox element.
     */
    public void setPropertyAssociation(String[] association) {
        this.association = association;
    }

    private String[] constructSetAssociations(Property property) {
        ArrayList<String> associations = new ArrayList<>();
        if (property.isTypeAssociation()) {
            associations.add(PropertyAssociation.TYPE);
        }
        if (property.isSlotAssociation()) {
            associations.add(PropertyAssociation.SLOT);
        }
        if (property.isDeviceAssociation()) {
            associations.add(PropertyAssociation.DEVICE);
        }
        if (property.isAlignmentAssociation()) {
            associations.add(PropertyAssociation.ALIGNMENT);
        }
        return associations.toArray(new String[] {});
    }

    private void setAssociationOnProperty(Property property) {
        // reset all data
        property.setNoneAssociation();

        if (association == null || association.length == 0) {
            throw new RuntimeException("Property association target not selected.");
        }

        for (String assoc : association) {
            switch (assoc) {
                case PropertyAssociation.ALIGNMENT:
                    property.setAlignmentAssociation(true);
                    break;
                case PropertyAssociation.DEVICE:
                    property.setDeviceAssociation(true);
                    break;
                case PropertyAssociation.SLOT:
                    property.setSlotAssociation(true);
                    break;
                case PropertyAssociation.TYPE:
                    property.setTypeAssociation(true);
                    break;
                default:
                    throw new RuntimeException("Unknow property associaton target.");
            }
        }
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

    /**
     * @return The {@link Property} selected in the dialog.
     */
    public Property getSelectedProperty() {
        return selectedProperty;
    }
    /**
     * @param selectedProperty The {@link Property} selected in the dialog.
     */
    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    /**
     * @return The {@link Property} selected in the dialog (modify property dialog).
     */
    public Property getSelectedPropertyToModify() {
        return selectedProperty;
    }
    /**
     * @param selectedProperty The {@link Property} selected in the dialog (modify property dialog).
     */
    public void setSelectedPropertyToModify(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
        prepareModifyPopup();
    }

    private void resetFields() {
        name = null;
        description = null;
        dataType = null;
        unit = null;
        association = null;
        unitComboEnabled = true;
    }

    /**
     * @return <code>true</code> if the property is already used in some {@link PropertyValue}, <code>false</code> otherwise.
     */
    public boolean isPropertyUsed() {
        return isPropertyUsed;
    }
}
