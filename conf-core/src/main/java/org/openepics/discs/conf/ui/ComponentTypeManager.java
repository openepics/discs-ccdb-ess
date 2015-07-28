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

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.dl.annotations.ComponentTypesLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 */@Named
@ViewScoped
public class ComponentTypeManager extends AbstractExcelSingleFileImportUI
                implements Serializable, SimpleTableExporter {
    private static final long serialVersionUID = -9007187646811006328L;
    private static final Logger LOGGER = Logger.getLogger(ComponentTypeManager.class.getCanonicalName());

    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @ComponentTypesLoader private transient DataLoader compTypesDataLoader;

    private List<ComponentType> deviceTypes;
    private List<ComponentType> filteredDeviceTypes;
    private List<ComponentType> selectedDeviceTypes;
    private List<ComponentType> usedDeviceTypes;
    private ComponentType editedDeviceType;
    private String name;
    private String description;

    private ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimpleDevTypeTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Device types";
        }

        @Override
        protected String getFileName() {
            return "device-types";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<ComponentType> exportData = filteredDeviceTypes == null || filteredDeviceTypes.isEmpty()
                    ? deviceTypes
                    : filteredDeviceTypes;
            for (final ComponentType devType : exportData) {
                exportTable.addDataRow(devType.getName(), devType.getDescription());
            }
        }
    }

    /** Creates a new instance of ComponentTypeMananger */
    public ComponentTypeManager() {
    }

    /** Java EE post construct life-cycle method. */
    @Override
    @PostConstruct
    public void init() {
        super.init();
        final String deviceTypeIdStr = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                getRequest()).getParameter("id");
        try {
            simpleTableExporterDialog = new ExportSimpleDevTypeTableDialog();
            deviceTypes = comptypeEJB.findAll();
            resetFields();

            if (!Strings.isNullOrEmpty(deviceTypeIdStr)) {
                final long deviceTypeId = Long.parseLong(deviceTypeIdStr);
                int elementPosition = 0;
                for (final ComponentType deviceType : deviceTypes) {
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

    @Override
    public void setDataLoader() {
        dataLoader = compTypesDataLoader;
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

    /** Prepares the data for the device type editing dialog fields based on the selected device type. */
    public void prepareEditPopup() {
        Preconditions.checkState(isSingleDeviceTypeSelected());
        editedDeviceType = comptypeEJB.findById(selectedDeviceTypes.get(0).getId());
        name = editedDeviceType.getName();
        description = editedDeviceType.getDescription();
    }

    /** Saves the new device type data (name and/or description) */
    public void onChange() {
        Preconditions.checkNotNull(editedDeviceType);
        editedDeviceType.setName(name);
        editedDeviceType.setDescription(description);
        try {
            comptypeEJB.save(editedDeviceType);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Device type updated");
            editedDeviceType = null;
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

    /**
     * The method builds a list of device types that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkDeviceTypesForDeletion() {
        Preconditions.checkNotNull(selectedDeviceTypes);
        Preconditions.checkState(!selectedDeviceTypes.isEmpty());

        usedDeviceTypes = Lists.newArrayList();
        for (final ComponentType deviceTypeToDelete : selectedDeviceTypes) {
            if (comptypeEJB.isComponentTypeUsed(deviceTypeToDelete)) {
                usedDeviceTypes.add(deviceTypeToDelete);
            }
        }
    }


    /** Called when the user clicks the "trash can" icon in the table listing the devices types. */
    public void onDelete() {
        Preconditions.checkNotNull(selectedDeviceTypes);
        Preconditions.checkState(!selectedDeviceTypes.isEmpty());
        Preconditions.checkNotNull(usedDeviceTypes);
        Preconditions.checkState(usedDeviceTypes.isEmpty());

        int deletedDeviceTypes = 0;
        for (final ComponentType deviceTypeToDelete : selectedDeviceTypes) {
            final ComponentType freshEntity = comptypeEJB.findById(deviceTypeToDelete.getId());
            freshEntity.getTags().clear();
            comptypeEJB.delete(freshEntity);
            ++deletedDeviceTypes;
        }
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                "Deleted " + deletedDeviceTypes + " device types.");
        selectedDeviceTypes = null;
        filteredDeviceTypes = null;
        usedDeviceTypes = null;
        deviceTypes = comptypeEJB.findAll();
    }

    /** @return <code>true</code> if a single device type is selected , <code>false</code> otherwise */
    public boolean isSingleDeviceTypeSelected() {
        return (selectedDeviceTypes != null) && (selectedDeviceTypes.size() == 1);
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, compTypesDataLoader));
            deviceTypes = comptypeEJB.findAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------- Getters and Setters ---------------------------------------

    /** @return the selectedDeviceTypes */
    public List<ComponentType> getSelectedDeviceTypes() {
        return selectedDeviceTypes;
    }
    /** @param selectedDeviceTypes the selectedDeviceTypes to set */
    public void setSelectedDeviceTypes(List<ComponentType> selectedDeviceTypes) {
        this.selectedDeviceTypes = selectedDeviceTypes;
    }

    /** @return the {@link List} of used device types */
    public List<ComponentType> getUsedDeviceTypes() {
        return usedDeviceTypes;
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

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }
}
