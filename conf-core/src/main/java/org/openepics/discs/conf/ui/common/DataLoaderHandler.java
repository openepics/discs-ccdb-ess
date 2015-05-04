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

import org.apache.commons.lang3.tuple.Pair;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ExcelImportFileReader;

/**
 * Common data loader handler for loading of all data.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
@Stateless
public class DataLoaderHandler {

    @Resource private EJBContext context;
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
     * @param dataLoader depending on which entity is to be loaded to database, different implementation of
     * the {@link DataLoader} interface is passed
     * @return a {@link DataLoaderResult} containing information about the operation completion status
     */
    public DataLoaderResult loadData(InputStream inputStream, DataLoader dataLoader) {
        final List<Pair<Integer, List<String>>> inputRows = ExcelImportFileReader.importExcelFile(inputStream);

        if (inputRows != null && !inputRows.isEmpty()) {
            loaderResult = dataLoader.loadDataToDatabase(inputRows, null);
            if (loaderResult.isError()) {
                context.setRollbackOnly();
            }
        }
        return loaderResult;
    }
}
