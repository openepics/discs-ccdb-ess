package org.openepics.discs.conf.ent;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openepics.discs.conf.util.As;

/**
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Entity
@Table(name = "report_action")
public class ReportAction extends ConfigurationEntity {
    @Basic(optional = false)
    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private ReportFilterAction operation;

    @Basic
    @Column(name = "field")
    private String field;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Basic
    @Column(name = "value")
    private String value;

    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report parentReport;

    protected ReportAction() {
    }

    public ReportAction(ReportFilterAction operation, String field, String value, Report parentReport, String modifiedBy) {
        this.operation = As.notNull(operation);
        this.field = As.notNull(field);
        this.value = As.notNull(value);
        this.parentReport = As.notNull(parentReport);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public ReportAction(ReportFilterAction operation, Property property, String value, Report parentReport, String modifiedBy) {
        this.operation = As.notNull(operation);
        this.property = As.notNull(property);
        this.value = As.notNull(value);
        this.parentReport = As.notNull(parentReport);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public ReportAction(ReportFilterAction operation, Tag tag, Report parentReport, String modifiedBy) {
        if (operation != ReportFilterAction.IS)
            throw new IllegalArgumentException("Tag can only have an \"IS\" operation.");
        this.operation = operation;
        this.tag = As.notNull(tag);
        this.parentReport = As.notNull(parentReport);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public ReportFilterAction getOperation() { return operation; }
    public void setOperation(ReportFilterAction operation) {
        if (tag != null && operation != ReportFilterAction.IS)
            throw new IllegalArgumentException("Tag can only have an \"IS\" operation.");
        this.operation = operation;
    }

    public String getField() { return field; }
    public void setField(String field) {
        this.field = field;
        this.property = null;
        this.tag = null;
    }

    public Property getProperty() { return property; }
    public void setProperty(Property property) {
        this.property = property;
        this.field = null;
        this.tag = null;
    }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) {
        this.tag = tag;
        this.field = null;
        this.property = null;
    }

    public String getValue() { return value; }
    public void setValue(String value) {
        if (tag != null)
            throw new IllegalStateException("Cannot set a value on a filter by tag.");
        this.value = value;
    }

    public Report getParentReport() { return parentReport; }
    public void setParentReport(Report parentReport) { this.parentReport = parentReport; }

    @Override
    public String toString() {
        if (field != null) return "[ Field: " + field + " " + operation.toString() + " " + value + " ]";
        if (property != null) return "[ Property: " + property.getName() + " " + operation.toString() + " " + value + " ]";
        if (tag != null) return "[ Tag IS " + tag.getName() + " ]";
        return "Error! Invalid report filter definition.";
    }
}
