/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.views;

import java.util.List;

import org.openepics.discs.conf.ent.ComponentType;
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
    private Long id;
    private String name;
    private String description;
    private boolean canDelete;
    private SlotView parentNode;
    private Slot slot;
    private boolean isHostingSlot;
    private ComponentType deviceType;
    private Double beamlinePosition;
    private Double globalX;
    private Double globalY;
    private Double globalZ;
    private Double globalPitch;
    private Double globalRoll;
    private Double globalYaw;

    public SlotView(Slot slot, SlotView parentNode, List<SlotPair> children) {
        this.slot = slot;
        this.name = slot.getName();
        this.description = slot.getDescription();
        this.id = slot.getId();
        this.parentNode = parentNode;
        this.isHostingSlot = slot.getIsHostingSlot();
        this.deviceType = slot.getComponentType();
        this.beamlinePosition = slot.getBeamlinePosition();
        this.globalX = slot.getPositionInformation().getGlobalX();
        this.globalY = slot.getPositionInformation().getGlobalY();
        this.globalZ = slot.getPositionInformation().getGlobalZ();
        this.globalPitch = slot.getPositionInformation().getGlobalPitch();
        this.globalRoll = slot.getPositionInformation().getGlobalRoll();
        this.globalYaw = slot.getPositionInformation().getGlobalYaw();

        canDelete = true;
        for (SlotPair child : children) {
            if (child.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                canDelete = false;
                break;
            }
        }
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public boolean getCanDelete() { return canDelete; }

    public SlotView getParentNode() { return parentNode; }

    public Slot getSlot() { return slot; }

    public boolean getIsHostingSlot() { return isHostingSlot; }

    public ComponentType getDeviceType() { return deviceType; }

    public Double getBeamlinePosition() { return beamlinePosition; }

    public Double getGlobalX() { return globalX; }

    public Double getGlobalY() { return globalY; }

    public Double getGlobalZ() { return globalZ; }

    public Double getGlobalPitch() { return globalPitch; }

    public Double getGlobalRoll() { return globalRoll; }

    public Double getGlobalYaw() { return globalYaw; }
}
