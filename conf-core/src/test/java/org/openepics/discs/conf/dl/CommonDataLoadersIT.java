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
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.annotations.PropertiesLoader;
import org.openepics.discs.conf.dl.annotations.UnitsLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for failures common to all data loaders
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
@RunWith(Arquillian.class)
@ApplyScriptAfter(value= "truncate_database.sql")
public class CommonDataLoadersIT {

    @Inject @UnitsLoader private DataLoader unitsDataLoader;
    @Inject @PropertiesLoader private DataLoader propertiesDataLoader;
    @Inject private SlotsAndSlotPairsDataLoaderHelper dataLoaderHelper;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;

    final static private String HDR_NAME = "NAME";
    final static private String HDR_QUANTITY = "QUANTITY";
    final static private String HDR = "HEADER";
    final static private String HDR_ACENPOS = "ACENPOS";
    final static private String HDR_IS_HOSTING_SLOT = "IS-HOSTING-SLOT";
    final static private String HDR_BLP = "BLP";

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        testUtility.loginForTests();
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    public void commandRelatedFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.RENAME_MISFORMAT, 2, HDR_NAME));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, 3, HDR_NAME));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, 4, HDR_NAME));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, 5, HDR));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "commands-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml"})
    public void duplicateFieldDefinitionFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // TODO check and fix / delete
        //expectedValidationMessages.add(new ValidationMessage(ErrorMessage.DUPLICATES_IN_HEADER, 1, HDR_NAME));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "duplicate-field-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, propertiesDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml", "basic_component_types.xml", "component_type.xml", "basic_slot.xml", "slot.xml", "slot_property_value.xml"})
    public void duplicatePropertyDefinitionFailureTest() throws IOException {
        final String slotsImportFileName = "duplicate-property-failure-test.xlsx";
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(slotsImportFileName));
        // TODO check and fix / delete
        // expectedValidationMessages.add(new ValidationMessage(ErrorMessage.DUPLICATES_IN_HEADER, 1, HDR_ACENPOS));

        final DataLoaderResult loaderResult = dataLoaderHelper.importSlotsAndSlotPairs(slotsImportFileName, null);

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
    }

    @Test
    public void requiredHeaderFieldsFailure() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        // TODO check and fix / remove
        //expectedValidationMessages.add(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, 1, HDR_NAME));
        //expectedValidationMessages.add(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, 1, HDR_QUANTITY));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "required-header-fields-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
    }

    @Test
    @UsingDataSet(value= {"unit.xml", "property.xml", "basic_component_types.xml", "component_type.xml", "basic_slot.xml", "slot.xml", "slot_property_value.xml"})
    public void dataTypeFormatFailureTest() throws IOException {
        final String slotsImportFileName = "data-type-format-failure-test.xlsx";
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(slotsImportFileName));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.SHOULD_BE_BOOLEAN_VALUE, 8, HDR_IS_HOSTING_SLOT));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.SHOULD_BE_NUMERIC_VALUE, 9, HDR_BLP));

        final DataLoaderResult loaderResult = dataLoaderHelper.importSlotsAndSlotPairs(slotsImportFileName, null);

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
    }
}
