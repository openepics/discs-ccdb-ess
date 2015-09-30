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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openepics.discs.conf.ui.common.ExcelImportUIHandlers.ImportFileStatistics;

import com.google.common.base.Preconditions;

/**
 * Skeleton for all data loaders.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public abstract class AbstractDataLoader implements DataLoader {

    protected static final String HDR_OPERATION = "OPERATION";
    protected static final int COL_INDEX_OPERATION = 0;

    public static final int DEFAULT_EXCEL_TAMPLATE_DATA_START_ROW = 8;

    /** The {@link DataLoaderResult} contains error information and state for the error loading process */
    protected DataLoaderResult result = new DataLoaderResult();

    /** Constant representing the index in the row data of the command field */
    private static final int COMMAND_INDEX = 0;

    /** Contextual data map */
    private Map<String, Object> contextualData;

    /**
     * Indexes of all relevant fields within a row, stateful.
     */
    protected Map<String, Integer> indicies = null;

    /** For stateful row processing, represents the data for the current row, private, exposed to sub-classes by
     * {@link #readCurrentRowCellForHeader(String)}
     */
    private List<String> currentRowData = null;

    @Override
    public int getImportDataStartIndex() {
        return DEFAULT_EXCEL_TAMPLATE_DATA_START_ROW;
    }

    @Override
    public abstract int getDataWidth();

    /**
     * Loads data from the input table of strings inputRows, into the database, gracefully handling errors.
     * Potential error messages and state are returned in the return value.
     *
     * @param inputRows a {@link List} of {@link Pair}s consisting of an integer representing excel row number
     * in left-hand position and a list of strings representing the cells for each column in that row
     * @param contextualData optional map of objects passed with string keys
     *
     * @return {@link DataLoaderResult} which represents error state &amp; information (or lack of)
     */
    @Override
    public DataLoaderResult loadDataToDatabase(List<Pair<Integer, List<String>>> inputRows,
            Map<String, Object> contextualData) {

        if (contextualData == null) {
            this.contextualData = new HashMap<>();
        } else {
            this.contextualData = contextualData;
        }

        init();

        int dataRows = 0;
        int createRows = 0;
        int updateRows = 0;
        int deleteRows = 0;
        int renameRows = 0;

        currentRowData = null;
        for (Pair<Integer, List<String>> row : inputRows) {
            result.resetRowError();

            result.setCurrentRowNumber(row.getLeft());
            currentRowData = row.getRight();

            if (DataLoader.CMD_END.equals(currentRowData.get(COMMAND_INDEX))) {
                break;
            } else {
                ++dataRows;

                final String command = checkCommandAndRequiredFields();
                if (command == null) {
                    continue;
                }

                assignMembersForCurrentRow();
                if (result.isRowError()) {
                    continue;
                }

                switch (command) {
                    case DataLoader.CMD_UPDATE:
                    case DataLoader.CMD_UPDATE_DEVICE:
                    case DataLoader.CMD_UPDATE_PROPERTY:
                    case DataLoader.CMD_UPDATE_DEVICE_TYPE:
                    case DataLoader.CMD_UPDATE_ENTITY:
                        handleUpdate(command);
                        ++updateRows;
                        break;
                    case DataLoader.CMD_DELETE:
                    case DataLoader.CMD_DELETE_DEVICE:
                    case DataLoader.CMD_DELETE_PROPERTY:
                    case DataLoader.CMD_DELETE_DEVICE_TYPE:
                    case DataLoader.CMD_DELETE_ENTITY:
                    case DataLoader.CMD_DELETE_RELATION:
                    case DataLoader.CMD_UNINSTALL:
                        handleDelete(command);
                        ++deleteRows;
                        break;
                    case DataLoader.CMD_RENAME:
                        handleRename();
                        ++renameRows;
                        break;
                    case DataLoader.CMD_CREATE:
                    case DataLoader.CMD_CREATE_DEVICE:
                    case DataLoader.CMD_CREATE_DEVICE_TYPE:
                    case DataLoader.CMD_CREATE_PROPERTY:
                    case DataLoader.CMD_CREATE_ENTITY:
                    case DataLoader.CMD_CREATE_RELATION:
                    case DataLoader.CMD_INSTALL:
                        handleCreate(command);
                        ++createRows;
                        break;
                    default:
                        result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION);
                }
            }
        }
        result.setImportFileStatistics(new
                            ImportFileStatistics(dataRows, createRows, updateRows, deleteRows, renameRows));
        return result;
    }

    /**
     * <p>
     * An method invoked from {@link #loadDataToDatabase(List, Map)} prior data-loading
     * is initiated.
     * </p>
     * <p>
     * Should be used by sub-classes to initialize shared state. In sub-classes {@link #getFromContext(String)} can
     * be called to get context-specific objects passed in {@link #loadDataToDatabase(List, Map)}
     * </p>
     */
    protected void init() {
        result.clear();
        setUpIndexesForFields();
    }

    /**
     * Sub-classes should implement list to get a set of required column entries
     *
     * @return a set containing column names that are required
     */
    protected abstract Set<String> getRequiredColumnNames();

    /**
     * Sub-classes should implement this abstract method to define the name of the unique column (a column used do
     * uniquely identify a data row, for example a column containing entity names or serial numbers)
     *
     * If the method returns null, there is no unique column in the spreadsheet.
     *
     * A unique column has special constraints around it (i.e. it must have a value)..
     *
     * @return the header column name of the unique column
     */
    protected abstract @Nullable Integer getUniqueColumnIndex();


    /**
     * Invoked by {@link #loadDataToDatabase(List, Map)} for sub-classes to initialize row-bound
     * state (class fields) from row-data.
     */
    protected abstract void assignMembersForCurrentRow();

    /**
     * <p>
     * Handle a row that contains an <code>UPDATE</code> command.
     * </p>
     * <p>
     * <b>Precondition:</b> {@link #assignMembersForCurrentRow()} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     * </p>
     * @param actualCommand the actual command used in update (update entity itself or its property)
     */
    protected abstract void handleUpdate(String actualCommand);

    /**
     * <p>
     * Handle a row that contains a <code>DELETE</code> command.
     * </p>
     * <p>
     * <b>Precondition:</b> {@link #assignMembersForCurrentRow()} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     * </p>
     * @param actualCommand the actual command used in delete (delete entity itself or its property)
     */
    protected abstract void handleDelete(String actualCommand);

    /**
     * <p>
     * Handle a row that contains a <code>RENAME</code> command.
     * </p>
     * <p>
     * <b>Precondition:</b> {@link #assignMembersForCurrentRow()} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     * </p>
     */
    protected abstract void handleRename();

    /**
     * <p>
     * Handle a row that contains a <code>CREATE</code> command.
     * </p>
     * <p>
     * <b>Precondition:</b> {@link #assignMembersForCurrentRow()} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     * </p>
     * @param actualCommand the actual command used in create (create entity itself or its property)
     */
    protected abstract void handleCreate(String actualCommand);


    /**
     * Sub-classes should use this to get data from the context passed from caller.
     *
     * @param key the key
     * @return contextual data
     */
    protected Object getFromContext(String key) {
        return contextualData.get(key);
    }

    /**
     * Checks the command field and availability of necessary data in the row cells
     *
     * @return the {@link String} command or <code>null</code> if checks failed
     */
    private String checkCommandAndRequiredFields() {
        Preconditions.checkNotNull(indicies);

        final String command = currentRowData.get(COMMAND_INDEX);
        if (StringUtils.isEmpty(command)) {
            result.addRowMessage(ErrorMessage.COMMAND_IS_MISSING, HDR_OPERATION);
            return null;
        }

        final @Nullable Integer uniqueIndex = getUniqueColumnIndex();
        final Set<String> requiredColumns = getRequiredColumnNames();

        for (Entry<String, Integer> indexEntry : indicies.entrySet()) {
            final String columnName = indexEntry.getKey();
            final Integer index = indexEntry.getValue();
            Preconditions.checkNotNull(index);

            // Check if data is missing for given conditions
            if ((uniqueIndex != null) && uniqueIndex.equals(index)) {
                if (currentRowData.get(index) == null) {
                    result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, columnName);
                }
            } else {
                if (requiredColumns.contains(columnName) && currentRowData.get(index) == null
                        && !DataLoader.CMD_RENAME.equals(command) && !DataLoader.CMD_DELETE.equals(command)) {
                    result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, columnName);
                }
            }
        }
        if (result.isRowError()) {
            return null;
        }

        return command;
    }

    /**
     * Sets up a map of row data indices based on the template header row data. This method is implemented in the
     * actual data loader implementation.
     */
    protected abstract void setUpIndexesForFields();

    /**
     * During row processing, returns a {@link String} cell within the row that belongs to given column
     *
     * @param index The index of the column to read
     * @return the contents of the appropriate column cell within the current row
     */
    protected String readCurrentRowCellForHeader(int index) {
        return currentRowData.get(index);
    }

    /**
     * Handles all errors that happen if the user is not authorized to perform the operation
     *
     * @param logger    Instance of logger to log the exception to server log
     * @param e         Exception that was caught in data loader implementation
     */
    protected void handleLoadingError(Logger logger, Exception e) { // NOSONAR
        logger.log(Level.FINE, e.getMessage(), e);
        if (e.getCause() instanceof org.openepics.discs.conf.security.SecurityException) {
            result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, HDR_OPERATION);
        } else {
            result.addRowMessage(ErrorMessage.SYSTEM_EXCEPTION);
        }
    }
}
