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
package org.openepics.discs.conf.ejb;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.testutil.TestUtility;

@RunWith(Arquillian.class)
public class DataTypeEJBIT {
    @Inject private DataTypeEJB dataTypeService;
    @Inject private TestUtility testUtility;


    @Deployment()
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUp() throws Exception {
        testUtility.loginForTests();
    }

    @Test
    public void testFindAll() {
        final List<DataType> result = dataTypeService.findAll();
        assertNotNull(result);
        assertNotEquals(result.size(), 0);
    }

    @Test
    public void testFindById() {
        final DataType result = dataTypeService.findById(  dataTypeService.findByName("Double").getId() );
        assertNotNull(result);
    }

    @Test
    public void testFindByName() {
        final DataType result = dataTypeService.findByName("Double");
        assertNotNull(result);
    }
}
