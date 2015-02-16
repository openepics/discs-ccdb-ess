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

import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ent.DataType;
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
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class DataTypeManager implements Serializable {
    private static final long serialVersionUID = -7538356350403365152L;

    @Inject private transient  DataTypeEJB dataTypeEJB;

    private List<UserEnumerationView> dataTypeViews;
    private transient List<UserEnumerationView> filteredDataTypesViews;
    private transient UserEnumerationView selectedEnum;
    private List<DataType> dataTypes;
    private List<String> builtInDataTypeNames;


    // * * * * * * * Add/modify dialog fields * * * * * * *
    private String name;
    private String description;
    private String definition;

    /**
     * Creates a new instance of DataTypeManager
     */
    public DataTypeManager() {
    }


    /**
     * Java EE post construct life-cycle method.
     */
    @PostConstruct
    public void init() {
        Builder<String> builtInDataTypeBuilder = ImmutableList.builder();
        for (BuiltInDataType type : BuiltInDataType.values()) {
            builtInDataTypeBuilder.add(type.toString());
        }
        builtInDataTypeNames = builtInDataTypeBuilder.build();

        refreshUserDataTypes();
    }

    /**
     * @return A list of all {@link DataType} entities in the database that are user defined enumerations.
     * The list members are wrapped into the {@link UserEnumerationView}.
     */
    public List<UserEnumerationView> getDataTypeViews() {
        return dataTypeViews;
    }

    /**
     * @return A list of all {@link DataType} entities in the database.
     */
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
        if ((selectedEnum == null && existingDataType != null)
                || (selectedEnum != null && existingDataType != null
                        && !selectedEnum.getEnumeration().equals(existingDataType))) {
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

    /**
     * This method clears all input fields used in the "Add enumeration" dialog.
     */
    public void prepareAddPopup() {
        selectedEnum = null;
        name = null;
        description = null;
        definition = null;
    }

    private void prepareModifyPopup() {
        name = selectedEnum.getName();
        description = selectedEnum.getDescription();
        definition = definitionsToMultiline(selectedEnum.getDefinition());
    }

    /**
     * Method that saves a new enumeration definition, when user presses the "Save" button in the "Add new" dialog.
     */
    public void onAdd() {
        final List<String> enumValues = multilineToDefinitions(definition);
        final DataType newEnum = new DataType(name, description, false, jsonDefinitionFromList(enumValues));
        dataTypeEJB.add(newEnum);
        refreshUserDataTypes();
    }

    /**
     * Method that saves the modified enumeration definition, when user presses the "Save" button in the
     * "Modify enumeration" dialog.
     */
    public void onModify() {
        final DataType modifiedEnum = selectedEnum.getEnumeration();
        final List<String> enumValues = multilineToDefinitions(definition);

        modifiedEnum.setName(name);
        modifiedEnum.setDescription(description);
        modifiedEnum.setDefinition(jsonDefinitionFromList(enumValues));
        dataTypeEJB.save(modifiedEnum);
    }

    /**
     * Method that deletes the user enumeration if that is allowed. Enumeration deletion is prevented if the enumeration
     * is used in some property value.
     */
    public void onDelete() {
        Preconditions.checkNotNull(selectedEnum);
        final DataType enumerationDataType = selectedEnum.getEnumeration();
        if (dataTypeEJB.isDataTypeUsed(enumerationDataType)) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "The enumeration data type cannot be deleted because it is in use.");
        } else {
            dataTypeEJB.delete(enumerationDataType);
            refreshUserDataTypes();
        }
    }

    /** Validates the enumeration inputs. For new enumeration it verifies that all values contain only alphanumeric
     * characters. For modified enumerations it additional checks whether the enumeration is already used somewhere. If
     * not it can be redefined freely. If it is used, then the user can only add a new enumeration value to the
     * definition - he cannot remove an existing one.
     * @param ctx
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void enumValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                        "No value to parse."));
        }

        final List<String> enumDefs = multilineToDefinitions(value.toString());
        // check whether redefinition is possible and correct
        if ((selectedEnum != null) && !isEnumModificationSafe(enumDefs)) {
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
        if (!dataTypeEJB.isDataTypeUsed(selectedEnum.getEnumeration())) {
            return true;
        }
        // enumeration already used.
        // for each value in the old definition
        for (String enumValue : selectedEnum.getDefinition()) {
            // check if it exists in the new definition
            if (!newDefinition.contains(enumValue)) {
                return false;
            }
        }
        return true;
    }

    private String jsonDefinitionFromList(List<String> definitionValues) {
        final SedsEnum testEnum = Seds.newFactory().newEnum(definitionValues.get(0), definitionValues.toArray(new String[] {}));
        final JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);
        return  jsonEnum.toString();
    }

    /* * * * * * * * * * * * * * * * * getters and setters * * * * * * * * * * * * * * * * */

    /**
     * @return The {@link DataType} selected in the dialog.
     */
    public UserEnumerationView getSelectedEnum() {
        return selectedEnum;
    }
    /**
     * @param selectedEnum The {@link DataType} selected in the dialog.
     */
    public void setSelectedEnum(UserEnumerationView selectedEnum) {
        this.selectedEnum = selectedEnum;
    }

    /**
     * @return The {@link DataType} selected in the dialog (modify property dialog).
     */
    public UserEnumerationView getSelectedEnumToModify() {
        return selectedEnum;
    }

    /**
     * @param selectedEnum The user enumeration selected in the dialog (modify property dialog).
     */
    public void setSelectedEnumToModify(UserEnumerationView selectedEnum) {
        this.selectedEnum = selectedEnum;
        prepareModifyPopup();
    }

    /**
     * @return The name of the user defined enumeration.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the user defined enumeration as set by the user through the UI.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
