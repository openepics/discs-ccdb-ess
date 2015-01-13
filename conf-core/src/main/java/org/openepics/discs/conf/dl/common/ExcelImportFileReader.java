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
package org.openepics.discs.conf.dl.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
     *         set. Each row is represented as a pair of the row number and a list of columns.
     */
    public static List<Pair<Integer, List<String>>> importExcelFile(InputStream inputStream) {
        boolean headerRowFound = false;
        final List<Pair<Integer, List<String>>> result= new ArrayList<>();
        
        try {
            final XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            final XSSFSheet sheet = workbook.getSheetAt(0);

            int headerRowLength = 0;

            for (Row excelRow : sheet) {
                if (Objects.equal(ExcelCell.asStringOrNull(excelRow.getCell(0), workbook), AbstractDataLoader.CMD_HEADER)) {
                    headerRowFound = true;
                    headerRowLength = excelRow.getLastCellNum();
                }

                final String firstColumnValue = Strings.emptyToNull(ExcelCell.asStringOrNull(excelRow.getCell(0), workbook));
                if (headerRowFound && firstColumnValue != null && !firstColumnValue.trim().isEmpty()) {
                    final List<String> row = new ArrayList<>();                    
                    final int rowNumber = excelRow.getRowNum() + 1;                    
                    final int lastCellIndex = headerRowLength > excelRow.getLastCellNum() ? headerRowLength : excelRow.getLastCellNum();
                    for (int i = 0; i < lastCellIndex; i++) {
                        row.add(ExcelCell.asStringOrNull(excelRow.getCell(i), workbook));
                    }
                    result.add(new ImmutablePair<Integer, List<String>>(rowNumber, row));
                }
            }

            if (!headerRowFound) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
