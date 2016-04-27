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
package org.openepics.seds.core.datatypes;

import java.util.Arrays;
import java.util.Objects;

import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;

class ITable implements SedsTable {

    private final Integer numRows;
    private final Integer numColumns;
    private final String[] names;
    private final String[] columnTypes;
    private final SedsScalarArray[] values;

    ITable(Integer numRows, Integer numColumns, String[] names, String[] columnTypes, SedsScalarArray[] values) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.names = names;
        this.columnTypes = columnTypes;
        this.values = values;
    }

    @Override
    public Integer getNumRows() {
        return numRows;
    }

    @Override
    public Integer getNumColumns() {
        return numColumns;
    }

    @Override
    public String[] getNames() {
        return names;
    }

    @Override
    public String[] getColumnTypes() {
        return columnTypes;
    }

    @Override
    public SedsScalarArray[] getValues() {
        return values;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(columnTypes);
        result = prime * result + Arrays.hashCode(names);
        result = prime * result + ((numColumns == null) ? 0 : numColumns.hashCode());
        result = prime * result + ((numRows == null) ? 0 : numRows.hashCode());
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ITable other = (ITable) obj;
        if (!Objects.equals(this.numRows, other.numRows)) {
            return false;
        }
        if (!Objects.equals(this.numColumns, other.numColumns)) {
            return false;
        }
        if (!Arrays.deepEquals(this.names, other.names)) {
            return false;
        }
        if (!Arrays.deepEquals(this.columnTypes, other.columnTypes)) {
            return false;
        }
        if (!Arrays.deepEquals(this.values, other.values)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ITable [numRows=" + numRows + ", numColumns=" + numColumns + ", names=" + Arrays.toString(names)
                + ", columnTypes=" + Arrays.toString(columnTypes) + ", values=" + Arrays.toString(values) + "]";
    }

}
