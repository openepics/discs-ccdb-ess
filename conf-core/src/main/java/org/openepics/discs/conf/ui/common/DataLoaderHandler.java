package org.openepics.discs.conf.ui.common;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.EntityNotFoundFailureDataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureReason;
import org.openepics.discs.conf.dl.common.DataLoaderResult.MissingHeaderFailureDataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.NotAuthorizedFailureDataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureDataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.SuccessDataLoaderResult;
import org.openepics.discs.conf.dl.common.ExcelImportFileReader;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;

/**
 * Common data loader handler for loading of all data.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless
public class DataLoaderHandler {

    @Resource private EJBContext context;

    /**
     * Loads data from import file to {@link List} and calls method on certain data loader
     * to save the data in the database. If the result of save is not {@link SuccessDataLoaderResult}
     * then the transaction is rolled back. In any case, the notification is shown to the user.
     *
     * @param inputStream input file from which the data should be loaded
     * @param dataLoader depending on which entity is to be loaded to database, different implementation of {@link DataLoader} interface is passed
     */
    public void loadData(InputStream inputStream, DataLoader dataLoader) {
        final List<List<String>> inputRows = ExcelImportFileReader.importExcelFile(inputStream);

        if (inputRows != null && inputRows.size() > 0) {
            DataLoaderResult dataLoaderResult = dataLoader.loadDataToDatabase(inputRows);
            if (dataLoaderResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                context.setRollbackOnly();
            }
            printDataLoadingResult(dataLoaderResult);
        } else {
            printDataLoadingResult(new DataLoaderResult.MissingHeaderFailureDataLoaderResult());
        }
    }

    private void printDataLoadingResult(DataLoaderResult result) {

        final Severity severity;
        final String resultSummary;
        final String resultMessage;

        if (result instanceof DataLoaderResult.FailureDataLoaderResult) {
            severity = FacesMessage.SEVERITY_ERROR;
            resultSummary = "Import Failure";
            if (result instanceof RowFormatFailureDataLoaderResult) {
                final RowFormatFailureDataLoaderResult rowFormatFaliureResult = (RowFormatFailureDataLoaderResult) result;
                if (rowFormatFaliureResult.getReason() == RowFormatFailureReason.COMMAND_NOT_VALID) {
                    resultMessage = "Command is not valid. In row " + rowFormatFaliureResult.getRowNumber();
                } else if (rowFormatFaliureResult.getReason() == RowFormatFailureReason.DUPLICATE_ENTITY) {
                    resultMessage = "Entity can not be renamed because new name already exists in the database. In row " + rowFormatFaliureResult.getRowNumber();
                } else if (rowFormatFaliureResult.getReason() == RowFormatFailureReason.HEADER_FIELD_MISSING) {
                    resultMessage = "Header does not contain all required fields. In row " + rowFormatFaliureResult.getRowNumber();
                } else if (rowFormatFaliureResult.getReason() == RowFormatFailureReason.RENAME_MISFORMAT) {
                    resultMessage = "Rename command misformat. Old name should be defined within [] followed by new name. Example [old name] new name. In row " + rowFormatFaliureResult.getRowNumber();
                } else if (rowFormatFaliureResult.getReason() == RowFormatFailureReason.REQUIRED_FIELD_MISSING) {
                    resultMessage = "Required field should not be empty. In row " + rowFormatFaliureResult.getRowNumber();
                } else {
                    throw new UnhandledCaseException();
                }
            } else if (result instanceof MissingHeaderFailureDataLoaderResult) {
                resultMessage = "No header was found";
            } else if (result instanceof NotAuthorizedFailureDataLoaderResult) {
                final NotAuthorizedFailureDataLoaderResult notAuthorizedResult = (NotAuthorizedFailureDataLoaderResult) result;
                resultMessage = "You are not authorized to perform operation " +  notAuthorizedResult.getOperation().name();
            } else if (result instanceof EntityNotFoundFailureDataLoaderResult) {
                final EntityNotFoundFailureDataLoaderResult entityNotFoundResult = (EntityNotFoundFailureDataLoaderResult) result;
                resultMessage = "Entity " +  entityNotFoundResult.getEntity().name() + " could not be found in the database. In row " + entityNotFoundResult.getRowNumber();
            } else {
                throw new UnhandledCaseException();
            }
        } else if (result instanceof DataLoaderResult.SuccessDataLoaderResult){
            severity = FacesMessage.SEVERITY_INFO;
            resultSummary = "Import Success";
            resultMessage = "Import was completed successfuly";
        } else {
            throw new UnhandledCaseException();
        }

        Utility.showMessage(severity, resultSummary, resultMessage);
    }

}
