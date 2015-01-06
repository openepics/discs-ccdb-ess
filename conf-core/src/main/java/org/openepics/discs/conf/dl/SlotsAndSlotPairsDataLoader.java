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
package org.openepics.discs.conf.dl;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.apache.commons.lang3.tuple.Pair;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;


/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Stateless
public class SlotsAndSlotPairsDataLoader extends AbstractDataLoader implements DataLoader {
    @Override
    protected List<String> getKnownColumnNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getUniqueColumnName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleDelete() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleRename() {
        // TODO Auto-generated method stub

    }

    public DataLoaderResult loadDataToDatabase(List<Pair<Integer, List<String>>> firstFileInputRows,
            List<Pair<Integer, List<String>>> secondFileInputRows, String firstFileName, String secondFileName) {
        // TODO Auto-generated method stub
        return null;
    }
}
