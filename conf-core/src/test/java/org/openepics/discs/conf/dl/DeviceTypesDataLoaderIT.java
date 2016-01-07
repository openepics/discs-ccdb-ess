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
import java.util.Comparator;
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
import org.openepics.discs.conf.dl.annotations.ComponentTypesLoader;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.testutil.TestUtility;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;

/**
 * Integration tests for {@link ComponentTypesLoader}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovic</a>
 *
 */
@RunWith(Arquillian.class)
@ApplyScriptBefore(value = "update_sequences.sql")
public class DeviceTypesDataLoaderIT {

    @Inject @ComponentTypesLoader private DataLoader compTypesDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
    @Inject private ComptypeEJB compTypeEJB;

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
     * Tests if importing devices works correctly
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypesImportSuccess() throws IOException {
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-types-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(294, compTypeEJB.findAll().size());
    }

    /**
     * Tests if updating and deleting device types works correctly
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_devicetype_property_value.xml" })
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypesUpdateSuccess() throws IOException {
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-types-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        // Check if there was an error and if a correct number of device types
        // remain.
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(2, compTypeEJB.findAll().size());
        // Check every device type's description.
        final ComponentType DCM3 = compTypeEJB.findByName("PRG");
        Assert.assertEquals("This has been changed 1.", DCM3.getDescription());
        final ComponentType BPM2 = compTypeEJB.findByName("BPM2");
        Assert.assertEquals("This has been changed 2.", BPM2.getDescription());
    }

    /**
     * Tests if creating device types properties works correctly
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml" })
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypesCreatePropertiesSuccess() throws IOException {
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-types-properties-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        // Check if there was an error and if a correct number of device types remain.
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(4, compTypeEJB.findAll().size());

        // Check every device type's properties.
        final List<ComptypePropertyValue> TOORProperties = compTypeEJB.findByName("TOOR").getComptypePropertyList();
        // We sort properties since we do not know their order before sorting.
        TOORProperties.sort(propertyNameComparator);
        Assert.assertEquals("ACENPOS", TOORProperties.get(0).getProperty().getName());
        Assert.assertEquals("ALIAS", TOORProperties.get(1).getProperty().getName());
        Assert.assertEquals("TOR", TOORProperties.get(1).getPropValue().toString());
        Assert.assertEquals("APERTURE", TOORProperties.get(2).getProperty().getName());

        final List<ComptypePropertyValue> PRGProperties = compTypeEJB.findByName("PRG").getComptypePropertyList();
        PRGProperties.sort(propertyNameComparator);
        Assert.assertEquals("ACENPOS", PRGProperties.get(0).getProperty().getName());
        Assert.assertEquals("ALIAS", PRGProperties.get(1).getProperty().getName());
        Assert.assertEquals("PG", PRGProperties.get(1).getPropValue().toString());
        Assert.assertEquals("APERTURE", PRGProperties.get(2).getProperty().getName());

        final List<ComptypePropertyValue> BPM1Properties = compTypeEJB.findByName("BPM1").getComptypePropertyList();
        BPM1Properties.sort(propertyNameComparator);
        Assert.assertEquals("ACENPOS", BPM1Properties.get(0).getProperty().getName());
        Assert.assertEquals("ALIAS", BPM1Properties.get(1).getProperty().getName());
        Assert.assertEquals("1st BPM", BPM1Properties.get(1).getPropValue().toString());
        Assert.assertEquals("APERTURE", BPM1Properties.get(2).getProperty().getName());

        final List<ComptypePropertyValue> BPM2Properties = compTypeEJB.findByName("BPM2").getComptypePropertyList();
        BPM2Properties.sort(propertyNameComparator);
        Assert.assertEquals("ACENPOS", BPM2Properties.get(0).getProperty().getName());
        Assert.assertEquals("ALIAS", BPM2Properties.get(1).getProperty().getName());
        Assert.assertEquals("2nd BPM", BPM2Properties.get(1).getPropValue().toString());
        Assert.assertEquals("APERTURE", BPM2Properties.get(2).getProperty().getName());
    }

    /**
     * Tests if deleting  device types and device types properties and updating device types properties works correctly.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_devicetype_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    @ApplyScriptAfter(value = "truncate_database.sql")
    public void deviceTypesUpdatePropertiesSuccess() throws IOException {
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-types-properties-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        // Check if there was an error and if a correct number of device types remain.
        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(4, compTypeEJB.findAll().size());

        // Check all device type properties
        final List<ComptypePropertyValue> TOORProperties = compTypeEJB.findByName("TOOR").getComptypePropertyList();
        Assert.assertEquals(1, TOORProperties.size());
        Assert.assertEquals("toor", TOORProperties.get(0).getPropValue().toString());

        final List<ComptypePropertyValue> BPM1Properties = compTypeEJB.findByName("BPM1").getComptypePropertyList();
        Assert.assertEquals(1, BPM1Properties.size());
        Assert.assertEquals("BPM1", BPM1Properties.get(0).getPropValue().toString());

    }
    /////////////////
    // NEGATIVE TESTS
    /////////////////

    /**
     * Tests if trying to create device types and device type properties fails when it should.
     *
     * @throws IOException
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_devicetype_property_value.xml" })
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypeCreationFails() throws IOException {
        // List of expected errors
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // error due to: adding device type which already exists
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 10, ComponentTypesDataLoader.HDR_NAME));
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 11, ComponentTypesDataLoader.HDR_NAME));
        // error due to: trying to add property to device type which doesn't exist
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 12, ComponentTypesDataLoader.HDR_NAME));
        // error due to: trying to add a property which does not exists
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 13, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to add a property which is already added
        expectedValidationMessages.add(
                new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 14, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to add a property with invalid type
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, 15, ComponentTypesDataLoader.HDR_PROP_TYPE));
        // error due to: trying to add a property without name specified
        expectedValidationMessages.add(
                new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 16, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to add a property without type specified
        expectedValidationMessages.add(
                new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 17, ComponentTypesDataLoader.HDR_PROP_TYPE));
        // error due to: trying to add a device type property without value specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 18,
                ComponentTypesDataLoader.HDR_PROP_VALUE));
        //error due to: trying to add a device type without name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 19,
                ComponentTypesDataLoader.HDR_NAME));
        // Trying to load data
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-type-creation-fails.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        // Comparing errors
        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                loaderResult.getMessages());
    }

    /**
     * Tests if trying to delete device types and device type properties fails when it should.
     *
     * @throws IOException
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml" })
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypeDeletionFails() throws IOException {
        // List of expected errors
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // error due to: deleting device type which doesen't exists
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 10, ComponentTypesDataLoader.HDR_NAME));
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11, ComponentTypesDataLoader.HDR_NAME));
        // error due to: trying to delete _ROOT
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, 12, AbstractDataLoader.HDR_OPERATION));
        // error due to: trying to delete property which isn't assigned to device type
        expectedValidationMessages.add(
                new ValidationMessage(ErrorMessage.PROPERTY_NOT_FOUND, 13, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to delete property which doesn't exist
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 14, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to delete device instance or slot property
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.PROPERTY_TYPE_INCORRECT, 15,
                ComponentTypesDataLoader.HDR_PROP_TYPE));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.PROPERTY_TYPE_INCORRECT, 16,
                ComponentTypesDataLoader.HDR_PROP_TYPE));
        //error due to: trying to delete a device type without name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 17,
                ComponentTypesDataLoader.HDR_NAME));
        //error due to: trying to delete a device type property without name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 18,
                ComponentTypesDataLoader.HDR_PROP_NAME));

        // Trying to load
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-type-deletion-fails.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        // Comparing errors
        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                loaderResult.getMessages());
    }

    /**
     * Tests if trying to update device types and device type properties fails when it should.
     *
     * @throws IOException
     */
    @Test
    @UsingDataSet(value = { "basic_component_types.xml", "unit.xml", "property.xml",
            "basic_comptype_property_value.xml" })
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypeUpdateFails() throws IOException {
        // List of expected errors
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // error due to: trying to update _ROOT
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, 10, AbstractDataLoader.HDR_OPERATION));
        // error due to: update device type which doesen't exists
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11, ComponentTypesDataLoader.HDR_NAME));
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 12, ComponentTypesDataLoader.HDR_NAME));
        // error due to: trying to update property to device type property which isn't assigned to device type
        expectedValidationMessages.add(
                new ValidationMessage(ErrorMessage.PROPERTY_NOT_FOUND, 13, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to update property to device type property which doesn't exist.
        expectedValidationMessages
                .add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 14, ComponentTypesDataLoader.HDR_PROP_NAME));
        // error due to: trying to update device instance or slot property
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.PROPERTY_TYPE_INCORRECT, 15,
                ComponentTypesDataLoader.HDR_PROP_TYPE));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.PROPERTY_TYPE_INCORRECT, 16,
                ComponentTypesDataLoader.HDR_PROP_TYPE));
        //error due to: trying to delete a device type without name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 17,
                ComponentTypesDataLoader.HDR_NAME));
        //error due to: trying to delete a device type property without name specified
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 18,
                ComponentTypesDataLoader.HDR_PROP_NAME));


        // Trying to load
        final InputStream testDataStream = this.getClass()
                .getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-type-update-fails.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();
        // Comparing errors
        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                loaderResult.getMessages());
    }

    /**
     * Helper comparator for sorting device type properties by name.
     */
    protected static final Comparator<ComptypePropertyValue> propertyNameComparator = new Comparator<ComptypePropertyValue>() {
        @Override
        public int compare(final ComptypePropertyValue prop1, final ComptypePropertyValue prop2) {
            return prop1.getProperty().getName().compareTo(prop2.getProperty().getName());
        }
    };
}