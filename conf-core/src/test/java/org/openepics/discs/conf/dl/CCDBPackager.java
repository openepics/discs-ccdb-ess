package org.openepics.discs.conf.dl;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * This is a static helper class that packages CCDB application into {@link WebArchive} appropriate
 * for running integration tests.
 * 
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 */
public class CCDBPackager {

	/** @return An archive packaging the whole cable application. */
	public static WebArchive createWebArchive() {
	    // Try to load the datasource-file to use from the test environment, if not present use default..
	    // If special string NONE is given the datasource file will not be deployed
	    String datasourceFile = System.getProperty("ccdb.test.datasource-file");
        if (datasourceFile==null) datasourceFile = "ccdb_test-ds.xml";          
                   
	    File[] libraries = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        WebArchive war = ShrinkWrap.create(WebArchive.class, "confmgr.war")
            .addAsLibraries(libraries)
            .addPackages(true, "org.openepics.discs.conf.ent", "org.openepics.discs.conf.ejb", "org.openepics.discs.conf.ui", "org.openepics.discs.conf.dl")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        // Optionally deploy the datasource file
        //war = "NONE".equals(datasourceFile) ? war : war.addAsWebInfResource(datasourceFile, "cabledb_test-ds.xml");
 
        return war;
	}
}
