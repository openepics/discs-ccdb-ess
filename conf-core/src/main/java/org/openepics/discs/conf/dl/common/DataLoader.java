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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Interface for all data loaders
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
public interface DataLoader {

    // Command string constants
    public static final String CMD_CREATE = "CREATE";
    public static final String CMD_UPDATE = "UPDATE";
    public static final String CMD_DELETE = "DELETE";
    public static final String CMD_RENAME = "RENAME";
    public static final String CMD_END = "END";

    public static final String CMD_CREATE_DEVICE = "CREATE DEVICE";

    public static final String CMD_UPDATE_DEVICE = "UPDATE DEVICE";
    public static final String CMD_UPDATE_PROPERTY = "UPDATE PROPERTY";

    public static final String CMD_DELETE_DEVICE = "DELETE DEVICE";
    public static final String CMD_DELETE_PROPERTY = "DELETE PROPERTY";

    /**
     * Saves data read from input file to the database
     *
     * @param inputRows {@link List} of all rows containing data from input file
     * @param contextualData optional map of objects passed with string keys
     *
     * @return {@link DataLoaderResult} describing the outcome of the data loading
     */
    public DataLoaderResult loadDataToDatabase(List<Pair<Integer, List<String>>> inputRows,
            Map<String, Object> contextualData);

    /**
     * The index is defined by the import data template and is returned by the method because different data types
     * could have a different template and the data could start at a different index.
     * @return The '0 based' index of row at which the loader should expect data.
     */
    public int getImportDataStartIndex();

    /**
     * This number is data specific.
     * @return the maximum number of columns containing data.
     */
    public int getDataWidth();
}
