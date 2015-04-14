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

import org.openepics.discs.conf.util.UnhandledCaseException;

/**
 * System defined slot relation types
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public enum SlotRelationName {
    /** Slot contains another slot */
    CONTAINS,
    /** Slot powers another slot */
    POWERS,
    /** Slot controls another slot */
    CONTROLS;

    /**
     * @return the name of the inverse slot relation. E.g.: "contained in" for "contains" relation.
     */
    public String inverseName() {
        switch(this) {
        case CONTAINS:
            return "contained in";
        case CONTROLS:
            return "controlled by";
        case POWERS:
            return "powered by";
        }
        throw new UnhandledCaseException();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
