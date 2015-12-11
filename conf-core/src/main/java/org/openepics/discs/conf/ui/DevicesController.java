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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractDeviceAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.util.BatchIterator;
import org.openepics.discs.conf.util.BatchSaveStage;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.DeviceView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

/**
 * Controller bean for manipulation of {@link Device} attributes
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Named
@ViewScoped
public class DevicesController
                    extends AbstractDeviceAttributesController
                    implements SimpleTableExporter{
    private static final long serialVersionUID = -2881746639197321061L;

    private static final Logger LOGGER = Logger.getLogger(DevicesController.class.getCanonicalName());
    private static final String CRLF = "\r\n";

    @Inject private transient ComptypeEJB componentTypesEJB;
    @Inject private transient InstallationEJB installationEJB;
    @Inject private transient DeviceEJB deviceEJB;

    private Device device;

    private ComponentType selectedComponentType;
    private List<ComponentType> availableDeviceTypes;

    private transient List<DeviceView> devices;
    private transient List<DeviceView> filteredDevices;
    private transient List<DeviceView> selectedDevices;
    private transient List<DeviceView> usedDevices;
    private transient List<DeviceView> filteredDialogDevices;

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
            exportTable.addHeaderRow("Type", "Inventory ID", "Installed in", "Installation timestamp");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<DeviceView> exportData = Utility.isNullOrEmpty(filteredDevices) ? devices
                    : filteredDevices;
            for (final DeviceView deviceInstance : exportData) {
                exportTable.addDataRow(deviceInstance.getDevice().getComponentType().getName(),
                        deviceInstance.getInventoryId(),
                        "-".equals(deviceInstance.getInstalledIn()) ? null : deviceInstance.getInstalledIn(),
                        deviceInstance.getInstallationTimestamp());
            }
        }
    }

    public DevicesController() {
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            final String deviceId = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                    getRequest()).getParameter("id");
            super.init();
            setArtifactClass(DeviceArtifact.class);
            setPropertyValueClass(DevicePropertyValue.class);
            setDao(deviceEJB);
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

    @Override
    protected void setPropertyValueParent(DevicePropertyValue child) {
        child.setDevice(device);
    }

    @Override
    protected void setArtifactParent(DeviceArtifact child) {
        device = deviceEJB.findById(device.getId());
        child.setDevice(device);
    }

    @Override
    protected void setTagParent(Tag tag) {
        Preconditions.checkNotNull(device);
        device = deviceEJB.findById(device.getId());
        final Set<Tag> existingTags = device.getTags();
        if (!existingTags.contains(tag)) {
            existingTags.add(tag);
            deviceEJB.save(device);
        }
    }

    @Override
    protected void setTagParentForOperations(Long parentId) {
        Preconditions.checkNotNull(parentId);
        device = deviceEJB.findById(parentId);
    }

    @Override
    protected void deleteTagFromParent(Tag tag) {
        Preconditions.checkNotNull(device);
        device = deviceEJB.findById(device.getId());
        device.getTags().remove(tag);
        deviceEJB.save(device);
    }

    @Override
    protected void filterProperties() {
        // nothing to do
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<EntityAttributeView>();

        for (final DeviceView deviceView : selectedDevices) {
            final Device attrDevice = deviceEJB.findById(deviceView.getDevice().getId());
            final ComponentType parent = attrDevice.getComponentType();

            for (final ComptypePropertyValue parentProp : parent.getComptypePropertyList()) {
                if (parentProp.getPropValue() != null) {
                    attributes.add(new EntityAttributeView(parentProp, EntityAttributeViewKind.DEVICE_TYPE_PROPERTY,
                                                                attrDevice, deviceEJB, parent.getName()));
                }
            }

            for (final ComptypeArtifact parentArtifact : parent.getComptypeArtifactList()) {
                attributes.add(new EntityAttributeView(parentArtifact, EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT,
                                                                attrDevice, deviceEJB, parent.getName()));
            }

            for (final Tag parentTag : parent.getTags()) {
                attributes.add(new EntityAttributeView(parentTag, EntityAttributeViewKind.DEVICE_TYPE_TAG,
                                                                attrDevice, deviceEJB, parent.getName()));
            }

            for (final DevicePropertyValue propVal : attrDevice.getDevicePropertyList()) {
                attributes.add(new EntityAttributeView(propVal, EntityAttributeViewKind.DEVICE_PROPERTY,
                                                                attrDevice, deviceEJB, parent.getName()));
            }

            for (final DeviceArtifact artf : attrDevice.getDeviceArtifactList()) {
                attributes.add(new EntityAttributeView(artf, EntityAttributeViewKind.DEVICE_ARTIFACT,
                                                                attrDevice, deviceEJB));
            }

            for (final Tag tagAttr : attrDevice.getTags()) {
                attributes.add(new EntityAttributeView(tagAttr, EntityAttributeViewKind.DEVICE_TAG,
                                                                attrDevice, deviceEJB));
            }

            final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForDevice(attrDevice);
            final Slot slot = installationRecord != null ? installationRecord.getSlot() : null;

            if (slot != null) {
                for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
                    attributes.add(new EntityAttributeView(value, EntityAttributeViewKind.INSTALL_SLOT_PROPERTY,
                                                                    attrDevice, deviceEJB,
                                                                    slot.getName() + ", " + parent.getName()));
                }
            } else {
                for (final ComptypePropertyValue parentProp : parent.getComptypePropertyList()) {
                    if (parentProp.isDefinitionTargetSlot())
                        attributes.add(new EntityAttributeView(parentProp, EntityAttributeViewKind.INSTALL_SLOT_PROPERTY,
                                                                    attrDevice, deviceEJB, parent.getName()));
                }
            }
        }
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
            selectedAttributes = null;
            filteredAttributes = null;
            populateAttributesList();
        } else {
            clearDeviceDialogFields();
            device = null;
            selectedComponentType = null;
            availableDeviceTypes = null;
            attributes = null;
            selectedAttributes = null;
            filteredAttributes = null;
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
            RequestContext.getCurrentInstance().execute("PF('devicesTableVar').filter();");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
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
        return createNewDevice(deviceSerailNo, selectedComponentType);
    }

    private Device createNewDevice(String deviceSerailNo, final ComponentType componentType) {
        final Device newDevice = new Device(deviceSerailNo);
        newDevice.setComponentType(componentType);
        return newDevice;
    }

    /** Updates an existing device with new information from the dialog */
    public void onDeviceEdit() {
        Preconditions.checkState(isSingleDeviceSelected());
        device = deviceEJB.findById(selectedDevices.get(0).getDevice().getId());
        device.setSerialNumber(serialNumber);
        device.setComponentType(selectedComponentType);
        deviceEJB.save(device);

        selectedDevices.get(0).refreshDevice(deviceEJB.findById(device.getId()));
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Device updated.");
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
            deleteDevice.getTags().clear();
            deviceEJB.delete(deleteDevice);
            ++deletedDevices;
        }

        prepareDevicesForDisplay(null);
        filteredDevices = null;
        selectedDevices = null;
        usedDevices = null;
        attributes = null;
        selectedAttributes = null;
        filteredAttributes = null;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Deleted " + deletedDevices
                + " devices.");
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
            int duplicated = 0;
            for (final DeviceView deviceView : selectedDevices) {
                final Device deviceToCopy =  deviceView.getDevice();
                final String newDeviceSerial = Utility.findFreeName(deviceToCopy.getSerialNumber(), deviceEJB);
                final Device newCopy = createNewDevice(newDeviceSerial, deviceToCopy.getComponentType());

                deviceEJB.duplicate(newCopy, deviceToCopy);
                duplicated++;
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Duplicated " + duplicated
                    + " devices.");
        } finally {
            prepareDevicesForDisplay(null);
            attributes = null;
            selectedAttributes = null;
            filteredAttributes = null;
        }
    }

    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        // this handler is shared between AbstractExcelSingleFileImportUI and Artifact loading
        if ("importDevicesForm:singleFileDLUploadCtl".equals(event.getComponent().getClientId())) {
            super.handleImportFileUpload(event);
        } else {
            try (InputStream inputStream = event.getFile().getInputstream()) {
                importData = ByteStreams.toByteArray(inputStream);
                importFileName = FilenameUtils.getName(event.getFile().getFileName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            final Device editedDevice = (!isSingleDeviceSelected() || !isDeviceBeingEdited)
                                                    ? null : selectedDevices.get(0).getDevice();
            if (existingDevice != null && !existingDevice.equals(editedDevice)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Device with this inventory ID already exists."));
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
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

    @Override
    public void doImport() {
        super.doImport();
        prepareDevicesForDisplay(null);
    }

    /** @return the filteredDialogDevices */
    public List<DeviceView> getFilteredDialogDevices() {
        return filteredDialogDevices;
    }

    /** @param filteredDialogDevices the filteredDialogDevices to set */
    public void setFilteredDialogDevices(List<DeviceView> filteredDialogDevices) {
        this.filteredDialogDevices = filteredDialogDevices;
    }

    /**
     *  Prevents deletion of installation slot properties in this view only.     *
     */
    @Override
    protected boolean canDelete(EntityAttributeView attributeView) {
        if (EntityAttributeViewKind.CONTAINER_SLOT_PROPERTY.equals(attributeView.getKind())) return false;
        if (EntityAttributeViewKind.INSTALL_SLOT_PROPERTY.equals(attributeView.getKind())) return false;
        if (EntityAttributeViewKind.DEVICE_TYPE_PROPERTY.equals(attributeView.getKind())) return false;
        return super.canDelete(attributeView);
    }
}
