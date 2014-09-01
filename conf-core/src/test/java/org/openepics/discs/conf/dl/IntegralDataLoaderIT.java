package org.openepics.discs.conf.dl;

import static org.junit.Assert.*;

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
import org.openepics.discs.conf.security.SecurityPolicy;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.CCDBPackager;

/**
 * Integration tests for {@link UnitsDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
public class IntegralDataLoaderIT {

    @Inject @UnitLoaderQualifier private DataLoader unitsDataLoader;
    @Inject @PropertiesLoaderQualifier private DataLoader propertiesDataLoader;
    @Inject @ComponentTypesLoaderQualifier private DataLoader ctypesDataLoader;
    @Inject @DevicesLoaderQualifier private DataLoader devicesDataLoader;

    @Inject private DataLoaderHandler dataLoaderHandler;

    @Inject private SecurityPolicy securityPolicy;

    @Deployment
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
    	securityPolicy.login("admin", "admin");
    }

    @Test
    public void testAll() {
    	testLoadData("conf-data-units.xlsx", unitsDataLoader, null, null);
    	testLoadData("conf-data-properties.xlsx", propertiesDataLoader, null, null);
    	testLoadData("conf-data-ctypes.xlsx", ctypesDataLoader, null, null);
    	testLoadData("conf-data-slots.xlsx", null, "conf-data-slot-pairs.xlsx", null);
    	testLoadData("conf-data-devices.xlsx", devicesDataLoader, null, null);
    }

	private void testLoadData(String fileName, Object dataLoader, String secondFileName, Object secondDataLoader) {
		final InputStream stream = this.getClass().getResourceAsStream("/dataloader/"+fileName);

		if (secondFileName==null) {
			final DataLoaderResult result = dataLoaderHandler.loadData(stream, (DataLoader) dataLoader);
			assertFalse(fileName+" loading failed.", result.isError());
		} else {
			final InputStream secondStream = this.getClass().getResourceAsStream("/dataloader/"+secondFileName);

			final DataLoaderResult result = dataLoaderHandler.loadDataFromTwoFiles(stream, secondStream, fileName, secondFileName);
			assertFalse(fileName+" or "+secondFileName+" loading failed.", result.isError());
		}
	}

}
