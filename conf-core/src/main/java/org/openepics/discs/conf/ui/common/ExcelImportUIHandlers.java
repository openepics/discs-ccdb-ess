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

import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;

/**
 * The interface contains all the methods that the UI control handling the import of any number of excel files must implement.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public interface ExcelImportUIHandlers {

    /**
     * A class returning basic statistics for the imported excel file, i.e. the number of lines in the Excel file
     * containing:
     * <ul>
     * <li>any data</li>
     * <li>entity to be created</li>
     * <li>entity to be updated</li>
     * <li>entity to be deleted</li>
     * <li>entity to be renamed</li>
     * </ul>
     *
     * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
     */
    public static class ImportFileStatistics {
        private final int dataRows;
        private final int createRows;
        private final int updateRows;
        private final int deleteRows;
        private final int renameRows;

        /** Default constructor with <code>0</code> statistics */
        public ImportFileStatistics() {
            dataRows = createRows = updateRows = deleteRows = renameRows = 0;
        }


        public ImportFileStatistics(int dataRows, int createRows, int updateRows, int deleteRows, int renameRows) {
            this.dataRows = dataRows;
            this.createRows = createRows;
            this.updateRows = updateRows;
            this.deleteRows = deleteRows;
            this.renameRows = renameRows;
        }

        /** @return the dataRows */
        public int getDataRows() {
            return dataRows;
        }

        /** @return the createRows */
        public int getCreateRows() {
            return createRows;
        }

        /** @return the updateRows */
        public int getUpdateRows() {
            return updateRows;
        }

        /** @return the deleteRows */
        public int getDeleteRows() {
            return deleteRows;
        }

        /** @return the renameRows */
        public int getRenameRows() {
            return renameRows;
        }

        /** @return the number of lines actually containing any of the 4 commands */
        public int getImportRows() {
            return createRows + updateRows + deleteRows + renameRows;
        }
    }

    /** The action called to actually import excel file containing properties. */
    public void doImport();

    /** Called to prepare the data to display in the UI "import excel" dialog. */
    public void prepareImportPopup();

    /** @return The results of the "excel import" operation to display to the user. */
    public DataLoaderResult getLoaderResult();

    /** @return the import statistics for the imported file */
    public ImportFileStatistics getImportedFileStatistics(DataLoader dataLoader);
}
