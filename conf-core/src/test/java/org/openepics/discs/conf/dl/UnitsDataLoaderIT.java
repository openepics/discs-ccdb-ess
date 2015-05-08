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
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link UnitsDataLoader}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
@RunWith(Arquillian.class)
public class UnitsDataLoaderIT {

    @Inject @UnitsLoaderQualifier private DataLoader unitsDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;
    @Inject private UnitEJB unitEJB;

    final static private String HDR_NAME = "NAME";
    final static private String HDR_SYMBOL = "SYMBOL";
    final static private String HDR_DESC = "DESCRIPTION";

    final static private int NUM_OF_UNITS_IF_FAILURE = 0;
    final static private int NUM_OF_UNITS_IF_SUCCESS = 18;

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
    public void unitsImportRequiredFieldsFailure() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 10, HDR_NAME));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 12, HDR_SYMBOL));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 13, HDR_DESC));

        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                        + "units-required-fields-failure-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
        Assert.assertEquals(NUM_OF_UNITS_IF_FAILURE, unitEJB.findAll().size());
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void unitsImportTest() throws IOException {
        final InputStream testDataStream = this.getClass().getResourceAsStream(TestUtility.DATALOADERS_PATH
                                                                        + "units-test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, unitsDataLoader);
        testDataStream.close();

        Assert.assertFalse(loaderResult.isError());
        Assert.assertEquals(NUM_OF_UNITS_IF_SUCCESS, unitEJB.findAll().size());
    }

}
