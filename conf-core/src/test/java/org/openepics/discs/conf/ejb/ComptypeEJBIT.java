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
import static org.junit.Assert.assertFalse;
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
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.IntValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.TestUtility;

/**
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@RunWith(Arquillian.class)
public class ComptypeEJBIT {
    @Inject ComptypeEJB compTypesService;
    @Inject PropertyEJB propertyService;

    @Inject private TestUtility testUtility;

    private static final long SEARCH_COMP_TYPE_ID_INVALID = 1000000;

    private static final String SEARCH_COMP_TYPE_NAME = "QSM1";
    private static final String SEARCH_COMP_TYPE_NAME_INVALID = "ThisShouldNotExist";

    @Deployment()
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUp() throws Exception {
        testUtility.loginForTests();
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    public void testFindComponentType() {
        final List<ComponentType> compTypes = compTypesService.findAll();
        assertNotEquals(compTypes.size(), 0);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    public void testFindComponentTypeLong() {
        final ComponentType compType = compTypesService.findById( compTypesService.findByName(SEARCH_COMP_TYPE_NAME).getId() );
        assertNotNull(compType);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    public void testFindComponentTypeLongInvalid() {
        final ComponentType compType = compTypesService.findById(SEARCH_COMP_TYPE_ID_INVALID);
        assertNull(compType);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    public void testFindComponentTypeByName() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        assertNotNull(compType);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    public void testFindComponentTypeByNameInvalid() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME_INVALID);
        assertEquals(compType, null);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    @ApplyScriptAfter(value= {"delete_tags.sql"})
    public void testAddComponentType() {
        final ComponentType compType = new ComponentType("someNewComponentType");

        compType.setDescription("some Description");
        compType.setSuperComponentType(compTypesService.findByName(SEARCH_COMP_TYPE_NAME));

        final Tag NEW_TAG = new Tag(UUID.randomUUID().toString());
        compType.getTags().add(NEW_TAG);

        compTypesService.add(compType);

        final ComponentType newCompType = compTypesService.findByName("someNewComponentType");

        assertNotNull(newCompType);
        assertEquals(newCompType.getName(), "someNewComponentType");
        assertEquals(newCompType.getDescription(), "some Description");
        assertTrue(newCompType.getTags().contains(NEW_TAG));
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    @ApplyScriptAfter(value= {"delete_tags.sql"})
    public void testSaveComponentType() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);

        final String NEW_NAME = "NewName";
        final String NEW_DESCRIPTION = "NewDescription";
        compType.setName(NEW_NAME);
        compType.setDescription(NEW_DESCRIPTION);

        final Tag NEW_TAG = new Tag(UUID.randomUUID().toString());
        compType.getTags().add(NEW_TAG);

        compTypesService.save(compType);

        final ComponentType newCompType = compTypesService.findByName(NEW_NAME);

        assertNotNull(newCompType);
        assertEquals(newCompType.getName(), NEW_NAME);
        assertEquals(newCompType.getDescription(), NEW_DESCRIPTION);
        assertTrue(newCompType.getTags().contains(NEW_TAG));

        // Test tag removal
        newCompType.getTags().remove(NEW_TAG);
        compTypesService.save(compType);

        final ComponentType newestCompType = compTypesService.findByName(NEW_NAME);

        assertNotNull(newestCompType);
        assertFalse(newestCompType.getTags().contains(NEW_TAG));
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "slot.xml"})
    public void testDeleteComponentType() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        compTypesService.delete(compType);

        assertNull( compTypesService.findByName(SEARCH_COMP_TYPE_NAME) );
    }


    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testAddCompTypeProp() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);

        final ComptypePropertyValue compValue = new ComptypePropertyValue(false);
        compValue.setProperty( propertyService.findByName("CURRENT") );
        compValue.setComponentType(compType);
        compValue.setUnit( null );
        final Value propValue = new DblValue("33.45");
        compValue.setPropValue(propValue);

        compTypesService.addChild(compValue);

        final ComponentType newCompType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        assertNotNull(newCompType);
        assertTrue(newCompType.getComptypePropertyList().contains(compValue));
        final Value newPropValue = newCompType.getComptypePropertyList().get( newCompType.getComptypePropertyList().indexOf(compValue) ).getPropValue();
        assertEquals(newPropValue, propValue);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testSaveCompTypeProp() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        final ComptypePropertyValue compValue = compType.getComptypePropertyList().get(0);
        final Value propValue = new IntValue(22);
        compValue.setPropValue(propValue);
        compTypesService.saveChild(compValue);

        final ComponentType newCompType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        final Value newPropValue = newCompType.getComptypePropertyList().get( newCompType.getComptypePropertyList().indexOf(compValue) ).getPropValue();
        assertEquals(propValue, newPropValue);
    }

    @Test
    @UsingDataSet(value= {"basic_component_types.xml", "component_type.xml", "unit.xml", "property.xml",
            "comptype_property_value.xml", "slot.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testDeleteCompTypeProp() {
        final ComponentType compType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        final ComptypePropertyValue compValue = compType.getComptypePropertyList().get(0);

        compTypesService.deleteChild(compValue);

        final ComponentType newCompType = compTypesService.findByName(SEARCH_COMP_TYPE_NAME);
        assertFalse(newCompType.getComptypePropertyList().contains(compValue));
    }

    /* * * * * * * *
    @Test
    public void testAddCompTypeArtifact() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveCompTypeArtifact() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteCompTypeArtifact() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveComptypeAsm() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteComptypeAsm() {
        fail("Not yet implemented");
    }*/

}
