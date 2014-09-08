package org.openepics.discs.conf.ejb;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.util.TestUtility;

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
