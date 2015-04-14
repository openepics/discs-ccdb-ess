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
package org.openepics.discs.conf.views;

import java.util.List;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;

/**
 * View of container used to compose and manipulate with container presentation in tree view
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class SlotView {
    private final Long id;
    private String name;
    private String description;
    private boolean canDelete;
    private final SlotView parentNode;
    private Slot slot;
    private final boolean isHostingSlot;
    private final ComponentType deviceType;
    private final Device installedDevice;
    private final int order;
    private boolean isFirst;
    private boolean isLast;

    /** Constructs a new SlotView object.
     * @param slot the {@link Slot} to create the UI view object for
     * @param parentNode a reference to the SlotView object of a parent in the hierarchy tree
     * @param children a reference to the SlotView objects of all children in the hierarchy tree
     * @param installedDevice a reference to the {@link Device} if one is installed in the installation slot
     * @param order the ordinal number of the SlotView object - defines the order in the hierarchy tree
     */
    public SlotView(Slot slot, SlotView parentNode, List<SlotPair> children, Device installedDevice, int order) {
        this.slot = slot;
        this.name = slot.getName();
        this.description = slot.getDescription();
        this.id = slot.getId();
        this.parentNode = parentNode;
        this.isHostingSlot = slot.isHostingSlot();
        this.deviceType = slot.getComponentType();
        this.installedDevice = installedDevice;
        this.order = order;

        canDelete = true;
        for (SlotPair child : children) {
            if (child.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                canDelete = false;
                break;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public SlotView getParentNode() {
        return parentNode;
    }

    public Slot getSlot() {
        return slot;
    }

    /** This method sets the name and description at the same time as the slot.
     * @param slot the slot
     */
    public void setSlot(Slot slot) {
        this.slot = slot;
        name = slot.getName();
        description = slot.getDescription();
    }

    public boolean getIsHostingSlot() {
        return isHostingSlot;
    }

    public ComponentType getDeviceType() {
        return deviceType;
    }

    public Device getInstalledDevice() {
        return installedDevice;
    }

    public int getOrder() {
        return order;
    }

    public boolean isFirst() {
        return isFirst;
    }
    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isLast() {
        return isLast;
    }
    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }
}
