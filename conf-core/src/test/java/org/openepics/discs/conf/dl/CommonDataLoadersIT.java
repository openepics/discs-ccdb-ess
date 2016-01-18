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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.annotations.DevicesLoader;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.testutil.TestUtility;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;

/**
 * Integration tests for failures common to all data loaders
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@RunWith(Arquillian.class)
@ApplyScriptBefore(value = "update_sequences.sql")
@ApplyScriptAfter(value = "truncate_database.sql")
public class CommonDataLoadersIT {

    @Inject @DevicesLoader private DataLoader devicesDataLoader;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject private TestUtility testUtility;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        testUtility.loginForTests();
    }

    /**
     * Tests if filtering lines without commands works and if inputing unknown commands fails.
     *
     * @throws IOException
     *             if there was an error with data
     */
    @Test
    public void commandWrongAndMissingFailTest() throws IOException {
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();

        expectedValidationMessages.
                add(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, 13, AbstractDataLoader.HDR_OPERATION));
        expectedValidationMessages.
                add(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, 14, AbstractDataLoader.HDR_OPERATION));
        expectedValidationMessages.
                add(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, 15, AbstractDataLoader.HDR_OPERATION));
        final InputStream testDataStream = this.getClass().
                getResourceAsStream(TestUtility.DATALOADERS_PATH + "common-fail-commands.test.xlsx");
        final DataLoaderResult loaderResult = dataLoaderHandler.loadData(testDataStream, devicesDataLoader);
        testDataStream.close();

        Assert.assertEquals("Error:\n" + loaderResult.toString(), expectedValidationMessages,
                                                                            loaderResult.getMessages());
    }
}
