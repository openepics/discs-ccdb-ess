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
package org.openepics.discs.conf.views;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public enum SlotBuiltInPropertyName implements BuiltInPropertyName {
    BIP_DESCRIPTION("Description"),
    BIP_BEAMLINE_POS("Beamline position"),
    BIP_GLOBAL_X("Global X"),
    BIP_GLOBAL_Y("Global Y"),
    BIP_GLOBAL_Z("Global Z"),
    BIP_GLOBAL_PITCH("Global pitch"),
    BIP_GLOBAL_ROLL("Global roll"),
    BIP_GLOBAL_YAW("Global yaw");
    
    private final String text;

    /**
     * @param text
     */
    private SlotBuiltInPropertyName(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
