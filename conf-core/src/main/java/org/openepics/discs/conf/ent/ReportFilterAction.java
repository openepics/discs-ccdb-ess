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
package org.openepics.discs.conf.ent;

/**
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public enum ReportFilterAction {
    /** Property value is a string (or enum) and contains a substring <code>filter</code>.
     *  Entities without this property will not be included. */
    CONTAINS,
    /** Property value is a string (or enum) and its value is <code>filter</code>.
     * Entities without this property will not be included. */
    IS,
    /** Property value is a string (or enum) and it starts with <code>filter</code>.
     * Entities without this property will not be included. */
    STARTS_WITH,
    /** The value of the actual property does not matter, but the entity will be included in the report,
     * if it has this property. */
    DISPLAY_ONLY,
    /** Property value is a scalar number and <code>propertyValue == filter</code>. */
    EQ,
    /** Property value is a scalar number and <code>propertyValue != filter</code>. */
    NE,
    /** Property value is a scalar number and <code>propertyValue &lt; filter</code>. */
    LT,
    /** Property value is a scalar number and <code>propertyValue &lt;= filter</code>. */
    LE,
    /** Property value is a scalar number and <code>propertyValue &gt; filter</code>. */
    GT,
    /** Property value is a scalar number and <code>propertyValue &gt;= filter</code>. */
    GE
}
