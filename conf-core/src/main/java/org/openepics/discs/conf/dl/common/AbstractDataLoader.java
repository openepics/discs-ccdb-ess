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

/**
 * Skeleton for all data loaders.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public abstract class AbstractDataLoader {

    public static final String CMD_HEADER = "HEADER";
    public static final String CMD_UPDATE = "UPDATE";
    public static final String CMD_DELETE = "DELETE";
    public static final String CMD_RENAME = "RENAME";
    public static final String CMD_END = "END";

    protected DataLoaderResult loaderResult;
    protected DataLoaderResult rowResult;
    protected final int commandIndex = 1;

    final List<String> duplicateFields = new ArrayList<>();


    /**
     * Sets up index for each necessary field.
     *
     * @param header List containing all header row column values
     */
    protected abstract void setUpIndexesForFields(List<String> header);

    /**
     * Method is used to find indexes of properties from header row. Returned is a map giving property index in the
     * header by property name.
     *
     * @param fields List of all necessary fields. All other columns in header row are considered to be properties.
     * @param headerRow List containing all header row column values
     *
     * @return Property index by property name map of properties in the header
     */
    protected Map<String, Integer> indexByPropertyName(List<String> fields, List<String> headerRow) {
        final Map<String, Integer> indexByPropertyName = new HashMap<>();
        for (String headerEntry : headerRow.subList(2, headerRow.size())) {
            if (!fields.contains(headerEntry) && headerEntry != null && headerEntry.length() > 0) {
                indexByPropertyName.put(headerEntry, headerRow.indexOf(headerEntry));
            }
        }
        return indexByPropertyName;
    }

    /**
     * Checks if there are multiple occurrences of same header entry in the header
     *
     * @param headerRow {@link List} containing header entries
     */
    protected void checkForDuplicateHeaderEntries(List<String> headerRow) {
        final List<String> duplicateHeaderEntries = new ArrayList<>();
        rowResult = new DataLoaderResult();
        for (String headerEntry : headerRow.subList(2, headerRow.size())) {
            if (headerEntry != null && headerEntry.length() > 0 && headerRow.indexOf(headerEntry) != headerRow.lastIndexOf(headerEntry) && !duplicateHeaderEntries.contains(headerEntry)) {
                rowResult.addMessage(new ValidationMessage(ErrorMessage.DUPLICATES_IN_HEADER, headerRow.get(0), headerEntry));
                duplicateHeaderEntries.add(headerEntry);
            }
        }
    }
}
