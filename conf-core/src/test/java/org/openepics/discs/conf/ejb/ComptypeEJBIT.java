package org.openepics.discs.conf.ejb;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.security.SecurityPolicy;
import org.openepics.discs.conf.util.CCDBPackager;

/**
 * 
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
public class ComptypeEJBIT {
    @Inject ComptypeEJB compTypesService;
    @Inject ConfigurationEJB confService;
    
    @Inject private SecurityPolicy securityPolicy;
    
    private static final long SEARCH_COMP_TYPE_ID = 485;
    private static final long SEARCH_COMP_TYPE_ID_INVALID = 1000;

    private static final String SEARCH_COMP_TYPE_NAME = "V2";
    private static final String SEARCH_COMP_TYPE_NAME_INVALID = "ThisShouldNotExist";
    
    @Deployment()
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }
    
    @Before
    public void setUp() throws Exception {
        securityPolicy.login("admin", "admin");
    }

    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testFindComponentType() {
        System.out.println("Test1");
        
        final List<ComponentType> compTypes = compTypesService.findComponentType();                
        assertNotEquals(compTypes.size(), 0);
    }

    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testFindComponentTypeLong() {
        System.out.println("Test2");
        
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);
        assertNotNull(compType);    
    }
    
    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testFindComponentTypeLongInvalid() {
        System.out.println("Test3");
        
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID_INVALID);
        assertNull(compType);    
    }

    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testFindComponentTypeByName() {
        System.out.println("Test4");
        
        final ComponentType compType = compTypesService.findComponentTypeByName(SEARCH_COMP_TYPE_NAME);
        assertNotNull(compType);         
    }
    
    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testFindComponentTypeByNameInvalid() {
        System.out.println("Test5");
        
        final ComponentType compType = compTypesService.findComponentTypeByName(SEARCH_COMP_TYPE_NAME_INVALID);
        assertEquals(compType, null);         
    }
    
    @Test
    @UsingDataSet(value={"component_type.xls"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testAddComponentType() {
        final ComponentType compType = new ComponentType("someNewComponentType");
        
        compType.setDescription("some Description");
        compType.setSuperComponentType(compTypesService.findComponentType(SEARCH_COMP_TYPE_ID));

        compTypesService.addComponentType(compType);
    }

    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testSaveComponentType() {
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);
        
        final String NEW_NAME = "NewName";
        final String NEW_DESCRIPTION = "NewDescription";
        compType.setName(NEW_NAME);
        compType.setDescription(NEW_DESCRIPTION);
        
        compTypesService.saveComponentType(compType);
        
        final ComponentType newCompType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);
        
        assertEquals(newCompType.getName(), NEW_NAME);
        assertEquals(newCompType.getDescription(), NEW_DESCRIPTION);        
    }

    @Test
    @UsingDataSet(value={"component_type.xls"})
    public void testDeleteComponentType() {
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);        
        compTypesService.deleteComponentType(compType);
           
        assertNull( compTypesService.findComponentType(SEARCH_COMP_TYPE_ID) );
    }

    
    @Test
    @UsingDataSet(value={"component_type.xls", "unit.xls", "property.xls", "property_values.xls", "comptype_property_values.xls"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testAddCompTypeProp() {
        final ComponentType compType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);
        
        final ComptypePropertyValue compValue = new ComptypePropertyValue(false);
        compValue.setProperty( confService.findProperty(23l) );
        compValue.setComponentType(compType);
        compValue.setUnit( null );
        final String propValue = "33.45";
        compValue.setPropValue(propValue);
        
        compTypesService.addCompTypeProp(compValue);
        
        final ComponentType newCompType = compTypesService.findComponentType(SEARCH_COMP_TYPE_ID);
        assertNotNull(newCompType);
        assertTrue(newCompType.getComptypePropertyList().contains(compValue));
        final String newPropValue = newCompType.getComptypePropertyList().get( newCompType.getComptypePropertyList().indexOf(compValue) ).getPropValue();
        assertEquals(newPropValue, propValue);
    }

    @Test
    @UsingDataSet(value={"component_type.xls", "unit.xls", "property.xls", "property_values.xls", "comptype_property_values.xls"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testSaveCompTypeProp() {
        final ComponentType compType = compTypesService.findComponentType(1L);        
        final ComptypePropertyValue compValue = compType.getComptypePropertyList().get(0);
        final String propValue = "22";
        compValue.setPropValue(propValue);
        compTypesService.saveCompTypeProp(compValue);
        
        final ComponentType newCompType = compTypesService.findComponentType(1L);
        final String newPropValue = newCompType.getComptypePropertyList().get( newCompType.getComptypePropertyList().indexOf(compValue) ).getPropValue();
        assertEquals(propValue, newPropValue);
    }

    @Test
    @UsingDataSet(value={"component_type.xls", "unit.xls", "property.xls", "property_values.xls", "comptype_property_values.xls"})
    @ApplyScriptBefore(value={"update_sequences.sql"})
    public void testDeleteCompTypeProp() {
        final ComponentType compType = compTypesService.findComponentType(1L);
        final ComptypePropertyValue compValue = compType.getComptypePropertyList().get(0);
        compTypesService.deleteCompTypeProp(compValue);
        
        final ComponentType newCompType = compTypesService.findComponentType(1L);
        assertFalse(newCompType.getComptypePropertyList().contains(compValue));
    }

    /**
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
