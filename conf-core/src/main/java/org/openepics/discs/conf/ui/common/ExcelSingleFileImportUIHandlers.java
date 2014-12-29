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

import org.primefaces.event.FileUploadEvent;

/**
 * The interface contains all the methods that the UI control handling the import of a single Excel file must implement.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public interface ExcelSingleFileImportUIHandlers extends ExcelImportUIHandlers {

    /** This method is called when user clicks the "Upload" button in the "excel import" UI. The data is stored on the
     * server to be parsed if the user actually decides to process the import data (he can still cancel the action instead).
     * @param event The PrimeFaces upload event.
     */
    public void handleImportFileUpload(FileUploadEvent event);

    /**
     * @return The name of the import file. Used in the "Import excel" procedure.
     */
    public String getImportFileName();
}
