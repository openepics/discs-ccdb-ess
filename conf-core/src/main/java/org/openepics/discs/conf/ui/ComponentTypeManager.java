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

import org.openepics.discs.conf.dl.ComponentTypesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.AuditRecordEJB;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 */
@Named
@ViewScoped
public class ComponentTypeManager extends AbstractExcelSingleFileImportUI implements Serializable {
    private static final long serialVersionUID = -9007187646811006328L;

    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient AuditRecordEJB auditRecordEJB;
    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @ComponentTypesLoaderQualifier private transient DataLoader compTypesDataLoader;

    private List<ComponentType> deviceTypes;
    private List<ComponentType> filteredDeviceTypes;
    private String name;
    private String description;
    private ComponentType selectedDeviceType;
    private List<AuditRecord> auditRecordsForEntity;

    /** Creates a new instance of ComponentTypeMananger */
    public ComponentTypeManager() {
    }

    /** Java EE post construct life-cycle method. */
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

    /** Prepares the UI data for the "Add a new device type" dialog. */
    public void prepareAddPopup() {
        resetFields();
        RequestContext.getCurrentInstance().update("addDeviceTypeForm:addDeviceType");
    }

    private void resetFields() {
        name = null;
        description = null;
    }

    /** Support method to enable clearing the selected device type from the UI */
    public void clearSelectedDeviceType() {
        selectedDeviceType = null;
    }

    /** Called when the user presses the "Save" button in the "Add a new device type" dialog. */
    public void onAdd() {
        final ComponentType componentTypeToAdd = new ComponentType(name);
        componentTypeToAdd.setDescription(description);
        try {
            comptypeEJB.add(componentTypeToAdd);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "New device type has been created");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "Device type could not be added because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            deviceTypes = comptypeEJB.findAll();
        }
    }

    public void prepareEditPopup() {
        name = selectedDeviceType.getName();
        description = selectedDeviceType.getDescription();
    }

    public void onChange() {
        selectedDeviceType.setName(name);
        selectedDeviceType.setDescription(description);
        try {
            comptypeEJB.save(selectedDeviceType);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Device type updated");
            selectedDeviceType = null;
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "Device type could not be modified because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            deviceTypes = comptypeEJB.findAll();
        }
    }

    /** Called when the user clicks the "trash can" icon in the table listing the devices types. */
    public void onDelete() {
        try {
            final ComponentType deleteDeviceType = comptypeEJB.findById(selectedDeviceType.getId());
            comptypeEJB.delete(deleteDeviceType);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Device type was deleted");
            selectedDeviceType = null;
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_DELETE_FAIL,
                        "The device type could not be deleted because it is used.");
            } else {
                throw e;
            }
        } finally {
            deviceTypes = comptypeEJB.findAll();
        }
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            loaderResult = dataLoaderHandler.loadData(inputStream, compTypesDataLoader);
            deviceTypes = comptypeEJB.findAll();
            RequestContext.getCurrentInstance().update("deviceTypesForm");
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
        auditRecordsForEntity = auditRecordEJB.findByEntityIdAndType(selectedDeviceType.getId(),
                                                                                EntityType.COMPONENT_TYPE);
        RequestContext.getCurrentInstance().update("deviceTypeLogForm:deviceTypeLog");
    }

    /**
     * @return The list of {@link AuditRecord} entries for selected entity.
     * @see ComponentTypeManager#setSelectedDeviceTypeForLog(ComponentType)
     */
    public List<AuditRecord> getAuditRecordsForEntity() {
        return auditRecordsForEntity;
    }

    /** @return The name of the device type the user is adding or modifying. Used in the UI dialog. */
    public String getName() {
        return name;
    }
    /** @param name The name of the device type the user is adding or modifying. Used in the UI dialog. */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The description of the device type the user is adding or modifying. Used in the UI dialog. */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description of the device type the user is adding or modifying. Used in the UI dialog.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return The list of filtered device types used by the PrimeFaces filter field. */
    public List<ComponentType> getFilteredDeviceTypes() {
        return filteredDeviceTypes;
    }
    /** @param filteredDeviceTypes The list of filtered device types used by the PrimeFaces filter field. */
    public void setFilteredDeviceTypes(List<ComponentType> filteredDeviceTypes) {
        this.filteredDeviceTypes = filteredDeviceTypes;
    }

    /** @return The list of all device types in the database. */
    public List<ComponentType> getDeviceTypes() {
        return deviceTypes;
    }
}
