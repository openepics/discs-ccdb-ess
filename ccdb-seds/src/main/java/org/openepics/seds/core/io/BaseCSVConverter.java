/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.seds.core.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ListDouble;
import org.epics.util.text.CsvParser;
import org.epics.util.text.CsvParserResult;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.io.CSVConverter;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.ArrayUtil;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;
import static org.openepics.seds.util.SedsException.assertNotNull;

/**
 * CSVConverter implementation.
 *
 * @author Aaron Barber
 */
class BaseCSVConverter implements CSVConverter {

    //Property Information
    //--------------------------------------------------------------------------
    private String separator;

    private enum State {

        INVALID_COLSIZES,
        NULL_META,
        NULL_TABLE,
        NULL_ENTRY,
        PARSE_ERROR;

        public IllegalStateException exception() {
            String msg;

            switch (this) {
                case INVALID_COLSIZES:
                    msg = "The arrays [column names, column units, column data] were not the same length";
                    break;
                case NULL_META:
                    msg = "There was missing meta data (missing column names or column units)";
                    break;
                case NULL_TABLE:
                    msg = "There was no table data";
                    break;
                case NULL_ENTRY:
                    msg = "There was a null entry as a value in the table";
                    break;
                case PARSE_ERROR:
                    msg = "Error in parsing the CSV for values";
                    break;
                default:
                    msg = "Unknown";
                    break;
            }
            return new IllegalStateException(this.name() + ": " + msg);
        }
    }

    private static class Factory {

        static SedsDisplay toDisplay(
                String units
        ) {
            assertNotNull(units, String.class, "Building a display (parameter 'units'");
            return Seds.newFactory().newDisplay(null, null, 0, 0, null, null, null, units);
        }

        static SedsScalarArray toScalarArray(
                Class<?> type,
                Object data,
                String units
        ) {
            assertNotNull(type, Class.class, "Building a scalar array (parameter 'type'");
            assertNotNull(data, Object.class, "Building a scalar array (parameter 'data'");
            assertNotNull(units, String.class, "Building a scalar array (parameter 'units'");

            //Pre-condition: data is a List
            if (double.class.equals(type) && data instanceof ListDouble) {
                return Seds.newFactory()
                        .newScalarArray(ArrayUtil.AsBoxedArray.typeNumber((ListDouble) data), null, null, null, toDisplay(units), null);
            } else if (String.class.equals(type) && data instanceof List) {
                return Seds.newFactory()
                        .newScalarArray(ArrayUtil.AsBoxedArray.typeString((List<String>) data), null, null, toDisplay(units), null);
            } else {
                throw SedsException.buildIAE(
                        data,
                        "Supported type..." + Arrays.deepToString(ScalarType.values()),
                        "Building a scalar array"
                );
            }
        }

        static SedsTable toTable(
                List<String> colNames,
                List<String> colUnits,
                List<Class<?>> colTypes,
                List<Object> colData,
                int rowCount
        ) {
            assertNotNull(colNames, List.class, "Building a table (names list");
            assertNotNull(colUnits, List.class, "Building a table (units list");
            assertNotNull(colTypes, List.class, "Building a table (column types list");
            assertNotNull(colData, List.class, "Building a table (column data list");

            int numRows;
            int numColumns;
            String[] names;
            SedsScalarArray[] columns;

            numRows = rowCount;
            numColumns = colData.size();
            names = new String[numColumns];
            columns = new SedsScalarArray[numColumns];

            for (int i = 0; i < numColumns; ++i) {
                columns[i] = toScalarArray(colTypes.get(i), colData.get(i), colUnits.get(i));
                names[i] = colNames.get(i);
            }

            return Seds.newFactory().newTable(numRows, numColumns, names, columns);
        }
    }
    //--------------------------------------------------------------------------

    //Constructor
    //--------------------------------------------------------------------------
    BaseCSVConverter(String separator) {
        assertNotNull(separator, String.class, "Setting the separator of the CSVConverter");
        this.separator = separator;
    }
    //--------------------------------------------------------------------------

    //Property Setter
    //--------------------------------------------------------------------------
    @Override
    public String getSeparator() {
        return separator;
    }

    @Override
    public CSVConverter withSeparator(String separator) {
        assertNotNull(separator, String.class, "Setting the separator of the CSVConverter");
        this.separator = separator;
        return this;
    }
    //--------------------------------------------------------------------------

