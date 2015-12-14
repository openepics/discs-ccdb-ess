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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.util.TestUtility;

/**
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@RunWith(Arquillian.class)
public class SlotEJBIT {
    @Inject SlotEJB slotService;
    @Inject ComptypeEJB compTypeService;


    @Inject private TestUtility testUtility;

    @Deployment()
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUp() throws Exception {
        testUtility.loginForTests();
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindAll() {
        final List<Slot> slots = slotService.findAll();
        assertNotEquals(slots.size(), 0);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindById() {
        final Slot slot = slotService.findById( slotService.findByName("FE").getId() );
        assertNotNull(slot);
        assertEquals("FE", slot.getName());
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testByIdInvalid() {
        final Slot slot = slotService.findById(12321321321L);
        assertNull(slot);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindByName() {
        final Slot slot = slotService.findByName("FE");
        assertNotNull(slot);
        assertEquals("FE", slot.getName());
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindByNameInvalid() {
        final Slot slot = slotService.findByName("R4nd0m_Stuff");
        assertNull(slot);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    @ApplyScriptAfter(value= {"delete_tags.sql"})
    public void testAdd() {
        final Slot slot = new Slot("ABrandNewSlot", true);

        slot.setDescription("some Description");
        slot.setComment("comment");
        slot.setComponentType( compTypeService.findByName("QM1") );
        slot.setDescription("A description");
        final Tag NEW_TAG = new Tag(UUID.randomUUID().toString());
        slot.getTags().add(NEW_TAG);

        slotService.add(slot);

        final Slot newSlot = slotService.findByName("ABrandNewSlot");
        assertNotNull(newSlot);
        assertEquals("ABrandNewSlot", newSlot.getName());
        assertEquals("A description", newSlot.getDescription());
        assertEquals("comment", newSlot.getComment());
        assertEquals("QM1", newSlot.getComponentType().getName());
        assertTrue(newSlot.getTags().contains(NEW_TAG));
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    @ApplyScriptAfter(value= {"delete_tags.sql"})
    public void testSave() {
        final Slot slot = slotService.findByName("FS1_CSS");

        slot.setDescription("some Description");
        slot.setComment("comment");
        slot.setComponentType( compTypeService.findByName("QM1") );
        slot.setDescription("A description");
        final Tag NEW_TAG = new Tag(UUID.randomUUID().toString());
        slot.getTags().add(NEW_TAG);
        slotService.save(slot);

        final Slot newSlot = slotService.findByName("FS1_CSS");
        assertNotNull(newSlot);
        assertEquals("FS1_CSS", newSlot.getName());
        assertEquals("A description", newSlot.getDescription());
        assertEquals("comment", newSlot.getComment());
        assertEquals("QM1", newSlot.getComponentType().getName());
        assertTrue(newSlot.getTags().contains(NEW_TAG));
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    @ApplyScriptAfter(value= {"delete_tags.sql"})
    public void testDelete() {
        final Slot slot = slotService.findByName("FS1_CSS");

        slotService.delete(slot);

        assertNull( slotService.findByName("FS1_CSS") );
    }

    /* TODO add later
    @Test
    @UsingDataSet(value={"component_type.xml", "unit.xml", "property.xml", "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testAddSlotProp() {
        final ComponentType compType = slotService.findByName(SEARCH_COMP_TYPE_NAME);

        final SlotPropertyValue compValue = new SlotPropertyValue(false);
        compValue.setProperty( propertyService.findByName("CURRENT") );
        compValue.setComponentType(compType);
        compValue.setUnit( null );
        final String propValue = "33.45";
        compValue.setPropValue(propValue);

        slotService.addChild(compValue);

        final ComponentType newCompType = slotService.findByName(SEARCH_COMP_TYPE_NAME);
        assertNotNull(newCompType);
        assertTrue(newCompType.getSlotPropertyList().contains(compValue));
        final String newPropValue = newCompType.getSlotPropertyList().get( newCompType.getSlotPropertyList().indexOf(compValue) ).getPropValue();
        assertEquals(newPropValue, propValue);
    }

    @Test
    @UsingDataSet(value={"component_type.xml", "unit.xml", "property.xml", "comptype_property_value.xml"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testSaveSlotProp() {
        final ComponentType compType = slotService.findByName(SEARCH_COMP_TYPE_NAME);
        final SlotPropertyValue compValue = compType.getSlotPropertyList().get(0);
        final String propValue = "22";
        compValue.setPropValue(propValue);
        slotService.saveChild(compValue);

        final ComponentType newCompType = slotService.findByName(SEARCH_COMP_TYPE_NAME);
        final String newPropValue = newCompType.getSlotPropertyList().get( newCompType.getSlotPropertyList().indexOf(compValue) ).getPropValue();
        assertEquals(propValue, newPropValue);
    }

    @Test
    @UsingDataSet(value={"component_type.xml", "unit.xml", "property.xml", "comptype_property_value.xml"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testDeleteSlotProp() {
        final ComponentType compType = slotService.findByName(SEARCH_COMP_TYPE_NAME);
        final SlotPropertyValue compValue = compType.getSlotPropertyList().get(0);

        slotService.deleteChild(compValue);

        final ComponentType newCompType = slotService.findByName(SEARCH_COMP_TYPE_NAME);
        assertFalse(newCompType.getSlotPropertyList().contains(compValue));
    }*/

}
