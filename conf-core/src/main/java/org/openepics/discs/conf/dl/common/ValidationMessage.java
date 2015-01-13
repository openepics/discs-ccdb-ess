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

import java.util.Objects;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * This holds one piece of validation information tied to a location.
 *
 * @author Sunil Sah <sunil.sah@cosylab.com>
 */
public class ValidationMessage {
    private ErrorMessage message;

    private Integer row;
    private String column;
    private String fileName;
    private String orphanSlotName;

    /**
     * Constructs the message.
     *
     * @param fileName the name of the file the validation was performed for
     */
    public ValidationMessage(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Construct the message with location information and information of {@link EntityType} and {@link EntityTypeOperation}
     *
     * @param message the message enumeration
     * @param row
     * @param column
     */
    public ValidationMessage(ErrorMessage message, Integer row,
                             String column) {
        super();
        this.message = message;
        this.row = row;
        this.column = column;
    }

    public void setOrphanSlotName(String orphanSlotName) {
        this.orphanSlotName = orphanSlotName;
    }

    /** @return the message enum */
    public ErrorMessage getMessage() {
        return message;
    }

    /** @return the row label, or null if not specified */
    public Integer getRow() {
        return row;
    }

    /** @return the column label, or null if not specified */
    public String getColumn() {
        return column;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (fileName != null) {
            builder.append("In file: " + fileName);
        } else {
            if (orphanSlotName != null) {
                builder.append("Found orphan slot ");
                builder.append(orphanSlotName);
                builder.append(", ");
            }
            if (getRow() != null) {
                builder.append("Row ");
                builder.append(getRow());
            }
            if (getRow() != null && getColumn() != null) {
                builder.append(", ");
            }
            if (getColumn() != null) {
                builder.append("Column ");
                builder.append(getColumn());
            }
            builder.append(": ");
            builder.append(getMessage().toString());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        final ValidationMessage valMesToCompare;
        try {
            valMesToCompare = (ValidationMessage) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return Objects.equals(this.fileName, valMesToCompare.fileName) &&
                Objects.equals(this.column, valMesToCompare.column) &&
                Objects.equals(this.message, valMesToCompare.message) &&
                Objects.equals(this.orphanSlotName, valMesToCompare.orphanSlotName) &&
                Objects.equals(this.row, valMesToCompare.row);
    }
}
