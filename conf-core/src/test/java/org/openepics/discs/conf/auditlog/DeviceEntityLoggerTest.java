package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;

import com.google.common.collect.ImmutableList;

public class DeviceEntityLoggerTest {

    final private Property prop1 = new Property("DETER", "deter", PropertyAssociation.ALL, "admin");
    final private Device device = new Device("serial1", "admin");
    final private DevicePropertyValue devicePropVal1 = new DevicePropertyValue(false, "admin");
    final private Property prop2 = new Property("APERTURE", "aperture", PropertyAssociation.ALL, "admin");
    final private DevicePropertyValue devicePropVal2 = new DevicePropertyValue(false, "admin");
    final private DeviceArtifact artifact1 = new DeviceArtifact("CAT Image", true, "Simple CAT image", "/var/usr/images/CAT", "admin");
    final private DeviceArtifact artifact2 = new DeviceArtifact("Manual", false, "Users manual", "www.deteriorator.com/user-manual", "admin");
    final private ComponentType compType = new ComponentType("Devices component", "admin");

    final private DeviceEntityLogger deviceEntityLogger = new DeviceEntityLogger();

    @Before
    public void setUp() {
        devicePropVal1.setProperty(prop1);
        devicePropVal2.setProperty(prop2);
        devicePropVal1.setPropValue("10");
        devicePropVal2.setPropValue("20");
        device.setDevicePropertyList(ImmutableList.of(devicePropVal1, devicePropVal2));
        device.setDeviceArtifactList(ImmutableList.of(artifact1, artifact2));
        device.setComponentType(compType);
    }

    @Test
    public void testGetType() {
        assertTrue(Device.class.equals(deviceEntityLogger.getType()));
    }

    @Test
    public void testSerializeEntity() {
        System.out.println("ReducedDevice:" + deviceEntityLogger.auditEntry(device, EntityTypeOperation.CREATE, "admin").getEntry());
    }
}
