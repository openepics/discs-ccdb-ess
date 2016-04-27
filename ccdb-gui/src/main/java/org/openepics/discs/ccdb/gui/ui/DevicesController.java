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
package org.openepics.discs.ccdb.gui.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.ccdb.core.dl.annotations.DevicesLoader;
import org.openepics.discs.ccdb.core.dl.common.DataLoader;
import org.openepics.discs.ccdb.core.dl.common.DataLoaderResult;
import org.openepics.discs.ccdb.core.ejb.ComptypeEJB;
import org.openepics.discs.ccdb.core.ejb.DeviceEJB;
import org.openepics.discs.ccdb.core.ejb.InstallationEJB;
import org.openepics.discs.ccdb.model.ComponentType;
import org.openepics.discs.ccdb.model.Device;
import org.openepics.discs.ccdb.model.DevicePropertyValue;
import org.openepics.discs.ccdb.model.InstallationRecord;
import org.openepics.discs.ccdb.gui.export.ExportTable;
import org.openepics.discs.ccdb.gui.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.ccdb.gui.ui.common.DataLoaderHandler;
import org.openepics.discs.ccdb.gui.ui.common.ExcelSingleFileImportUIHandlers;
import org.openepics.discs.ccdb.gui.ui.common.UIException;
import org.openepics.discs.ccdb.gui.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.ccdb.gui.ui.export.SimpleTableExporter;
import org.openepics.discs.ccdb.gui.ui.util.UiUtility;
import org.openepics.discs.ccdb.core.util.BatchIterator;
import org.openepics.discs.ccdb.core.util.BatchSaveStage;
import org.openepics.discs.ccdb.core.util.ImportFileStatistics;
import org.openepics.discs.ccdb.core.util.Utility;
import org.openepics.discs.ccdb.gui.views.DeviceView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Controller bean for manipulation of {@link Device} attributes
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Named
@ViewScoped
public class DevicesController implements SimpleTableExporter, ExcelSingleFileImportUIHandlers, Serializable {
    private static final long serialVersionUID = -2881746639197321061L;

    private static final Logger LOGGER = Logger.getLogger(DevicesController.class.getCanonicalName());
    private static final String CRLF = "\r\n";

    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @DevicesLoader private DataLoader devicesDataLoader;
    @Inject private ComptypeEJB componentTypesEJB;
    @Inject private InstallationEJB installationEJB;
    @Inject private DeviceEJB deviceEJB;
    @Inject private DeviceAttributesController deviceAttributesController;

    private Device device;

    private List<DeviceView> devices;
    private List<DeviceView> filteredDevices;
    private List<DeviceView> selectedDevices;
    private List<DeviceView> usedDevices;
    private List<DeviceView> filteredDialogDevices;

    // Add/Edit device dialog
    private ComponentType selectedComponentType;
    private List<ComponentType> availableDeviceTypes;
    private String serialNumber;
    private boolean isDeviceBeingEdited;

    //---- batch device creation
    private boolean isBatchCreation;
    private int batchStartIndex;
    private int batchEndIndex;
    private int batchLeadingZeros;
    private String batchSerialConflicts;
    private BatchSaveStage batchSaveStage;
    private boolean batchSkipExisting;
    private int selectedIndex = -1;

    private transient ExportSimpleTableDialog simpleTableExporterDialog;

    private transient ExcelSingleFileImportUI excelSingleFileImportUI;

    private class ExcelSingleFileImportUI extends AbstractExcelSingleFileImportUI {
        /** Construct the file import UI for the device data loader. */
        public ExcelSingleFileImportUI() {
            super.init();
        }

        @Override
        public void setDataLoader() {
            dataLoader = devicesDataLoader;
        }

        @Override
        public void doImport() {
            try (InputStream inputStream = new ByteArrayInputStream(importData)) {
                setLoaderResult(dataLoaderHandler.loadData(inputStream, devicesDataLoader));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ExportSimpleDeviceTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Devices";
        }

        @Override
        protected String getFileName() {
            return "ccdb_devices";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Operation", "Type", "Inventory ID", "Property Name", "Property Value",
                    "Installed In", "Installation Timestamp");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<DeviceView> exportData = Utility.isNullOrEmpty(filteredDevices) ? devices
                    : filteredDevices;
            for (final DeviceView deviceInstance : exportData) {
                exportTable.addDataRow(DataLoader.CMD_UPDATE_DEVICE,
                        deviceInstance.getDevice().getComponentType().getName(), deviceInstance.getInventoryId(),
                        null, null,
                        "-".equals(deviceInstance.getInstalledIn()) ? null : deviceInstance.getInstalledIn(),
                        deviceInstance.getInstallationTimestamp());
                for (final DevicePropertyValue pv : deviceInstance.getDevice().getDevicePropertyList()) {
                    exportTable.addDataRow(DataLoader.CMD_UPDATE_PROPERTY, null, deviceInstance.getInventoryId(),
                            pv.getProperty().getName(), pv.getPropValue());
                }
            }
        }

