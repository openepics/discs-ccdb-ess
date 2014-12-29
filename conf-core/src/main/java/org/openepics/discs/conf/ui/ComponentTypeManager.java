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

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.ComponentTypesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.AuditRecordEJB;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.ExcelSingleFileImportUIHandlers;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 */
@Named
@ViewScoped
public class ComponentTypeManager implements Serializable, ExcelSingleFileImportUIHandlers {

    @Inject transient private ComptypeEJB comptypeEJB;
    @Inject transient private AuditRecordEJB auditRecordEJB;
    @Inject transient private DataLoaderHandler dataLoaderHandler;
    @Inject @ComponentTypesLoaderQualifier transient private DataLoader compTypesDataLoader;

    private byte[] importData;
    private String importFileName;
    transient private DataLoaderResult loaderResult;

    private List<ComponentType> deviceTypes;
    private List<ComponentType> filteredDeviceTypes;
    private String name;
    private String description;
    private ComponentType selectedDeviceType;
    private List<AuditRecord> auditRecordsForEntity;

    /**
     * Creates a new instance of ComponentTypeMananger
     */
    public ComponentTypeManager() {
    }

    /**
     * Java EE post construct life-cycle method.
     */
    @PostConstruct
    public void init() {
        try {
            deviceTypes = comptypeEJB.findAll();
            resetFields();
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    /** Called when the user clicks the "pencil" icon in the table listing the device types. The user is redirected
     * to the attribute manager screen.
     * @param id The primary key of the {@link ComponentType} entity
     * @return The URL to redirect to
     */
    public String deviceTypePropertyRedirect(Long id) {
        return "device-type-attributes-manager.xhtml?faces-redirect=true&id=" + id;
    }

    /**
     * Prepares the UI data for the "Add a new device type" dialog.
     */
    public void prepareAddPopup() {
        resetFields();
        RequestContext.getCurrentInstance().update("addDeviceTypeForm:addDeviceType");
    }

    private void resetFields() {
        name = null;
        description = null;
    }

    /**
     * Called when the user presses the "Save" button in the "Add a new device type" dialog.
     */
    public void onAdd() {
        final ComponentType componentTypeToAdd = new ComponentType(name);
        componentTypeToAdd.setDescription(description);
        try {
            comptypeEJB.add(componentTypeToAdd);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New device type has been created");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Device type could not be added because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            init();
        }
    }

    /**
     * Called when the user clicks the "trash can" icon in the table listing the devices types.
     */
    public void onDelete() {
        try {
            comptypeEJB.delete(selectedDeviceType);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type was deleted");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The device type could not be deleted because it is used.");
            } else {
                throw e;
            }
        } finally {
            init();
        }
    }

    @Override
    public String getImportFileName() {
        return importFileName;
    }

    @Override
    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, compTypesDataLoader);
    }

    @Override
    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    @Override
    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
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

    // -------------------- Getters and Setters ---------------------------------------

    /**
     * @return The reference to the device type displayed in the row the user clicked the action for.
     * @see ComponentTypeManager#setSelectedDeviceType(ComponentType)
     */
    public ComponentType getSelectedDeviceType() {
        return selectedDeviceType;
    }
    /**
     * @param selectedDeviceType When the user clicks on the action in the "Action" column of the table listing all the
     * device type, this method stores the reference to the device type displayed in that table row.
     */
    public void setSelectedDeviceType(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
    }

    /**
     * @return Returns the reference to the device type displayed in the row the user clicked the action for.
     * @see ComponentTypeManager#getSelectedDeviceTypeForLog()
     */
    public ComponentType getSelectedDeviceTypeForLog() {
        return selectedDeviceType;
    }
    /** Sets the same reference as {@link #setSelectedDeviceType(ComponentType)} plus addition data required for audit
     * log display.
     * @param selectedDeviceType When the user clicks on the "pencil" action in the "Action" column of the table
     * listing all the device type, this method stores the reference to the device type displayed in that table row.
     */
    public void setSelectedDeviceTypeForLog(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
        auditRecordsForEntity = auditRecordEJB.findByEntityIdAndType(selectedDeviceType.getId(), EntityType.COMPONENT_TYPE);
        RequestContext.getCurrentInstance().update("deviceTypeLogForm:deviceTypeLog");
    }

    /**
     * @return The list of {@link AuditRecord} entries for selected entity.
     * @see ComponentTypeManager#setSelectedDeviceTypeForLog(ComponentType)
     */
    public List<AuditRecord> getAuditRecordsForEntity() {
        return auditRecordsForEntity;
    }

    /**
     * @return The name of the device type the user is adding or modifying. Used in the UI dialog.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name of the device type the user is adding or modifying. Used in the UI dialog.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The description of the device type the user is adding or modifying. Used in the UI dialog.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description of the device type the user is adding or modifying. Used in the UI dialog.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The list of filtered device types used by the PrimeFaces filter field.
     */
    public List<ComponentType> getFilteredDeviceTypes() {
        return filteredDeviceTypes;
    }
    /**
     * @param filteredDeviceTypes The list of filtered device types used by the PrimeFaces filter field.
     */
    public void setFilteredDeviceTypes(List<ComponentType> filteredDeviceTypes) {
        this.filteredDeviceTypes = filteredDeviceTypes;
    }

    /**
     * @return The list of all device types in the database.
     */
    public List<ComponentType> getDeviceTypes() {
        if (deviceTypes == null) {
            deviceTypes = comptypeEJB.findAll();
        }
        return deviceTypes;
    }
}
