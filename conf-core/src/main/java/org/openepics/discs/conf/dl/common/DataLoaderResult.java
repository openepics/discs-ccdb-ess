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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * This represents a result of load operation, consisting of the error status,
 * load {@link ValidationMessage}s, and the list of objects affected by the load.
 *
 * @author Sunil Sah <sunil.sah@cosylab.com>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
public class DataLoaderResult {
    private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
    private Map<String, Object> contextualData = new HashMap<>();

    /** Per row-processing error status, to be reset for each new row */
    private boolean rowError = false;

    /** Global error status */
    private boolean error = false;

    /** Current row tracking */
    private int currentRowNumber;

    /** clears the state of the data result */
    public void clear() {
        rowError = error = false;
        messages = new ArrayList<ValidationMessage>();
    }

    /** @return the messages of this report */
    public List<ValidationMessage> getMessages() {
        return messages;
    }

    /** @return the report error state */
    public boolean isError() {
        return error;
    }

    /** @return the report error state for current row*/
    public boolean isRowError() {
        return rowError;
    }

    /**
     * Adds new message to the messages list
     *
     * @param message
     * @param row
     * @param column
     * @param operation
     * @param entity
     */
    private void addMessageInternal(ErrorMessage message, Integer row, String column,
            EntityTypeOperation operation, EntityType entity) {
        messages.add(new ValidationMessage(message, row, column, operation, entity));
    }

    /**
     * Adds new message to the messages list
     *
     * @param fileName
     */
    private void addMessageInternal(String fileName) {
        messages.add(new ValidationMessage(fileName));
    }

    public void addGlobalMessage(ErrorMessage message, String column,
            EntityTypeOperation operation, EntityType entity) {
        error = true;
        addMessageInternal(message, currentRowNumber, column, operation, entity);
    }

    public void addGlobalMessage(ErrorMessage message) {
        error = true;
        addMessageInternal(message, currentRowNumber, null, null, null);
    }

    public void addGlobalMessage(ErrorMessage message, String column) {
        error = true;
        addMessageInternal(message, currentRowNumber, column, null, null);
    }

    public void addRowMessage(ErrorMessage message, String column,
            EntityTypeOperation operation, EntityType entity) {
        rowError = true;
        addMessageInternal(message, currentRowNumber, column, operation, entity);
    }

    public void addRowMessage(ErrorMessage message, String column) {
        rowError = true;
        addMessageInternal(message, currentRowNumber, column, null, null);
    }

    public void addRowMessage(ErrorMessage message) {
        rowError = true;
        addMessageInternal(message, currentRowNumber, null, null, null);
    }

    public void resetRowError() {
        rowError = false;
    }

    public int getCurrentRowNumber() {
        return currentRowNumber;
    }

    public void setCurrentRowNumber(int currentRowNumber) {
        this.currentRowNumber = currentRowNumber;
    }

    public Map<String, Object> getContextualData() {
        return contextualData;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (ValidationMessage message: messages) {
            builder.append(message.toString());
            builder.append("\n");
        }

        if (isError()) {
            builder.append("There were some errors.\n");
        }
        return builder.toString();
    }


}
