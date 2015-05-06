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
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
public enum EntityAttributeViewKind {

    BUILT_IN_PROPERTY("Built-in property"),
    DEVICE_TYPE_PROPERTY("Device type property"),
    DEVICE_TYPE_ARTIFACT("Device type artifact"),
    DEVICE_TYPE_TAG("Device type tag"),
    INSTALL_SLOT_PROPERTY("Installation slot property"),
    INSTALL_SLOT_ARTIFACT("Installation slot artifact"),
    INSTALL_SLOT_TAG("Installation slot tag"),
    CONTAINER_SLOT_PROPERTY("Container property"),
    CONTAINER_SLOT_ARTIFACT("Container artifact"),
    CONTAINER_SLOT_TAG("Container tag"),
    DEVICE_PROPERTY("Device instance property"),
    DEVICE_ARTIFACT("Device artifact"),
    DEVICE_TAG("Device tag"),
    ARTIFACT("Artifact"),
    PROPERTY("Property"),
    TAG("Tag"),
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
}
