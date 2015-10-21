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
 * System defined slot relation types
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
public enum SlotRelationName {
    /** Slot contains another slot */
    CONTAINS("Contains", "Contained in"),
    /** Slot controls another slot */
    CONTROLS("Controls", "Controlled by"),
    /** Slot powers another slot */
    POWERS("Powers", "Powered by");

    private String name, inverseName;

    private SlotRelationName(String name, String inverseName) {
        this.name = name;
        this.inverseName = inverseName;
    }

    /**
     * @return the name of the inverse slot relation. E.g.: "contained in" for "contains" relation.
     */
    public String inverseName() {
        return inverseName;
    }

    @Override
    public String toString() {
        return name;
    }
}
