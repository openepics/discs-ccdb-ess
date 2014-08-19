package org.openepics.discs.conf.dl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;

import com.google.common.collect.ImmutableList;

/**
 * Integration tests for {@link UnitsDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
public class UnitsDataLoaderIT {

    @Inject @UnitLoaderQualifier private DataLoader unitsDataLoader;
    private List<List<String>> inputRows;

    @Deployment
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        inputRows = new ArrayList<>();
    }

    @Test
    public void generalTest() {
        inputRows.add(ImmutableList.of("1", "HEADER", "NAME", "SYMBOL", "QUANTITY", "DESCRIPTION"));
        inputRows.add(ImmutableList.of("2", "UPDATE", "Ampere", "A", "Current", "Electric current"));
        final DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertEquals(ErrorMessage.NOT_AUTHORIZED, dataLoaderResult.getMessages().get(0).getMessage());
    }

    @Test
    public void entityNotFoundTest() {
        inputRows.add(ImmutableList.of("1", "HEADER", "NAME", "SYMBOL", "QUANTITY", "DESCRIPTION"));
        inputRows.add(ImmutableList.of("1", "DELETE", "Ampere", "A", "Current", "Electric current"));
        final DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertEquals(ErrorMessage.ENTITY_NOT_FOUND, dataLoaderResult.getMessages().get(0).getMessage());
    }

    @Test
    public void renameMisformatTest() {
        inputRows.add(ImmutableList.of("1", "HEADER", "NAME", "SYMBOL", "QUANTITY", "DESCRIPTION"));
        inputRows.add(ImmutableList.of("1", "RENAME", "Ampere", "A", "Current", "Electric current"));
        final DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertEquals(ErrorMessage.RENAME_MISFORMAT, dataLoaderResult.getMessages().get(0).getMessage());
    }

    /**
     * Test if data loader correctly detects missing header fields and returns correct {@link DataLoaderResult}
     */
    @Test
    public void headerMissingFieldsTest() {
        inputRows.add(ImmutableList.of("1", "HEADER"));

        DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertEquals(ErrorMessage.HEADER_FIELD_MISSING, dataLoaderResult.getMessages().get(0).getMessage());
        assertEquals("1", dataLoaderResult.getMessages().get(0).getRow());

        inputRows.clear();

        inputRows.add(ImmutableList.of("1", "HEADER", "NAME", "SYMBOL", "QUANTITIES", "DESCRIPTION"));
        dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertEquals(ErrorMessage.HEADER_FIELD_MISSING, dataLoaderResult.getMessages().get(0).getMessage());
        assertEquals("1", dataLoaderResult.getMessages().get(0).getRow());
    }
}
