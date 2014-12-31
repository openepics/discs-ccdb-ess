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

import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.SlotsAndSlotPairsDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ExcelImportFileReader;
import org.openepics.discs.conf.dl.common.ValidationMessage;

/**
 * Common data loader handler for loading of all data.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless
public class DataLoaderHandler {

    @Resource private EJBContext context;
    @Inject SlotsAndSlotPairsDataLoader slotLoader;
    private DataLoaderResult loaderResult;

    /**
     * Java EE post construct life-cycle method.
     */
    @PostConstruct
    public void init() {
        loaderResult = null;
    }

    /**
     * Loads data from import file to {@link List} and calls method on certain data loader
     * to save the data in the database. If the result of save is {@link DataLoaderResult#isError()}
     * then the transaction is rolled back. In any case, the notification is shown to the user.
     *
     * @param inputStream input file from which the data should be loaded
     * @param dataLoader depending on which entity is to be loaded to database, different implementation of {@link DataLoader} interface is passed
     * @return a {@link DataLoaderResult} containing information about the operation completion status
     */
    public DataLoaderResult loadData(InputStream inputStream, DataLoader dataLoader) {
        final List<List<String>> inputRows = ExcelImportFileReader.importExcelFile(inputStream);

        if (inputRows != null && !inputRows.isEmpty()) {
            loaderResult = dataLoader.loadDataToDatabase(inputRows);
            if (loaderResult.isError()) {
                context.setRollbackOnly();
            }
        } else {
            loaderResult = new DataLoaderResult();
            loaderResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_ROW_MISSING));
        }
        return loaderResult;
    }

    /**
     * Loads data from two import files to {@link List} and calls method on certain data loader
     * to save the data in the database. If the result of save is {@link DataLoaderResult#isError()}
     * then the transaction is rolled back. In any case, the notification is shown to the user.
     * The two files in this case are the Slots information Excel worksheet and Slot relationship Excel worksheet.
     *
     * @param firstInputStream input file containing the Slots information Excel worksheet
     * @param secondInputStream input file containing the Slot relationship Excel worksheet
     * @param firstFileName the name of the Slots information Excel worksheet file
     * @param secondFileName the name of the Slot relationship Excel worksheet file
     * @return a {@link DataLoaderResult} containing information about the operation completion status
     */
    public DataLoaderResult loadDataFromTwoFiles(InputStream firstInputStream, InputStream secondInputStream, String firstFileName, String secondFileName) {
        final List<List<String>> firstFileInputRows;
        final List<List<String>> secondFileInputRows;

        if (firstInputStream != null) {
            firstFileInputRows = ExcelImportFileReader.importExcelFile(firstInputStream);
        } else {
            firstFileInputRows = null;
        }

        if (secondInputStream != null) {
            secondFileInputRows = ExcelImportFileReader.importExcelFile(secondInputStream);
        } else {
            secondFileInputRows = null;
        }

        if ((firstFileInputRows != null && !firstFileInputRows.isEmpty()) || (secondFileInputRows != null && !secondFileInputRows.isEmpty())) {
            loaderResult = slotLoader.loadDataToDatabase(firstFileInputRows, secondFileInputRows, firstFileName, secondFileName);
            if (loaderResult.isError()) {
                context.setRollbackOnly();
            }
        }
        return loaderResult;
    }


}
