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

import javax.inject.Inject;

import org.openepics.discs.conf.dl.annotations.DevicesLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.primefaces.event.FileUploadEvent;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public abstract class AbstractDeviceAttributesController
        extends AbstractAttributesController<DevicePropertyValue, DeviceArtifact>
        implements ExcelSingleFileImportUIHandlers {

    private static final long serialVersionUID = -1920073445045143338L;

    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @DevicesLoader private transient DataLoader devicesDataLoader;

    private ExcelSingleFileImportUI excelSingleFileImportUI;

    private class ExcelSingleFileImportUI extends AbstractExcelSingleFileImportUI {
        public ExcelSingleFileImportUI() {
            super.init();
        }

        @Override
        public void setDataLoader() {
            dataLoader = devicesDataLoader;
        }

        @Override
        public void doImport() {
            try (InputStream inputStream = new ByteArrayInputStream(importData)) {
                setLoaderResult(dataLoaderHandler.loadData(inputStream, devicesDataLoader));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void init() {
        excelSingleFileImportUI = new ExcelSingleFileImportUI();
    }

    /** @see org.openepics.discs.conf.ui.common.AbstractDeviceAttributesController.ExcelSingleFileImportUI#doImport()
     */
    @Override
    public void doImport() {
        excelSingleFileImportUI.doImport();
    }

    /** @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#prepareImportPopup() */
    @Override
    public void prepareImportPopup() {
        excelSingleFileImportUI.prepareImportPopup();
    }

    /** @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#setDataLoader() */
    @Override
    public void setDataLoader() {
        excelSingleFileImportUI.setDataLoader();
    }

    /** @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getLoaderResult() */
    @Override
    public DataLoaderResult getLoaderResult() {
        return excelSingleFileImportUI.getLoaderResult();
    }

    /**
     * @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#handleImportFileUpload(org.primefaces.event.FileUploadEvent)
     */
    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        excelSingleFileImportUI.handleImportFileUpload(event);
    }

    /** @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getImportFileName() */
    @Override
    public String getImportFileName() {
        return excelSingleFileImportUI.getImportFileName();
    }

    /** @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getImportedFileStatistics() */
    @Override
    public ImportFileStatistics getImportedFileStatistics() {
        return excelSingleFileImportUI.getImportedFileStatistics();
    }

    /**
     * @return the import statistics for the imported file
     * @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getImportFileStatistics() */
    public ImportFileStatistics getImportFileStatistics() {
        return excelSingleFileImportUI.getImportFileStatistics();
    }

    /**
     * @return the dialog containing a simple error message
     * @see org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI#getSimpleErrorTableExportDialog() */
    public ExportSimpleTableDialog getSimpleErrorTableExportDialog() {
        return excelSingleFileImportUI.getSimpleErrorTableExportDialog();
    }
}
