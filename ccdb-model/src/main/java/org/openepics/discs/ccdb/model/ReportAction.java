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
package org.openepics.discs.ccdb.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Preconditions;

/**
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Entity
@Table(name = "report_action")
public class ReportAction extends ConfigurationEntity {
    private static final long serialVersionUID = -7839739114053750431L;

    @Basic(optional = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report parentReport;

    protected ReportAction() {
    }

    /** Constructs a new report action
     * @param operation The operation to be used (see {@link ReportFilterAction})
     * @param field The name of the built-in property (database field)
     * @param value The value to use when performing the action
     * @param parentReport The {@link Report} this action is used in
     * @param modifiedBy The user adding this action
     */
    public ReportAction(ReportFilterAction operation, String field, String value,
            Report parentReport, String modifiedBy) {
        this.operation = Preconditions.checkNotNull(operation);
        this.field = Preconditions.checkNotNull(field);
        this.value = Preconditions.checkNotNull(value);
        this.parentReport = Preconditions.checkNotNull(parentReport);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    /** Constructs a new report action
     * @param operation The operation to be used (see {@link ReportFilterAction})
     * @param property The {@link Property} to use
     * @param value The value to use when performing the action
     * @param parentReport The {@link Report} this action is used in
     * @param modifiedBy The user adding this action
     */
    public ReportAction(ReportFilterAction operation, Property property, String value,
            Report parentReport, String modifiedBy) {
        this.operation = Preconditions.checkNotNull(operation);
        this.property = Preconditions.checkNotNull(property);
        this.value = Preconditions.checkNotNull(value);
        this.parentReport = Preconditions.checkNotNull(parentReport);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    /** Constructs a new report action
     * @param operation The operation to be used (see {@link ReportFilterAction})
     * @param tag The {@link Tag} to use
     * @param parentReport The {@link Report} this action is used in
     * @param modifiedBy The user adding this action
     */
    public ReportAction(ReportFilterAction operation, Tag tag, Report parentReport, String modifiedBy) {
        if (operation != ReportFilterAction.IS)
            throw new IllegalArgumentException("Tag can only have an \"IS\" operation.");
        this.operation = operation;
        this.tag = Preconditions.checkNotNull(tag);
        this.parentReport = Preconditions.checkNotNull(parentReport);
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    /**
     * @return The {@link ReportFilterAction} used in this action
     */
    public ReportFilterAction getOperation() {
        return operation;
    }
    /**
     * @param operation The {@link ReportFilterAction} to use in this action. The makes sure that only "IS" operation
     * is used on a {@link Tag}
     */
    public void setOperation(ReportFilterAction operation) {
        if (tag != null && operation != ReportFilterAction.IS)
            throw new IllegalArgumentException("Tag can only have an \"IS\" operation.");
        this.operation = operation;
    }

    /**
     * @return The name of the built-in property (database field) to use in this action
     */
    public String getField() {
        return field;
    }
    /**
     * @param field The name of the built-in property (database field) to use in this action
     */
    public void setField(String field) {
        this.field = field;
        this.property = null;
        this.tag = null;
    }

    /**
     * @return The {@link Property} to use in the action
     */
    public Property getProperty() {
        return property;
    }
    /**
     * @param property The {@link Property} to use in the action
     */
    public void setProperty(Property property) {
        this.property = property;
        this.field = null;
        this.tag = null;
    }

    /**
     * @return The {@link Tag} to use in the action
     */
    public Tag getTag() {
        return tag;
    }
    /**
     * @param tag The {@link Tag} to use in the action
     */
    public void setTag(Tag tag) {
        this.tag = tag;
        this.field = null;
        this.property = null;
    }

    /**
     * @return The value to use when performing the action
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value The value to use when performing the action
     */
    public void setValue(String value) {
        if (tag != null)
            throw new IllegalStateException("Cannot set a value on a filter by tag.");
        this.value = value;
    }

    /**
     * @return The {@link Report} this action is used in
     */
    public Report getParentReport() {
        return parentReport;
    }
    /**
     * @param parentReport The {@link Report} this action is used in
     */
    public void setParentReport(Report parentReport) {
        this.parentReport = parentReport;
    }

    @Override
    public String toString() {
        final String reportActionText;
        if (field != null) {
            reportActionText = "[ Field: " + field + " " + operation.toString() + " " + value + " ]";
        } else  if (property != null) {
            reportActionText = "[ Property: " + property.getName() + " " + operation.toString() + " " + value + " ]";
        } else if (tag != null) {
            reportActionText = "[ Tag IS " + tag.getName() + " ]";
        } else {
            reportActionText = "Error! Invalid report filter definition.";
        }
        return reportActionText;
    }
}
