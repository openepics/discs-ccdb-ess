package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Unit;

public class PropertyEntityLoggerTest {
    private final Unit unit = new Unit("Ampre", "Current", "A", "BlahBlha");
    private final DataType dt = new DataType("Float", "Float", true, "Well.. a scalar float");
    private final Property prop = new Property("TestProperty", "Description of test Property", PropertyAssociation.TYPE_DEVICE);

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
        final String RESULT = "{\"description\":\"Description of test Property\",\"association\":\"TYPE_DEVICE\",\"dataType\":\"Float\",\"unit\":\"Ampre\"}";

        assertEquals(RESULT, pel.auditEntries(prop, EntityTypeOperation.CREATE).get(0).getEntry());
    }
}
