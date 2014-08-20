package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;

public class ComponentTypeEntityLoggerTest {

    private final Property prop1 = new Property("DETER", "deter", PropertyAssociation.ALL);
    private final ComptypePropertyValue compTypePropVal1 = new ComptypePropertyValue(false);
    private final Property prop2 = new Property("APERTURE", "aperture", PropertyAssociation.ALL);
    private final ComptypePropertyValue compTypePropVal2 = new ComptypePropertyValue(false);
    private final ComponentType compType = new ComponentType("Deteriorator");
    private final ComptypeArtifact artifact1 = new ComptypeArtifact("CAT Image", true, "Simple CAT image", "/var/usr/images/CAT");
    private final ComptypeArtifact artifact2 = new ComptypeArtifact("Manual", false, "Users manual", "www.deteriorator.com/user-manual");

    final private ComponentTypeEntityLogger ctel = new ComponentTypeEntityLogger();

    @Before
    public void setUp() throws Exception {
        compTypePropVal1.setPropValue("15");
        compTypePropVal1.setProperty(prop1);
        compTypePropVal2.setPropValue("10");
        compTypePropVal2.setProperty(prop2);

        compType.getComptypePropertyList().add(compTypePropVal1);
        compType.getComptypePropertyList().add(compTypePropVal2);
        compType.getComptypeArtifactList().add(artifact1);
        compType.getComptypeArtifactList().add(artifact2);
    }

    @Test
    public void testGetType() {
        assertTrue(ComponentType.class.equals(ctel.getType()));
    }

    @Test
    public void testSerializeEntity() {
        final String RESULT = "{\"comptypePropertyList\":[{\"APERTURE\":\"10\"},{\"DETER\":\"15\"}],\"comptypeArtifactList\":[{\"CAT Image\":\"/var/usr/images/CAT\"},{\"Manual\":\"www.deteriorator.com/user-manual\"}]}";
        assertEquals(RESULT, ctel.auditEntries(compType, EntityTypeOperation.CREATE).get(0).getEntry());
    }

}