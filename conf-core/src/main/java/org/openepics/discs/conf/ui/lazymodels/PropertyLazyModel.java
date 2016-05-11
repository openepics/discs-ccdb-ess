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

import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.fields.PropertyFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.views.PropertyView;
import org.primefaces.model.SortOrder;

public class PropertyLazyModel extends CCDBLazyModel<PropertyView> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PropertyLazyModel.class.getCanonicalName());

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String UNIT = "unit.name";
    private static final String DATA_TYPE = "dataType.name";

    private final PropertyEJB propertyEJB;

    public PropertyLazyModel(final PropertyEJB propertyEJB) {
        super(propertyEJB);
        this.propertyEJB = propertyEJB;
    }

    @Override
    public List<PropertyView> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINEST, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINEST, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.FINER, "filter[" + filterKey + "] = " + filters.get(filterKey).toString());
        }

        setLatestLoadData(sortField, sortOrder, filters);

        final String name = filters.containsKey(NAME) ? filters.get(NAME).toString() : null;
        final String description = filters.containsKey(DESCRIPTION) ? filters.get(DESCRIPTION).toString() : null;
        final String unit = filters.containsKey(UNIT) ? filters.get(UNIT).toString() : null;
        final String dataType = filters.containsKey(DATA_TYPE) ? filters.get(DATA_TYPE).toString() : null;

        // sort by name is the default
        final List<Property> results = propertyEJB.findLazy(first, pageSize,
                selectSortField(sortField), (sortField == null) ? org.openepics.discs.conf.util.SortOrder.ASCENDING :
                UiUtility.translateToCCDBSortOrder(sortOrder),
                name, description, unit, dataType);

        final List<PropertyView> transformedResults = results == null ? null : results.stream().map(PropertyView::new).
                                                                                        collect(Collectors.toList());
        setEmpty(first, transformedResults);

        return transformedResults;
    }

    @Override
    public Object getRowKey(PropertyView object) {
        return object.getDataType().getId();
    }

    @Override
    public PropertyView getRowData(String rowKey) {
        final Property foundProp = propertyEJB.findById(Long.parseLong(rowKey));
        return foundProp != null ? new PropertyView(foundProp) : null;
    }

    private PropertyFields selectSortField(final String sortField) {
        if (sortField == null) return PropertyFields.NAME;

        switch (sortField) {
        case NAME:
            return PropertyFields.NAME;
        case DESCRIPTION:
            return PropertyFields.DESCRIPTION;
        case UNIT:
            return PropertyFields.UNIT;
        case DATA_TYPE:
            return PropertyFields.DATA_TYPE;
        default:
            return null;
        }
    }


}
