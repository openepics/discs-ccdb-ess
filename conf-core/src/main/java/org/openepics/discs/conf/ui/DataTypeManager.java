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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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

    private List<UserEnumerationView> dataTypeViews;
    private transient List<UserEnumerationView> filteredDataTypesViews;
    private transient List<UserEnumerationView> selectedEnums;
    private transient List<UserEnumerationView> usedEnums;
    private transient UserEnumerationView editedEnum;
    private List<DataType> dataTypes;
    private List<String> builtInDataTypeNames;

    // * * * * * * * Add/modify dialog fields * * * * * * *
    private String name;
    private String description;
    private String definition;
    private boolean isEnumerationBeingAdded;

    private ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimpleEnumTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Enumerations";
        }

        @Override
        protected String getFileName() {
            return "enums";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description", "Definition");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<UserEnumerationView> exportData = filteredDataTypesViews == null
                    || filteredDataTypesViews.isEmpty()
                                ? dataTypeViews
                                : filteredDataTypesViews;
            for (final UserEnumerationView enumeration : exportData) {
                exportTable.addDataRow(enumeration.getName(), enumeration.getDescription(),
                        enumeration.getDefinitionAsString());
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
        final DataType existingDataType = dataTypeEJB.findByName(enumName);
        if ((editedEnum == null && existingDataType != null)
                || (editedEnum != null && existingDataType != null
                        && !editedEnum.getEnumeration().equals(existingDataType))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "Enumeration with the same name already exists."));
        }
    }

    private void refreshUserDataTypes() {
        dataTypes = ImmutableList.copyOf(dataTypeEJB.findAll());

        dataTypeViews = ImmutableList.copyOf(
                    // transform the List<DataType> into a List<UserEnumerationView>
                    Lists.transform(
                        // filter returns a Collection, transform works only on Lists
                        Lists.newArrayList(
                                // Omit all the built-in data types.
                                Collections2.filter(dataTypes,
                                        new Predicate<DataType>() {
                                            @Override
                                            public boolean apply(DataType input) {
                                                return !builtInDataTypeNames.contains(input.getName());
                                            }
                                        })
                        ), new Function<DataType, UserEnumerationView>() {
                                @Override
                                public UserEnumerationView apply(DataType input) {
                                    return new UserEnumerationView(input);
                                }
                            }
                    ));
    }

    /** This method clears all input fields used in the "Add enumeration" dialog. */
    public void prepareAddPopup() {
        name = null;
        description = null;
        definition = null;
        isEnumerationBeingAdded = true;
        editedEnum = null;
    }

    /** This method prepares the input fields used in the "Edit enumeration" dialog. */
    public void prepareModifyPopup() {
        Preconditions.checkState(isSingleEnumSelected());
        editedEnum = selectedEnums.get(0);
        name = editedEnum.getName();
        description = editedEnum.getDescription();
        definition = definitionsToMultiline(editedEnum.getDefinition());
        isEnumerationBeingAdded = false;
    }

    /** Method that saves a new enumeration definition, when user presses the "Save" button in the "Add new" dialog */
    public void onAdd() {
        final List<String> enumValues = multilineToDefinitions(definition);
        final DataType newEnum = new DataType(name, description, false, jsonDefinitionFromList(enumValues));
        dataTypeEJB.add(newEnum);
        editedEnum = null;
        refreshUserDataTypes();
    }

    /**
     * Method that saves the modified enumeration definition, when user presses the "Save" button in the
     * "Modify enumeration" dialog.
     */
    public void onModify() {
        Preconditions.checkNotNull(editedEnum);
        final DataType modifiedEnum = editedEnum.getEnumeration();
        final List<String> enumValues = multilineToDefinitions(definition);

        modifiedEnum.setName(name);
        modifiedEnum.setDescription(description);
        modifiedEnum.setDefinition(jsonDefinitionFromList(enumValues));
        dataTypeEJB.save(modifiedEnum);
        editedEnum = null;
        refreshUserDataTypes();
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
            if (dataTypeEJB.isDataTypeUsed(enumToDelete.getEnumeration(), true)) {
                usedEnums.add(enumToDelete);
            }
        }
    }

    /**
     * Method that deletes the user enumeration if that is allowed. Enumeration deletion is prevented if the enumeration
     * is used in some property value.
     */
    public void onDelete() {
        Preconditions.checkNotNull(selectedEnums);
        Preconditions.checkState(!selectedEnums.isEmpty());
        Preconditions.checkNotNull(usedEnums);
        Preconditions.checkState(usedEnums.isEmpty());

        int deletedEnums = 0;
        for (final UserEnumerationView enumToDelete : selectedEnums) {
            dataTypeEJB.delete(enumToDelete.getEnumeration());
            ++deletedEnums;
        }
        selectedEnums = null;
        usedEnums = null;
        refreshUserDataTypes();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Deleted " + deletedEnums + " enumerations.");
    }

    /** Validates the enumeration inputs. For new enumeration it verifies that all values contain only alphanumeric
     * characters. For modified enumerations it additional checks whether the enumeration is already used somewhere. If
     * not it can be redefined freely. If it is used, then the user can only add a new enumeration value to the
     * definition - he cannot remove an existing one.
     * @param ctx the context
     * @param component the component
     * @param value the value
     * @throws ValidatorException validation failed
     */
    public void enumValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                        "No value to parse."));
        }

        final List<String> enumDefs = multilineToDefinitions(value.toString());
        // check whether redefinition is possible and correct
        if (!isEnumerationBeingAdded && (editedEnum != null) && !isEnumModificationSafe(enumDefs)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                        "Enumeration already in use. Values can only be added."));
        }
    }

    private String definitionsToMultiline(List<String> definitionList) {
        final StringBuilder multiline = new StringBuilder();
        for( String enumValue : definitionList) {
            multiline.append(enumValue).append("\r\n");
        }
        return multiline.toString();
    }

    private List<String> multilineToDefinitions(String multiline) {
        final List<String> definitions = new ArrayList<>();
        try (Scanner scanner = new Scanner(multiline)) {

            int lines = 0;
            while (scanner.hasNext()) {
                String enumVal = scanner.nextLine().replaceAll("\\u00A0", " ").trim();
                if (!enumVal.isEmpty()) {
                    if (!enumVal.matches("^\\w*$")) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                Utility.MESSAGE_SUMMARY_ERROR,
                                "Enumeration value can only contain alphanumerical characters: " + enumVal));
                    }
                    // ignore multiple definitions of the same value
                    if (!definitions.contains(enumVal)) {
                        definitions.add(enumVal);
                        lines++;
                    }
                }
            }
            if (lines < 2) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "Enumeration definition must contain at least 2 values."));
            }
        }
        return definitions;
    }

    private boolean isEnumModificationSafe(List<String> newDefinition) {
        if (!dataTypeEJB.isDataTypeUsed(editedEnum.getEnumeration())) {
            return true;
        }
        // enumeration already used.
        // for each value in the old definition
        for (String enumValue : editedEnum.getDefinition()) {
            // check if it exists in the new definition
            if (!newDefinition.contains(enumValue)) {
                return false;
            }
        }
        return true;
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

    /** @return The name of the user defined enumeration */
    public String getName() {
        return name;
    }
    /** @param name The name of the user defined enumeration as set by the user through the UI */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }
    /** @param description the description to set */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the definition */
    public String getDefinition() {
        return definition;
    }
    /** @param definition the definition to set */
    public void setDefinition(String definition) {
        this.definition = definition;
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
}
