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

import org.epics.util.time.Timestamp;

import com.google.common.base.Preconditions;

/**
 * A value representing time and date.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class TimestampValue implements Value {
    private final Timestamp timestampValue;

    public TimestampValue(Timestamp timestampValue) {
        this.timestampValue = Preconditions.checkNotNull(timestampValue);
    }

    /**
     * @return the timestampValue
     */
    public Timestamp getTimestampValue() { return timestampValue; }

    @Override
    public String toString() { return timestampValue.toString(); }

    @Override
    public String auditLogString(int... dimensions) { return timestampValue.toString(); }

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
