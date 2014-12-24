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

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link PropertyValue} used for {@link AlignmentRecord}s
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_property_value", indexes = { @Index(columnList = "alignment_record") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentPropertyValue.findAll", query = "SELECT a FROM AlignmentPropertyValue a"),
    @NamedQuery(name = "AlignmentPropertyValue.findByAlignPropId", query = "SELECT a FROM AlignmentPropertyValue a "
            + "WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentPropertyValue.findByInRepository", query = "SELECT a FROM AlignmentPropertyValue a "
            + "WHERE a.inRepository = :inRepository"),
    @NamedQuery(name = "AlignmentPropertyValue.findByModifiedBy", query = "SELECT a FROM AlignmentPropertyValue a "
            + "WHERE a.modifiedBy = :modifiedBy")
})
public class AlignmentPropertyValue extends PropertyValue {
    @JoinColumn(name = "alignment_record")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    public AlignmentPropertyValue() { }

    public AlignmentPropertyValue(boolean inRepository) {
        super(inRepository);
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }
    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    @Override
    public String toString() {
        return "AlignmentProperty[ alignPropId=" + id + " ]";
    }
}
