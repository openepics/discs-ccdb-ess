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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.PostActivate;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.dl.annotations.ComponentTypesLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.ExcelSingleFileImportUIHandlers;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.ImportFileStatistics;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.ComponentTypeView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Controller bean for manipulation of {@link ComponentType} attributes
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 */
@Named
@ViewScoped
public class ComponentTypeManager implements SimpleTableExporter, ExcelSingleFileImportUIHandlers, Serializable {
    private static final long serialVersionUID = 1156974438243970794L;

    private static final Logger LOGGER = Logger.getLogger(ComponentTypeManager.class.getCanonicalName());

    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private ComptypeAttributesController comptypeAttributesController;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @ComponentTypesLoader private DataLoader compTypesDataLoader;

    private ExcelSingleFileImportUI excelSingleFileImportUI;

    private ComponentTypeView selectedComponent;

    private List<ComponentTypeView> deviceTypes;
    private List<ComponentTypeView> filteredDeviceTypes;
    private List<ComponentTypeView> selectedDeviceTypes;
    private List<ComponentTypeView> usedDeviceTypes;
    private List<ComponentTypeView> filteredDialogTypes;

    private transient ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExcelSingleFileImportUI extends AbstractExcelSingleFileImportUI {
        /** Construct the file import UI for the device type data loader. */
        public ExcelSingleFileImportUI() {
            super.init();
        }

        @Override
        public void setDataLoader() {
            dataLoader = compTypesDataLoader;
        }

        @Override
        public void doImport() {
            try (InputStream inputStream = new ByteArrayInputStream(importData)) {
                setLoaderResult(dataLoaderHandler.loadData(inputStream, compTypesDataLoader));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ExportSimpleDevTypeTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Device types";
        }

        @Override
        protected String getFileName() {
            return "ccdb_device_types";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<ComponentTypeView> exportData = Utility.isNullOrEmpty(filteredDeviceTypes)
                                                            ? deviceTypes
                                                            : filteredDeviceTypes;
            for (final ComponentTypeView devType : exportData) {
                exportTable.addDataRow(devType.getName(), devType.getDescription());
            }
        }
    }

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        final String deviceTypeIdStr = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                getRequest()).getParameter("id");
        try {
            excelSingleFileImportUI = new ExcelSingleFileImportUI();
            simpleTableExporterDialog = new ExportSimpleDevTypeTableDialog();
            comptypeAttributesController.setUIParent(this);
            reloadDeviceTypes();
            resetFields();

            if (!Strings.isNullOrEmpty(deviceTypeIdStr)) {
                final long deviceTypeId = Long.parseLong(deviceTypeIdStr);
                int elementPosition = 0;
                for (final ComponentTypeView deviceType : deviceTypes) {
                    if (deviceType.getId() == deviceTypeId) {
                        RequestContext.getCurrentInstance().execute("selectEntityInTable(" + elementPosition
                                + ", 'deviceTypeTableVar');");
                        return;
                    }
                    ++elementPosition;
                }
            }
        } catch (NumberFormatException e) {
            // just log
            LOGGER.log(Level.WARNING, "URL contained strange device type ID: " + deviceTypeIdStr);
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    @PostActivate
    public void postActivate() {
        comptypeAttributesController.setUIParent(this);
    }

    /** @see org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#doImport() */
    @Override
    public void doImport() {
        excelSingleFileImportUI.doImport();
        clearDeviceTypeRelatedInformation();
        reloadDeviceTypes();
    }

    /** @see org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#prepareImportPopup() */
    @Override
    public void prepareImportPopup() {
        excelSingleFileImportUI.prepareImportPopup();
    }

    /** @see org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#setDataLoader() */
    @Override
    public void setDataLoader() {
        excelSingleFileImportUI.setDataLoader();
    }

    /** org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#getLoaderResult() */
    @Override
    public DataLoaderResult getLoaderResult() {
        return excelSingleFileImportUI.getLoaderResult();
    }

    /**
     * @see org.openepics.discs.conf.ui.common.ExcelSingleFileImportUIHandlers#handleImportFileUpload(FileUploadEvent)
     */
    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        excelSingleFileImportUI.handleImportFileUpload(event);
    }

