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
 * Public interface with possible association string literals.
 *
 */
public interface PropertyAssociation {
    /** The property is only for <i>Component Type</i>, E.g.: a CAD drawing. */
    public static final String TYPE = "TYPE";
    /** The property is only for <i>Slot</i>. */
    public static final String SLOT = "SLOT";
    /** The property is only for <i>Physical Device</i>. */
    public static final String DEVICE = "DEVICE";
    /** The property is for <i>Alignment Record</i>. */
    public static final String ALIGNMENT = "ALIGNMENT";
    /** The property is for <i>Component Type</i>, <i>Slot</i>, <i>Physical Device</i> and <i>Alignment</i>.*/
    public static final String ALL = "ALL";
}
