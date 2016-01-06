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
import java.util.Set;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.ImportFileStatistics;

import com.google.common.base.Preconditions;

/**
 * This represents a result of load operation, consisting of the error status,
 * load {@link ValidationMessage}s, and the list of objects affected by the load.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class DataLoaderResult {

    /**
     * A key for {@link DataLoaderResult#getContextualData()} that will hold a {@link Set} of {@link Slot}s
     */
    public static final String CTX_NEW_SLOTS = "CTX_NEW_SLOTS";
    /**
     * A key for {@link DataLoaderResult#getContextualData()} that will hold a {@link Set} of {@link Slot}s that have
     * become children in a relationship during the new data loading.
     */
    public static final String CTX_NEW_SLOT_PAIR_CHILDREN = "CTX_NEW_SLOT_PAIR_CHILDREN";

    private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
    private List<ValidationMessage> filteredMessages;
    private Map<String, Object> contextualData = new HashMap<>();

    /** Per row-processing error status, to be reset for each new row */
    private boolean rowError = false;

    /** Global error status */
    private boolean error = false;

    /** Current row tracking */
    private int currentRowNumber;

    private ImportFileStatistics importFileStatistics;

    /** clears the state of the data result */
    public void clear() {
        rowError = error = false;
        messages = new ArrayList<>();
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
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        messages.add(new ValidationMessage(fileName));
    }

    /**
     * Adds new message to the messages list
     *
     * @param message the message
     * @param row the row
     * @param column the column
     * @param operation the operation
     * @param entity the entity
     */
    private void addMessageInternal(ErrorMessage message, Integer row, String column) {
        error = true;
        messages.add(new ValidationMessage(message, row, column));
    }

    /**
     * Adds an error message to the message list that is not related to any Excel spreadsheet row.
     *
     * @param message the error message
     */
    public void addGlobalMessage(ErrorMessage message) {
        addMessageInternal(message, currentRowNumber, null);
    }

    /**
     * Adds an error message to the message list that is not related to any Excel spreadsheet row.
     *
     * @param message the error message
     * @param column the column containing the error
     */
    public void addGlobalMessage(ErrorMessage message, String column) {
        addMessageInternal(message, currentRowNumber, column);
    }

    /**
     * Adds an error message to the message list that is related to the current Excel spreadsheet row.
     *
     * @param message the error message
     * @param column the column containing the error
     */
    public void addRowMessage(ErrorMessage message, String column) {
        rowError = true;
        addMessageInternal(message, currentRowNumber, column);
    }

    /**
     * Adds an error message to the message list that is related to the current Excel spreadsheet row.
     *
     * @param message the error message
     */
    public void addRowMessage(ErrorMessage message) {
        rowError = true;
        addMessageInternal(message, currentRowNumber, null);
    }

    /**
     * An error message indicating that the {@link Slot} does not have a parent defined (Contains relationship)
     *
     * @param slotName the name of the slot
     */
    public void addOrphanSlotMessage(final String slotName) {
        final ValidationMessage orphanSlotMessage = new ValidationMessage(ErrorMessage.ORPHAN_SLOT, null, null);
        orphanSlotMessage.setOrphanSlotName(slotName);
        messages.add(orphanSlotMessage);
        error = true;
    }

    /** Reset the status of the row error. Called before processing a new row. */
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

    /**
     * Adds message of another data loader result list to this object.
     *
     * @param loaderResult the loader result that contains additional messages
     */
    public void copyDataLoaderResult(DataLoaderResult loaderResult) {
        Preconditions.checkNotNull(loaderResult);
        this.messages.addAll(loaderResult.getMessages());
        this.error = this.error || loaderResult.isError();
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

    public void setImportFileStatistics(ImportFileStatistics importFileStatistics) {
        this.importFileStatistics = importFileStatistics;
    }

    /** @return the statistics of the import, or <code>0</code> statistics in case of an error */
    public ImportFileStatistics getImportFileStatistics() {
        if (error || importFileStatistics == null) {
            return new ImportFileStatistics();
        }
        return importFileStatistics;

    }

    /** @return the filteredMessages */
    public List<ValidationMessage> getFilteredMessages() {
        return filteredMessages;
    }

    /** @param filteredMessages the filteredMessages to set */
    public void setFilteredMessages(List<ValidationMessage> filteredMessages) {
        this.filteredMessages = filteredMessages;
    }
}
