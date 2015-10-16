/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.util;

import java.io.File;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.openepics.discs.conf.security.SecurityPolicy;

/**
 * A collection of helper methods for running integration tests.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
public class TestUtility {

    public static final String DATALOADERS_PATH = "/dataloader/";

    @Inject private SecurityPolicy securityPoloicy;

    /** @return An archive packaging the whole cable application. */
    public static WebArchive createWebArchive() {

        final File[] libraries = Maven.resolver().loadPomFromFile("pom.xml", "jboss").importRuntimeDependencies()
                                                    .resolve().withTransitivity().asFile();
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "confmgr_test.war")
                               .addAsLibraries(libraries)
                               .addPackages(true,
                                            "org.openepics.discs.conf.auditlog",
                                            "org.openepics.discs.conf.dl",
                                            "org.openepics.discs.conf.dl.common",
                                            "org.openepics.discs.conf.ejb",
                                            "org.openepics.discs.conf.ent",
                                            "org.openepics.discs.conf.ent.values",
                                            "org.openepics.discs.conf.security",
                                            "org.openepics.discs.conf.ui",
                                            "org.openepics.discs.conf.ui.common",
                                            "org.openepics.discs.conf.util",
                                            "org.openepics.discs.conf.util.names",
                                            "org.openepics.discs.conf.valueconverters",
                                            "org.openepics.discs.conf.views")
                               .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                               .addAsResource("messages.properties", "/messages.properties")
                               .addAsWebInfResource("beans.xml")
                               .addAsWebInfResource("jboss-web.xml")
                               .addAsWebInfResource("web.xml");

        // Add all files in dataloader
        final File datasetsDir = new File("src/test/resources/dataloader");
        for (File f : datasetsDir.listFiles()) {
            war.addAsResource(f, "dataloader/" + f.getName());
        }

        return war;
    }

    public void loginForTests() {
        securityPoloicy.login("admin", "admin");
    }
}
