/*
pre * Copyright (c) 2014 European Spallation Source
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
import javax.json.JsonObject;

import org.openepics.discs.conf.dl.annotations.DataTypeLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.UserEnumerationView;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.core.Seds;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * The Java EE managed bean for supporting UI actions for data types an user defined enumeration manipulation.
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class DataTypeManager extends AbstractExcelSingleFileImportUI implements Serializable, SimpleTableExporter {
    private static final long serialVersionUID = -7538356350403365152L;

    @Inject private transient  DataTypeEJB dataTypeEJB;
    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @DataTypeLoader private transient DataLoader enumsDataLoader;

    private transient List<UserEnumerationView> dataTypeViews;
    private transient List<UserEnumerationView> filteredDataTypesViews;
    private transient List<UserEnumerationView> selectedEnums;
    private transient List<UserEnumerationView> usedEnums;
    private transient List<UserEnumerationView> filteredDialogEnums;
    private List<DataType> dataTypes;
    private List<String> builtInDataTypeNames;

    private UserEnumerationView dialogEnum;

    // * * * * * * * Add/modify dialog fields * * * * * * *

    private transient ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimpleEnumTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Enumerations";
        }

        @Override
        protected String getFileName() {
            return "ccdb_enumerations";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description", "Definition");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<UserEnumerationView> exportData = filteredDataTypesViews;
            for (final UserEnumerationView enumeration : exportData) {
                exportTable.addDataRow(enumeration.getName(), enumeration.getDescription(),
                        enumeration.getDisplayDefinition());
            }
        }
    }

    /** Creates a new instance of DataTypeManager */
    public DataTypeManager() {
    }

    /** Java EE post construct life-cycle method */
    @Override
    @PostConstruct
    public void init() {
        super.init();
        try {
            simpleTableExporterDialog = new ExportSimpleEnumTableDialog();

            Builder<String> builtInDataTypeBuilder = ImmutableList.builder();
            for (BuiltInDataType type : BuiltInDataType.values()) {
                builtInDataTypeBuilder.add(type.toString());
            }
            builtInDataTypeNames = builtInDataTypeBuilder.build();

            refreshUserDataTypes();
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    @Override
    public void setDataLoader() {
        dataLoader = enumsDataLoader;
    }

    /**
     * @return A list of all {@link DataType} entities in the database that are user defined enumerations.
     * The list members are wrapped into the {@link UserEnumerationView}.
     */
    public List<UserEnumerationView> getDataTypeViews() {
        return dataTypeViews;
    }

    /** @return A list of all {@link DataType} entities in the database */
    public List<DataType> getDataTypes() {
        return dataTypes;
    }

    /** Getter for PrimeFaces filtering functionality.
     * @return A list of data types as filtered by the UI
     */
    public List<UserEnumerationView> getFilteredDataTypeViews() {
        return filteredDataTypesViews;
    }

    /** Setter for PrimeFaces filtering functionality.
     * @param filteredDataTypeViews A list of filtered properties, set b PrimeFaces based on user filters.
     */
    public void setFilteredDataTypeViews(List<UserEnumerationView> filteredDataTypeViews) {
        this.filteredDataTypesViews = filteredDataTypeViews;
    }

    /** The validator for the UI input field for user defined enumeration name.
     * Called when saving enumeration.
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException {@link javax.faces.validator.ValidatorException}
     */
    public void nameValidator(FacesContext ctx, UIComponent component, Object value) {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "Enumeration name required."));
        }

        final String enumName = value.toString();

        if (dialogEnum.isEnumerationBeingAdded() || !dialogEnum.getName().equals(enumName)) {
            final DataType existingDataType = dataTypeEJB.findByName(enumName);
            if (existingDataType != null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "Enumeration with the same name already exists."));
            }
        }
    }

    private void refreshUserDataTypes() {
        dataTypes = ImmutableList.copyOf(dataTypeEJB.findAll());

        dataTypeViews = dataTypes.stream().filter(dt -> !builtInDataTypeNames.contains(dt.getName()))
                .map(UserEnumerationView::new).collect(Collectors.toList());
        filteredDataTypesViews = dataTypeViews;
        selectedEnums = null;
    }

    /** This method clears all input fields used in the "Add enumeration" dialog. */
    public void prepareAddPopup() {
        dialogEnum = new UserEnumerationView();
    }

    /** This method prepares the input fields used in the "Edit enumeration" dialog. */
    public void prepareModifyPopup() {
        Preconditions.checkState(isSingleEnumSelected());
        dialogEnum = new UserEnumerationView(dataTypeEJB.findById(selectedEnums.get(0).getEnumeration().getId()));
        dialogEnum.setUsed(dataTypeEJB.isDataTypeUsed(dialogEnum.getEnumeration()));
    }

    /** Method that saves a new enumeration definition, when user presses the "Save" button in the "Add new" dialog */
    public void onAdd() {
        try {
            final DataType newEnum = dialogEnum.getEnumeration();
            newEnum.setDefinition(jsonDefinitionFromList(dialogEnum.getDefinitionList()));
            dataTypeEJB.add(newEnum);
            Utility.showMessage(FacesMessage.SEVERITY_INFO,  Utility.MESSAGE_SUMMARY_SUCCESS, "Enumeration has been successfully created.");
        } finally {
            dialogEnum = null;
            refreshUserDataTypes();

        }
    }

    /**
     * Method that saves the modified enumeration definition, when user presses the "Save" button in the
     * "Modify enumeration" dialog.
     */
    public void onModify() {
        try {
            Preconditions.checkNotNull(dialogEnum);
            final DataType modifiedEnum = dialogEnum.getEnumeration();
            modifiedEnum.setDefinition(jsonDefinitionFromList(dialogEnum.getDefinitionList()));
            dataTypeEJB.save(modifiedEnum);
            Utility.showMessage(FacesMessage.SEVERITY_INFO,  Utility.MESSAGE_SUMMARY_SUCCESS, "Enumeration has been successfully modified.");
        } finally {
            dialogEnum = null;
            refreshUserDataTypes();
        }
    }

    /**
     * The method builds a list of user enumerations that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkEnumsForDeletion() {
        Preconditions.checkNotNull(selectedEnums);
        Preconditions.checkState(!selectedEnums.isEmpty());

        usedEnums = Lists.newArrayList();
        for (final UserEnumerationView enumToDelete : selectedEnums) {
            List<Property> properties = dataTypeEJB.findProperties(enumToDelete.getEnumeration(), 2);
            if (!properties.isEmpty()) {
                enumToDelete.setUsedBy(properties.get(0).getName()+(properties.size()>1 ? ", ..." : ""));
                usedEnums.add(enumToDelete);
            }
        }
    }

    /**
     * Method that deletes the user enumeration if that is allowed. Enumeration deletion is prevented if the enumeration
     * is used in some property value.
     */
    public void onDelete() {
        try {
            Preconditions.checkNotNull(selectedEnums);
            Preconditions.checkState(!selectedEnums.isEmpty());
            Preconditions.checkNotNull(usedEnums);
            Preconditions.checkState(usedEnums.isEmpty());

            int deletedEnums = 0;
            for (final UserEnumerationView enumToDelete : selectedEnums) {
                dataTypeEJB.delete(enumToDelete.getEnumeration());
                ++deletedEnums;
            }

            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Deleted " + deletedEnums + " enumerations.");
        } finally {
            selectedEnums = null;
            usedEnums = null;
            refreshUserDataTypes();
        }
    }

    private String jsonDefinitionFromList(List<String> definitionValues) {
        final SedsEnum testEnum = Seds.newFactory().newEnum(definitionValues.get(0),
                                                                definitionValues.toArray(new String[] {}));
        final JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);
        return  jsonEnum.toString();
    }

    /** @return <code>true</code> if a single enumeration is selected , <code>false</code> otherwise */
    public boolean isSingleEnumSelected() {
        return (selectedEnums != null) && (selectedEnums.size() == 1);
    }

    /**
     * The method creates a new copy of the currently selected user enumeration(s)
     */
    public void duplicate() {
        try {
            Preconditions.checkState(!Utility.isNullOrEmpty(selectedEnums));
            int duplicated = 0;
            for (final UserEnumerationView userEnumerationView : selectedEnums) {
                final DataType enumToCopy = userEnumerationView.getEnumeration();
                final String newEnumName = Utility.findFreeName(enumToCopy.getName(), dataTypeEJB);
                final DataType newEnum = new DataType(newEnumName, enumToCopy.getDescription(), enumToCopy.isScalar(),
                        enumToCopy.getDefinition());
                dataTypeEJB.add(newEnum);
                duplicated++;
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Duplicated " + duplicated + " enumerations.");
        } finally {
            refreshUserDataTypes();
        }
    }

    /* * * * * * * * * * * * * * * * * getters and setters * * * * * * * * * * * * * * * * */

    /** @return The {@link DataType}s selected in the table */
    public List<UserEnumerationView> getSelectedEnums() {
        return selectedEnums;
    }
    /** @param selectedEnums The {@link DataType}s selected in the table */
    public void setSelectedEnums(List<UserEnumerationView> selectedEnums) {
        this.selectedEnums = selectedEnums;
    }

    /** @return The sub {@link List} of selected {@link DataType}s that are in use in the database */
    public List<UserEnumerationView> getUsedEnums() {
        return usedEnums;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, enumsDataLoader));
            refreshUserDataTypes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return the filteredDialogEnums */
    public List<UserEnumerationView> getFilteredDialogEnums() {
        return filteredDialogEnums;
    }

    /** @param filteredDialogEnums the filteredDialogEnums to set */
    public void setFilteredDialogEnums(List<UserEnumerationView> filteredDialogEnums) {
        this.filteredDialogEnums = filteredDialogEnums;
    }

    /** @return the dialogEnum */
    public UserEnumerationView getDialogEnum() {
        return dialogEnum;
    }
}
