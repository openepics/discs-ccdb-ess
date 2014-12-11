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
public enum EntityAttributeViewKind {
    
    INSTALL_SLOT("Installation slot"),
    CONTAINER_SLOT("Container"),
    DEVICE("Device"),
    DEVICE_TYPE("Device type"), 
    BUILT_IN("Built-in"),
    PROPERTY_SUFFIX("property"),
    ARTIFACT_SUFFIX("artifact"),
    ARTIFACT("Artifact"),
    PROPERTY("Property"),
    TAG_SUFFIX("tag"),
    TAG("Tag"),
    UNKNOWN("Unknown type");       
    
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
