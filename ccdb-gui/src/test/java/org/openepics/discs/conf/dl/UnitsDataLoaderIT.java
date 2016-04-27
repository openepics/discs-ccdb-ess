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
import org.openepics.discs.ccdb.core.dl.annotations.UnitsLoader;
import org.openepics.discs.ccdb.core.dl.common.DataLoader;
import org.openepics.discs.ccdb.core.dl.common.DataLoaderResult;
import org.openepics.discs.ccdb.core.dl.common.ErrorMessage;
import org.openepics.discs.ccdb.core.dl.common.ValidationMessage;
import org.openepics.discs.ccdb.core.ejb.UnitEJB;
import org.openepics.discs.ccdb.gui.testutil.TestUtility;
import org.openepics.discs.ccdb.gui.ui.common.DataLoaderHandler;

/**
 * Integration tests for {@link UnitsDataLoader}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@RunWith(Arquillian.class)
@ApplyScriptBefore(value = "update_sequences.sql")
@ApplyScriptAfter(value = "truncate_database.sql")
public class UnitsDataLoaderIT {

    @Inject @UnitsLoader private DataLoader unitsDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
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
    @Transactional(TransactionMode.DISABLED)
    public void unitsCreateSucess() throws IOException {
        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                            + "units-success-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(18, unitEJB.findAll().size());
    }

    @Test
    @UsingDataSet(value = { "unit.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void unitsUpdateSucess() throws IOException {
        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                                    + "units-success-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals("Imperial length", unitEJB.findByName("inch").getDescription());
        Assert.assertEquals("3d", unitEJB.findByName("cubic-meter").getDescription());
        Assert.assertEquals("2d", unitEJB.findByName("square-meter").getDescription());
        Assert.assertEquals("1000ms", unitEJB.findByName("second").getSymbol());
        Assert.assertEquals("100cm", unitEJB.findByName("meter").getSymbol());
    }

    @Test
    @UsingDataSet(value = { "unit.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void unitsDeleteSucess() throws IOException {
        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                                    + "units-success-delete.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertFalse("Failed while importing: " + loaderResult.getMessages(), loaderResult.isError());
        Assert.assertEquals(0, unitEJB.findAll().size());
    }

    /////////////////
    // NEGATIVE TESTS
    /////////////////

    @Test
    @UsingDataSet(value = { "unit.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void unitsCreateFail() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to create a property that already exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 10,
                                                                                    UnitsDataLoader.HDR_NAME, "meter"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 11,
                                                                                    UnitsDataLoader.HDR_NAME, "inch"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 12,
                                                                            UnitsDataLoader.HDR_NAME, "cubic-meter"));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                                    + "units-fail-create.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml", "property.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void unitsDeleteFail() throws IOException {
         final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to delete a property that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 10,
                                                                            UnitsDataLoader.HDR_NAME, "horse-power"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                            UnitsDataLoader.HDR_NAME, "nanometer"));
        // Error due to trying to delete a property that is currently being used
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.DELETE_IN_USE, 12,
                                                                            UnitsDataLoader.HDR_NAME, "meter"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.DELETE_IN_USE, 13,
                                                                            UnitsDataLoader.HDR_NAME, "square-meter"));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                                    + "units-fail-delete.test.xlsx");

        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value = { "unit.xml", "data_types.xml", "property.xml" })
    @Transactional(TransactionMode.DISABLED)
    public void unitsUpdateFail() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to update a property that does not exist
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 10,
                                                                            UnitsDataLoader.HDR_NAME, "horse-power"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 11,
                                                                            UnitsDataLoader.HDR_NAME, "nanometer"));
        // Error due to trying to update a property that is currently being used
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.MODIFY_IN_USE, 12,
                                                                            UnitsDataLoader.HDR_SYMBOL, "100cm"));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.MODIFY_IN_USE, 13,
                                                                            UnitsDataLoader.HDR_SYMBOL, "10000cm^2"));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                                    + "units-fail-update.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void unitsImportRequiredFieldsFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // Error due to trying to work with a property without specifying required data
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 11,
                                                                                    UnitsDataLoader.HDR_NAME, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 13,
                                                                                    UnitsDataLoader.HDR_SYMBOL, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 14,
                                                                                    UnitsDataLoader.HDR_DESC, null));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                            + "units-fail-required-fields.test.xlsx");

        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                                        loaderResult.getMessages());
     }
}
