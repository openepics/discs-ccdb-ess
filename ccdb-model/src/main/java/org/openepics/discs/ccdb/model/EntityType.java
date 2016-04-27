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
 * An enum used to represent different entity types supported by the database
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public enum EntityType {
    DEVICE("Device"),
    SLOT("Slot"),
    COMPONENT_TYPE("Device type"),
    USER("User"),
    INSTALLATION_RECORD("Installation record"),
    ALIGNMENT_RECORD("Alignment record"),
    MENU("Menu"),
    UNIT("Unit"),
    PROPERTY("Property"),
    DATA_TYPE("Enumeration");

    private final String label;

    private EntityType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
