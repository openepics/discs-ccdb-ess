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

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.ws.rs.core.GenericType;

import org.openepics.discs.client.impl.ClosableResponse;
import org.openepics.discs.client.impl.ResponseException;
import org.openepics.discs.conf.jaxb.DeviceType;

/**
 * This is CCDB service clientdataType parser that is used to get data from server.
 * <p>
 * All class methods are static.
 * </p>
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */

public class DeviceTypeResource {
    private static final String PATH_DEVICE_TYPE = "deviceType";
    private static final Logger LOGGER = Logger.getLogger(DeviceTypeResource.class.getName());

    @Nonnull private final CCDBClient client;

    public DeviceTypeResource(CCDBClient client) { this.client = client; }

    /**
     * Requests a {@link List} of all {@link DeviceType}s from the REST service.
     *
     * @throws ResponseException
     *             if data couldn't be retrieved
     *
     * @return {@link List} of all {@link DeviceType}s
     */
    public List<DeviceType> getAllDeviceTypes() {
        LOGGER.fine("Invoking getAllDeviceTypes");

        final String url = client.buildUrl(PATH_DEVICE_TYPE);
        try (final ClosableResponse response = client.getResponse(url)) {
            final List<DeviceType> list = response.readEntity(new GenericType<List<DeviceType>>() {});
            return list;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }

    /**
     * Requests particular {@link DeviceType} from the REST service.
     *
     * @param id
     *            the id of desired DeviceType
     *
     * @throws ResponseException
     *             if data couldn't be retrieved
     *
     * @return {@link DeviceType}
     */
    public DeviceType getDeviceType(final int id) {
        LOGGER.fine("Invoking getDeviceType");

        final String url = client.buildUrl(PATH_DEVICE_TYPE, Integer.toString(id));
        try (final ClosableResponse response = client.getResponse(url)) {
            final DeviceType device = response.readEntity(DeviceType.class);
            return device;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }
}
