package org.openepics.discs.conf.util;


import javax.annotation.Nullable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * A static utility class for reading single Excel file cell
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 */
public class ExcelCell {
    private ExcelCell() {}

    /**
     * Creating a String from Excel file cell. If cell contains numeric value, this value is cast to String.
     * If there is no value for this cell, null is returned.
     *
     * @param cell the Excel {@link Cell}
     * @param workbook the Excel {@link Workbook}
     *
     * @return the {@link String} result
     */
    public static String asStringOrNull(@Nullable Cell cell, Workbook workbook) {
        if (cell != null) {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                return cell.getStringCellValue() != null ? cell.getStringCellValue() : null;
            } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                return null;
            } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                final CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue != null) {
                    final String columnValue = cellValue.getStringValue();
                    if (columnValue == null) {
                        return Double.toString(cellValue.getNumberValue());
                    } else {
                        return columnValue;
                    }
                } else {
                    return null;
                }
            } else {
                throw new UnhandledCaseException();
            }
        } else {
            return null;
        }
    }


    /**
     * Reading Excel file cell with numeric value and returning its value
     *
     * @param cell the Excel {@link Cell}
     *
     * @return the numeric {@link Double} value
     */
    public static double asNumber(Cell cell) {
        return cell.getNumericCellValue();
    }
}