    //API
    //--------------------------------------------------------------------------
    /**
     * Creates a {@link SedsTable} from CSV data from the input source.
     *
     * <p>
     * The input data taken is: column names, units, and value arrays.
     *
     * <p>
     * The structure of the CSV should be:
     * <ol>
     * <li>Row 1: Column Names
     * <li>Row 2: Column Units (leave blank if unit-less)
     * <li>Row 3 ... n: Column Values
     * </ol>
     *
     * An example of acceptable CSV would be:
     * <pre>
     * distance,time,shape
     * meters,seconds,
     * 10, 5, flat
     * 30, 5, round
     * 20, 10, flat
     * 90, 10, round
     * </pre>
     *
     * @param reader input source
     * @return a SEDS table with column name, unit, and value information taken
     * from the reader
     * @throws IOException if unable to read correctly from the buffer
     */
    @Override
    public SedsTable importTable(
            BufferedReader reader
    ) throws IOException {
        assertNotNull(reader, BufferedReader.class, "Importing a table");

        //Reads names and units
        String names = reader.readLine();
        String units = reader.readLine();

        //Asserts names and units
        if (names == null || units == null) {
            throw State.NULL_META.exception();
        }

        //Containers for names and units
        List<Object> dataNames = CsvParser.parseCSVLine(names, separator);
        List<Object> dataUnits = CsvParser.parseCSVLine(units, separator);

        List<String> colNames = new ArrayList<>(dataNames.size());
        List<String> colUnits = new ArrayList<>(dataUnits.size());

        //Converts names
        for (Object data : dataNames) {
            if (data == null) {
                throw State.NULL_ENTRY.exception();
            }
            colNames.add(data.toString());
        }

        //Converts units
        for (Object data : dataUnits) {
            if (data == null) {
                throw State.NULL_ENTRY.exception();
            }
            colUnits.add(data.toString());
        }

        //Reads the table
        CsvParserResult table;
        try {
            table = CsvParser.AUTOMATIC
                    .withHeader(CsvParser.Header.NONE)
                    .parse(reader);

            if (!table.isParsingSuccessful()) {
                throw State.PARSE_ERROR.exception();
            }
        } catch (IndexOutOfBoundsException e) {
            throw State.NULL_TABLE.exception();
        }

        //Asserts Sizes
        if (colUnits.size() != colUnits.size() || colNames.size() != table.getColumnTypes().size()) {
            throw State.INVALID_COLSIZES.exception();
        }

        //Converts to SEDS
        return Factory.toTable(
                colNames,
                colUnits,
                table.getColumnTypes(),
                table.getColumnValues(),
                table.getRowCount()
        );

    }

    /**
     * Writes the data of a {@link SedsTable} to a CSV output source.
     *
     * <p>
     * The output data included is: column names, units, and value arrays.
     *
     * <p>
     * The structure of the CSV is:
     * <ol>
     * <li>Row 1: Column Names
     * <li>Row 2: Column Units (leave blank if unit-less)
     * <li>Row 3 ... n: Column Values
     * </ol>
     *
     * An example of an output CSV is:
     * <pre>
     * distance,time,shape
     * meters,seconds,
     * 10, 5, flat
     * 30, 5, round
     * 20, 10, flat
     * 90, 10, round
     * </pre>
     *
     * @param table data
     * @param writer output source
     * @throws IOException if unable to write correctly to the buffer
     */
    @Override
    public void exportTable(
            SedsTable table,
            BufferedWriter writer
    ) throws IOException {
        assertNotNull(table, SedsTable.class, "Exporting a table");
        assertNotNull(writer, BufferedWriter.class, "Exporting a table");

        List<List<Object>> rows = new ArrayList<>();

        //Names
        List<Object> names = new ArrayList<>();
        names.addAll(Arrays.asList(table.getNames()));

        //Units
        List<Object> units = new ArrayList<>();
        for (SedsScalarArray array : table.getValues()) {
            if (array != null && array.getDisplay() != null && array.getDisplay().getUnits() != null) {
                units.add(array.getDisplay().getUnits());
            } else {
                units.add("");
            }
        }

        //Updated container
        rows.add(names);
        rows.add(units);

        //Values
        for (int r = 0; r < table.getNumRows(); ++r) {
            List<Object> row = new ArrayList<>();
            for (int c = 0; c < table.getNumColumns(); ++c) {
                if (table.getValues() != null
                        && table.getValues()[c] != null
                        && table.getValues()[c].getValueArray() != null
                        && table.getValues()[c].getValueArray()[r] != null) {

                    row.add(table.getValues()[c].getValueArray()[r]);
                } else {
                    row.add("");
                }
            }
            rows.add(row);
        }

        for (List<Object> row : rows) {
            for (int i = 0; i < row.size() - 1; ++i) {
                writer.append(row.get(i).toString())
                        .append(separator);
            }
            for (int i = row.size() - 1; i < row.size(); ++i) {
                writer.append(row.get(i).toString());
            }

            writer.newLine();
        }
        writer.flush();
    }
    //--------------------------------------------------------------------------
}
