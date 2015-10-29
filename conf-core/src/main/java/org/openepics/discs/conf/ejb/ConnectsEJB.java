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
package org.openepics.discs.conf.ejb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.openepics.cable.client.CableDBClient;
import org.openepics.cable.jaxb.CableElement;
import org.openepics.cable.jaxb.CableResource;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.AppProperties;

/**
 * Service for CableDB
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovic</a>
 *
 */
@Singleton
public class ConnectsEJB {
    @Inject private transient SlotEJB slotEJB;
    @Inject private transient AppProperties properties;

    private List<CableElement> cables = null;
    private boolean cableDBStatus = false;

    @PostConstruct
    public void init()
    {
        final String cableDBStatusStr = properties.getProperty(AppProperties.CABLEDB_STATUS);
        cableDBStatus = cableDBStatusStr == null ? false : "TRUE".equals(cableDBStatusStr.toUpperCase());

        if (cableDBStatus) {
            CableDBClient c = new CableDBClient(null);
            CableResource cr = c.createCableResource();
            cables = cr.getAllCables();
        } else {
            cables = Arrays.asList();
        }
    }

    /**
     * @param slot - the slot to use in query.
     * @return The list of all {@link Slot}s to which the slot is connected.
     */
    public List<Slot> getSlotConnects(Slot slot) {
        String n1 = slot.getName();
        List<Slot> connects = new ArrayList<>();
        HashSet<String> uniqueEndpoints = new HashSet<String>();

        for (CableElement cable : cables) {
            String n2;
            if (n1.equals(cable.getEndpointA().getDevice())) {
                n2 = cable.getEndpointB().getDevice();
            } else if (n1.equals(cable.getEndpointB().getDevice())) {
                n2 = cable.getEndpointA().getDevice();
            } else continue;

            if (!uniqueEndpoints.contains(n2)) {
                connects.addAll(slotEJB.findAllByName(n2));
                uniqueEndpoints.add(n2);
            }
        }
        uniqueEndpoints.clear();

        return connects;
    }

    /**
     * @return the cableDBStatus
     */
    public boolean getCableDBStatus() {
        return cableDBStatus;
    }
}
