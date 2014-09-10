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
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.util.TestUtility;

@RunWith(Arquillian.class)
public class PropertyEJBIT {
    @Inject private PropertyEJB propertyService;
    @Inject private DataTypeEJB dataTypeService;
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
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindById() {
        final Property property = propertyService.findById( propertyService.findByName("APERTURE").getId() );

        assertNotNull(property);
        assertEquals("APERTURE", property.getName());
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindByName() {
        final Property property = propertyService.findByName("APERTURE");

        assertNotNull(property);
        assertEquals("APERTURE", property.getName());
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindAll() {
        final List<Property> properties = propertyService.findAll();

        assertNotNull(properties);
        assertNotEquals(properties.size(), 0);
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testAdd() {
        final Property newProperty = new Property("TESTPROP", "TestPropDescription", PropertyAssociation.SLOT);
        newProperty.setDataType( dataTypeService.findByName("Double") );
        newProperty.setUnit( unitService.findByName("meter") );
        propertyService.add(newProperty);

        final Property newerProperty = propertyService.findByName("TESTPROP");
        assertNotNull(newerProperty);
        assertEquals(newerProperty.getName(), "TESTPROP");
        assertEquals(newerProperty.getDescription(), "TestPropDescription");
        assertEquals(newerProperty.getAssociation(), PropertyAssociation.SLOT);
        assertEquals(newerProperty.getDataType().getName(), "Double");
        assertEquals(newerProperty.getUnit().getName(), "meter");
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testSave() {
        final Property property = propertyService.findByName("APERTURE");
        final Long id = property.getId();
        property.setName("APERTURE2");
        property.setDescription("APERTURE2DESC");
        property.setDataType(dataTypeService.findByName("String"));
        property.setUnit( unitService.findByName("ohm") );
        propertyService.save(property);

        final Property newerProperty = propertyService.findByName("APERTURE2");
        assertNotNull(newerProperty);
        assertEquals(newerProperty.getId(), id);
        assertEquals(newerProperty.getName(), "APERTURE2");
        assertEquals(newerProperty.getDescription(), "APERTURE2DESC");
        assertEquals(newerProperty.getDataType().getName(), "String");
        assertEquals(newerProperty.getUnit().getName(), "ohm");
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testDelete() {
        final Property property = propertyService.findByName("APERTURE");
        propertyService.delete(property);

        final Property newProperty = propertyService.findByName("APERTURE");
        assertNull(newProperty);
    }
}
