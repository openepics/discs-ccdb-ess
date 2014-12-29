package org.openepics.discs.conf.ui.common;

import org.openepics.discs.conf.dl.common.DataLoaderResult;

/**
 * The interface contains all the methods that the UI control handling the import of any number of excel files must implement.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public interface ExcelImportUIHandlers {

    /**
     * The action called to actually import excel file containing properties.
     */
    public void doImport();

    /**
     * Called to prepare the data to display in the UI "import excel" dialog.
     */
    public void prepareImportPopup();

    /**
     * @return The results of the "excel import" operation to display to the user.
     */
    public DataLoaderResult getLoaderResult();
}
