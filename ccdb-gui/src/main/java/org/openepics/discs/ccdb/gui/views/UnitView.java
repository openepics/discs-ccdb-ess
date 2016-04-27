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
package org.openepics.discs.ccdb.gui.views;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.ccdb.model.Unit;

/**
 * An UI view object for showing {@link Unit} entity in a table.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class UnitView implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Unit unit;
    private final boolean unitAdd;
    private String usedBy;

    /** Constructs a new UnitView based on fresh (non-existing) {@link Unit} */
    public UnitView() {
        this.unit = new Unit();
        unitAdd = true;
    }

    /** Constructs a new immutable view of the {@link Unit}
     * @param unit the {@link Unit} to base the view on
     */
    public UnitView(Unit unit) {
        this.unit = unit;
        unitAdd = false;
    }

    /** @return the name */
    @NotNull
    @Size(min = 1, max = 32, message = "Name can have at most 32 characters.")
    public String getName() {
        return unit.getName();
    }

    /** @return the description */
    public String getDescription() {
        return unit.getDescription();
    }

    /** @return the symbol */
    @NotNull
    @Size(min = 1, max = 128, message = "Symbol can have at most 128 charactes.")
    public String getSymbol() {
        return unit.getSymbol();
    }

    /** @return the unit */
    @NotNull
    @Size(min = 1, max = 255, message = "Unit can have at most 255 characters.")
    public Unit getUnit() {
        return unit;
    }

    /** @return the usedBy */
    public String getUsedBy() {
        return usedBy;
    }

    /** @param usedBy the usedBy to set */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    /** @param name the name to set */
    public void setName(String name) {
        unit.setName(name);
    }

    /** @param symbol the symbol to set */
    public void setSymbol(String symbol) {
        unit.setSymbol(symbol);
    }

    /** @param description the description to set */
    public void setDescription(String description) {
        unit.setDescription(description);
    }

    /** @return the unitAdd */
    public boolean isUnitAdd() {
        return unitAdd;
    }
}
