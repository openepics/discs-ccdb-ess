package org.openepics.discs.conf.ejb;

import static org.junit.Assert.*;

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
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.util.TestUtility;

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
        final Unit unit = new Unit("foot", "length2", "ft", "length in feet");
        unitService.add(unit);

        final Unit newUnit = unitService.findByName("foot");
        assertNotNull(newUnit);
        assertEquals("foot", newUnit.getName());
        assertEquals("length in feet", newUnit.getDescription());
        assertEquals("ft", newUnit.getSymbol());
        assertEquals("length2", newUnit.getQuantity());
    }

    @Test
    @UsingDataSet(value= {"unit.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testSave() {
        final Unit unit = unitService.findByName("meter");
        unit.setName("foot");
        unit.setDescription("length in feet");
        unit.setSymbol("ft");
        unit.setQuantity("length2");
        Long id = unit.getId();
        unitService.save(unit);

        final Unit newUnit = unitService.findByName("foot");
        assertNotNull(newUnit);
        assertEquals(id, newUnit.getId());
        assertEquals("foot", newUnit.getName());
        assertEquals("length in feet", newUnit.getDescription());
        assertEquals("ft", newUnit.getSymbol());
        assertEquals("length2", newUnit.getQuantity());
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
