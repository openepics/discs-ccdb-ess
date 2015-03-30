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
package org.openepics.seds.api.datatypes;

/**
 * A table. Tables are collections of columns, each of which is composed of a
 * column name, unit, type, and data array. All elements of the same column
 * SHOULD be of the same type.
 * <p>
 * Unit information is stored within the {@link SedsScalarArray} column data.
 *
 * @author Aaron Barber
 */
public interface SedsTable extends SedsType {

    /**
     * Returns the number of rows in the table.
     *
     * @return number of rows
     */
    public Integer getNumRows();

    /**
     * Returns the number of columns in the table.
     *
     * @return number of columns
     */
    public Integer getNumColumns();

    /**
     * Returns the name of each column.
     *
     * @return column names
     */
    public String[] getNames();

    /**
     * Returns the types (represented as strings) of each column.
     *
     * @return column types
     */
    public String[] getColumnTypes();

    /**
     * Returns the column data, where each column is represented as a
     * {@code ScalarArray}.
     *
     * @return column data
     */
    public SedsScalarArray[] getValues();
}
