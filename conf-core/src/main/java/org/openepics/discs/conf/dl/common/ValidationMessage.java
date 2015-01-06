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
    private EntityTypeOperation operation;
    private EntityType entity;
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
     * @param operation
     * @param entity
     */
    public ValidationMessage(ErrorMessage message, Integer row,
                             String column, EntityTypeOperation operation, EntityType entity) {
        super();
        this.message = message;
        this.row = row;
        this.column = column;
        this.operation = operation;
        this.entity = entity;
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

    /** @return operation on entity */
    public EntityTypeOperation getOperation() {
        return operation;
    }

    /** @return entity */
    public EntityType getEntity() {
        return entity;
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
            if ((getRow() != null || getColumn() != null) && (getEntity() != null || getOperation() != null)) {
                builder.append(", ");
            }
            if (getEntity() != null) {
                builder.append("Entity ");
                builder.append(getEntity().name());
            }
            if ((getRow() != null || getColumn() != null || getEntity() != null) && getOperation() != null) {
                builder.append(", ");
            }
            if (getOperation() != null) {
                builder.append("Operation ");
                builder.append(getOperation().name());
            }
            if (getRow() != null || getColumn() != null || getEntity() != null || getOperation() != null) {
                builder.append(": ");
            }
            builder.append(getMessage().toString());
        }
        return builder.toString();
    }
}
