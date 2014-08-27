package org.openepics.discs.conf.ent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.openepics.discs.conf.util.As;

/**
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Entity
@Table(name = "report")
public class Report extends ConfigurationEntity {
    @Basic(optional = false)
    @Nonnull
    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(name = "filter_by_type", joinColumns = { @JoinColumn(name = "report_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "type_id", referencedColumnName = "id") })
    private List<ComponentType> typeFilters;

    @OneToMany(mappedBy = "parentReport")
    private List<ReportAction> filters;

    protected Report() {
    }

    public Report(String name, String modifiedBy) {
        this.name = As.notNull(name);
        this.typeFilters = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.modifiedAt = new Date();
        this.modifiedBy = modifiedBy;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = As.notNull(name); }

    public List<ComponentType> getTypeFilters() { return typeFilters; }
    public void setTypeFilters(List<ComponentType> typeFilters) {
        if (typeFilters == null)
            this.typeFilters.clear();
        else
            this.typeFilters = typeFilters;
    }

    public List<ReportAction> getFilters() { return filters; }
    public void setFilters(List<ReportAction> filters) {
        for (ReportAction filter : As.notNull(filters))
            if (filter.getParentReport() != this)
                throw new IllegalArgumentException("Filter does not belong to parent. " + filter.toString());
        this.filters = filters;
    }
}