        @Override
        protected String getExcelTemplatePath() {
            return "/resources/templates/ccdb_devices.xlsx";
        }

        @Override
        protected int getExcelDataStartRow() {
            return 10;
        }
    }

    public DevicesController() {}

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        try {
            final String deviceId = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                    getRequest()).getParameter("id");
            excelSingleFileImportUI = new ExcelSingleFileImportUI();
            simpleTableExporterDialog = new ExportSimpleDeviceTableDialog();
            availableDeviceTypes = componentTypesEJB.findAll();

            Long selectedDeviceId = null;
            if (!Strings.isNullOrEmpty(deviceId)) {
                try {
                    selectedDeviceId = Long.valueOf(deviceId);
                } catch (NumberFormatException e) {
                    // just log
                    LOGGER.log(Level.WARNING, "URL contained strange unit ID: " + deviceId );
                    selectedDeviceId = null;
                }
            }

            prepareDevicesForDisplay(selectedDeviceId);
            if (selectedIndex > -1) {
                RequestContext.getCurrentInstance().execute("selectEntityInTable(" + selectedIndex
                        + ", 'devicesTableVar');");
            }
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    /** @see org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#doImport() */
    @Override
    public void doImport() {
        excelSingleFileImportUI.doImport();
        deviceAttributesController.clearRelatedAttributeInformation();
        prepareDevicesForDisplay(null);
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

    /** @see org.openepics.discs.conf.ui.common.ExcelImportUIHandlers#getLoaderResult() */
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

    // --------------------------------------------------------------------------------------------------
    //
    //    Old DevicesByTypeManager methods
    //
    // --------------------------------------------------------------------------------------------------

    /** Called when user selects a row */
    public void onRowSelect() {
        if (selectedDevices != null && !selectedDevices.isEmpty()) {
            // selectedDeviceTypes = getFreshTypes(deviceTypes);
            if (selectedDevices.size() == 1) {
                device = deviceEJB.findById(selectedDevices.get(0).getDevice().getId());
            } else {
                device = null;
            }
            deviceAttributesController.clearRelatedAttributeInformation();
            deviceAttributesController.populateAttributesList();
        } else {
            clearDeviceDialogFields();
            device = null;
            selectedComponentType = null;
            availableDeviceTypes = null;
            deviceAttributesController.clearRelatedAttributeInformation();
        }
     }

    /** Creates a new device instance and adds all properties to it which are defined by device type */
    public void onDeviceAdd() {
        if (isBatchCreation) {
            if (!multiDeviceAdd()) {
                return;
            }
        } else {
            singleDeviceAdd();
        }

        clearDeviceDialogFields();
        prepareDevicesForDisplay(null);
        deviceAttributesController.clearRelatedAttributeInformation();
    }

    private void singleDeviceAdd() {
        deviceEJB.addDeviceAndPropertyDefs(createNewDevice(serialNumber));
        RequestContext.getCurrentInstance().execute("PF('addDeviceDialog').hide();");
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, "Device saved.", null);
    }

