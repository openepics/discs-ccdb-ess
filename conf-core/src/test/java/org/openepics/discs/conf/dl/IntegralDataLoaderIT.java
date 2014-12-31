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

import static org.junit.Assert.assertFalse;

import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link UnitsDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
public class IntegralDataLoaderIT {

    @Inject @UnitsLoaderQualifier private DataLoader unitsDataLoader;
    @Inject @PropertiesLoaderQualifier private DataLoader propertiesDataLoader;
    @Inject @ComponentTypesLoaderQualifier private DataLoader ctypesDataLoader;
    @Inject @DevicesLoaderQualifier private DataLoader devicesDataLoader;

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

    @Test
    public void testAll() {
        testLoadData("conf-data-units.xlsx", unitsDataLoader, null, null);
        testLoadData("conf-data-properties.xlsx", propertiesDataLoader, null, null);
        testLoadData("conf-data-ctypes.xlsx", ctypesDataLoader, null, null);
        testLoadData("conf-data-slots.xlsx", null, "conf-data-slot-pairs.xlsx", null);
        testLoadData("conf-data-devices.xlsx", devicesDataLoader, null, null);
    }


    // ToDo review the interface wierdness of slot data loader with Andraz
    private void testLoadData(String fileName, Object dataLoader, String secondFileName, Object secondDataLoader) {
        final InputStream stream = this.getClass().getResourceAsStream("/dataloader/"+fileName);

        if (secondFileName==null) {
            final DataLoaderResult result = dataLoaderHandler.loadData(stream, (DataLoader) dataLoader);

            if (result.isError()) {
                StringBuilder sb = new StringBuilder();
                for (ValidationMessage vm : result.getMessages())
                    sb.append(vm.toString() + '\n');
            }

            assertFalse(fileName+" loading failed." +
                    dumpDataLoaderMessages(result), result.isError());
        } else {
            final InputStream secondStream = this.getClass().getResourceAsStream("/dataloader/"+secondFileName);

            final DataLoaderResult result = dataLoaderHandler.loadDataFromTwoFiles(stream, secondStream, fileName, secondFileName);

            assertFalse(fileName+" or "+secondFileName+" loading failed:" +
                    dumpDataLoaderMessages(result), result.isError());
        }
    }

    private String dumpDataLoaderMessages(DataLoaderResult result) {
        String errorMessage = "";
        if (result.isError()) {
            StringBuilder sb = new StringBuilder();
            for (ValidationMessage vm : result.getMessages())
                sb.append(vm.toString() + '\n');
            errorMessage = sb.toString();
        }
        return errorMessage;
    }

}
