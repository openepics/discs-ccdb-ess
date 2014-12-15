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
public enum DeviceBuiltInPropertyName implements BuiltInPropertyName {
    
    BIP_DESCRIPTION("Description"),
    BIP_LOCATION("Location"),
    BIP_MANUFACTURER("Manufacturer"),
    BIP_MANUFACTURER_MODEL("Manufacturer model"),
    BIP_MANUFACTURER_SERIAL_NO("Manufacturer serial #"),
    BIP_P_O_REFERENCE("Purchase order reference"),
    BIP_STATUS("Status");
    
    private final String text;

    /**
     * @param text
     */
    private DeviceBuiltInPropertyName(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
