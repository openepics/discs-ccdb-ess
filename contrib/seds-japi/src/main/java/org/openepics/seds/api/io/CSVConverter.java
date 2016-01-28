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
package org.openepics.seds.api.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import org.openepics.seds.api.datatypes.SedsTable;

/**
 * Interface to convert common {@link SedsType} values to and from
 * comma-separated value (CSV) format.
 *
 * <p>
 * Currently, only the most common data type conversion is supported:
 * {@link SedsTable}.
 *
 * @author Aaron Barber
 */
public interface CSVConverter {

    /**
     * Gets the separator for values of the CSV format.
     *
     * <p>
     * For example, the separator might be a comma (",").
     *
     * @return separator delimiter of the values
     */
    public String getSeparator();

    /**
     * Modifies the CSVConverter to have the parameter as the new separator
     * delimiter, and returns the converter object.
     *
     * @param separator new separator delimiter (such as a comma)
     * @return new converter with the given separator delimiter
     */
    public CSVConverter withSeparator(String separator);

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
    public SedsTable importTable(
            BufferedReader reader
    ) throws IOException;

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
    public void exportTable(
            SedsTable table,
            BufferedWriter writer
    ) throws IOException;
}
