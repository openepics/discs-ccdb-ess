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

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.openepics.discs.conf.jaxb.DeviceType;
import org.openepics.discs.conf.jaxb.InstallationSlot;
import org.openepics.discs.conf.jaxb.InstallationSlotBasic;

/**
 * This is CCDB service client installation slot parser that is used to get data from server.
 * <p>All class methods are static.</p>
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */

public class InstallationSlotResource {

    private static final Logger LOGGER = Logger.getLogger(InstallationSlot.class.getName());

    /**
     * Requests a {@link List} of all {@link InstallationSlot}s from the REST service.
     *
     * @throws ResponseException
     *             if data couldn't be retrieved
     *
     * @return {@link List} of all {@link InstallationSlot}s received from the REST service
     */
    public static List<InstallationSlot> getAllSlots() {
        LOGGER.fine("Invoking getAllSlots");

        final CCDBClient client = CCDBClient.getInstance();
        try {
            final Response response = client.getResponse(client.URL_INSTALLATION_SLOT);
            final List<InstallationSlot> list = response.readEntity(new GenericType<List<InstallationSlot>>() {});
            return list;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + client.URL_INSTALLATION_SLOT + ".",
                    e);
        }
    }

    /**
     * Requests particular {@link InstallationSlot} from the REST service.
     *
     * @param name
     *            the name of the desired {@link InstallationSlot}
     * @throws ResponseException
     *             if data couldn't be retrieved.
     *
     * @return {@link InstallationSlot}
     */
    public static InstallationSlot getInstallationSlot(final String name) {
        LOGGER.fine("Invoking getInstallationSlot.");

        final CCDBClient client = CCDBClient.getInstance();

        try {
            final Response response = client.getResponse(client.URL_INSTALLATION_SLOT + CCDBClient.PATH_SEPARATOR
                    + name);
            final InstallationSlot slot = response.readEntity(InstallationSlot.class);
            return slot;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + client.URL_INSTALLATION_SLOT
                    + CCDBClient.PATH_SEPARATOR + name + ".", e);
        }
    }

    /**
     * Requests a {@link List} of all {@link InstallationSlot} names from the REST service.
     *
     * @throws ResponseException
     *             if data couldn't be retrieved
     *
     * @return {@link List} of {@link InstallationSlotBasic}
     */
    public static List<InstallationSlotBasic> getNamesList() {
        LOGGER.fine("Invoking getNamesList.");

        final CCDBClient client = CCDBClient.getInstance();

        try {
            final Response response = client.getResponse(client.URL_SLOT_BASICS);
            final List<InstallationSlotBasic> list = response
                    .readEntity(new GenericType<List<InstallationSlotBasic>>() {});
            return list;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + client.URL_SLOT_BASICS + ".", e);
        }
    }

    /**
     * Requests a {@link List} of all {@link InstallationSlot} names of particular {@link DeviceType} from
     * the REST service.
     *
     * @param type
     *            the requested {@link DeviceType}
     *
     * @throws ResponseException
     *             if data couldn't be retrieved.
     *
     * @return {@link List} of all {@link InstallationSlotBasic} of particular {@link DeviceType}
     */
    public static List<InstallationSlotBasic> getNamesList(final DeviceType type) {
        LOGGER.fine("Invoking getNamesList.");
        final CCDBClient client = CCDBClient.getInstance();

        try {
            final Response response = client.getResponse(client.URL_SLOT_BASICS, "type", type.getName());
            final List<InstallationSlotBasic> list = response
                    .readEntity(new GenericType<List<InstallationSlotBasic>>() {});
            return list;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + client.URL_SLOT_BASICS + ".", e);
        }
    }
}
