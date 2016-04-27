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
package org.openepics.discs.ccdb.gui.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openepics.discs.ccdb.core.util.Conversion;
import org.openepics.discs.ccdb.core.util.DeleteOnCloseFileInputStream;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class CSVExportTable implements ExportTable {

    private static final String SEPARATOR = ",";
    private static final String NEWLINE = "\r\n";

    private boolean headerAdded;
    final private List<String> fileLines;
    final SimpleDateFormat timestampFormatter;

    /** Constructs a CSV file exporter. */
    public CSVExportTable() {
        headerAdded = false;
        timestampFormatter = new SimpleDateFormat(Conversion.DATE_TIME_FORMAT);
        fileLines = Lists.newArrayList();
    }

    @Override
    public void createTable(String title) {
        fileLines.clear();
    }

    @Override
    public void addHeaderRow(String... titles) {
        if (!headerAdded) {
            headerAdded = true;
            fileLines.add(0, dataToString(titles));
        } else {
            fileLines.set(0, dataToString(titles));
        }
    }

    @Override
    public void addDataRow(Object... data) {
        fileLines.add(dataToString(data));
    }

    @Override
    public InputStream exportTable() {
        try {
            final File temporaryFile = File.createTempFile("ccdb_table_exp", "csv");
            final FileOutputStream outputStream = new FileOutputStream(temporaryFile);
            for (String line : fileLines) {
                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.close();
            return new DeleteOnCloseFileInputStream(temporaryFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String dataToString(Object... entries) {
        final StringBuilder line = new StringBuilder();
        boolean first = true;
        for (final Object entry : entries) {
            if (!first) {
                line.append(SEPARATOR);
            } else {
                first = false;
            }
            line.append(escapeEntry(entry));
        }
        line.append(NEWLINE);
        return line.toString();
    }

    private String escapeEntry(Object entry) {
        if (entry == null) {
            return "";
        }
        String representation = entry.toString();
        if (entry instanceof Date) {
            representation = timestampFormatter.format(entry);
        } else if (entry instanceof Calendar) {
            final Calendar calEntry = (Calendar) entry;
            representation = timestampFormatter.format(calEntry.getTime());
        }
        boolean addQuotes = false;
        if (representation.contains("\"")) {
            addQuotes = true;
            representation = representation.replace("\"", "\"\"");
        }
        if (addQuotes || representation.contains(SEPARATOR) || representation.contains("\n")
                || representation.contains("\r")) {
            return "\"" + representation + "\"";
        } else {
            return representation;
        }
    }
}
