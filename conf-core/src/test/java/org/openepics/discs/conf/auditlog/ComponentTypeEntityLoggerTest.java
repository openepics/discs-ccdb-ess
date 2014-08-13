package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ComponentTypeEntityLoggerTest {

    final private Property prop1 = new Property("DETER", "deter", PropertyAssociation.ALL, "admin");
    final private ComptypePropertyValue compTypePropVal1 = new ComptypePropertyValue(false, "admin");
    final private Property prop2 = new Property("APERTURE", "aperture", PropertyAssociation.ALL, "admin");
    final private ComptypePropertyValue compTypePropVal2 = new ComptypePropertyValue(false, "admin");
    final private ComponentType compType = new ComponentType("Deteriorator", "admin");
    final private ComptypeArtifact artifact1 = new ComptypeArtifact("CAT Image", true, "Simple CAT image", "/var/usr/images/CAT", "admin");
    final private ComptypeArtifact artifact2 = new ComptypeArtifact("Manual", false, "Users manual", "www.deteriorator.com/user-manual", "admin");

    final private ComponentTypeEntityLogger ctel = new ComponentTypeEntityLogger();

    @Before
    public void setUp() throws Exception {
        compTypePropVal1.setPropValue("15");
        compTypePropVal1.setProperty(prop1);
        compTypePropVal2.setPropValue("10");
        compTypePropVal2.setProperty(prop2);

        compType.setComptypePropertyList(ImmutableList.of(compTypePropVal1, compTypePropVal2));
        compType.setComptypeArtifactList(ImmutableList.of(artifact1, artifact2));
    }

    @Test
    public void testGetType() {
        assertTrue(ComponentType.class.equals(ctel.getType()));
    }

    @Test
    public void testSerializeEntity() {
        System.out.println("ReducedCompType:" + ctel.auditEntry(compType, EntityTypeOperation.CREATE, "admin").getEntry());
    }

}
