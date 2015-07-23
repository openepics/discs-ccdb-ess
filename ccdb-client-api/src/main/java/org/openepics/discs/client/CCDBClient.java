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
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.openepics.discs.client.impl.CCDBClientConfigException;
import org.openepics.discs.client.impl.ClosableResponse;

/**
 * This is CCDB service client API that clients can use to access the service.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
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
    /** Path separator */
    private static final String PATH_SEPARATOR = "/";

    @Nonnull final private String baseUrl;
    @Nonnull final private String username;
    @Nonnull final private String password;

    @Nonnull final Client client = ClientBuilder.newClient();

    /**
     * <p>
     * Constructs the instance of the client and loads properties from {@link #PROPERTIES_FILENAME} file found on class
     * path. All values can be overridden by setting the system properties, but that has to be done before this class is
     * loaded.
     * </p><p>
     * Configurable properties are {@link CCDBClient#PROPERTY_NAME_BASE_URL}, {@link CCDBClient#PROPERTY_NAME_USERNAME},
     * {@link CCDBClient#PROPERTY_NAME_PASSWORD}
     * </p>
     * @param userProperties optional (can be <code>null</code>) properties file that contains configurable properties
     *
     */
    public CCDBClient(@Nullable Properties userProperties) {
        Properties properties = resolveProperties(userProperties);

        baseUrl = getProperty(properties, PROPERTY_NAME_BASE_URL);
        username = getProperty(properties, PROPERTY_NAME_USERNAME);
        password = getProperty(properties, PROPERTY_NAME_PASSWORD);
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
    public ClosableResponse getResponse(final String url,
                                                @Nullable final MultivaluedMap<String, Object> queryParameters) {
        final UriBuilder ub = UriBuilder.fromUri(url);
        if (queryParameters != null) {
            for (Entry<String, List<Object>> entry : queryParameters.entrySet()) {
                ub.queryParam(entry.getKey(), entry.getValue().toArray());
            }
        }
        return new ClosableResponse(client.target(ub).request(MediaType.APPLICATION_JSON_TYPE).get());
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
    public ClosableResponse getResponse(final String url) {
        return getResponse(url, null);
    }

    /**
     * Requests JSON type data from URL using a query string parameter.
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
    public ClosableResponse getResponse(final String url, final String paramName, final Object paramValue) {
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

    /**
     * Builds a url with the base path and the path parameter specified as parameter
     * @param path the sub-path of the base url
     *
     * @return URL path section under the base URL
     */
    public String buildUrl(String... path)
    {
        final StringBuilder builder = new StringBuilder(baseUrl);
        for (final String subPath : path) {
            builder.append(PATH_SEPARATOR);
            builder.append(subPath);
        }
        return builder.toString();
    }

    public String getBaseURL() { return baseUrl; }
    public String getUserName() { return username; }
    public String getPassword() { return password; }

    /**
     * If user provided a {@link Properties} object, use that and return, otherwise try to load from
     * classpath the {@link CCDBClient#PROPERTIES_FILENAME} file.
     *
     * Throws {@link CCDBClientConfigException} if fails to get properties
     *
     * @param userProperties
     * @return
     */
    private Properties resolveProperties(@Nullable final Properties userProperties) {
        final Properties properties;
        if (userProperties != null) {
            properties = userProperties;
        } else {
            properties = new Properties();
            try (final InputStream stream = CCDBClient.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME)) {
                properties.load(stream);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Loading properties from resource file " + PROPERTIES_FILENAME
                        + " failed.");
                throw new CCDBClientConfigException("Failed to load CCDB client configuration", e);
            }
        }
        return properties;
    }

    /**
     * Gets a property from a {@link Properties} or if not found, looks into system properties
     *
     * @param custom
     * @param key
     * @return
     */
    private static String getProperty(final Properties custom, final String key) {
        final String customPropValue = custom.getProperty(key);
        final String propValue = customPropValue != null ? customPropValue : System.getProperties().getProperty(key);

        if (propValue == null) {
            throw new CCDBClientConfigException("CCDB Client property not found: " + key);
        }

        return propValue;
    }
}
