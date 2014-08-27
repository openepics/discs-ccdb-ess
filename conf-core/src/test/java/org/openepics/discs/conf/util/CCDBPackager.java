package org.openepics.discs.conf.util;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
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

	    final File[] libraries = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
	    final WebArchive war = ShrinkWrap.create(WebArchive.class, "confmgr_test.war")
            .addAsLibraries(libraries)
            .addPackages(true, 
                    "org.openepics.discs.conf.ent", 
                    "org.openepics.discs.conf.ejb", 
                    "org.openepics.discs.conf.ui", 
                    "org.openepics.discs.conf.dl", 
                    "org.openepics.discs.conf.util", 
                    "org.openepics.discs.conf.security",
                    "org.openepics.discs.conf.auditlog")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("beans.xml")
            .addAsWebInfResource("jboss-web.xml")
            .addAsWebInfResource("web.xml");
	    
//	    // Add all files in datasets 
//	    final File datasetsDir = new File("src/test/resources/datasets");
//	    for (File f : datasetsDir.listFiles()) {
//	        war.addAsResource(f, "datasets/" + f.getName());
//	    }
            
        return war;
	}
}
