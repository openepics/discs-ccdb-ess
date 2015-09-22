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
package org.openepics.discs.conf.ui.export;

import org.openepics.discs.conf.export.CSVExportTable;
import org.openepics.discs.conf.export.ExcelExportTable;
import org.openepics.discs.conf.export.ExportTable;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * An abstract class that implements common methods for exporting a simple table into an Excel or CSV format.
 * The class provides support for an Export data dialog, which behaves the same whenever user wants to export tabular
 * data. The implementing classes just need to implement methods that fill the file with the actual data.
 * 
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public abstract class ExportSimpleTableDialog {
    private String fileFormat;
    private boolean includeHeaderRow;

    public ExportSimpleTableDialog() {
    }

    /** Prepares the default values of the Export data dialog: file format and header row */
    public void prepareTableExportPopup() {
        fileFormat = ExportTable.FILE_FORMAT_EXCEL;
        includeHeaderRow = true;
    }

    /** @return the fileFormat */
    public String getFileFormat() {
        return fileFormat;
    }
    /** @param fileFormat the fileFormat to set */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    /** @return the includeHeaderRow */
    public boolean isIncludeHeaderRow() {
        return includeHeaderRow;
    }
    /** @param includeHeaderRow the includeHeaderRow to set */
    public void setIncludeHeaderRow(boolean includeHeaderRow) {
        this.includeHeaderRow = includeHeaderRow;
    }

    /** @return The data from the table exported into the PrimeFaces file download stream */
    public StreamedContent getExportedTable() {
        final ExportTable exportTable;
        final String mimeType;
        final String fileName;

        if (ExportTable.FILE_FORMAT_EXCEL.equals(fileFormat)) {
            exportTable = new ExcelExportTable();
            mimeType = ExportTable.MIME_TYPE_EXCEL;
            fileName = getFileName() + ".xlsx";
        } else {
            exportTable = new CSVExportTable();
            mimeType = ExportTable.MIME_TYPE_CSV;
            fileName = getFileName() + ".csv";
        }

        exportTable.createTable(getTableName());
        if (includeHeaderRow) {
            addHeaderRow(exportTable);
        }

        addData(exportTable);

        return new DefaultStreamedContent(exportTable.exportTable(), mimeType, fileName);
    }

    /** @return the name of the table to be used when creating. This name may be used by the {@link ExportTable}
     * implementation. */
    protected abstract String getTableName();

    /** @return the name of the file to use WITHOUT extension */
    protected abstract String getFileName();

    /** The method that adds a header row to the export data.
     * @param exportTable the table to add header row to
     */
    protected abstract void addHeaderRow(ExportTable exportTable);

    /** The method that fills the table with the exported data.
     * @param exportTable the table to add the data rows to
     */
    protected abstract void addData(ExportTable exportTable);
}
