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
package org.openepics.discs.conf.export;

import java.io.InputStream;

/**
 * Defines methods for exporting the data in tabular form.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public interface ExportTable {

    /** The item value for the PrimeFaces UI selection of the export file format : CSV */
    public static final String FILE_FORMAT_CSV = "CSV";
    /** The item value for the PrimeFaces UI selection of the export file format : Excel */
    public static final String FILE_FORMAT_EXCEL = "XLSX";
    /** MIME type / content type to use when initiating download of a CSV file */
    public static final String MIME_TYPE_CSV = "text/csv";
    /** MIME type / content type to use when initiating download of an Excel file */
    public static final String MIME_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * The method creates the table. Use to prepare the internal structures for the data that will be received.
     *
     * @param title name of the table if supported by the implementation
     */
    public void createTable(String title);

    /**
     * Defines a header row. For some formats the header row can be added multiple times (and may signify just a row
     * displayed in different style), while for other formats can only be added once and before any of the data rows.
     *
     * @param titles a collection of strings to be used as column titles
     */
    public void addHeaderRow(String... titles);


    /** This method adds a new row of data to the table. The input data can be in any format. The method is responsible
     * for correctly interpreting the data entries.
     * @param data the data
     */
    public void addDataRow(Object... data);

    /**
     * This method puts the data into a stream that can be saved/set to the client.
     * @return
     */
    public InputStream exportTable();
}