    /** @return <code>true</code> creation successful, <code>false</code> means error */
    private boolean multiDeviceAdd() {
        if (batchSaveStage == BatchSaveStage.VALIDATION) {
            batchSerialConflicts = "";
            for (final BatchIterator bi = new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
                    bi.hasNext();) {
                final String deviceSerialNo = serialNumber.replace("{i}", bi.next());
                if (deviceEJB.findByName(deviceSerialNo) != null) {
                    batchSerialConflicts += deviceSerialNo + CRLF;
                }
            }
            if (batchSerialConflicts.isEmpty()) {
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
                        "Incorrect interal state: Batch device creation triggered with 'skip existing' set to false.");
                return false;
            }
            int devicesCreated = 0;
            for (final BatchIterator bi = new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
                    bi.hasNext();) {
                final String deviceSerialNo = serialNumber.replace("{i}", bi.next());
                if (deviceEJB.findByName(deviceSerialNo) == null) {
                    deviceEJB.addDeviceAndPropertyDefs(createNewDevice(deviceSerialNo));
                    devicesCreated++;
                }
            }
            RequestContext.getCurrentInstance().execute("PF('devicesTableVar').filter();");
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Created " + devicesCreated + " new devices.");
        }
        return true;
    }

    /** Called when the user confirms the batch property creation if there were some conflicts */
    public void creationProceed() {
        batchSaveStage = BatchSaveStage.CREATION;
        batchSkipExisting = true;
        multiDeviceAdd();
        clearDeviceDialogFields();
        prepareDevicesForDisplay(null);
    }

    private Device createNewDevice(String deviceSerailNo) {
        final Device newDevice = new Device(deviceSerailNo);
        newDevice.setComponentType(selectedComponentType);
        return newDevice;
    }

    /** Updates an existing device with new information from the dialog */
    public void onDeviceEdit() {
        Preconditions.checkState(isSingleDeviceSelected());
        device = deviceEJB.findById(selectedDevices.get(0).getDevice().getId());
        device.setSerialNumber(serialNumber);
        final boolean deviceTypeChange = !device.getComponentType().equals(selectedComponentType);
        if (deviceTypeChange) {
            device = deviceEJB.changeDeviceType(device, selectedComponentType);
        } else {
            deviceEJB.save(device);
        }

        selectedDevices.get(0).refreshDevice(deviceEJB.findById(device.getId()));
        if (deviceTypeChange) {
            deviceAttributesController.clearRelatedAttributeInformation();
            deviceAttributesController.populateAttributesList();
        }
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS, "Device updated.");
    }

    /**
     * The method builds a list of device types that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkDevicesForDeletion() {
        Preconditions.checkNotNull(selectedDevices);
        Preconditions.checkState(!selectedDevices.isEmpty());

        usedDevices = Lists.newArrayList();
        for (final DeviceView deviceToDelete : selectedDevices) {
            if (deviceToDelete.getInstallationTimestamp() != null) {
                usedDevices.add(deviceToDelete);
            }
        }
    }

    /** Event handler which handles the device delete */
    public void onDeviceDelete() {
        Preconditions.checkNotNull(selectedDevices);
        Preconditions.checkState(!selectedDevices.isEmpty());
        Preconditions.checkNotNull(usedDevices);
        Preconditions.checkState(usedDevices.isEmpty());

        int deletedDevices = 0;
        for (final DeviceView deviceToDelete : selectedDevices) {
            final Device deleteDevice = deviceEJB.findById(deviceToDelete.getDevice().getId());
            deviceEJB.delete(deleteDevice);
            ++deletedDevices;
        }

        prepareDevicesForDisplay(null);
        filteredDevices = null;
        selectedDevices = null;
        usedDevices = null;
        deviceAttributesController.clearRelatedAttributeInformation();
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                            "Deleted " + deletedDevices + " devices.");
    }

    /** Clears the data displayed in the "Add device instance" dialog */
    public void clearDeviceDialogFields() {
        serialNumber = null;
        selectedComponentType = null;
        isDeviceBeingEdited = false;
        isBatchCreation = false;
        batchStartIndex = 0;
        batchEndIndex = 0;
        batchLeadingZeros = 0;
        batchSaveStage = BatchSaveStage.VALIDATION;
        batchSkipExisting = false;
    }

    /** Prepares the data displayed in the "Edit device instance" dialog */
    public void prepareEditPopup() {
        Preconditions.checkState(isSingleDeviceSelected());
        serialNumber = device.getSerialNumber();
        selectedComponentType = device.getComponentType();
        isDeviceBeingEdited = true;
        isBatchCreation = false;
        batchStartIndex = 0;
        batchEndIndex = 0;
        batchLeadingZeros = 0;
        batchSaveStage = BatchSaveStage.VALIDATION;
        batchSkipExisting = false;
    }

    /** @return The ID of device type of the device the user is adding or editing in device manager */
    public Long getDeviceTypeSelection() {
        return selectedComponentType == null ? null : selectedComponentType.getId();
    }

    /** @param deviceTypeId The ID of the device type of device the user is adding or editing in device manager */
    public void setDeviceTypeSelection(Long deviceTypeId) {
        if (deviceTypeId == null) {
            selectedComponentType = null;
        } else {
            selectedComponentType = componentTypesEJB.findById(deviceTypeId);
        }
    }

    /** @return <code>true</code> if a single device type is selected , <code>false</code> otherwise */
    public boolean isSingleDeviceSelected() {
        return (selectedDevices != null) && (selectedDevices.size() == 1);
    }

    /** The method prepares the list of all {@link Device} instances in the database to show to the user. */
    private void prepareDevicesForDisplay(final Long deviceToSelect) {
        if (selectedDevices == null) {
            selectedDevices = Lists.newArrayList();
        }
        selectedDevices.clear();
        isDeviceBeingEdited = false;
        final List<Device> deviceList = deviceEJB.findAll();

        int devTableRowCounter = 0;
        devices = Lists.newArrayList();
        // transform the list of Device into an immutable list of DeviceView
        for (final Device dev : deviceList) {
            final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForDevice(dev);
            final String installationSlot = installationRecord == null ? "-": installationRecord.getSlot().getName();
            final String installationSlotId = installationRecord == null ? null
                                                    : Long.toString(installationRecord.getSlot().getId());
            final Date installationDate = installationRecord == null ? null: installationRecord.getInstallDate();
            final DeviceView devView = new DeviceView(dev, installationSlot, installationSlotId, installationDate);
            if (deviceToSelect != null && selectedDevices.isEmpty() && dev.getId().equals(deviceToSelect)) {
                selectedIndex = devTableRowCounter;
                selectedDevices.add(devView);
            }
            devTableRowCounter++;
            devices.add(devView);
        }
    }

    /**
     * The method creates a new copy of the currently selected {@link Device}(s)
     */
    public void duplicate() {
        Preconditions.checkState(!Utility.isNullOrEmpty(selectedDevices));
        try {
            final int duplicated = deviceEJB.duplicate(selectedDevices.stream().map(DeviceView::getDevice).
                                                        collect(Collectors.toList()));
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Duplicated " + duplicated + " devices.");
        } finally {
            prepareDevicesForDisplay(null);
            deviceAttributesController.clearRelatedAttributeInformation();
        }
    }

    /** @return The list of all {@link Device} instances to display to the the user */
    public List<DeviceView> getDevices() {
        return devices;
    }

    /** @return The reference to the device instance displayed in the row the user clicked the action for */
    public List<DeviceView> getSelectedDevices() {
        return selectedDevices;
    }
    /**
     * @param selectedDevices The reference to the device instance displayed in the row the user clicked the action for.
     */
    public void setSelectedDevices(List<DeviceView> selectedDevices) {
        this.selectedDevices = selectedDevices;
    }

    /** @return The inventory ID (see {@link Device#getSerialNumber()}) of the {@link Device} */
    @NotNull
    @Size(min = 1, max = 64, message="Inventory ID can have at most 64 characters.")
    public String getSerialNumber() {
        return serialNumber;
    }
    /** @param serialNumber The inventory ID (see {@link Device#getSerialNumber()}) of the {@link Device} */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /** The validator for the inventory ID input field. Called when creating a new device {@link Device}
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException {@link javax.faces.validator.ValidatorException}
     */
    public void newDeviceSerialNoValidator(FacesContext ctx, UIComponent component, Object value)
            throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                                                    "No value to parse."));
        }
        final String newDeviceSerial = value.toString();

        if (isBatchCreation && !newDeviceSerial.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                    "Batch creation selected, but index position \"{i}\" not set"));
        }
        if (!isBatchCreation && newDeviceSerial.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                    "Error in name: \"{i}\""));
        }

        // empty input value is handled by PrimeFaces and configured from xhtml
        if (!newDeviceSerial.isEmpty()) {
            final Device existingDevice = deviceEJB.findDeviceBySerialNumber(newDeviceSerial);
            final Device editedDevice = (!isSingleDeviceSelected() || !isDeviceBeingEdited)
                                                    ? null : selectedDevices.get(0).getDevice();
            if (existingDevice != null && !existingDevice.equals(editedDevice)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        UiUtility.MESSAGE_SUMMARY_ERROR, "Device with this inventory ID already exists."));
            }
        }
    }

    /** @return the usedDevices */
    public List<DeviceView> getUsedDevices() {
        return usedDevices;
    }

    /** @return the filteredDevices */
    public List<DeviceView> getFilteredDevices() {
        return filteredDevices;
    }

    /** @param filteredDevices the filteredDevices to set */
    public void setFilteredDevices(List<DeviceView> filteredDevices) {
        this.filteredDevices = filteredDevices;
    }

    /** @return the availableDeviceTypes */
    public List<ComponentType> getAvailableDeviceTypes() {
        return availableDeviceTypes;
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
     * @param ctx the context
     * @param component the component
     * @param value the value
     * @throws ValidatorException validation failed
     */
    public void batchEndValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (batchStartIndex >= (Integer)value) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                    "End index must be greater than start index."));
        }
    }

    /** The validator for the start index field
     * @param ctx the context
     * @param component the component
     * @param value the value
     * @throws ValidatorException validation failed
     */
    public void batchStartValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if ((Integer)value >= batchEndIndex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                    "Start index must be less than end index."));
        }
    }

    /** @return a new line separated list of all devices in conflict */
    public String getBatchSerialConflicts() {
        return batchSerialConflicts;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    /** @return the filteredDialogDevices */
    public List<DeviceView> getFilteredDialogDevices() {
        return filteredDialogDevices;
    }

    /** @param filteredDialogDevices the filteredDialogDevices to set */
    public void setFilteredDialogDevices(List<DeviceView> filteredDialogDevices) {
        this.filteredDialogDevices = filteredDialogDevices;
    }
}
