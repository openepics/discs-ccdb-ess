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
 * Enumerator that marks current status of the {@link Device}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
public enum DeviceStatus {
    DEFINED("Defined"),
    IN_FABRICATION("In fabrication"),
    UNDER_TESTING("Under testing"),
    UNDER_REPAIR("Under repair"),
    READY("Ready"),
    SPARE("Spare");

    private String label;

    private DeviceStatus(String label) {
        this.label = label;
    }

    /**
     * Gets the string representation of the enum value
     *
     * @return string representation of the enum value
     */
    public String getLabel() {
        return label;
    }
}
