package org.openepics.discs.conf.ejb;

import static org.junit.Assert.*;

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
import org.openepics.discs.conf.util.TestUtility;

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
        final SlotRelation slotRelation = slotRelationService.findById( slotRelationService.findBySlotRelationName(SlotRelationName.CONTAINS).getId() );
        assertNotNull(slotRelation);
        assertEquals(slotRelation.getName(), SlotRelationName.CONTAINS);
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
