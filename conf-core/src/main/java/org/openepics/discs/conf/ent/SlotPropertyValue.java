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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot_property_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotPropertyValue.findAll", query = "SELECT s FROM SlotPropertyValue s"),
    @NamedQuery(name = "SlotPropertyValue.findBySlotPropId", query = "SELECT s FROM SlotPropertyValue s "
            + "WHERE s.id = :id"),
    @NamedQuery(name = "SlotPropertyValue.findByInRepository", query = "SELECT s FROM SlotPropertyValue s "
            + "WHERE s.inRepository = :inRepository"),
    @NamedQuery(name = "SlotPropertyValue.findByModifiedBy", query = "SELECT s FROM SlotPropertyValue s "
            + "WHERE s.modifiedBy = :modifiedBy")
})
public class SlotPropertyValue extends PropertyValue {
    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    public SlotPropertyValue() { }

    public SlotPropertyValue(boolean inRepository) {
        super(inRepository);
    }

    public Slot getSlot() {
        return slot;
    }
    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "SlotProperty[ slotPropId=" + id + " ]";
    }
}
