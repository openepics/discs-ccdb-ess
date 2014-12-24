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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.Preconditions;

/**
 * An entity that persist information about reports
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Entity
@Table(name = "report")
public class Report extends ConfigurationEntity {
    @Basic(optional = false)
    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name = "filter_by_type", joinColumns = { @JoinColumn(name = "report_id", referencedColumnName = "id") },
               inverseJoinColumns = { @JoinColumn(name = "type_id", referencedColumnName = "id") })
    private List<ComponentType> typeFilters = new ArrayList<>();

    @OneToMany(mappedBy = "parentReport")
    private List<ReportAction> filters = new ArrayList<>();

    protected Report() {
    }

    public Report(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public List<ComponentType> getTypeFilters() {
        return typeFilters;
    }
    public void setTypeFilters(List<ComponentType> typeFilters) {
        if (typeFilters == null)
            this.typeFilters.clear();
        else
            this.typeFilters = typeFilters;
    }

    public List<ReportAction> getFilters() {
        return filters;
    }
    public void setFilters(List<ReportAction> filters) {
        for (ReportAction filter : Preconditions.checkNotNull(filters))
            if (filter.getParentReport() != this)
                throw new IllegalArgumentException("Filter does not belong to parent. " + filter.toString());
        this.filters = filters;
    }
}
