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
package org.openepics.discs.conf.ui.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ExcelImportFileReader;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.primefaces.event.FileUploadEvent;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

/**
 * A class to implement common code for all single file importers.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public abstract class AbstractExcelSingleFileImportUI implements ExcelSingleFileImportUIHandlers {
    protected byte[] importData;
    private transient DataLoaderResult loaderResult;
    private String importFileName;
    protected ImportFileStatistics importFileStatistics;

    private ExportSimpleErrorsTableDialog errorsTableDialog;
    private class ExportSimpleErrorsTableDialog extends ExportSimpleTableDialog {
        public ExportSimpleErrorsTableDialog() {
        }

        @Override
        protected String getTableName() {
            return "Errors";
        }

        @Override
        protected String getFileName() {
            return "errors";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Row", "Column", "Error");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<ValidationMessage> filteredMessages = loaderResult.getFilteredMessages();
            final List<ValidationMessage> exportData = filteredMessages == null || filteredMessages.isEmpty()
                    ? loaderResult.getMessages() : filteredMessages;
            for (final ValidationMessage message : exportData) {
                exportTable.addDataRow(message.getRow(), message.getColumn(), message.getMessage().toString());
            }
        }
    }

    @Override
    public abstract void doImport();

    @Override
    public void prepareImportPopup() {
        loaderResult = null;
        importData = null;
        importFileName = null;
    }

    @Override
    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    public void setLoaderResult(DataLoaderResult loaderResult) {
        this.loaderResult = loaderResult;
        errorsTableDialog = new ExportSimpleErrorsTableDialog();
    }

    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImportFileName() {
        return importFileName;
    }

    @Override
    public ImportFileStatistics getImportedFileStatistics(DataLoader dataLoader) {
        Preconditions.checkNotNull(importData);
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            final List<Pair<Integer, List<String>>> inputRows = ExcelImportFileReader.importExcelFile(inputStream,
                    dataLoader.getImportDataStartIndex(), dataLoader.getDataWidth());

            int dataRows = 0;
            int createRows = 0;
            int updateRows = 0;
            int deleteRows = 0;
            int renameRows = 0;

            for (Pair<Integer, List<String>> row : inputRows) {
                switch(row.getRight().get(0)) {
                    case DataLoader.CMD_CREATE:
                        ++createRows;
                        break;
                    case DataLoader.CMD_UPDATE:
                        ++updateRows;
                        break;
                    case DataLoader.CMD_DELETE:
                        ++deleteRows;
                        break;
                    case DataLoader.CMD_RENAME:
                        ++renameRows;
                        break;
                }
                if (row.getRight().get(0).equals(DataLoader.CMD_END)) {
                    break;
                }
                ++dataRows;
            }
            return new ImportFileStatistics(dataRows, createRows, updateRows, deleteRows, renameRows);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return the importFileStatistics */
    public ImportFileStatistics getImportFileStatistics() {
        return importFileStatistics;
    }

    public ExportSimpleTableDialog getSimpleErrorTableExportDialog() {
        return errorsTableDialog;
    }
}
