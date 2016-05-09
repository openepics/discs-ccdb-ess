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

import javax.annotation.Nullable;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * This is the CCDB extension to lazy loading. It takes care of the the data that needs to be known for exporting.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 * @param <T> The class to display in the data table
 */
public class CCDBLazyModel<T> extends LazyDataModel<T> {
    private static final long serialVersionUID = 1L;

    private boolean empty = true;
    private String sortField;
    private SortOrder sortOrder;
    private Map<String, Object> filters;

    /** @return <code>true</code> if the current filter returns no data, <code>false</code> otherwise */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * The lazy data collection is empty, if it returns no data for the first page.
     * Set from {@link LazyDataModel#load(int, int, String, SortOrder, Map)}
     *
     * @param first the index of the first element to be loaded
     * @param results the database results
     */
    protected void setEmpty(final int first, final @Nullable List<T> results) {
        empty = (first == 0) && ((results == null) || results.isEmpty());
    }

    /**
     * Important parameters of the data load request
     * Set from {@link LazyDataModel#load(int, int, String, SortOrder, Map)}
     * @param sortField name of the sort field
     * @param sortOrder the sort order
     * @param filters active filters
     */
    protected void setLatestLoadData(final @Nullable String sortField, final @Nullable SortOrder sortOrder,
            final @Nullable Map<String, Object> filters) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.filters = filters;
    }

    public String getSortField() {
        return sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }
}