    /** @see org.openepics.discs.conf.ui.common.ExcelSingleFileImportUIHandlers#getExcelImportFileName() */
    @Override
    public String getExcelImportFileName() {
        return excelSingleFileImportUI.getExcelImportFileName();
    }

    /** @see org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#getImportedFileStatistics() */
    @Override
    public ImportFileStatistics getImportedFileStatistics() {
        return excelSingleFileImportUI.getImportedFileStatistics();
    }

    /**
     * @return the import statistics for the imported file
     * @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getImportFileStatistics() */
    public ImportFileStatistics getImportFileStatistics() {
        return excelSingleFileImportUI.getImportFileStatistics();
    }

    /**
     * @return the dialog containing a simple error message
     * @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getSimpleErrorTableExportDialog() */
    public ExportSimpleTableDialog getSimpleErrorTableExportDialog() {
        return excelSingleFileImportUI.getSimpleErrorTableExportDialog();
    }

    private void reloadDeviceTypes() {
        deviceTypes = comptypeEJB.findAll().stream().map(ComponentTypeView::new).collect(Collectors.toList());
    }

    /** Called when user selects a row */
    public void onRowSelect() {
        if (selectedDeviceTypes != null && !selectedDeviceTypes.isEmpty()) {
            // selectedDeviceTypes = getFreshTypes(deviceTypes);
            if (selectedDeviceTypes.size() == 1) {
                selectedComponent = selectedDeviceTypes.get(0);
            } else {
                selectedComponent = null;
            }
            comptypeAttributesController.clearRelatedAttributeInformation();
            comptypeAttributesController.populateAttributesList();
        } else {
            clearDeviceTypeRelatedInformation();
        }
    }

    /**
     * This method duplicates selected device types. This method actually copies
     * selected device type name, description, tags, artifacts and properties
     * into new device type. If property has set universally unique value,
     * copied property value is set to null.
     */
    public void duplicate() {
        try {
            Preconditions.checkState(!Utility.isNullOrEmpty(selectedDeviceTypes));

            final int duplicated = comptypeEJB.duplicate(selectedDeviceTypes.stream().
                                    map(ComponentTypeView::getComponentType).collect(Collectors.toList()));
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Duplicated " + duplicated + " device types.");
        } finally {
            clearDeviceTypeRelatedInformation();
            reloadDeviceTypes();
        }
    }

    private void clearDeviceTypeRelatedInformation() {
        selectedDeviceTypes = null;
        filteredDeviceTypes = null;
        selectedComponent = null;
        comptypeAttributesController.clearRelatedAttributeInformation();
        resetFields();
    }

    /** Prepares the UI data for the "Add a new device type" dialog. */
    public void prepareAddPopup() {
        comptypeAttributesController.resetFields();
        selectedComponent = new ComponentTypeView();
        RequestContext.getCurrentInstance().update("addDeviceTypeForm:addDeviceType");
    }

    /** This method resets the dialog fields related to the selected device type */
    public void resetFields() {
        if (selectedDeviceTypes != null && selectedDeviceTypes.size() == 1) {
            selectedComponent = selectedDeviceTypes.get(0);
        } else {
            selectedComponent = null;
        }
        comptypeAttributesController.resetFields();
    }

    /** Called when the user presses the "Save" button in the "Add a new device type" dialog. */
    public void onAdd() {
        try {
            comptypeEJB.add(selectedComponent.getComponentType());
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "New device type has been created");
        } catch (Exception e) {
            if (UiUtility.causedByPersistenceException(e)) {
                UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                        "Device type could not be added because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            clearDeviceTypeRelatedInformation();
            reloadDeviceTypes();
        }
    }

    /** Prepares the data for the device type editing dialog fields based on the selected device type. */
    public void prepareEditPopup() {
        Preconditions.checkState(isSingleDeviceTypeSelected());
    }

