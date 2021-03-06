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
package org.openepics.discs.conf.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.IntValue;
import org.openepics.discs.conf.ent.values.TimestampValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.DeleteOnCloseFileInputStream;

/**
 * The class implements exporting the a single table into an Excel file.
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class ExcelExportTable implements ExportTable {

    private Workbook wb;
    private Sheet sheet;
    private CellStyle headerStyle;
    private CellStyle timestampStyle;
    private int rowNumber;
    private final String templateName;
    final ServletContext servletContext;

    /**
     * Constructs an Excel file exporter.
     *
     * @param templateName the path to the Excel file inside the WAR archive to use when exporting data.
     * <code>null</code> means create an empty file.
     * @param startRow the 0 based index of the row to start the export at.
     */
    public ExcelExportTable(final String templateName, final int startRow) {
        servletContext = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).
                                                                                    getServletContext();
        rowNumber = startRow;
        this.templateName = templateName;
    }

    @Override
    public void createTable(String title) {
        if (templateName != null) {
            try (final InputStream template = servletContext.getResourceAsStream(templateName)) {
                wb = new XSSFWorkbook(template);
                sheet = wb.getSheetAt(0);
            } catch (IOException e) {
                throw new CannotOpenTemplateException(e);
            }
        } else {
            wb = new XSSFWorkbook();
            sheet = wb.createSheet(title);
        }
        initHeaderStyle();
        initTimestampStyle();
    }

    @Override
    public void addHeaderRow(String... titles) {
        if (templateName != null) return;

        if (rowNumber != 0) {
            throw new CannotAddHeaderRowException("addHeaderRow must be called before data is added to the table.");
        }
        final Row headerRow = sheet.createRow(rowNumber);
        ++rowNumber;
        int column = 0;
        for (final String title : titles) {
            final Cell cell = headerRow.createCell(column, Cell.CELL_TYPE_STRING);
            ++column;
            cell.setCellValue(title);
            cell.setCellStyle(headerStyle);
        }
    }

    @Override
    public void addDataRow(Object... data) {
        Row row = sheet.getRow(rowNumber);

        if (row == null) {
            row = sheet.createRow(rowNumber);
        }

        ++rowNumber;
        int column = 0;
        for (final Object value : data) {
            final Cell cell = row.createCell(column);
            ++column;
            setCellValue(cell, value);
        }
    }

    private void setCellValue(final Cell cell, final Object value) {
        if (value == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else if (value instanceof Boolean) {
            final boolean boolValue = ((Boolean) value).booleanValue();
            cell.setCellValue(boolValue);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar)value);
            cell.setCellStyle(timestampStyle);
        } else if (value instanceof Date) {
            cell.setCellValue((Date)value);
            cell.setCellStyle(timestampStyle);
        } else if (value instanceof Double) {
            final double dblValue = ((Double)value).doubleValue();
            cell.setCellValue(dblValue);
        } else if (value instanceof DblValue) {
            cell.setCellValue(((DblValue)value).getDblValue());
        } else if (value instanceof IntValue) {
            cell.setCellValue(((IntValue)value).getIntValue());
        } else if (value instanceof TimestampValue) {
            cell.setCellValue(((TimestampValue)value).getTimestampValue().toDate());
            cell.setCellStyle(timestampStyle);
        } else if (value instanceof Value) {
            cell.setCellValue(Conversion.valueToString((Value)value));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    @Override
    public InputStream exportTable() {
        File temporaryFile;
        try {
            temporaryFile = File.createTempFile("ccdb_table_exp", "xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (final FileOutputStream outputStream = new FileOutputStream(temporaryFile)) {
            wb.write(outputStream);
            outputStream.close();
            return new DeleteOnCloseFileInputStream(temporaryFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initHeaderStyle() {
        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        headerStyle.setBottomBorderColor(IndexedColors.PALE_BLUE.getIndex());

        final Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setColor(IndexedColors.DARK_TEAL.getIndex());

        headerStyle.setFont(headerFont);
    }

    private void initTimestampStyle() {
        timestampStyle = wb.createCellStyle();
        final CreationHelper creationHelper = wb.getCreationHelper();
        timestampStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
    }
}
