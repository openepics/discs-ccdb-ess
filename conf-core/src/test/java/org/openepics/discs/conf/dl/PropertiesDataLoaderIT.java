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
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link PropertiesDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
@UsingDataSet(value= "unit.xml")
@ApplyScriptBefore(value= "update_sequences.sql")
public class PropertiesDataLoaderIT {

    @Inject @PropertiesLoaderQualifier private DataLoader propertiesDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
    @Inject private PropertyEJB propertyEJB;

    final static private String HDR_NAME = "NAME";
    final static private String HDR_DESC = "DESCRIPTION";
    final static private String HDR_DATATYPE = "DATA-TYPE";
    final static private String HDR_UNIT = "UNIT";

    final static private int NUM_OF_PROPS_IF_FAILURE = 0;
    final static private int NUM_OF_PROPS_IF_SUCCESS = 31;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        testUtility.loginForTests();
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void propertiesImportRequiredFieldsFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 4, HDR_NAME));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 5, HDR_DESC));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 8, HDR_DATATYPE));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-required-fields-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
        Assert.assertEquals(NUM_OF_PROPS_IF_FAILURE, propertyEJB.findAll().size());
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void propertiesImportEntityNotFoundFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 3, HDR_UNIT));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 4, HDR_DATATYPE));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-entity-not-found-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
        Assert.assertEquals(NUM_OF_PROPS_IF_FAILURE, propertyEJB.findAll().size());
    }

    @Test
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void propertiesImportTest() throws IOException {
        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "properties-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertFalse(loaderResult.isError());
        Assert.assertEquals(NUM_OF_PROPS_IF_SUCCESS, propertyEJB.findAll().size());

        // check default value for properties without the UNIQUE column
        final Property propertyAlias = propertyEJB.findByName("ALIAS");
        Assert.assertTrue(propertyAlias.getValueUniqueness() == PropertyValueUniqueness.NONE);
        // check property with explicit value of the UNIQUE column : NONE
        final Property propertyFieldPoly = propertyEJB.findByName("FIELDPOLY");
        Assert.assertTrue(propertyFieldPoly.getValueUniqueness() == PropertyValueUniqueness.NONE);
        // check property with explicit value of the UNIQUE column : UNIVERSAL
        final Property propertyDoc01 = propertyEJB.findByName("DOC01");
        Assert.assertTrue(propertyDoc01.getValueUniqueness() == PropertyValueUniqueness.UNIVERSAL);
        // check property with explicit value of the UNIQUE column : TYPE
        final Property propertyDcfleo = propertyEJB.findByName("DCFLEO");
        Assert.assertTrue(propertyDcfleo.getValueUniqueness() == PropertyValueUniqueness.TYPE);
        // check property with NO/EMPTY value of the UNIQUE column : NONE (default)
        final Property propertyLfelb = propertyEJB.findByName("LFELB");
        Assert.assertTrue(propertyLfelb.getValueUniqueness() == PropertyValueUniqueness.NONE);
    }

}