    /** Saves the new device type data (name and/or description) */
    public void onChange() {
        Preconditions.checkNotNull(selectedComponent);
        try {
            comptypeEJB.save(selectedComponent.getComponentType());
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Device type updated");
        } catch (Exception e) {
            if (UiUtility.causedByPersistenceException(e)) {
                UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                        "Device type could not be modified because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            refreshSelectedComponent();
            reloadDeviceTypes();
        }
    }

    /**
     * The method builds a list of device types that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkDeviceTypesForDeletion() {
        Preconditions.checkNotNull(selectedDeviceTypes);
        Preconditions.checkState(!selectedDeviceTypes.isEmpty());

        usedDeviceTypes = Lists.newArrayList();
        for (final ComponentTypeView deviceTypeToDelete : selectedDeviceTypes) {
            List<String> usedBy = comptypeEJB.findWhereIsComponentTypeUsed(deviceTypeToDelete.getComponentType(), 2);
            if (!usedBy.isEmpty()) {
                deviceTypeToDelete.setUsedBy(usedBy.get(0) + (usedBy.size() > 1 ? ", ..." : ""));
                usedDeviceTypes.add(deviceTypeToDelete);
            }
        }
    }

    /** Called when the user presses the "Delete" button under table listing the devices types. */
    public void onDelete() {
        try {
            Preconditions.checkNotNull(selectedDeviceTypes);
            Preconditions.checkState(!selectedDeviceTypes.isEmpty());
            Preconditions.checkNotNull(usedDeviceTypes);
            Preconditions.checkState(usedDeviceTypes.isEmpty());

            int deletedDeviceTypes = 0;
            for (final ComponentTypeView deviceTypeToDelete : selectedDeviceTypes) {
                final ComponentType freshEntity = comptypeEJB.findById(deviceTypeToDelete.getId());
                freshEntity.getTags().clear();
                comptypeEJB.delete(freshEntity);
                ++deletedDeviceTypes;
            }
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Deleted " + deletedDeviceTypes + " device types.");
        } finally {
            clearDeviceTypeRelatedInformation();
            reloadDeviceTypes();
        }
    }

    /** @return <code>true</code> if a single device type is selected , <code>false</code> otherwise */
    public boolean isSingleDeviceTypeSelected() {
        return (selectedDeviceTypes != null) && (selectedDeviceTypes.size() == 1);
    }

    /** Pulls a fresh ComponentType entity from the database for the <code>selectedComponent</code>. */
    protected void refreshSelectedComponent() {
        selectedComponent.setComponentType(comptypeEJB.findById(selectedComponent.getComponentType().getId()));
    }

    // -------------------- Getters and Setters ---------------------------------------

    /** @return the selectedDeviceTypes */
    public List<ComponentTypeView> getSelectedDeviceTypes() {
        return selectedDeviceTypes;
    }
    /** @param selectedDeviceTypes the selectedDeviceTypes to set */
    public void setSelectedDeviceTypes(List<ComponentTypeView> selectedDeviceTypes) {
        this.selectedDeviceTypes = selectedDeviceTypes;
    }

    /** @return the {@link List} of used device types */
    public List<ComponentTypeView> getUsedDeviceTypes() {
        return usedDeviceTypes;
    }

    /** @return The list of filtered device types used by the PrimeFaces filter field. */
    public List<ComponentTypeView> getFilteredDeviceTypes() {
        return filteredDeviceTypes;
    }
    /** @param filteredDeviceTypes The list of filtered device types used by the PrimeFaces filter field. */
    public void setFilteredDeviceTypes(List<ComponentTypeView> filteredDeviceTypes) {
        this.filteredDeviceTypes = filteredDeviceTypes;
    }

    /** @return The list of all device types in the database. */
    public List<ComponentTypeView> getDeviceTypes() {
        return deviceTypes;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    /** @return the filteredDialogTypes */
    public List<ComponentTypeView> getFilteredDialogTypes() {
        return filteredDialogTypes;
    }

    /** @param filteredDialogTypes the filteredDialogTypes to set */
    public void setFilteredDialogTypes(List<ComponentTypeView> filteredDialogTypes) {
        this.filteredDialogTypes = filteredDialogTypes;
    }

    /** @return the selectedComponent */
    public ComponentTypeView getSelectedComponent() {
        return selectedComponent;
    }
}
