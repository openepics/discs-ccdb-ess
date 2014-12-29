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
