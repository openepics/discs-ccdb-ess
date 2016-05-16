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

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.fields.DeviceTypeFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.views.ComponentTypeView;
import org.primefaces.model.SortOrder;

public class ComponentTypeLazyModel extends CCDBLazyModel<ComponentTypeView> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ComponentTypeLazyModel.class.getCanonicalName());

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    private final ComptypeEJB comptypeEJB;

    public ComponentTypeLazyModel(ComptypeEJB comptypeEJB) {
        super(comptypeEJB);
        this.comptypeEJB = comptypeEJB;
    }

    @Override
    public List<ComponentTypeView> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINEST, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINEST, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.FINER, "filter[" + filterKey + "] = " + filters.get(filterKey).toString());
        }

        setLatestLoadData(sortField, sortOrder, filters);

        final String name = filters.containsKey(NAME) ? filters.get(NAME).toString() : null;
        final String description = filters.containsKey(DESCRIPTION) ? filters.get(DESCRIPTION).toString() : null;

        // sort by name is the default
        final List<ComponentType> results = comptypeEJB.findLazy(first, pageSize,
                selectSortField(sortField), (sortField == null) ? org.openepics.discs.conf.util.SortOrder.ASCENDING :
                UiUtility.translateToCCDBSortOrder(sortOrder),
                name, description);

        final List<ComponentTypeView> transformedResults = results == null ? null
                                            : results.stream().map(ComponentTypeView::new).collect(Collectors.toList());
        setEmpty(first, transformedResults);

        return transformedResults;
    }

    @Override
    public Object getRowKey(ComponentTypeView object) {
        return object.getComponentType().getId();
    }

    @Override
    public ComponentTypeView getRowData(String rowKey) {
        final ComponentType foundDeviceType = comptypeEJB.findById(Long.parseLong(rowKey));
        return foundDeviceType != null ? new ComponentTypeView(foundDeviceType) : null;
    }

    private DeviceTypeFields selectSortField(final String sortField) {
        if (sortField == null) return DeviceTypeFields.NAME;

        switch (sortField) {
        case NAME:
            return DeviceTypeFields.NAME;
        case DESCRIPTION:
            return DeviceTypeFields.DESCRIPTION;
        default:
            return null;
        }
    }


}
