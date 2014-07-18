package org.openepics.discs.conf.dl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

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
import org.openepics.discs.conf.dl.common.DataLoaderResult.NotAuthorizedFailureDataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureDataLoaderResult;

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
    public void notAuthorizedTest() {
        inputRows.add(ImmutableList.of("1", "HEADER", "NAME", "SYMBOL", "EXPR", "QUANTITY", "DESCRIPTION"));
        inputRows.add(ImmutableList.of("2", "UPDATE", "Ampere", "A", "", "Current", "Electric current"));
        DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertThat(dataLoaderResult, instanceOf(DataLoaderResult.NotAuthorizedFailureDataLoaderResult.class));

        inputRows.remove(1);
        inputRows.add(ImmutableList.of("2", "DELETE", "Ampere", "A", "", "Current", "Electric current"));
        dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertThat(dataLoaderResult, instanceOf(DataLoaderResult.NotAuthorizedFailureDataLoaderResult.class));

        inputRows.remove(1);
        inputRows.add(ImmutableList.of("2", "RENAME", "Ampere", "A", "", "Current", "Electric current"));
        dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertThat(dataLoaderResult, instanceOf(DataLoaderResult.NotAuthorizedFailureDataLoaderResult.class));
    }

    /**
     * Test if data loader correctly detects missing header fields and returns correct {@link DataLoaderResult}
     */
    @Test
    public void headerMissingFieldsTest() {
        inputRows.add(ImmutableList.of("1", "HEADER"));

        DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertThat(dataLoaderResult, instanceOf(DataLoaderResult.RowFormatFailureDataLoaderResult.class));
        assertEquals(((RowFormatFailureDataLoaderResult)dataLoaderResult).getReason(), DataLoaderResult.RowFormatFailureReason.HEADER_FIELD_MISSING);
        assertEquals("1", ((RowFormatFailureDataLoaderResult)dataLoaderResult).getRowNumber());

        inputRows.clear();

        inputRows.add(ImmutableList.of("1", "HEADER", "NAME", "SYMBOL", "EXPRESSION", "QUANTITY", "DESCRIPTION"));
        dataLoaderResult = unitsDataLoader.loadDataToDatabase(inputRows);
        assertThat(dataLoaderResult, instanceOf(DataLoaderResult.RowFormatFailureDataLoaderResult.class));
        assertEquals(((RowFormatFailureDataLoaderResult)dataLoaderResult).getReason(), DataLoaderResult.RowFormatFailureReason.HEADER_FIELD_MISSING);
        assertEquals("1", ((RowFormatFailureDataLoaderResult)dataLoaderResult).getRowNumber());
    }
}
