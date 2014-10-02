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
package org.openepics.discs.conf.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.DeviceInstallation;

/**
 * Tree builder for tree presentation of {@link Slot}s when installing device
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
@DeviceInstallation
public class InstallationSlotsTreeBuilder extends SlotsTreeBuilder {

    @Override
    protected List<Slot> filterSlotsAndPrepareCache(final List<Slot> incomingSlotList, final Map<Long, Slot> slotCache,
            final String requestedComponentTypeName) {
        List<Slot> filteredList = new ArrayList<>(incomingSlotList.size());

        for (Slot slot : incomingSlotList) {
            slotCache.put(slot.getId(), slot);
            if (requestedComponentTypeName.isEmpty()) {
                filteredList.add(slot);
            } else {
                // include only installation slots of the requested type (and all containers)
                final String componentTypeName = slot.getComponentType().getName();
                if (componentTypeName.equals(SlotEJB.ROOT_COMPONENT_TYPE)
                        || componentTypeName.equals(SlotEJB.GRP_COMPONENT_TYPE)
                        || componentTypeName.equals(requestedComponentTypeName)) {
                    // The installation slot of this type is not needed and can be removed.
                    // This shrinks the slots collection and makes subsequent tree build shorter.
                    filteredList.add(slot);
                }
            }
        }
        return filteredList;
    }

    @Override
    protected Device getInstalledDeviceForSlot(final Slot slot, final ComponentType installationSlotType) {
        final Device installedDevice;
        if ((installationSlotType == null) || !slot.isHostingSlot()) {
            // no installed device is required or this is a container
            installedDevice = null;
        } else {
            final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);
            installedDevice = installationRecord == null ? null : installationRecord.getDevice();
        }
        return installedDevice;
    }

    @Override
    protected boolean isRootNodeSelectable() {
        // in installation dialog the root is never selectable
        return false;
    }

    @Override
    protected boolean isNodeSelectable(Slot slot, ComponentType installationSlotType, Device installedDevice) {
        return (installationSlotType != null) && slot.isHostingSlot()
                && slot.getComponentType().equals(installationSlotType) && installedDevice == null;
    }
}
