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
package org.openepics.discs.conf.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.testutil.TestUtility;

@RunWith(Arquillian.class)
public class SlotRelationEJBIT {
    @Inject private SlotRelationEJB slotRelationService;
    @Inject private TestUtility testUtility;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Deployment()
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUp() throws Exception {
        testUtility.loginForTests();
    }

    @Test
    public void testFindById() {
        // findById is implicitly tested (called by refreshEntity)
        final SlotRelation slotRelation = slotRelationService.
                                refreshEntity(slotRelationService.findBySlotRelationName(SlotRelationName.CONTAINS));
        assertNotNull(slotRelation);
        assertEquals(SlotRelationName.CONTAINS, slotRelation.getName());
    }

    @Test
    public void testFindByName() {
        final SlotRelation slotRelation = slotRelationService.findBySlotRelationName(SlotRelationName.CONTAINS);
        assertNotNull(slotRelation);
        assertEquals(slotRelation.getName(), SlotRelationName.CONTAINS);
    }

    @Test
    public void testFindAll() {
        final List<SlotRelation> slotRelations = slotRelationService.findAll();
        assertNotNull(slotRelations);
        assertEquals(slotRelations.size(), 3);
    }
}
