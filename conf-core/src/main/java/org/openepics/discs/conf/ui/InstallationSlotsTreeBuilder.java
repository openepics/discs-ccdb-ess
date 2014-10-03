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
import java.util.Set;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.DeviceInstallation;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;

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
    public TreeNode newSlotsTree(List<Slot> slots, Slot selected, Set<Long> collapsedNodes,
            boolean withInstallationSlots, ComponentType installationSlotType) {
        Preconditions.checkNotNull(installationSlotType);
        return super.newSlotsTree(slots, selected, collapsedNodes, withInstallationSlots, installationSlotType);
    }

    @Override
    protected List<Slot> filterSlots(final List<Slot> incomingSlotList, final String requestedComponentTypeName,
            boolean withInstallationSlots) {
        Preconditions.checkArgument(!requestedComponentTypeName.isEmpty());
        List<Slot> filteredList = new ArrayList<>(incomingSlotList.size());

        for (Slot slot : incomingSlotList) {
            // Include only installation slots of the requested type (and all containers).
            // This shrinks the slots collection and makes subsequent tree build shorter.
            final String componentTypeName = slot.getComponentType().getName();
            if (componentTypeName.equals(SlotEJB.ROOT_COMPONENT_TYPE)
                    || componentTypeName.equals(SlotEJB.GRP_COMPONENT_TYPE)
                    || componentTypeName.equals(requestedComponentTypeName)) {
                filteredList.add(slot);
            }
        }
        return filteredList;
    }

    @Override
    protected Device getInstalledDeviceForSlot(final Slot slot) {
        if (!slot.isHostingSlot()) {
            // this is a container
            return null;
        } else {
            final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);
            return installationRecord == null ? null : installationRecord.getDevice();
        }
    }

    @Override
    protected boolean isRootNodeSelectable() {
        // in installation dialog the root is never selectable
        return false;
    }

    @Override
    protected boolean isNodeSelectable(Slot slot, ComponentType installationSlotType, Device installedDevice) {
        return installedDevice == null && slot.isHostingSlot() && slot.getComponentType().equals(installationSlotType);
    }
}
