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
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link ComponentTypesDataLoader}
 *
 * @author Andraž Požar &lt;andraz.pozar@cosylab.com&gt;
 *
 */
@RunWith(Arquillian.class)
@UsingDataSet(value= {"unit.xml", "property.xml"})
@ApplyScriptBefore(value= "update_sequences.sql")
public class DeviceTypesDataLoaderIT {

    @Inject @ComponentTypesLoaderQualifier private DataLoader compTypesDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
    @Inject private ComptypeEJB compTypeEJB;

    final static private String HDR_NAME = "NAME";
    final static private String HDR_ACENPOS = "ACENPOS";

    final static private int NUM_OF_DEV_TYPES_IF_FAILURE = 0;
    final static private int NUM_OF_DEV_TYPES_IF_SUCCESS = 483;

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
    public void deviceTypesImportRequiredFieldsFailureTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 10, HDR_NAME));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-types-required-fields-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
        Assert.assertEquals(NUM_OF_DEV_TYPES_IF_FAILURE, compTypeEJB.findAll().size());
    }

    @Test
    @ApplyScriptAfter(value = "truncate_database.sql")
    @Transactional(TransactionMode.DISABLED)
    public void deviceTypesImportTest() throws IOException {
        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH + "device-types-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, compTypesDataLoader);
        testDataStream.close();

        Assert.assertFalse(loaderResult.isError());
        Assert.assertEquals(NUM_OF_DEV_TYPES_IF_SUCCESS, compTypeEJB.findAll().size());
    }
}
