/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ent.values;

import java.util.List;

/**
 * A table is a collection columns.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class DblTableValue extends Value {
    private static final int ROWS = 3;
    private static final int COLS = 5;

    private List<List<Double>> dblTableValue;

    public DblTableValue(List<List<Double>> dblTableValue) {
        this.dblTableValue = dblTableValue;
    }

    /**
     * @return the table
     */
    public List<List<Double>> getDblTableValue() { return dblTableValue; }

    /**
     * @param table the table to set
     */
    public void setDblTableValue(List<List<Double>> dblTableValue) { this.dblTableValue = dblTableValue; }

    @Override
    public String toString() {
        StringBuilder retStr = new StringBuilder() ;
        int cols = 0;
        retStr.append("(double table): [");

        for (List<Double> row : dblTableValue) {
            if (cols > 0) retStr.append(", ");
            if (cols > COLS) {
                retStr.append("...");
                break;
            }

            int rows = 0;
            retStr.append("[");
            for (Double item : row) {
                if (rows > 0) retStr.append(", ");
                if (rows > ROWS) {
                    retStr.append("...");
                    break;
                }
                retStr.append(item);
                rows++;
            }
            retStr.append("]");

            cols++;
        }
        retStr.append(']');

        return retStr.toString();
    }

}
