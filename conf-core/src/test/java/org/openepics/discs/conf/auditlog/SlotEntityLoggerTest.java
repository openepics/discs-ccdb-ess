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
package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.values.IntValue;

/**
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
public class SlotEntityLoggerTest {

    private final Property prop1 = new Property("DETER", "deter");
    private final SlotPropertyValue slotPropVal1 = new SlotPropertyValue(true);
    private final Property prop2 = new Property("APERTURE", "aperture");
    private final SlotPropertyValue slotPropVal2 = new SlotPropertyValue(false);
    private final Slot slot = new Slot("Test slot", true);
    private final SlotArtifact artifact1 = new SlotArtifact("CAT Image", true, "Simple CAT image", "/var/usr/images/CAT");
    private final SlotArtifact artifact2 = new SlotArtifact("Manual", false, "Users manual", "www.deteriorator.com/user-manual");
    private final SlotRelation contains = new SlotRelation(SlotRelationName.CONTAINS);

    final private SlotEntityLogger sel = new SlotEntityLogger();

    @Before
    public void setUp() throws Exception {
        slotPropVal1.setProperty(prop1);
        slotPropVal2.setProperty(prop2);
        slotPropVal1.setPropValue(new IntValue(10));
        slotPropVal2.setPropValue(new IntValue(20));
        slot.getSlotPropertyList().add(slotPropVal1);
        slot.getSlotPropertyList().add(slotPropVal2);
        slot.getSlotArtifactList().add(artifact1);
        slot.getSlotArtifactList().add(artifact2);

        final ComponentType slotComponentType = new ComponentType("slotCompType");
        slot.setComponentType(slotComponentType);

        // set up child
        final Slot childSlot = new Slot("childSlot", false);
        childSlot.setComponentType(slotComponentType);

        // set up parent
        final Slot parentSlot = new Slot("parentSlot", false);
        parentSlot.setComponentType(slotComponentType);

        // set up pairs
        final SlotPair slotToChildRelationship = new SlotPair(childSlot, slot, contains);
        final SlotPair slotToParentRelationship = new SlotPair(slot, parentSlot, contains);

        // set up slot connections
        childSlot.getPairsInWhichThisSlotIsAChildList().add(slotToChildRelationship);
        parentSlot.getPairsInWhichThisSlotIsAParentList().add(slotToParentRelationship);
        slot.getPairsInWhichThisSlotIsAParentList().add(slotToChildRelationship);
        slot.getPairsInWhichThisSlotIsAChildList().add(slotToParentRelationship);
    }

    @Test
    public void testGetType() {
        assertTrue(Slot.class.equals(sel.getType()));
    }

    @Test
    public void testSerializeEntity() {
        final String RESULT = "{\"hostingSlot\":true,\"componentType\":\"slotCompType\",\"slotPropertyList\":["
                + "{\"APERTURE\":\"20\"},{\"DETER\":\"10\"}],"
                + "\"slotArtifactList\":[{\"CAT Image\":\"/var/usr/images/CAT\"},"
                + "{\"Manual\":\"www.deteriorator.com/user-manual\"}],"
                + "\"childrenSlots\":[{\"childSlot\":\"[Contains]\"}],"
                + "\"parentSlots\":[{\"parentSlot\":\"[Contained in]\"}]}";

        assertEquals(RESULT, sel.auditEntries(slot, EntityTypeOperation.CREATE).get(0).getEntry());
    }
}
