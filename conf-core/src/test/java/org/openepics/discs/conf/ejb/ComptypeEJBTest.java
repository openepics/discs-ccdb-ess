package org.openepics.discs.conf.ejb;

import static org.junit.Assert.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.security.SecurityPolicy;
import org.openepics.discs.conf.util.CCDBPackager;

@RunWith(Arquillian.class)
public class ComptypeEJBTest {
    @EJB ComptypeEJB compTypes;
    @Inject private SecurityPolicy securityPolicy;
    
    @Deployment
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }
    
    @Before
    public void setUp() throws Exception {
        securityPolicy.login("admin", "admin");
    }

    @Test
    @UsingDataSet({"inital_db.xls", "component_types.xls"})
    public void testFindComponentType() {
        
    }
    
/*
    @Test
    public void testFindComponentTypeLong() {
        fail("Not yet implemented");
    }

    @Test
    public void testFindComponentTypeByName() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddComponentType() {
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
