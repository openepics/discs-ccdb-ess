/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.ccdb.core.util;

/**
 * A class returning basic statistics for the imported excel file, i.e. the number of lines in the Excel file
 * containing:
 * <ul>
 * <li>any data</li>
 * <li>entity to be created</li>
 * <li>entity to be updated</li>
 * <li>entity to be deleted</li>
 * </ul>
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class ImportFileStatistics {
    private final int dataRows;
    private final int createRows;
    private final int updateRows;
    private final int deleteRows;

    /** Default constructor with <code>0</code> statistics */
    public ImportFileStatistics() {
        dataRows = createRows = updateRows = deleteRows = 0;
    }

    /**
     * Creates a new statistics object with the following parameters.
     * @param dataRows the number of rows processed
     * @param createRows the number of rows with the CREATE command
     * @param updateRows the number of rows with the UPDATE command
     * @param deleteRows the number of rows with the DELETE command
     */
    public ImportFileStatistics(int dataRows, int createRows, int updateRows, int deleteRows) {
        this.dataRows = dataRows;
        this.createRows = createRows;
        this.updateRows = updateRows;
        this.deleteRows = deleteRows;
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

    /** @return the number of lines actually containing any of the 3 commands */
    public int getImportRows() {
        return createRows + updateRows + deleteRows;
    }
}
