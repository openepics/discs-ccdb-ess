/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceStatus;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.export.CSVExportTable;
import org.openepics.discs.conf.export.ExcelExportTable;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.util.BatchIterator;
import org.openepics.discs.conf.util.BatchSaveStage;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.DeviceView;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;


/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class DevicesByTypeManager implements Serializable {
    private static final long serialVersionUID = 3236468538191653638L;
    private static final Logger LOGGER = Logger.getLogger(DevicesByTypeManager.class.getCanonicalName());
    private static final String CRLF = "\r\n";

    @Inject private transient ComptypeEJB componentTypesEJB;
    @Inject private transient DeviceEJB deviceEJB;
    @Inject private transient InstallationEJB installationEJB;

    private ComponentType selectedComponentType;
    private List<ComponentType> availableDeviceTypes;

    private List<DeviceView> devices;
    private transient List<DeviceView> filteredDevices;
    private transient DeviceView selectedDevice;

    private List<SelectItem> statusLabels;

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

    // ---- table download properties
    private String fileFormat;
    private boolean includeHeaderRow;

    public DevicesByTypeManager() {
    }

    /** Java EE post construct life-cycle method */
    @PostConstruct
    public void init() {
        availableDeviceTypes = componentTypesEJB.findAll();

        Long selectedDeviceId = null;
        final String deviceId = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                getRequest()).getParameter("id");
        if (deviceId != null) {
            selectedDeviceId = Long.valueOf(deviceId);
        }

        prepareDevicesForDisplay(selectedDeviceId);
        prepareStatusLabels();
        if (selectedIndex > -1) {
            RequestContext.getCurrentInstance().execute("selectDeviceInTable(" + selectedIndex + ");");
        }
    }

    /** Creates a new device instance and adds all properties to it which are defined by device type */
    public void onDeviceAdd() {
        if (isBatchCreation) {
            if (!multiDeviceAdd()) {
                return;
            }
            RequestContext.getCurrentInstance().execute("PF('addDeviceDialog').hide();");
        } else {
            singleDeviceAdd();
        }

        clearDeviceDialogFields();
        prepareDevicesForDisplay(null);
    }

    private void singleDeviceAdd() {
        deviceEJB.addDeviceAndPropertyDefs(createNewDevice(serialNumber));
        RequestContext.getCurrentInstance().execute("PF('addDeviceDialog').hide();");
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Device saved.", null);
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
                    final Device deviceToAdd = createNewDevice(deviceSerialNo);
                    deviceEJB.add(deviceToAdd);
                    devicesCreated++;
                }
            }
            RequestContext.getCurrentInstance().execute("PF('devicesTableVar').filter();clearDeviceInstance();");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Created " + devicesCreated + " new properties.");
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
        Preconditions.checkNotNull(selectedDevice);
        final Device exitingDevice = selectedDevice.getDevice();
        exitingDevice.setSerialNumber(serialNumber);
        exitingDevice.setComponentType(selectedComponentType);
        deviceEJB.save(exitingDevice);

        selectedDevice.refreshDevice(deviceEJB.findById(exitingDevice.getId()));
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Device updated.", null);
    }

    /** Event handler which handles the device delete */
    public void onDeviceDelete() {
        Preconditions.checkNotNull(selectedDevice);

        if (installationEJB.getActiveInstallationRecordForDevice(selectedDevice.getDevice()) == null) {
            try {
                final Device deleteDevice = deviceEJB.findById(selectedDevice.getDevice().getId());
                deviceEJB.delete(deleteDevice);
                prepareDevicesForDisplay(null);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Device deleted.", null));
            } catch (Exception e) {
                if (Utility.causedByPersistenceException(e)) {
                    Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_DELETE_FAIL,
                            "The property could not be deleted because it is used.");
                } else {
                    throw e;
                }
            }
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_DELETE_FAIL,
                    "Device instance could not be deleted because it is installed.");
        }
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
        serialNumber = selectedDevice.getInventoryId();
        selectedComponentType = selectedDevice.getDevice().getComponentType();
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

    /** The method prepares the list of all {@link Device} instances in the database to show to the user. */
    private void prepareDevicesForDisplay(final Long deviceToSelect) {
        selectedDevice = null;
        isDeviceBeingEdited = false;
        final List<Device> deviceList = deviceEJB.findAll();

        int devTableRowCounter = 0;
        final Builder<DeviceView> listBuilder = new ImmutableList.Builder<>();
        // transform the list of Device into an immutable list of DeviceView
        for (final Device dev : deviceList) {
            final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForDevice(dev);
            final String installationSlot = installationRecord == null ? "-": installationRecord.getSlot().getName();
            final String installationSlotId = installationRecord == null ? null
                                                    : Long.toString(installationRecord.getSlot().getId());
            final Date installationDate = installationRecord == null ? null: installationRecord.getInstallDate();
            final DeviceView devView = new DeviceView(dev, installationSlot, installationSlotId, installationDate);
            if (deviceToSelect != null && selectedDevice == null && dev.getId().equals(deviceToSelect)) {
                selectedIndex = devTableRowCounter;
                selectedDevice = devView;
            }
            devTableRowCounter++;
            listBuilder.add(devView);
        }
        devices = listBuilder.build();
    }

    private void prepareStatusLabels() {
        statusLabels = Lists.newArrayList();
        statusLabels.add(new SelectItem("", "Select one"));
        for (final DeviceStatus status : DeviceStatus.values()) {
            statusLabels.add(new SelectItem(status.getLabel(), status.getLabel()));
        }
    }

    /** @return the list of labels for the filter */
    public List<SelectItem> getStatusLabels() {
        return statusLabels;
    }

    /** @return The list of all {@link Device} instances to display to the the user */
    public List<DeviceView> getDevices() {
        return devices;
    }

    /** @return The reference to the device instance displayed in the row the user clicked the action for */
    public DeviceView getSelectedDevice() {
        return selectedDevice;
    }
    /**
     * @param selectedDevice The reference to the device instance displayed in the row the user clicked the action for.
     */
    public void setSelectedDevice(DeviceView selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    /** @return The inventory ID (see {@link Device#getSerialNumber()}) of the {@link Device} */
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "No value to parse."));
        }
        final String newDeviceSerial = value.toString();

        if (isBatchCreation && !newDeviceSerial.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Batch creation selected, but index position \"{i}\" not set"));
        }
        if (!isBatchCreation && newDeviceSerial.contains("{i}")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Error in name: \"{i}\""));
        }

        // empty input value is handled by PrimeFaces and configured from xhtml
        if (!newDeviceSerial.isEmpty()) {
            final Device existingDevice = deviceEJB.findDeviceBySerialNumber(newDeviceSerial);
            final Device editedDevice = (selectedDevice == null || !isDeviceBeingEdited)
                                                    ? null : selectedDevice.getDevice();
            if (existingDevice != null && !existingDevice.equals(editedDevice)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Device instance with this inventory ID already exists."));
            }
        }
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

    /** @return a new line separated list of all devices in conflict */
    public String getBatchSerialConflicts() {
        return batchSerialConflicts;
    }

    /** @return the fileFormat */
    public String getFileFormat() {
        return fileFormat;
    }
    /** @param fileFormat the fileFormat to set */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    /** @return the includeHeaderRow */
    public boolean isIncludeHeaderRow() {
        return includeHeaderRow;
    }
    /** @param includeHeaderRow the includeHeaderRow to set */
    public void setIncludeHeaderRow(boolean includeHeaderRow) {
        this.includeHeaderRow = includeHeaderRow;
    }

    /** Prepares the default values of the Export data dialog: file format and header row */
    public void prepareTableExportPopup() {
        fileFormat = ExportTable.FILE_FORMAT_EXCEL;
        includeHeaderRow = true;
    }

    /** @return The data from the table exported into the PrimeFaces file download stream */
    public StreamedContent getExportedTable() {
        final List<DeviceView> exportData = filteredDevices == null || filteredDevices.isEmpty() ? devices
                : filteredDevices;
        final ExportTable exportTable;
        final String mimeType;
        final String fileName;

        if (fileFormat.equals(ExportTable.FILE_FORMAT_EXCEL)) {
            exportTable = new ExcelExportTable();
            mimeType = ExportTable.MIME_TYPE_EXCEL;
            fileName = "devices.xlsx";
        } else {
            exportTable = new CSVExportTable();
            mimeType = ExportTable.MIME_TYPE_CSV;
            fileName = "devices.csv";
        }

        exportTable.createTable("Device instances");
        if (includeHeaderRow) {
            exportTable.addHeaderRow("Type", "Inventory ID", "Status", "Installed in", "Installation timestamp");
        }

        for (final DeviceView deviceInstance : exportData) {
            exportTable.addDataRow(deviceInstance.getDevice().getComponentType().getName(),
                    deviceInstance.getInventoryId(), deviceInstance.getStatusLabel(),
                    deviceInstance.getInstalledIn().equals("-") ? null : deviceInstance.getInstalledIn(),
                    deviceInstance.getInstallationTimestamp());
        }

        return new DefaultStreamedContent(exportTable.exportTable(), mimeType, fileName);
    }
}
