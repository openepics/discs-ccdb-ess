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
package org.openepics.discs.conf.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Preconditions;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class UserEnumerationView implements Serializable {
    private static final long serialVersionUID = 1L;

    private final DataType enumeration;
    private String usedBy;
    private final boolean enumerationBeingAdded;
    private List<String> definitionList;
    private boolean used;

    /**
     *  Creates an new view object that will expose the user enumeration to the UI layer.
     *
     * @param enumeration The user defined enumeration to base the view object on
     */
    public UserEnumerationView(DataType enumeration) {
        Preconditions.checkArgument(!Preconditions.checkNotNull(enumeration).isScalar());
        this.enumeration = enumeration;
        definitionList = Conversion.prepareEnumSelections(enumeration);
        enumerationBeingAdded = false;
    }

    /** Default constructor. Creates view based on fresh (non-existing) {@link DataType} */
    public UserEnumerationView() {
        enumeration = new DataType();
        enumerationBeingAdded = true;
        definitionList = new ArrayList<>();
    }

    /** @return The name of the user defined enumeration */
    @NotNull
    @Size(min = 1, max = 64, message = "Name can have at most 64 characters.")
    public String getName() {
        return enumeration.getName();
    }

    /** @return the user enumeration description */
    public String getDescription() {
        return enumeration.getDescription();
    }

    /** @return the {@link List} of possible user enumeration values */
    public List<String> getDefinitionList() {
        return definitionList;
    }

    /** @return all possible enumeration values as a string - to display in the UI */
    public String getDisplayDefinition() {
        return '[' + String.join(", ",  definitionList) + ']';
    }

    public String getMultilineDefinition() {
        return String.join("\r\n", definitionList) + "\r\n";
    }

    /** @return the enumeration data type entity */
    public DataType getEnumeration() {
        return enumeration;
    }

    /** @return the usedBy */
    public String getUsedBy() {
        return usedBy;
    }

    /** @param usedBy the usedBy to set */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    /** @return the enumerationBeingAdded */
    public boolean isEnumerationBeingAdded() {
        return enumerationBeingAdded;
    }

    /** @param name The name of the user defined enumeration as set by the user through the UI */
    public void setName(String name) {
        enumeration.setName(name);
    }

    /** @param description the description to set */
    public void setDescription(String description) {
        enumeration.setDescription(description);
    }

    /** @param definition the definition to set */
    public void setMultilineDefinition(String definition) {
        definitionList = multilineToDefinitions(definition);
    }

    private List<String> multilineToDefinitions(String multiline) throws ValidatorException {
        final List<String> definitions = new ArrayList<>();
        try (Scanner scanner = new Scanner(multiline)) {

            int lines = 0;
            while (scanner.hasNext()) {
                String enumVal = scanner.nextLine().replaceAll("\\u00A0", " ").trim();
                if (!enumVal.isEmpty()) {
                    if (!enumVal.matches(DataType.ALLOWED_ENUM_CHARS_REGEX)) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                UiUtility.MESSAGE_SUMMARY_ERROR,
                                "Enumeration value can only contain alphanumerical characters, hyphens and "
                                        + "underscores: " + enumVal));
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
                        UiUtility.MESSAGE_SUMMARY_ERROR, "Enumeration definition must contain at least 2 values."));
            }
        }
        return definitions;
    }

    /** @return the used */
    public boolean isUsed() {
        return used;
    }

    /** @param used the used to set */
    public void setUsed(boolean used) {
        this.used = used;
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                                        "No value to parse."));
        }

        final List<String> enumDefs = multilineToDefinitions(value.toString());
        // check whether redefinition is possible and correct
        if (!enumerationBeingAdded && used) {
            if (!isEnumModificationSafe(enumDefs)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                        UiUtility.MESSAGE_SUMMARY_ERROR,
                                                        "Enumeration already in use. Values can only be added."));
            }
        }
    }

    private boolean isEnumModificationSafe(List<String> newDefinition) {
        // enumeration already used.
        // for each value in the old definition
        for (String enumValue : definitionList) {
            // check if it exists in the new definition
            if (!newDefinition.contains(enumValue)) {
                return false;
            }
        }
        return true;
    }
}
