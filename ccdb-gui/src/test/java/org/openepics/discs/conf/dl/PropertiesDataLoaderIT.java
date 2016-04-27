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
import org.openepics.discs.ccdb.core.dl.annotations.PropertiesLoader;
import org.openepics.discs.ccdb.core.dl.common.DataLoader;
import org.openepics.discs.ccdb.core.dl.common.DataLoaderResult;
import org.openepics.discs.ccdb.core.dl.common.ErrorMessage;
import org.openepics.discs.ccdb.core.dl.common.ValidationMessage;
import org.openepics.discs.ccdb.core.ejb.DataTypeEJB;
import org.openepics.discs.ccdb.core.ejb.PropertyEJB;
import org.openepics.discs.ccdb.core.ejb.UnitEJB;
import org.openepics.discs.ccdb.model.Property;
import org.openepics.discs.ccdb.model.PropertyValueUniqueness;
import org.openepics.discs.ccdb.gui.testutil.TestUtility;
import org.openepics.discs.ccdb.gui.ui.common.DataLoaderHandler;

/**
 * Integration tests for {@link PropertiesDataLoader}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@RunWith(Arquillian.class)
@ApplyScriptBefore(value = "update_sequences.sql")
@ApplyScriptAfter(value = "truncate_database.sql")
public class PropertiesDataLoaderIT {

    @Inject @PropertiesLoader private DataLoader propertiesDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
    @Inject private PropertyEJB propertyEJB;
    @Inject private DataTypeEJB dataTypeEJB;
    @Inject private UnitEJB unitEJB;

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

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesCreateSuccess() throws IOException {
        final InputStream testDataStream = this.getClass().
                        getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-success-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(31, propertyEJB.findAll().size());

        // check property with explicit value of the UNIQUE column : NONE
        final Property propertyAlias = propertyEJB.findByName("ALIAS");
        Assert.assertTrue(propertyAlias.getValueUniqueness() == PropertyValueUniqueness.NONE);
        Assert.assertTrue(propertyAlias.getUnit() == null);

        final Property propertyLfelb = propertyEJB.findByName("LFELB");
        Assert.assertTrue(propertyLfelb.getValueUniqueness() == PropertyValueUniqueness.NONE);

        final Property propertyFieldPoly = propertyEJB.findByName("FIELDPOLY");
        Assert.assertTrue(propertyFieldPoly.getValueUniqueness() == PropertyValueUniqueness.NONE);

        // check property with explicit value of the UNIQUE column : UNIVERSAL
        final Property propertyDoc01 = propertyEJB.findByName("DOC01");
        Assert.assertTrue(propertyDoc01.getValueUniqueness() == PropertyValueUniqueness.UNIVERSAL);

        // check property with explicit value of the UNIQUE column : TYPE
        final Property propertyDcfleo = propertyEJB.findByName("DCFLEO");
        Assert.assertTrue(propertyDcfleo.getValueUniqueness() == PropertyValueUniqueness.TYPE);
    }

    @Test
    @UsingDataSet(value = { "unit.xml", "property.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesDeleteSuccess() throws IOException {
        final InputStream testDataStream = this.getClass().
                        getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-success-delete.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());

        Assert.assertEquals(0, propertyEJB.findAll().size());
    }

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml", "property.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesUpdateSuccess() throws IOException {
        final InputStream testDataStream = this.getClass().
                        getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-success-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());

        Assert.assertEquals(31, propertyEJB.findAll().size());
        final Property propertyAcenpos = propertyEJB.findByName("ACENPOS");
        Assert.assertEquals("Just a description", propertyAcenpos.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("Integer"), propertyAcenpos.getDataType());
        Assert.assertEquals(unitEJB.findByName("volt"), propertyAcenpos.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.UNIVERSAL, propertyAcenpos.getValueUniqueness());

        final Property propertyAendpos = propertyEJB.findByName("AENDPOS");
        Assert.assertEquals("Just another description", propertyAendpos.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("Double"), propertyAendpos.getDataType());
        Assert.assertEquals(unitEJB.findByName("kilogram"), propertyAendpos.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.TYPE, propertyAendpos.getValueUniqueness());

        final Property propertyAlias = propertyEJB.findByName("ALIAS");
        Assert.assertEquals("The description", propertyAlias.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("String"), propertyAlias.getDataType());
        Assert.assertEquals(null, propertyAlias.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.UNIVERSAL, propertyAlias.getValueUniqueness());

        final Property propertyAperture = propertyEJB.findByName("APERTURE");
        Assert.assertEquals("Modified description", propertyAperture.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("Test enums"), propertyAperture.getDataType());
        Assert.assertEquals(unitEJB.findByName("ohm"), propertyAperture.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.TYPE, propertyAperture.getValueUniqueness());

        final Property propertyBangle = propertyEJB.findByName("BANGLE");
        Assert.assertEquals("Updated description", propertyBangle.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("Doubles Table"), propertyBangle.getDataType());
        Assert.assertEquals(unitEJB.findByName("ampere"), propertyBangle.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.UNIVERSAL, propertyBangle.getValueUniqueness());

        final Property propertyBpole = propertyEJB.findByName("BPOLE");
        Assert.assertEquals("Just an updated description", propertyBpole.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("Doubles Vector"), propertyBpole.getDataType());
        Assert.assertEquals(unitEJB.findByName("inch"), propertyBpole.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.TYPE, propertyBpole.getValueUniqueness());

        final Property propertyBradius = propertyEJB.findByName("BRADIUS");
        Assert.assertEquals("Just a modified description", propertyBradius.getDescription());
        Assert.assertEquals(dataTypeEJB.findByName("Double"), propertyBradius.getDataType());
        Assert.assertEquals(unitEJB.findByName("watt"), propertyBradius.getUnit());
        Assert.assertEquals(PropertyValueUniqueness.UNIVERSAL, propertyBradius.getValueUniqueness());
    }


    /////////////////
    // NEGATIVE TESTS
    /////////////////

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml", "property.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesCreateFail() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to use a datatype that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 10,
                                                                    PropertiesDataLoader.HDR_DATATYPE, "Diagram"));
        // Error due to trying to use an unit that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                    PropertiesDataLoader.HDR_UNIT, "kilometer"));
        // Error due to trying to add property with a name that does already exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 12,
                                                                    PropertiesDataLoader.HDR_NAME, "ALIAS"));
        // Error due to trying to
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.UNIQUE_INCORRECT, 13,
                                                                    PropertiesDataLoader.HDR_UNIQUE, "YES"));

        final InputStream testDataStream = this.getClass().
                        getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-fail-create.test.xlsx");
         final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
         testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
     }

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml", "property.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesDeleteFail() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to delete a property that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 10,
                                                                        PropertiesDataLoader.HDR_NAME, "FD"));
        // Error due to trying to use an unit that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                        PropertiesDataLoader.HDR_NAME, "ER"));

        final InputStream testDataStream = this.getClass().
                            getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-fail-delete.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml", "property.xml", "basic_component_types.xml", "device.xml",
            "device_property_value.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesUpdateFail() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to delete a property that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 10,
                                                                        PropertiesDataLoader.HDR_NAME, "FD"));
        // Error due to trying to use an unit that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                        PropertiesDataLoader.HDR_NAME, "ER"));

        // Error due to trying to update data type on property that is being used
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.MODIFY_IN_USE, 12,
                                                                        PropertiesDataLoader.HDR_DATATYPE, "String"));
        // Error due to trying to update uniqueness on property that is being used
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.MODIFY_IN_USE, 13,
                                                                        PropertiesDataLoader.HDR_UNIQUE, "TYPE"));
        // Error due to trying to update unit on property that is being used
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.MODIFY_IN_USE, 14,
                                                                        PropertiesDataLoader.HDR_UNIT, "meter"));

        final InputStream testDataStream = this.getClass().
                            getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-fail-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void propertiesImportRequiredFieldsFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 10,
                                                                            PropertiesDataLoader.HDR_NAME, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 11,
                                                                            PropertiesDataLoader.HDR_DESC, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 13,
                                                                            PropertiesDataLoader.HDR_UNIQUE, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 14,
                                                                            PropertiesDataLoader.HDR_DATATYPE, null));

        final InputStream testDataStream = this.getClass().
                    getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-fail-required-fields.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
        Assert.assertEquals(0, propertyEJB.findAll().size());
    }
}
