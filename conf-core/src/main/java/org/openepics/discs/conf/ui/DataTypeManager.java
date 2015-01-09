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
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.views.UserEnumerationView;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.core.Seds;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class DataTypeManager implements Serializable {
    private static final long serialVersionUID = -7538356350403365152L;

    @Inject private transient  DataTypeEJB dataTypeEJB;

    private List<UserEnumerationView> dataTypes;
    private List<UserEnumerationView> fileteredDataTypes;
    private UserEnumerationView selectedEnum;

    // * * * * * * * Add/modify dialog fields * * * * * * *
    private String name;
    private String description;
    private String definition;

    /**
     * Creates a new instance of DataTypeManager
     */
    public DataTypeManager() {
    }

    @PostConstruct
    public void init() {
        refreshUserDataTypes();
    }

    /**
     * @return A list of all {@link DataType} entities in the database.
     */
    public List<UserEnumerationView> getDataTypes() {
        return dataTypes;
    }

    public List<UserEnumerationView> getFileteredDataTypes() {
        return fileteredDataTypes;
    }

    public void setFileteredDataTypes(List<UserEnumerationView> fileteredDataTypes) {
        this.fileteredDataTypes = fileteredDataTypes;
    }

    public void refreshUserDataTypes() {
        List<DataType> allDataTypes = dataTypeEJB.findAll();

        final List<String> builtInDataTypeNames = new ArrayList<>();
        for (BuiltInDataType type : BuiltInDataType.values()) {
            builtInDataTypeNames.add(type.toString());
        }

        // transform the list of DataType into a list of UserEnumerationView
        dataTypes = Lists.transform(
                        // filter returns a collection, transform works only on Lists
                        Lists.newArrayList(
                                // Omit all the built-in data types.
                                Collections2.filter(allDataTypes,
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
                    );
    }

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

    public void onAdd() {
        final List<String> enumValues = multilineToDefinitions(definition);
        final DataType newEnum = new DataType(name, description, false, jsonDefinitionFromList(enumValues));
        dataTypeEJB.add(newEnum);
        refreshUserDataTypes();
    }

    public void onModify() {
        final DataType modifiedEnum = selectedEnum.getEnumeration();
        final List<String> enumValues = multilineToDefinitions(definition);

        modifiedEnum.setName(name);
        modifiedEnum.setDescription(description);
        modifiedEnum.setDefinition(jsonDefinitionFromList(enumValues));
        dataTypeEJB.save(modifiedEnum);
    }

    public void onDelete() {
        // TODO implement
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "No value to parse."));
        }

        List<String> enumDefs = multilineToDefinitions(value.toString());
        // check whether redefinition is possible and correct
        if ((selectedEnum != null) && !isEnumModificationSafe(enumDefs)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error",
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
        List<String> definitions = new ArrayList<>();
        try (Scanner scanner = new Scanner(multiline)) {

            int lines = 0;
            while (scanner.hasNext()) {
                String enumVal = scanner.nextLine().replaceAll("\\u00A0", " ").trim();
                if (!enumVal.isEmpty()) {
                    if (!enumVal.matches("^\\w*$")) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
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
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                        "Enumeration definition must contain at least 2 values."));
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
        JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);
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
     * @param selectedProperty The {@link Property} selected in the dialog (modify property dialog).
     */
    public void setSelectedEnumToModify(UserEnumerationView selectedEnum) {
        this.selectedEnum = selectedEnum;
        prepareModifyPopup();
    }

    public String getName() {
        return name;
    }

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
