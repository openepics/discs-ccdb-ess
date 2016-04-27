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
package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.values.IntValue;

public class DeviceEntityLoggerTest {

    private final Property prop1 = new Property("DETER", "deter");
    private final Device device = new Device("serial1");
    private final DevicePropertyValue devicePropVal1 = new DevicePropertyValue(false);
    private final Property prop2 = new Property("APERTURE", "aperture");
    private final DevicePropertyValue devicePropVal2 = new DevicePropertyValue(false);
    private final DeviceArtifact artifact1 = new DeviceArtifact("CAT Image", true, "Simple CAT image", "/var/usr/images/CAT");
    private final DeviceArtifact artifact2 = new DeviceArtifact("Manual", false, "Users manual", "www.deteriorator.com/user-manual");
    private final ComponentType compType = new ComponentType("Devices component");

    private final DeviceEntityLogger deviceEntityLogger = new DeviceEntityLogger();

    @Before
    public void setUp() {
        devicePropVal1.setProperty(prop1);
        devicePropVal2.setProperty(prop2);
        devicePropVal1.setPropValue(new IntValue(10));
        devicePropVal2.setPropValue(new IntValue(20));
        device.getDevicePropertyList().add(devicePropVal1);
        device.getDevicePropertyList().add(devicePropVal2);
        device.getDeviceArtifactList().add(artifact1);
        device.getDeviceArtifactList().add(artifact2);
        device.setComponentType(compType);
    }

    @Test
    public void testGetType() {
        assertEquals(Device.class, deviceEntityLogger.getType());
    }

    @Test
    public void testSerializeEntity() {
        final String RESULT = "{\"componentType\":\"Devices component\",\"devicePropertyList\":"
                + "[{\"APERTURE\":\"20\"},{\"DETER\":\"10\"}],\"deviceArtifactList\":"
                + "[{\"CAT Image\":\"/var/usr/images/CAT\"},{\"Manual\":\"www.deteriorator.com/user-manual\"}]}";

        assertEquals(RESULT, deviceEntityLogger.auditEntries(device, EntityTypeOperation.CREATE).get(0).getEntry());
    }
}
