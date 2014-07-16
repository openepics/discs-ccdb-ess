package org.openepics.discs.conf.dl;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
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
	    final BeansDescriptor beans = Descriptors.create(BeansDescriptor.class).getOrCreateAlternatives().clazz("org.openepics.discs.conf.util.AppPropertiesJBoss").up();
        WebArchive war = ShrinkWrap.create(WebArchive.class, "confmgr.war")
            .addAsLibraries(libraries)
            .addPackages(true, "org.openepics.discs.conf.ent", "org.openepics.discs.conf.ejb", "org.openepics.discs.conf.ui", "org.openepics.discs.conf.dl", "org.openepics.discs.conf.util")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(new StringAsset(beans.exportAsString()), "beans.xml")
            .addAsWebInfResource("ccdb_test-ds.xml");

        return war;
	}
}
