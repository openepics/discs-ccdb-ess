/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.conf.ui.lazymodels;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ent.fields.UnitFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.views.UnitView;
import org.primefaces.model.SortOrder;

public class UnitLazyModel extends CCDBLazyModel<UnitView> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UnitLazyModel.class.getCanonicalName());

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String SYMBOL = "symbol";

    final private UnitEJB unitEJB;

    public UnitLazyModel(UnitEJB unitEJB) {
        this.unitEJB = unitEJB;
    }

    @Override
    public List<UnitView> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINEST, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINEST, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.FINER, "filter[" + filterKey + "] = " + filters.get(filterKey).toString());
        }

        setLatestLoadData(sortField, sortOrder, filters);

        final String name = filters.containsKey(NAME) ? filters.get(NAME).toString() : null;
        final String description = filters.containsKey(DESCRIPTION) ? filters.get(DESCRIPTION).toString() : null;
        final String symbol = filters.containsKey(SYMBOL) ? filters.get(SYMBOL).toString() : null;

        // sort by name is the default
        final List<Unit> results = unitEJB.findLazy(first, pageSize,
                selectSortField(sortField), (sortField == null) ? org.openepics.discs.conf.util.SortOrder.ASCENDING :
                UiUtility.translateToCCDBSortOrder(sortOrder),
                name, description, symbol);

        final List<UnitView> transformedResults = results == null ? null
                                                    : results.stream().map(UnitView::new).collect(Collectors.toList());
        setEmpty(first, transformedResults);

        return transformedResults;
    }

    @Override
    public Object getRowKey(UnitView object) {
        return object.getUnit().getId();
    }

    @Override
    public UnitView getRowData(String rowKey) {
        final Unit foundUnit = unitEJB.findById(Long.parseLong(rowKey));
        return foundUnit != null ? new UnitView(foundUnit) : null;
    }

    @Override
    public int getRowCount() {
        final long rowCount = unitEJB.getRowCount();
        return rowCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)rowCount;
    }

    private UnitFields selectSortField(final String sortField) {
        if (sortField == null) return UnitFields.NAME;

        switch (sortField) {
        case NAME:
            return UnitFields.NAME;
        case DESCRIPTION:
            return UnitFields.DESCRIPTION;
        case SYMBOL:
            return UnitFields.SYMBOL;
        default:
            return null;
        }
    }
}
