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
import javax.ws.rs.core.MultivaluedHashMap;

import org.openepics.discs.client.impl.ClosableResponse;
import org.openepics.discs.client.impl.ResponseException;
import org.openepics.discs.conf.jaxb.DeviceType;
import org.openepics.discs.conf.jaxb.InstallationSlot;
import org.openepics.discs.conf.jaxb.InstallationSlotNames;

/**
 * This is CCDB service client installation slot parser that is used to get data from server.
 * <p>All class methods are static.</p>
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>\
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */

public class InstallationSlotResource {
    private static final Logger LOGGER = Logger.getLogger(InstallationSlot.class.getName());

    private static final String PATH_SLOTS = "slot";
    private static final String PATH_SLOT_NAMES = "slotName";

    @Nonnull private final CCDBClient client;

    public InstallationSlotResource(CCDBClient client) { this.client = client; }

    /**
     * Requests a {@link List} of all {@link InstallationSlot}s from the REST service.
     *
     * @throws ResponseException
     *             if data couldn't be retrieved
     *
     * @return {@link List} of all {@link InstallationSlot}s received from the REST service
     */
    public List<InstallationSlot> getAllSlots() {
        LOGGER.fine("Invoking getAllSlots");

        final String url = client.buildUrl(PATH_SLOTS);
        try (final ClosableResponse response = client.getResponse(url)) {
            final List<InstallationSlot> list = response.readEntity(new GenericType<List<InstallationSlot>>() {});
            return list;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }
    
    /**
     * Requests a {@link List} of all {@link InstallationSlot} names from the REST service.
     *
     * @throws ResponseException
     *             if data couldn't be retrieved
     *
     * @return {@link List} of {@link InstallationSlotName}
     */
    public List<String> getAllSlotNames() {
        LOGGER.fine("Invoking getNamesList.");

        final String url = client.buildUrl(PATH_SLOT_NAMES);
        try (final ClosableResponse response = client.getResponse(url)) {
            return response.readEntity(new GenericType<InstallationSlotNames>(){}).getNames();
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }
    
    /**
     * Gets a list of all Installation Slots for a given Device Type
     * 
     * @param devType
     * @return 
     */
    public List<InstallationSlot> getSlotsByDeviceType(String devType) {
        LOGGER.fine("Invoking getAllSlots");

        final String url = client.buildUrl(PATH_SLOTS);
        MultivaluedHashMap queryParams = new MultivaluedHashMap();
        queryParams.put("deviceType", devType);
        try (final ClosableResponse response = client.getResponse(url, queryParams)) {
            final List<InstallationSlot> list = response.readEntity(new GenericType<List<InstallationSlot>>() {});
            return list;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
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
    public InstallationSlot getSlotByName(final String name) {
        LOGGER.fine("Invoking getInstallationSlot.");

        final String url = client.buildUrl(PATH_SLOTS, name);
        try (final ClosableResponse response = client.getResponse(url)) {
            final InstallationSlot slot = response.readEntity(InstallationSlot.class);
            return slot;
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }
}
