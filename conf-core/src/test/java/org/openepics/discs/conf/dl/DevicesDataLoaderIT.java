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
package org.openepics.discs.conf.dl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.annotations.DevicesLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.testutil.TestUtility;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;

/**
 * Integration tests for {@link DevicesDataLoader}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@RunWith(Arquillian.class)
@ApplyScriptBefore(value = "update_sequences.sql")
@ApplyScriptAfter(value = "truncate_database.sql")
public class DevicesDataLoaderIT {

    @Inject @DevicesLoader private DataLoader devicesDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
    @Inject private DeviceEJB deviceEJB;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        testUtility.loginForTests();
    }

    /////////////////
    // POSITIVE TESTS
    /////////////////

    /**
     * Tests if creating devices works correctly. Checks if devices have properties received from device types.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void devicesCreateSuccess() throws IOException {
        final InputStream testDataStream = this.getClass().
                getResourceAsStream(TestUtility.DATALOADERS_PATH + "devices-success-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());

        // Checking one device of each device type for properties it should have received from its device type.
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("TOOR-01").getDevicePropertyList(), "ACENPOS"));
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("TOOR-02").getDevicePropertyList(), "ACENPOS"));

        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("PRG-01").getDevicePropertyList(), "ACENPOS"));
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("PRG-01").getDevicePropertyList(), "POWER"));
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("PRG-02").getDevicePropertyList(), "ACENPOS"));
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("PRG-02").getDevicePropertyList(), "POWER"));

        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("BPM1-01").getDevicePropertyList(), "ACENPOS"));
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("BPM1-02").getDevicePropertyList(), "ACENPOS"));

        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("BPM2-01").getDevicePropertyList(), "ACENPOS"));
        Assert.assertNotEquals(null,
                TestUtility.getProperty(deviceEJB.findByName("BPM2-02").getDevicePropertyList(), "ACENPOS"));
    }

    /**
     * Tests if deleting devices works correctly.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml", "device.xml", "device_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void devicesDeleteSuccess() throws IOException {
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "devices-success-delete.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        // Checking if devices really were deleted
        Assert.assertEquals(null, deviceEJB.findByName("BPM1-02"));
        Assert.assertEquals(null, deviceEJB.findByName("BPM2-01"));
        Assert.assertEquals(null, deviceEJB.findByName("BPM2-02"));
        Assert.assertEquals(null, deviceEJB.findByName("TOOR-01"));
        Assert.assertEquals(null, deviceEJB.findByName("TOOR-02"));
        Assert.assertEquals(null, deviceEJB.findByName("PRG-02"));
        // Checking if "deleted" properties have the value null.
        Assert.assertEquals(null, TestUtility.
                getProperty(deviceEJB.findByName("BPM1-01").getDevicePropertyList(), "ACENPOS").getPropValue());
        Assert.assertNotEquals(null, TestUtility.
                getProperty(deviceEJB.findByName("PRG-01").getDevicePropertyList(), "ACENPOS").getPropValue());
        Assert.assertEquals(null, TestUtility.
                getProperty(deviceEJB.findByName("PRG-01").getDevicePropertyList(), "POWER").getPropValue());
    }

    /**
     * Tests if updating devices works correctly.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml", "device.xml", "device_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void devicesUpdateSuccess() throws IOException {
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "devices-success-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        // Device component types
        Assert.assertEquals("BPM2", deviceEJB.findByName("BPM1-02").getComponentType().getName());
        Assert.assertEquals("BPM1", deviceEJB.findByName("BPM2-02").getComponentType().getName());
        Assert.assertEquals("PRG", deviceEJB.findByName("TOOR-02").getComponentType().getName());
        Assert.assertEquals("TOOR", deviceEJB.findByName("PRG-02").getComponentType().getName());
        // Properties
        Assert.assertEquals("3.35",
                TestUtility.getProperty(deviceEJB.findByName("BPM1-01").getDevicePropertyList(), "ACENPOS").
                        getPropValue().toString());
        Assert.assertEquals("2.4",
                TestUtility.getProperty(deviceEJB.findByName("BPM2-01").getDevicePropertyList(), "ACENPOS").
                        getPropValue().toString());
        Assert.assertEquals("1.5",
                TestUtility.getProperty(deviceEJB.findByName("TOOR-01").getDevicePropertyList(), "ACENPOS").
                        getPropValue().toString());
        Assert.assertEquals("5.4",
                TestUtility.getProperty(deviceEJB.findByName("PRG-01").getDevicePropertyList(), "ACENPOS").
                        getPropValue().toString());
        Assert.assertEquals("5.3",
                TestUtility.getProperty(deviceEJB.findByName("PRG-01").getDevicePropertyList(), "POWER").getPropValue().
                        toString());
     }


    /////////////////
    // NEGATIVE TESTS
    /////////////////

    /**
     * Tests if trying to create devices fails when it should.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml", "device.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void devicesCreateFail() throws IOException {
        // List of expected errors
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // error due to: adding device for which component type doesn't exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                        DevicesDataLoader.HDR_CTYPE, "BPM56"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 12,
                                                                        DevicesDataLoader.HDR_CTYPE, "PTR"));
        // error due to: trying to add device which already exist.
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 13,
                                                                        DevicesDataLoader.HDR_SERIAL, "BPM2-01"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 14,
                                                                        DevicesDataLoader.HDR_SERIAL, "PRG-02"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 15,
                                                                        DevicesDataLoader.HDR_SERIAL, "TOOR-02"));
        // error due to: trying to add device without type defined
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 16,
                                                                        DevicesDataLoader.HDR_CTYPE, null));
        // error due to: trying to add device without name defined
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 17,
                                                                        DevicesDataLoader.HDR_SERIAL, null));

        // Trying to load data
        final InputStream testDataStream = this.getClass().
                                getResourceAsStream(TestUtility.DATALOADERS_PATH + "devices-fail-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();

        // Comparing errors
        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                    loaderResult.getMessages());
    }

    /**
     * Tests if trying to delete devices fails when it should.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml", "device.xml", "device_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void devicesDeleteFail() throws IOException {
        // List of expected errors
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // error due to: trying to delete device which doesen't exist.
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                        DevicesDataLoader.HDR_SERIAL, "BPM2-31"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 12,
                                                                        DevicesDataLoader.HDR_SERIAL, "PRG-07"));
        // error due to: trying to delete a non existing property
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 13,
                                                                        DevicesDataLoader.HDR_PROP_NAME, "PWR"));
        // error due to: trying to delete a property not assigned to device
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.PROPERTY_NOT_FOUND, 14,
                                                                        DevicesDataLoader.HDR_PROP_NAME, "VOLTAGE"));
        // error due to: trying to delete device without a name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 16,
                                                                        DevicesDataLoader.HDR_SERIAL, null));
        // error due to: trying to delete a property without property name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 17,
                                                                        DevicesDataLoader.HDR_PROP_NAME, null));

        // Trying to load data
        final InputStream testDataStream = this.getClass().
                    getResourceAsStream(TestUtility.DATALOADERS_PATH + "devices-fail-delete.test.xlsx");

        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();

        // Comparing errors
        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                    loaderResult.getMessages());
    }

    /**
     * Tests if trying to update devices fails when it should.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml", "device.xml", "device_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void devicesUpdateFail() throws IOException {
        // List of expected errors
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // error due to: trying to update device which doesen't exist.
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                    DevicesDataLoader.HDR_SERIAL, "BPM"));
        // error due to: trying to update device with type that doesen't exist.
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 12,
                                                                    DevicesDataLoader.HDR_CTYPE, "BPM545"));

        // error due to: trying to update a non existing property
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 13,
                                                                    DevicesDataLoader.HDR_PROP_NAME, "PWR"));
        // error due to: trying to update a property not assigned to device
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.PROPERTY_NOT_FOUND, 14,
                                                                    DevicesDataLoader.HDR_PROP_NAME, "VOLTAGE"));
        // error due to: trying to update device without a name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 16,
                                                                    DevicesDataLoader.HDR_SERIAL, null));
        // error due to: trying to update a property without property name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 17,
                                                                    DevicesDataLoader.HDR_PROP_NAME, null));

        // Trying to load data
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "devices-fail-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();

        // Comparing errors
        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                loaderResult.getMessages());
    }
}
