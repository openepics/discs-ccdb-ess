/**
 *
 */
package org.openepics.discs.conf.dl.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openepics.discs.conf.util.ExcelCell;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * This class returns the excel spreadsheet as a list of rows. The first
 * element in each row is a row number, followed by the values of all columns.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class ExcelImportFileReader {

	/**
	 * This method returns the contents of the first worksheet found in the
	 * Excel workbook file.
	 *
	 * @param inputStream
	 *            the Excel file to parse. Only Excel file version >=12.0
	 *            supported (.xslx).
	 * @return Only the lines from the first worksheet that contain a string
	 *         value. Lines with the empty first cell are not part of the return
	 *         set. The first element of each row is a string representation of
	 *         its index (starting with 1).
	 */
	public static List<List<String>> importExcelFile(InputStream inputStream) {
		boolean headerRowFound = false;
		final List<List<String>> allRows = new ArrayList<>();

		try {
			final XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			final XSSFSheet sheet = workbook.getSheetAt(0);

			int headerRowLength = 0;

			for (Row row : sheet) {
				if (Objects.equal(ExcelCell.asStringOrNull(row.getCell(0), workbook), AbstractDataLoader.CMD_HEADER)) {
					headerRowFound = true;
					headerRowLength = row.getLastCellNum();
				}

				final String firstColumnValue = Strings.emptyToNull(ExcelCell.asStringOrNull(row.getCell(0), workbook));
				if (headerRowFound && firstColumnValue != null && !firstColumnValue.trim().isEmpty()) {
					final List<String> oneRow = new ArrayList<>();
					oneRow.add(String.valueOf(row.getRowNum() + 1));
					final int lastCellIndex = headerRowLength > row.getLastCellNum() ? headerRowLength : row.getLastCellNum();
					for (int i = 0; i < lastCellIndex; i++) {
						oneRow.add(ExcelCell.asStringOrNull(row.getCell(i), workbook));
					}
					allRows.add(oneRow);
				}
			}

			if (!headerRowFound) {
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return allRows;
	}
}
