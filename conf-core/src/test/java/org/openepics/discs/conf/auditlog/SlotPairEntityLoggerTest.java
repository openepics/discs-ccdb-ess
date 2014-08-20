/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class SlotPairEntityLoggerTest {

    private final Slot parentSlot = new Slot("parentSlot", false);
    private final Slot childSlot = new Slot("childSlot", false);
    private final SlotPair slotPair = new SlotPair(childSlot, parentSlot, new SlotRelation(SlotRelationName.CONTAINS));

    private final SlotPairEntityLogger spel = new SlotPairEntityLogger();

    @Before
    public void setUp() throws Exception {
        parentSlot.setComponentType(new ComponentType());
        childSlot.setComponentType(new ComponentType());
        parentSlot.getChildrenSlotsPairList().add(slotPair);
        childSlot.getParentSlotsPairList().add(slotPair);
    }

    @Test
    public void testGetType() {
        assertTrue(SlotPair.class.equals(spel.getType()));
    }

    @Test
    public void testSerializeEntity() {
        final List<AuditRecord> auditRecords = spel.auditEntries(slotPair, EntityTypeOperation.CREATE);
        
        final String RESULT_1 = "{\"isHostingSlot\":false,\"positionInformation\":{},\"componentType\":null,\"slotPropertyList\":[],\"slotArtifactList\":[],\"childrenSlots\":[],\"parentSlots\":[{\"parentSlot\":\"CONTAINS\"}],\"installationRecordList\":[]}";
        final String RESULT_2 = "{\"isHostingSlot\":false,\"positionInformation\":{},\"componentType\":null,\"slotPropertyList\":[],\"slotArtifactList\":[],\"childrenSlots\":[{\"childSlot\":\"CONTAINS\"}],\"parentSlots\":[],\"installationRecordList\":[]}";
        
        assertEquals(RESULT_1, auditRecords.get(0).getEntry());
        assertEquals(RESULT_2, auditRecords.get(1).getEntry());        
    }

}
