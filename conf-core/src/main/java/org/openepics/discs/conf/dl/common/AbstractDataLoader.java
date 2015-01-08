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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Skeleton for all data loaders.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public abstract class AbstractDataLoader implements DataLoader {
    // Command string constants
    public static final String CMD_HEADER = "HEADER";
    public static final String CMD_UPDATE = "UPDATE";
    public static final String CMD_DELETE = "DELETE";
    public static final String CMD_RENAME = "RENAME";
    public static final String CMD_END = "END";

    /** The {@link DataLoaderResult} contains error information and state for the error loading process */
    protected DataLoaderResult result = new DataLoaderResult();

    /** Constant representing the index in the row data of the command field */
    private final int COMMAND_INDEX = 0;

    /** Contextual data map */
    private Map<String, Object> contextualData;

    /**
     * Indexes of all relevant fields within a row, stateful, always should be refreshed from within
     * {@link AbstractDataLoader}{@link #updateHeaderRowAndIndexes(Pair)}. Changes when new headers are present
     * ("HEADER" command appears)
     */
    private Map<String, Integer> indicies = null;

    /**
     * Indexes of all unknown (probably property) fields. Changes when new headers are present
     * ("HEADER" command appears)
     */

    private Map<String, Integer> propertyIndicies = null;

    /** For stateful row processing, represents the data for the current row, private, exposed to sub-classes by
     * {@link AbstractDataLoader}{@link #readCurrentRowCellForHeader(String)}
     */
    private List<String> currentRowData = null;

    /**
     * Loads data from the input table of strings inputRows, into the database, gracefully handling errors.
     * Potential error messages and state are returned in the return value.
     *
     * @param inputRows a {@link List} of {@link Pair}s consisting of an integer representing excel row number
     * in left-hand position and a list of strings representing the cells for each column in that row
     * @param contextualData @see {@link DataLoader}{@link #loadDataToDatabase(List, Map)}
     *
     * @return {@link DataLoaderResult} which represents error state & information (or lack of)
     */
    @Override
    public DataLoaderResult loadDataToDatabase(List<Pair<Integer, List<String>>> inputRows,
            Map<String, Object> contextualData) {

        if (contextualData == null) {
            this.contextualData = new HashMap<>();
        } else {
            this.contextualData = new HashMap<>(contextualData);
        }

        init();

        // First row must be a header, updateHeaderRowAndIndexes checks this...
        result.setCurrentRowNumber(inputRows.get(0).getLeft());
        if (!updateHeaderRowAndIndexes(inputRows.get(0))) return result;

        currentRowData = null;
        for (Pair<Integer, List<String>> row : inputRows.subList(1, inputRows.size())) {
            result.resetRowError();

            result.setCurrentRowNumber(row.getLeft());
            currentRowData = row.getRight();

            if ( CMD_HEADER.equals(currentRowData.get(COMMAND_INDEX)) ) {
                if (!updateHeaderRowAndIndexes(row)) return result;
            } else if ( CMD_END.equals(currentRowData.get(COMMAND_INDEX)) ) {
                break;
            } else {
                final String command = checkCommandAndRequiredFields();
                if (command==null) continue;

                assignMembersForCurrentRow();
                if (result.isRowError()) continue;

                switch (command) {
                case CMD_UPDATE:
                    handleUpdate();
                    break;
                case CMD_DELETE:
                    handleDelete();
                    break;
                case CMD_RENAME:
                    handleRename();
                    break;
                default:
                    result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, CMD_HEADER);
                }
            }
        }

        return result;
    }

    /**
     * An method invoked from {@link AbstractDataLoader}{@link #loadDataToDatabase(List)} prior data-loading
     * is initiated.
     *
     * Should be used by sub-classes to initialize shared state.
     *
     * {@link AbstractDataLoader}{@link #getFromContext(String)} can be called to get context-specific objects passed in
     * {@link AbstractDataLoader}{@link #loadDataToDatabase(List, Map)}
     */
    protected void init() {
        result.clear();
        indicies = propertyIndicies = null;
    }

    /**
     * Sub-classes should implement this and get a list of known column names (headers) for the data loader
     *
     * @return list of column names used by the data loader
     */
    protected abstract List<String> getKnownColumnNames();

    /**
     * Sub-classes should implement list to get a set of required column entries
     *
     * @return a set containing column names that are required
     */
    protected abstract Set<String> getRequiredColumnNames();

    /**
     * Sub-clases should implement this abstract method to define the name of the unique column (a column used do
     * uniquely identify a data row, for example a column containing entity names or serial numbers)
     *
     * If the method returns null, there is no unique column in the spreadsheet.
     *
     * A unique column has special constriants around it (i.e. it must have a value)..
     *
     * @return the header column name of the unique column
     */
    protected abstract String getUniqueColumnName();


    /**
     * A boolean method specifying whether to index unknown column names (which might be property names for example)
     * Sub-classes should override the method to change the default behavior which is, not to build this map.
     *
     * @return whether the data-loader should build a Map containing indexes of unknown (probably property) columns
     */
    protected boolean indexPropertyColumns() { return false; }

    /**
     * Invoked by {@link AbstractDataLoader}{@link #loadDataToDatabase(List)} for sub-classes to initialize row-bound
     * state (class fields) from row-data.
     *
     */
    protected abstract void assignMembersForCurrentRow();

    /**
     * Handle a row that contains an update command.
     *
     * Precondition: {@link AbstractDataLoader}{@link #assignMembers(List)} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     *
     */
    protected abstract void handleUpdate();

    /**
     * Handle a row that contains a delete command.
     *
     * Precondition: {@link AbstractDataLoader}{@link #assignMembers(List)} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     *
     */
    protected abstract void handleDelete();

    /**
     * Handle a row that contains a rename command.
     *
     * Precondition: {@link AbstractDataLoader}{@link #assignMembers(List)} has been invoked. This gives chance
     * to the sub-class to extract row data for the call to this method.
     *
     */
    protected abstract void handleRename();

    /**
     * Sub-classes should use this to get data from the context passed from caller.
     *
     * @param key
     * @return
     */
    protected Object getFromContext(String key) {
        return contextualData.get(key);
    }

    /**
     * Checks if there are multiple occurrences of same header entry in the header
     *
     * @param headerRow {@link List} the header row containing the header command and other columns
     *
     * @return <code>true</code> if no duplicates in header
     */
    private boolean checkForDuplicateHeaderEntries(List<String> headerRow) {
        final Set<String> headerEntriesSet = new HashSet<>();

        for (String headerEntry : headerRow.subList(1, headerRow.size())) {
            if (StringUtils.isEmpty(headerEntry)) continue;

            if (headerEntriesSet.contains(headerEntry)) {
                result.addGlobalMessage(ErrorMessage.DUPLICATES_IN_HEADER, headerEntry);
            } else {
                headerEntriesSet.add(headerEntry);
            }
        }

        return !result.isError();
    }

    /**
     * Updates row internal state for row indices and checks for errors
     *
     * @param inputRow the row with header
     * @return <code>false</code> if an error occurred
     */
    private boolean updateHeaderRowAndIndexes(Pair<Integer, List<String>> inputRow) {
        final List<String> headerRow = inputRow.getRight();

        if (!CMD_HEADER.equals(headerRow.get(COMMAND_INDEX))) {
            result.addGlobalMessage(ErrorMessage.HEADER_ROW_EXPECTED);
            return false;
        }

        if (!checkForDuplicateHeaderEntries(headerRow))
            return false;

        indicies=setUpIndexesForFields(headerRow);
        propertyIndicies = null;
        if (indicies!=null && indexPropertyColumns()) {
            propertyIndicies = setUpIndexesForProperties(headerRow, indicies);
            return propertyIndicies != null;
        } else {
            return indicies != null;
        }
    }

    /**
     * Checks the command field and availability of necessary data in the row cells
     *
     * @return the {@link String} command or <code>null</code> if checks failed
     */
    private String checkCommandAndRequiredFields() {
        Preconditions.checkNotNull(indicies);

        final String command = currentRowData.get(COMMAND_INDEX);
        if ( StringUtils.isEmpty(command) ) {
            result.addRowMessage(ErrorMessage.COMMAND_IS_MISSING, CMD_HEADER);
            return null;
        }

        final @Nullable String uniqueColumnName = getUniqueColumnName();
        final Integer uniqueIndex = uniqueColumnName!=null ? indicies.get(uniqueColumnName) : null;
        if (uniqueColumnName!=null && uniqueIndex==null) {
            throw new NullPointerException("uniqueColumnName not empty, but uniqueIndex is null");
        }

        final Set<String> requiredColumns = getRequiredColumnNames();

        for (Entry<String, Integer> indexEntry : indicies.entrySet()) {
            final String columnName = indexEntry.getKey();
            final Integer index = indexEntry.getValue();
            Preconditions.checkNotNull(index);

            // Check if data is missing for given conditions
            if (uniqueColumnName != null && index == uniqueIndex) {
                if (currentRowData.get(index) == null) {
                    result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, columnName);
                }
            } else {
                if (requiredColumns.contains(columnName) && currentRowData.get(index) == null && !CMD_RENAME.equals(command)
                        && !CMD_DELETE.equals(command)) {
                    result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, columnName);
                }
            }
        }
        if (result.isRowError()) return null;

        return command;
    }

    /**
     * Returns a map of row data indices given a hether row data AND checks whether required columns are missing from
     * the header
     *
     * @param headerRow a heather row data
     *
     * @return the requested map or <code>null</code> in case of missing fields
     */
    private Map<String, Integer> setUpIndexesForFields(List<String> headerRow) {
        final Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        for (String column : getKnownColumnNames()) {
            int index = headerRow.indexOf(column);
            if (index == -1 && getRequiredColumnNames().contains(column)) {
                result.addGlobalMessage(ErrorMessage.HEADER_FIELD_MISSING, column);
            } else {
                mapBuilder.put(column, index);
            }
        }
        return result.isError() ? null : mapBuilder.build();
    }

    private Map<String, Integer> setUpIndexesForProperties(List<String> headerRow, Map<String, Integer> knownColumns) {
        Preconditions.checkNotNull(headerRow);
        Preconditions.checkNotNull(knownColumns);
        Preconditions.checkArgument(!headerRow.isEmpty());

        final Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        for (int i = 0; i < headerRow.size(); i++) {
            final String columnName = headerRow.get(i);
            if (!Objects.equals(null, columnName) && !Objects.equals(CMD_HEADER, columnName) && !knownColumns.containsKey(columnName)) {
                // This method could affet the global error result
                if (checkPropertyHeader(columnName)) {
                    mapBuilder.put(columnName, i);
                } else {
                    return null;
                }

            }
        }
        return mapBuilder.build();
    }


    /** A hook that sub-classes could use to check whether a property column (non field column) is valid,
     * or to do any processing during header processing.
     *
     * @param propertyColumnName the name of the column, as specified in its header. Should correspond to the
     * property name
     * @return <code>false</code> if there was an error during the check
     */
    protected boolean checkPropertyHeader(String propertyColumnName) { return true; }

    /**
     * During row processing, returns a {@link String} cell within the row that belongs to given column
     *
     * @param columnName the header column name
     * @return the contents of the appropriate column cell within the current row
     */
    protected String readCurrentRowCellForHeader(String columnName) {
        Preconditions.checkNotNull(indicies);
        Integer index = indicies.get(columnName);

        return (index != null && index != -1) ? currentRowData.get(index) : null;
    }

    /**
     * During row processing, returns a {@link String} cell within the row that belongs to given property column

     * @param propertyColumnName header column name
     * @return the content of the appropriate column cell within the current row
     */
    protected String readCurrentRowCellForProperty(String propertyColumnName) {
        Preconditions.checkNotNull(propertyIndicies);
        Integer index = propertyIndicies.get(propertyColumnName);

        return (index != null && index != -1) ? currentRowData.get(index) : null;
    }

    /**
     * Returns the column-names for the properties.
     * @return
     */
    protected Set<String> getProperties() {
        Preconditions.checkNotNull(propertyIndicies);
        return propertyIndicies.keySet();
    }
}
