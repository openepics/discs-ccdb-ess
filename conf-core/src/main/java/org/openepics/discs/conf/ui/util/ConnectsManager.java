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
package org.openepics.discs.conf.ui.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.openepics.cable.client.CableDBClient;
import org.openepics.cable.jaxb.CableElement;
import org.openepics.cable.jaxb.CableResource;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.AppProperties;

/**
 * Service for CableDB
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovic</a>
 */
@ViewScoped
public class ConnectsManager implements Serializable  {
    private static final long serialVersionUID = 1615712215239730844L;

    @Inject private SlotEJB slotEJB;
    @Inject private AppProperties properties;

    private List<CableElement> cables = null;
    private boolean cableDBStatus = false;

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        cableDBStatus = "TRUE".equalsIgnoreCase(properties.getProperty(AppProperties.CABLEDB_STATUS));

        if (cableDBStatus) {
            CableDBClient c = new CableDBClient(null);
            CableResource cr = c.createCableResource();
            cables = cr.getAllCables(Collections.emptyList(), "");
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

    /** Return all cables connecting two {@link Slot}s
     * @param slot1 slot1
     * @param slot2 slot2
     * @return A {@link List} of {@link CableElement}s connecting the two {@link Slot}s
     */
    public List<CableElement> getCables(Slot slot1, Slot slot2) {
        List<CableElement> r = new ArrayList<CableElement>();
        String n1 = slot1.getName();
        String n2 = slot2.getName();

        for (CableElement cable : cables) {
            if (n1.equals(cable.getEndpointA().getDevice()) && n2.equals(cable.getEndpointB().getDevice())) {
                r.add(cable);
            } else if (n1.equals(cable.getEndpointB().getDevice()) && n2.equals(cable.getEndpointA().getDevice())) {
                r.add(cable);
            }
        }
        return r;
    }

    /** @return the cableDBStatus */
    public boolean getCableDBStatus() {
        return cableDBStatus;
    }

    public String getRelationshipName() {
        return "Connects";
    }
}
