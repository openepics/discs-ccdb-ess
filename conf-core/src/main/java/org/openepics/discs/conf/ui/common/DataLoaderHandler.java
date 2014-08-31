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
import org.openepics.discs.conf.dl.common.ExcelImportFileReader;

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

        if (inputRows != null && inputRows.size() > 0) {
            loaderResult = dataLoader.loadDataToDatabase(inputRows);
            if (loaderResult.isError()) {
                context.setRollbackOnly();
            }
        }
        return loaderResult;
    }

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

        if ((firstFileInputRows != null && firstFileInputRows.size() > 0) || (secondFileInputRows != null && secondFileInputRows.size() > 0)) {
            loaderResult = slotLoader.loadDataToDatabase(firstFileInputRows, secondFileInputRows, firstFileName, secondFileName);
            if (loaderResult.isError()) {
                context.setRollbackOnly();
            }
        }
        return loaderResult;
    }


}
