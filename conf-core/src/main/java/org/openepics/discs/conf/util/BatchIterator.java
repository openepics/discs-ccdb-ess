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
package org.openepics.discs.conf.util;

import java.util.Iterator;

import com.google.common.base.Preconditions;

/**
 * Iterates over a set of indexes starting at startIndex, finishing at endIndex and prepending leading zeroes if
 * required.
 *
 * @author Miha Vitorovič &lt;miha.vitorovic@cosylab.com&gt;
 */
public class BatchIterator implements Iterator<String> {
    private int index;
    private int endIndex;
    private String prefix;
    private int indexLen;

    /** Constructs a batch iterator.
     *
     * @param startIndex the first index to use
     * @param endIndex the last index to use
     * @param leadingZeroes number of leading zeroes to prepend if greater than 0
     */
    public BatchIterator(int startIndex, int endIndex, int leadingZeroes) {
        Preconditions.checkArgument(startIndex >= 0);
        Preconditions.checkArgument(endIndex > startIndex);
        Preconditions.checkArgument(leadingZeroes >= 0);
        index = startIndex;
        this.endIndex = endIndex;
        final StringBuilder sb = new StringBuilder(leadingZeroes);
        for (int i = 0; i < leadingZeroes; i++) {
            sb.append('0');
        }
        prefix = sb.toString();
        if (leadingZeroes == 0) {
            indexLen = 0;
        } else {
            indexLen = (prefix + leadingZeroes).length();
        }
    }

    @Override
    public boolean hasNext() {
        return index <= endIndex;
    }

    @Override
    public String next() {
        final String indexOnly = Integer.toString(index);
        final String indexStr = prefix + indexOnly;
        index++;
        return indexStr.substring(indexStr.length() - Math.max(indexOnly.length(), indexLen));
    }

    @Override
    public void remove() {
        // nothing to remove
    }
}
