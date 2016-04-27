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

import org.openepics.discs.conf.ent.ComptypePropertyValue;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public enum EntityAttributeViewKind {

    DEVICE_TYPE_PROPERTY("Device type property"),
    DEVICE_TYPE_ARTIFACT("Device type artifact"),
    DEVICE_TYPE_TAG("Device type tag"),
    INSTALL_SLOT_PROPERTY("Installation slot property"),
    INSTALL_SLOT_ARTIFACT("Installation slot artifact"),
    INSTALL_SLOT_TAG("Installation slot tag"),
    CONTAINER_SLOT_PROPERTY("Container property"),
    CONTAINER_SLOT_ARTIFACT("Container artifact"),
    CONTAINER_SLOT_TAG("Container tag"),
    DEVICE_PROPERTY("Device property"),
    DEVICE_ARTIFACT("Device artifact"),
    DEVICE_TAG("Device tag"),
    UNKNOWN_PROPERTY("Unknown type property");

    private final String text;

    /**
     * @param text
     */
    private EntityAttributeViewKind(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }


    /** The method to map the database entity into the EntityAttributeViewKind
     * @param property the {@link ComptypePropertyValue} to determine the kind for
     * @return the EntityAttributeViewKind
     */
    public static EntityAttributeViewKind getPropertyValueKind(ComptypePropertyValue property) {
        if (property.isDefinitionTargetSlot()) {
            return EntityAttributeViewKind.INSTALL_SLOT_PROPERTY;
        } else if (property.isDefinitionTargetDevice()) {
            return EntityAttributeViewKind.DEVICE_PROPERTY;
        } else {
            return EntityAttributeViewKind.DEVICE_TYPE_PROPERTY;
        }
    }
}
