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
package org.openepics.discs.conf.dl.common;

/**
 * This enumeration contains all possible error messages for data loaders.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 */
public enum ErrorMessage {
    NAME_ALREADY_EXISTS("Entity with this name already exists"),
    NOT_AUTHORIZED("You are not authorized to perform this action"),
    COMMAND_IS_MISSING("Command is missing"),
    COMMAND_NOT_VALID("Command is not valid"),
    RENAME_MISFORMAT("Rename syntax is not correct"),
    ENTITY_NOT_FOUND("Entity to be affected was not found"),
    UNKNOWN_SLOT_RELATION_TYPE("Slot Relation specified is of unknown type"),
    REQUIRED_FIELD_MISSING("Required field is missing"),
    SHOULD_BE_NUMERIC_VALUE("The value of this field should be numeric"),
    SHOULD_BE_BOOLEAN_VALUE("The value of this field should be TRUE or FALSE"),
    CANT_ADD_PARENT_TO_ROOT("Can't add parent to root node"),
    INSTALL_CANT_CONTAIN_CONTAINER("Installation slot can't be the parent of container"),
    SLOT_RELATIONSHIP_NOT_FOUND("This slot relationship does not exist"),
    POWER_RELATIONSHIP_RESTRICTIONS("\"Powers\" slot relationship can only be set up between two installation slots"),
    CONTROL_RELATIONSHIP_RESTRICTIONS("\"Controls\" slot relationship can only be set up between two installation slots"),
    ORPHAN_SLOT("Newly added slot was not assigned a parent."),
    SAME_CHILD_AND_PARENT("Loop relationship. Child and parent can't be the same slot"),
    PROPERTY_NOT_FOUND("Property with this name was not found"),
    PROPERTY_TYPE_INCORRECT("The command is incorrect for this type of property"),
    SYSTEM_EXCEPTION("Internal error occured"),
    MODIFY_IN_USE("Cannot modify entity field because it is in use."),
    DELETE_IN_USE("Cannot delete entity because it is in use."),
    UNIQUE_INCORRECT("The value of the uniquness is incorrect."),
    ENUM_NOT_ENOUGH_ELEMENTS("The enumeration needs at least two elements."),
    ENUM_INVALID_CHARACTERS("Enumeration value can only contain alphanumerical characters.");

    private final String text;

    /**
     * @param text
     */
    private ErrorMessage(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}


