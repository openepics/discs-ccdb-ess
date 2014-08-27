package org.openepics.discs.conf.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.security.SecurityPolicy;
import org.openepics.discs.conf.util.CCDBPackager;

@RunWith(Arquillian.class)
public class ComptypeEJBIT {
    @Inject ComptypeEJB compTypesService;
    @Inject private SecurityPolicy securityPolicy;
    
    private static final long SEARCH_COMP_TYPE_ID = 485;
    private static final long SEARCH_COMP_TYPE_ID_INVALID = 1000;

    private static final String SEARCH_COMP_TYPE_NAME = "V2";
    private static final String SEARCH_COMP_TYPE_NAME_INVALID = "ThisShouldNotExist";
    
    @Deployment
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }
    
    @Before
    public void setUp() throws Exception {
        securityPolicy.login("admin", "admin");
    }

    @Test
    @UsingDataSet(value={"component_types.xls"})
    public void testFindComponentType() {
        final List<ComponentType> compTypes = compTypesService.findComponentType();                
        assertNotEquals(compTypes.size(), 0);
    }

    @Test
    @UsingDataSet(value={"component_types.xls"})
    public void testFindComponentTypeLong() {
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);
        assertNotEquals(compType, null);    
    }
    
    @Test
    @UsingDataSet(value={"component_types.xls"})
    public void testFindComponentTypeLongInvalid() {
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID_INVALID);
        assertEquals(compType, null);    
    }

    @Test
    @UsingDataSet(value={"component_types.xls"})
    public void testFindComponentTypeByName() {
        final ComponentType compType = compTypesService.findComponentTypeByName(SEARCH_COMP_TYPE_NAME);
                assertNotEquals(compType, null);         
    }
    
    @Test
    @UsingDataSet(value={"component_types.xls"})
    public void testFindComponentTypeByNameInvalid() {
        final ComponentType compType = compTypesService.findComponentTypeByName(SEARCH_COMP_TYPE_NAME_INVALID);
        assertEquals(compType, null);         
    }

    /*
    @Test
    public void testAddComponentType() {
        final ComponentType compType = new ComponentType("someNewComponentType");
        
        compType.setDescription("some Description");
        compType.setSuperComponentType(compTypesService.findComponentType(SEARCH_COMP_TYPE_ID));
        
        final HashSet<Tag> tagSet = new HashSet<>();
        tagSet.add(new Tag("QUAD", ""));
        
        fail("Not yet implemented");
    }

    @Test
    public void testSaveComponentType() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteComponentType() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddCompTypeProp() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveCompTypeProp() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteCompTypeProp() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddCompTypeArtifact() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveCompTypeArtifact() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteCompTypeArtifact() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveComptypeAsm() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteComptypeAsm() {
        fail("Not yet implemented");
    }*/

}
