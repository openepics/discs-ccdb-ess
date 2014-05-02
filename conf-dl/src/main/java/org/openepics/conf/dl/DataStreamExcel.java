/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 * 
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 * 
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 * 
 */
package org.openepics.conf.dl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author vuppala
 */
public class DataStreamExcel implements DataStream {

    private int nextRowNum;
    private InputStream xlfile;
    private Workbook workbook;
    private Sheet sheet;
    private int lastRowNumber;
    private FormulaEvaluator evaluator;
    private Row currentRow = null;
    // private Cell currentCell;
    private static final Logger logger = Logger.getLogger("org.openepics.conf.dl");

    @Override
    public void open(String fileName, String sheetName) throws Exception {
        xlfile = new FileInputStream(fileName);
        workbook = WorkbookFactory.create(xlfile);
        if (workbook == null) {
            throw new CDLException(CDLExceptionCode.OSTREAM, "Invalid file (workbook) name " + xlfile);
        }
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new CDLException(CDLExceptionCode.OSTREAM, "Invalid sheet name " + sheetName);
        }
        evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        currentRow = null;
        nextRowNum = 0;
        lastRowNumber = sheet.getLastRowNum();
    }

    @Override
    public void close() throws Exception {
        if (xlfile != null) {
            xlfile.close();
            xlfile = null;
        }
    }

    @Override
    public void nextRow() throws Exception {
        if (!endOfStream()) {
            currentRow = sheet.getRow(nextRowNum);
        }
        logger.finer("row " + nextRowNum);
        ++nextRowNum;
    }

    @Override
    public String getColumn(int colNum) throws Exception {
        if (currentRow == null) {
            logger.log(Level.FINE, "current row is NULL or blank");
            return "";
        }
        Cell cell = currentRow.getCell(colNum);

        String columnValue = "";
        if (cell != null) {
            // cell.setCellType(Cell.CELL_TYPE_STRING);
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue != null) {
                        columnValue = cellValue.getStringValue();
                        if (columnValue == null) {
                            columnValue = Double.toString(cellValue.getNumberValue());
                        }
                    }
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    columnValue = Double.toString(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    columnValue = cell.getStringCellValue();
                    break;
                default:
                    columnValue = cell.getStringCellValue();
                    break;
            }
        }
        logger.log(Level.FINER,"Cell value " + columnValue);
        return columnValue;
    }
    /*
     //cell.setCellType(Cell.CELL_TYPE_STRING);
     CellValue cellValue;
     cellValue = evaluator.evaluate(cell);
     if (cellValue == null) {
     // return null;
     return ""; // ToDo: Fix it. null or empty string?
     }

     String columnValue = cellValue.getStringValue();
     if ( columnValue == null ) {
     columnValue = Double.toString(cellValue.getNumberValue());
     }
     // logger.log(Level.FINER,"Cell type " + cell.getCellType());
     logger.log(Level.FINER,"Cell value " + columnValue);
     //return cellValue.formatAsString();
     //return cellValue.getStringValue();
     return columnValue;
     * */

    @Override
    public int getRowSize() throws Exception {
        // ToDo: if ( currentRow != null ) ....
        if (currentRow == null) {
            throw new CDLException(CDLExceptionCode.INVALIDROW);
        }
        return currentRow.getLastCellNum();
    }

    @Override
    public boolean endOfStream() {
        return nextRowNum > lastRowNumber;
    }
}
