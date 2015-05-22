/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Cable Database.
 * Cable Database is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This contains properties of the CCDB application, such as URL of the UI and services.
 * Properties are loaded from the <code>ccdb.properties</code> file which needs to be
 * present on the classpath. All values can be overridden by setting the system properties.
 * System properties are not cached and are reflected immediately.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
public class CcdbProperties {

    private static final Logger LOGGER = Logger.getLogger(CcdbProperties.class.getName());

    /** Base web UI application URL property name. */
    public static final String APPLICATION_BASE_URL_PROPERTY_NAME = "ccdb.applicationBaseURL";
    /** Base web services URL property name. */
    public static final String SERVICES_BASE_URL_PROPERTY_NAME = "ccdb.servicesBaseURL";
    /** Cable numbering document URL property name. */

    private static final String FILE_CABLE_PROPERTIES = "ccdb.properties";

    private static final String LOADING_FAILED = "Loading properties from file " + FILE_CABLE_PROPERTIES + " failed."
            + " Using default.";

    private static CcdbProperties instance;

    private final Properties properties;

    private CcdbProperties() {
        properties = new Properties();
        try (InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(FILE_CABLE_PROPERTIES)) {
            properties.load(stream);
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.FINEST, LOADING_FAILED, e);
            LOGGER.info(LOADING_FAILED);
            properties.setProperty(APPLICATION_BASE_URL_PROPERTY_NAME, "https://localhost:8080/confmgr");
            properties.setProperty(SERVICES_BASE_URL_PROPERTY_NAME, "https://localhost:8080/confmgr/rest");
        }
    }

    /**
     * Returns the singleton instance of this class.
     * Properties are read from the properties file when the instance is first created. If
     * properties could not be read, default values are used instead. Properties can be
     * overridden by setting them as system properties.
     * <p>
     * The default values are:
     * <ul>
     * <li>{@value #APPLICATION_BASE_URL_PROPERTY_NAME} -&gt; https://localhost:8080/confmgr</li>
     * <li>{@value #SERVICES_BASE_URL_PROPERTY_NAME} -&gt; https://localhost:8080/confmgr/rest</li>
     * </ul>
     *
     * @return the singleton instance
     */
    public static synchronized CcdbProperties getInstance() {
        if (instance == null) {
            instance = new CcdbProperties();
        }
        return instance;
    }

    /**
     * Searches for the property with the specified name amongst system properties and loaded properties.
     *
     * @param name the property name
     *
     * @return the value of the property or null if not found
     */
    public String getProperty(String name) {
        final String systemProperty = System.getProperties().getProperty(name);
        return systemProperty != null ? systemProperty : properties.getProperty(name);
    }

    /** @return the value of the {@value #APPLICATION_BASE_URL_PROPERTY_NAME} property. */
    public String getApplicationBaseURL() {
        return getProperty(APPLICATION_BASE_URL_PROPERTY_NAME);
    }

    /** @return the value of the {@value #SERVICES_BASE_URL_PROPERTY_NAME} property. */
    public String getPrimarySSLHostname() {
        return getProperty(SERVICES_BASE_URL_PROPERTY_NAME);
    }
}
