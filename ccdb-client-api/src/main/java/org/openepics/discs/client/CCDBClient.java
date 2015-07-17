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
package org.openepics.discs.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * This is CCDB service client API that clients can use to access the service.
 *
 * <p>
 * CCDBClient is implemented as a singleton; use {@link #getInstance()} to get an instance of the client.
 * </p>
 * <p>
 * CCDBCleint is thread-safe. The {@link #getResponse(String, MultivaluedMap)} creates new REST connections, all
 * other methods work with {@link Properties} which is thread-safe as well.
 * </p>
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */

public class CCDBClient {

    private static final Logger LOGGER = Logger.getLogger(CCDBClient.class.getName());

    /** Name of the Base REST service URL property */
    public static final String PROPERTY_NAME_BASE_URL = "CCDB.servicesBaseURL";
    /** Name of the username property */
    public static final String PROPERTY_NAME_USERNAME = "CCDB.UserName";
    /** Name of the password property */
    public static final String PROPERTY_NAME_PASSWORD = "CCDB.Password";
    /** Name of the properties file */
    public static final String PROPERTIES_FILENAME = "CCDB.properties";

    private static final String PATH_DEVICE_TYPE = "deviceType";
    private static final String PATH_SLOT_BASICS = "slotBasic";
    private static final String PATH_INSTALLATION_SLOT = "installationSlot";
    /** Path separator to avoid SonarQube complaints */
    protected static final String PATH_SEPARATOR = "/";

    /** URL for getting installationSlots from the REST service */
    protected final String URL_INSTALLATION_SLOT;
    /** URL for getting slotBasics from the REST service */
    protected final String URL_SLOT_BASICS;
    /** URL for getting deviceType from the REST service */
    protected final String URL_DEVICE_TYPE;

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/confmgr/rest";

    private static final CCDBClient INSTANCE = new CCDBClient();
    private Properties properties;

    /** @return singleton instance of {@link CCDBClient} */
    public static CCDBClient getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs the instance of the client and loads properties from {@link #PROPERTIES_FILENAME} file found on class
     * path. All values can be overridden by setting the system properties, but that has to be done before this class is
     * loaded.
     */
    private CCDBClient() {
        properties = new Properties();
        try (final InputStream stream = CCDBClient.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME)) {
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Loading properties from file " + PROPERTIES_FILENAME
                    + " failed. Loading default values.");
            properties.setProperty(PROPERTY_NAME_BASE_URL, DEFAULT_BASE_URL);
        }
        properties.putAll(System.getProperties());
        URL_DEVICE_TYPE = getBaseURL() + PATH_SEPARATOR + PATH_DEVICE_TYPE;
        URL_INSTALLATION_SLOT = getBaseURL() + PATH_SEPARATOR + PATH_INSTALLATION_SLOT;
        URL_SLOT_BASICS = getBaseURL() + PATH_SEPARATOR + PATH_SLOT_BASICS;
    }

    /**
     * Requests JSON type data from URL.
     *
     * <p>Method is thread-safe.</p>
     *
     * @param url
     *            to send request
     * @param queryParameters
     *            optional list of query parameters to use in request
     *
     * @throws IllegalStateException
     *             if either the username or password not set
     *
     * @return received response
     */
    public Response getResponse(final String url,
                                                @Nullable MultivaluedMap<String, Object> queryParameters) {
        final String userName = getUserName();
        final String password = getPassword();
        if (userName == null || password == null) {
            throw new IllegalStateException("Username or password not set.");
        }
        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(url);
        if (queryParameters != null) {
            target.queryParams(queryParameters);
        }
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    /**
     * Requests JSON type data from URL.
     *
     * <p>Method is thread-safe.</p>
     *
     * @param url
     *            to send request
     *
     * @throws IllegalStateException
     *             if either the username or password not set
     *
     * @return received response
     */
    public Response getResponse(final String url) {
        return getResponse(url, null);
    }

    /**
     * Requests Json type data from URL using a query string parameter.
     *
     * <p>Method is thread-safe.</p>
     *
     * @param url
     *              to send request to
     * @param paramName
     *              query string parameter name to set
     * @param paramValue
     *              query string parameter value to set
     *
     * @throws IllegalStateException
     *              if either the username or password are not set
     * @throws IllegalArgumentException
     *              if either paramName or paramValue are <code>null</code>
     *
     * @return received response
     */
    public Response getResponse(final String url, final String paramName, final Object paramValue) {
        if (paramName == null) {
            throw new IllegalArgumentException("Parameter paramName must not be null.");
        }
        if (paramValue == null) {
            throw new IllegalArgumentException("Parameter paramValue must not be null.");
        }
        final MultivaluedHashMap<String, Object> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add(paramName, paramValue);
        return getResponse(url, queryParameters);
    }

    //------------------------------ GETTERS ------------------------------

    public String getBaseURL() {
        return properties.getProperty(PROPERTY_NAME_BASE_URL);
    }

    public String getUserName() {
        return properties.getProperty(PROPERTY_NAME_USERNAME);
    }

    public String getPassword() {
        return properties.getProperty(PROPERTY_NAME_PASSWORD);
    }

    //------------------------------ SETTERS ------------------------------

    public void setUserName(final String username) {
        properties.setProperty(PROPERTY_NAME_USERNAME, username);
    }

    public void setPassword(final String password) {
        properties.setProperty(PROPERTY_NAME_PASSWORD, password);
    }
}
