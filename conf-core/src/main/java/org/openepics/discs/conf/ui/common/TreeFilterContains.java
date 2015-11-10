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
package org.openepics.discs.conf.ui.common;

import javax.annotation.Nullable;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;

/**
 * This interface implements the method to be used for filtering the hierarchy tree.
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class TreeFilterContains implements TreeFilterMethod {
    /**
     * @param filterValue the valuer to check against
     * @param installationSlotType the type of the installation the candidate must match
     * @param candidate the candidate to check for match (the match is in the name)
     * @return <code>true</code>, if the <code>candidate</code> matches the <code>filterValue</code>, or if either
     * <code>candidate</code> or <code>filterValue</code> are <code>null</code>.
     */
    @Override
    public boolean matches(@Nullable String filterValue, @Nullable ComponentType installationSlotType,
            @Nullable Slot candidate) {
        if (candidate == null) return false;
        if (filterValue == null) return true;
        return candidate.getName().toUpperCase().contains(filterValue.toUpperCase());
    }
}
