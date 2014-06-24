/**
 * 
 */
package org.openepics.discs.conf.dl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openepics.discs.conf.util.ExcelCell;
import org.openepics.discs.conf.util.IllegalImportFileFormatException;

import com.google.common.base.Objects;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * 
 */
public class ExcelImportFileReader {

    public static List<List<String>> importExcelFile(InputStream inputStream) throws IllegalImportFileFormatException {
        boolean headerRowFound = false;
        final List<List<String>> allRows = new ArrayList<>();

        try {
            final XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            final XSSFSheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (!headerRowFound && Objects.equal(ExcelCell.asStringOrNull(row.getCell(0)), "HEADER")) {
                    headerRowFound = true;
                }

                if (headerRowFound && ExcelCell.asStringOrNull(row.getCell(0)) != null) {
                    final List<String> oneRow = new ArrayList<>();
                    oneRow.add(String.valueOf(row.getRowNum() + 1));
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        oneRow.add(ExcelCell.asStringOrNull(row.getCell(i)));
                    }
                    allRows.add(oneRow);
                }
            }
            
            if (!headerRowFound) {
                throw new IllegalImportFileFormatException("Header row was not found!", "anywhere");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allRows;
    }
}
