package org.openepics.discs.conf.dl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.fest.assertions.AssertExtension;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
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

    @Deployment
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }

    @Test
    public void importUnitsHeaderTest() {
        final List<List<String>> imputRows = new ArrayList<>();
        imputRows.add(ImmutableList.of("HEADER"));

        final DataLoaderResult dataLoaderResult = unitsDataLoader.loadDataToDatabase(imputRows);
        assertThat(dataLoaderResult, instanceOf(DataLoaderResult.RowFormatFailureDataLoaderResult.class));
        assertEquals(((RowFormatFailureDataLoaderResult)dataLoaderResult).getReason(), DataLoaderResult.RowFormatFailureReason.HEADER_FIELD_MISSING);
    }

}
