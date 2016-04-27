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
package org.openepics.discs.ccdb.gui.ejb;

import org.openepics.discs.ccdb.core.ejb.UnitEJB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openepics.discs.ccdb.model.Unit;
import org.openepics.discs.ccdb.gui.testutil.TestUtility;

@RunWith(Arquillian.class)
public class UnitEJBIT {
    @Inject private UnitEJB unitService;
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
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindById() {
        final Unit unit = unitService.findById( unitService.findByName("meter").getId() );

        assertNotNull(unit);
        assertEquals("meter", unit.getName());
    }

    @Test
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindByName() {
        final Unit unit = unitService.findByName("meter");

        assertNotNull(unit);
        assertEquals("meter", unit.getName());
    }

    @Test
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindAll() {
        final List<Unit> units = unitService.findAll();

        assertNotNull(units);
        assertNotEquals(0, units.size());
    }

    @Test
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testAdd() {
        final Unit unit = new Unit("foot", "ft", "length in feet");
        unitService.add(unit);

        final Unit newUnit = unitService.findByName("foot");
        assertNotNull(newUnit);
        assertEquals("foot", newUnit.getName());
        assertEquals("length in feet", newUnit.getDescription());
        assertEquals("ft", newUnit.getSymbol());
    }

    @Test
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testSave() {
        final Unit unit = unitService.findByName("meter");
        unit.setName("foot");
        unit.setDescription("length in feet");
        unit.setSymbol("ft");
        Long id = unit.getId();
        unitService.save(unit);

        final Unit newUnit = unitService.findByName("foot");
        assertNotNull(newUnit);
        assertEquals(id, newUnit.getId());
        assertEquals("foot", newUnit.getName());
        assertEquals("length in feet", newUnit.getDescription());
        assertEquals("ft", newUnit.getSymbol());
    }

    @Test
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testDelete() {
        final Unit unit = unitService.findByName("meter");
        unitService.delete(unit);

        final Unit newUnit = unitService.findByName("meter");
        assertNull(newUnit);
    }
}
