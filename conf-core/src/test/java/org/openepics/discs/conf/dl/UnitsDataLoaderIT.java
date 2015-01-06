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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link UnitsDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
public class UnitsDataLoaderIT {

    @Inject @UnitsLoaderQualifier private DataLoader unitsDataLoader;
    @Inject private TestUtility testUtility;

    private List<Pair<Integer, List<String>>> inputRows;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        inputRows = new ArrayList<>();

        testUtility.loginForTests();
    }

    @Test
    public void generalTest() {
        inputRows.add(ImmutablePair.of(1, Arrays.asList("HEADER", "NAME", "SYMBOL", "QUANTITY", "DESCRIPTION")));
        inputRows.add(ImmutablePair.of(2, Arrays.asList("UPDATE", "Ampere", "A", "Current", "Electric current")));
        unitsDataLoader.loadDataToDatabase(inputRows, null);
    }

    @Test
    public void entityNotFoundTest() {
        inputRows.add(ImmutablePair.of(1, Arrays.asList("HEADER", "NAME", "SYMBOL", "QUANTITY", "DESCRIPTION")));
        inputRows.add(ImmutablePair.of(2, Arrays.asList("DELETE", "AmpereNonExistant", "A", "Current",
                "Electric current")));
        final DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows, null);
        assertEquals(ErrorMessage.ENTITY_NOT_FOUND, dataLoaderResult.getMessages().get(0).getMessage());
    }

    @Test
    public void renameMisformatTest() {
        inputRows.add(ImmutablePair.of(1, Arrays.asList( "HEADER", "NAME", "SYMBOL", "QUANTITY", "DESCRIPTION")));
        inputRows.add(ImmutablePair.of(3, Arrays.asList("RENAME", "Ampere", "A", "Current", "Electric current")));
        final DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows, null);
        assertEquals(ErrorMessage.RENAME_MISFORMAT, dataLoaderResult.getMessages().get(0).getMessage());
    }

    /**
     * Test if data loader correctly detects missing header fields and returns correct {@link DataLoaderResult}
     */
    @Test
    public void headerMissingFieldsTest() {
        inputRows.add(ImmutablePair.of(1, Arrays.asList("HEADER")));

        DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows, null);
        assertEquals(ErrorMessage.HEADER_FIELD_MISSING, dataLoaderResult.getMessages().get(0).getMessage());
        assertEquals("1", dataLoaderResult.getMessages().get(0).getRow());

        inputRows.clear();

        inputRows.add(ImmutablePair.of(1, Arrays.asList("HEADER", "NAME", "SYMBOL", "QUANTITIES", "DESCRIPTION")));
        dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows, null);
        assertEquals(ErrorMessage.HEADER_FIELD_MISSING, dataLoaderResult.getMessages().get(0).getMessage());
        assertEquals("1", dataLoaderResult.getMessages().get(0).getRow());
    }
}
