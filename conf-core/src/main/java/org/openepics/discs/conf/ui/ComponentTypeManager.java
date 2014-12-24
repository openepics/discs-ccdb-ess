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
import javax.ejb.EJB;
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
public class ComponentTypeManager implements Serializable {

    @EJB private ComptypeEJB comptypeEJB;
    @Inject private AuditRecordEJB auditRecordEJB;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @ComponentTypesLoaderQualifier private DataLoader compTypesDataLoader;

    private byte[] importData;
    private String importFileName;
    private DataLoaderResult loaderResult;

    private List<ComponentType> objects;
    private List<ComponentType> sortedObjects;
    private List<ComponentType> filteredObjects;
    private ComponentType selectedObject;
    private String name;
    private String description;
    private ComponentType selectedDeviceType;
    private List<AuditRecord> auditRecordsForEntity;

    /**
     * Creates a new instance of ComponentTypeMananger
     */
    public ComponentTypeManager() {
    }

    @PostConstruct
    public void init() {
        try {
            objects = comptypeEJB.findAll();
            resetFields();
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    public String deviceTypePropertyRedirect(Long id) {
        return "device-type-attributes-manager.xhtml?faces-redirect=true&id=" + id;
    }

    public void prepareAddPopup() {
        resetFields();
        RequestContext.getCurrentInstance().update("addDeviceTypeForm:addDeviceType");
    }

    private void resetFields() {
        name = null;
        description = null;
    }

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

    public String getImportFileName() {
        return importFileName;
    }

    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, compTypesDataLoader);
    }

    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    // -------------------- Getters and Setters ---------------------------------------

    public ComponentType getSelectedDeviceType() {
        return selectedDeviceType;
    }
    public void setSelectedDeviceType(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
    }

    public ComponentType getSelectedDeviceTypeForLog() {
        return selectedDeviceType;
    }
    public void setSelectedDeviceTypeForLog(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
        auditRecordsForEntity = auditRecordEJB.findByEntityIdAndType(selectedDeviceType.getId(), EntityType.COMPONENT_TYPE);
        RequestContext.getCurrentInstance().update("deviceTypeLogForm:deviceTypeLog");
    }

    public List<AuditRecord> getAuditRecordsForEntity() {
        return auditRecordsForEntity;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<ComponentType> getSortedObjects() {
        return sortedObjects;
    }
    public void setSortedObjects(List<ComponentType> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<ComponentType> getFilteredObjects() {
        return filteredObjects;
    }
    public void setFilteredObjects(List<ComponentType> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public ComponentType getSelectedObject() {
        return selectedObject;
    }
    public void setSelectedObject(ComponentType selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<ComponentType> getObjects() {
        if (objects == null) objects = comptypeEJB.findAll();
        return objects;
    }
}
