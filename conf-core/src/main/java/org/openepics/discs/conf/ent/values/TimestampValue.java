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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.epics.util.time.Timestamp;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Preconditions;

/**
 * A value representing time and date.
 *
 * @author Miha Vitorovič &lt;miha.vitorovic@cosylab.com&gt;
 *
 */
public class TimestampValue implements Value {
    private final Timestamp timestampValue;

    /** Constructs a new timestamp value
     * @param timestampValue timestamp (see {@link org.epics.util.time.Timestamp})
     */
    public TimestampValue(Timestamp timestampValue) {
        this.timestampValue = Preconditions.checkNotNull(timestampValue);
    }

    /**
     * @return the timestampValue
     */
    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    @Override
    public String toString() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Conversion.DATE_TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final StringBuilder returnString = new StringBuilder(simpleDateFormat.format(timestampValue.toDate()));
        if (timestampValue.getNanoSec() > 0) {
            returnString.append('.').append(Integer.toString(timestampValue.getNanoSec()).replaceAll("0*$", ""));
        }

        return returnString.toString();
    }

    @Override
    public String auditLogString(int... dimensions) {
        if (dimensions.length > 2) {
            throw new IllegalArgumentException("Invalid number of parameter.");
        }
        return this.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimestampValue)) {
            return false;
        }
        TimestampValue other = (TimestampValue) obj;
        if (timestampValue == null) {
            return other.timestampValue == null;
        }

        return timestampValue.equals(other.timestampValue);
    }

}
