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
import java.util.List;
import java.util.stream.Collectors;

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
import org.openepics.discs.conf.ent.EntityWithProperties;
import org.openepics.discs.conf.ent.NamedEntity;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.BatchSaveStage;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.NewPropertyView;
import org.openepics.discs.conf.views.PropertyView;
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
    private static final String CRLF = "\r\n";

    @Inject private transient PropertyEJB propertyEJB;

    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @PropertiesLoader private transient DataLoader propertiesDataLoader;

    private List<PropertyView> properties;
    private List<PropertyView> filteredProperties;
    private List<PropertyView> selectedProperties;
    private List<PropertyView> usedProperties;
    private List<PropertyView> filteredDialogProperties;

    private PropertyView dialogProperty;

    private transient ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimplePropertyTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Properties";
        }

        @Override
        protected String getFileName() {
            return "ccdb_properties";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description", "Unit", "Data Type");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<PropertyView> exportData = filteredProperties;
            for (final PropertyView prop : exportData) {
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
            properties = propertyEJB.findAllOrderedByName().stream().map(PropertyView::new).collect(Collectors.toList());
            selectedProperties = null;
            filteredProperties = properties;
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
        NewPropertyView propView = (NewPropertyView)dialogProperty;
        if (propView.isBatchCreation()) {
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
        propertyEJB.add(dialogProperty.getProperty());
        RequestContext.getCurrentInstance().execute("PF('addProperty').hide();");
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "New property has been created");
    }

    private boolean multiPropertyAdd() {
        final NewPropertyView propView = (NewPropertyView)dialogProperty;
        final Property prop = dialogProperty.getProperty();

        if (propView.getBatchSaveStage().equals(BatchSaveStage.VALIDATION)) {
            String batchPropertyConflicts = "";
            for (String batchNumber : propView) {
                final String propertyName = prop.getName().replace("{i}", batchNumber);
                if (propertyEJB.findByName(propertyName) != null) {
                    batchPropertyConflicts += propertyName + CRLF;
                }
            }
            if (batchPropertyConflicts.isEmpty()) {
                propView.setBatchSaveStage(BatchSaveStage.CREATION);
            } else {
                propView.setBatchPropertyConflicts(batchPropertyConflicts);
                RequestContext.getCurrentInstance().update("batchConflictForm");
                RequestContext.getCurrentInstance().execute("PF('batchConflict').show();");
                return false;
            }
        }

        // validation complete. Batch creation of all the properties.
        if (propView.getBatchSaveStage().equals(BatchSaveStage.CREATION)) {
            int propertiesCreated = 0;

            for (final String batchNumber : propView) {
                final String propertyName = prop.getName().replace("{i}", batchNumber);
                if (propertyEJB.findByName(propertyName) == null) {
                    final Property propertyToAdd = new Property(prop);
                    propertyToAdd.setName(propertyName);
                    propertyToAdd.setDescription(propertyToAdd.getDescription().replace("{i}", batchNumber));
                    propertyEJB.add(propertyToAdd);
                    propertiesCreated++;
                }
            }
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Created " + propertiesCreated + " new properties.");
        }
        return true;
    }

    /** Called when the user confirms the batch property creation if there were some conflicts */
    public void creationProceed() {
        try {
            ((NewPropertyView)dialogProperty).setBatchSaveStage(BatchSaveStage.CREATION);
            multiPropertyAdd();
        } finally {
            init();
        }
    }

    /** Called when the user presses the "Save" button in the "Modify a property" dialog */
    public void onModify() {
        try {
            Preconditions.checkNotNull(dialogProperty);
            propertyEJB.save(dialogProperty.getProperty());
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Property was modified");
        } finally {
            init();
        }
    }

    /** Prepares the data to be used in the "Add new property" dialog */
    public void prepareAddPopup() {
        resetFields();
        dialogProperty = new NewPropertyView();
        RequestContext.getCurrentInstance().update("addPropertyForm:addProperty");
    }

    /** Prepares the data to be used in the "Modify a property" dialog */
    public void prepareModifyPopup() {
        Preconditions.checkState(isSinglePropertySelected());
        dialogProperty = selectedProperties.get(0);
        initUsedBy(dialogProperty);
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
        for (final PropertyView propToDelete : selectedProperties) {
            if (initUsedBy(propToDelete)) {
                usedProperties.add(propToDelete);
            }
        }
    }

    private boolean initUsedBy(PropertyView prop) {
        List<? extends PropertyValue> propertyValues = propertyEJB.findPropertyValues(prop.getProperty(), 2);
        if (propertyValues.isEmpty()) {
            prop.setUsedBy(null);
            return false;
        }
        EntityWithProperties parent = propertyValues.get(0).getPropertiesParent();
        prop.setUsedBy((parent instanceof NamedEntity ? ((NamedEntity)parent).getName() : "other")+(propertyValues.size()>1 ? ", ..." : ""));
        return true;
    }

    /** Called when the user clicks the "trash can" icon in the UI */
    public void onDelete() {
        try {
            Preconditions.checkNotNull(selectedProperties);
            Preconditions.checkState(!selectedProperties.isEmpty());
            Preconditions.checkNotNull(usedProperties);
            Preconditions.checkState(usedProperties.isEmpty());

            int deletedProperties = 0;
            for (PropertyView propertyToDelete : selectedProperties) {
                propertyEJB.delete(propertyToDelete.getProperty());
                ++deletedProperties;
            }

            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Deleted " + deletedProperties + " properties.");
        } finally {
            selectedProperties = null;
            usedProperties = null;

            init();
        }
    }

    /** @return The list of filtered properties used by the PrimeFaces filter field */
    public List<PropertyView> getFilteredProperties() {
        return filteredProperties;
    }

    /** @param filteredObjects The list of filtered properties used by the PrimeFaces filter field */
    public void setFilteredProperties(List<PropertyView> filteredObjects) {
        this.filteredProperties = filteredObjects;
    }

    /** @return The list of all properties in the database ordered by name */
    public List<PropertyView> getProperties() {
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

    /**
     * The method creates a new copy of the currently selected {@link Property}(ies)
     */
    public void duplicate() {
        try {
            Preconditions.checkState(!Utility.isNullOrEmpty(selectedProperties));
            final int duplicated = propertyEJB.duplicate(selectedProperties.stream().map(PropertyView::getProperty).
                                            collect(Collectors.toList()));
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Duplicated " + duplicated + " properties.");
        } finally {
            init();
        }
    }




    /** @return The {@link List} of {@link Property} selected in the table */
    public List<PropertyView> getSelectedProperties() {
        return selectedProperties;
    }
    /** @param selectedProperties The {@link List} of {@link Property} selected in the table */
    public void setSelectedProperties(List<PropertyView> selectedProperties) {
        this.selectedProperties = selectedProperties;
    }

    /** @return the {@link List} of {@link Property} definitions that are used in some property value */
    public List<PropertyView> getUsedProperties() {
        return usedProperties;
    }

    public void resetFields() {
        dialogProperty = null;
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                                                    "Please enter a name"));
        }

        final String propertyName = value.toString();

        dialogProperty.nameValidator(propertyName);

        if (dialogProperty.isBeingAdded() || !dialogProperty.getName().equals(propertyName)) {
            if (propertyEJB.findByName(propertyName) != null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                    UiUtility.MESSAGE_SUMMARY_ERROR,
                                                                    "The property with this name already exists."));
            }
        }
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    /** @return the filteredDialogProperties */
    public List<PropertyView> getFilteredDialogProperties() {
        return filteredDialogProperties;
    }

    /** @param filteredDialogProperties the filteredDialogProperties to set */
    public void setFilteredDialogProperties(List<PropertyView> filteredDialogProperties) {
        this.filteredDialogProperties = filteredDialogProperties;
    }

    /**
     * @return the dialogProperty
     */
    public PropertyView getDialogProperty() {
        return dialogProperty;
    }
}
