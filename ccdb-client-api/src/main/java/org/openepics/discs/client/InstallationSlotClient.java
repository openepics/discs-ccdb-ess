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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedHashMap;

import org.openepics.discs.client.impl.ClosableResponse;
import org.openepics.discs.client.impl.ResponseException;
import org.openepics.discs.ccdb.jaxb.InstallationSlot;
import org.openepics.discs.ccdb.jaxb.InstallationSlotNames;
import org.openepics.discs.ccdb.jaxrs.InstallationSlotResource;

/**
 * This is CCDB service client installation slot parser that is used to get data from server.
 * <p>All class methods are static.</p>
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>\
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */

class InstallationSlotClient implements 
        InstallationSlotResource {
    private static final Logger LOG = Logger.getLogger(InstallationSlot.class.getName());

    private static final String PATH_SLOTS = "slot";
    
    @Nonnull private final CCDBClient client;

    InstallationSlotClient(CCDBClient client) { this.client = client; }

    /**
     * Gets either all installation slots, or filtered by the deviceType parameter
     * 
     * @param deviceType optional (can be null) device type to filter the slots
     * @return list of InstallationSlot
     */
    @Override
    public List<InstallationSlot> getInstallationSlots(String deviceType) {        
        LOG.fine("Invoking getInstallationSlots");

        final String url = client.buildUrl(PATH_SLOTS);
        
        MultivaluedHashMap queryParams = new MultivaluedHashMap();
        if (deviceType!=null) {
            queryParams.put("deviceType", Arrays.asList(deviceType));
        }
        try (final ClosableResponse response = client.getResponse(url, queryParams)) {
            return response.readEntity(new GenericType<List<InstallationSlot>>(){});
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }

    @Override
    public InstallationSlot getInstallationSlot(String name) {
        LOG.fine("Invoking getInstallationSlot");

        final String url = client.buildUrl(PATH_SLOTS, name);
        try (final ClosableResponse response = client.getResponse(url)) {
            return response.readEntity(InstallationSlot.class);
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }
}
