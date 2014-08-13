package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Unit;

public class PropertyEntityLoggerTest {
    private Unit unit = new Unit("Ampre", "Current", "A", "BlahBlha", "Miki");
    private DataType dt = new DataType("Float", "Float", true, "Well.. a scalar float", "Iznogud");
    private Property prop = new Property("TestProperty", "Description of test Property", PropertyAssociation.TYPE_DEVICE, "Iznogud");

    private PropertyEntityLogger pel = new PropertyEntityLogger();


    @Before
    public void setUp() throws Exception {
        prop.setUnit(unit);
        prop.setDataType(dt);
    }

    @Test
    public void testGetType() {
        assertTrue(Property.class.equals(pel.getType()));
    }

    @Test
    public void testSerializeEntity() {
        System.out.println("ReducedProperty:" + pel.auditEntry(prop, EntityTypeOperation.CREATE, "admin").getEntry());
    }
}
