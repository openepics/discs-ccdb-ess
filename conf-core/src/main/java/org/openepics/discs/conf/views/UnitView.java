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

import org.openepics.discs.conf.ent.Unit;

/**
 * An UI view object for showing {@link Unit} entity in a table.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class UnitView {
    private final String name;
    private final String description;
    private final String symbol;

    private final Unit unit;

    private String usedBy;

    /** Constructs a new immutable view of the {@link Unit}
     * @param unit the {@link Unit} to base the view on
     */
    public UnitView(Unit unit) {
        name = unit.getName();
        description = unit.getDescription();
        symbol = unit.getSymbol();

        this.unit = unit;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /** @return the symbol */
    public String getSymbol() {
        return symbol;
    }

    /** @return the unit */
    public Unit getUnit() {
        return unit;
    }

    /**
     * @return the usedBy
     */
    public String getUsedBy() {
        return usedBy;
    }

    /**
     * @param usedBy the usedBy to set
     */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }


}
