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

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.Slot;

/**
 * View of container used to compose and manipulate with container presentation in tree view
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
public class SlotView {
    private final Long id;
    private String name;
    private String description;
    private boolean isDeletable;
    private final SlotView parentNode;
    private final boolean isHostingSlot;
    private String deviceTypeName;
    private Device installedDevice;
    private final int order;
    private boolean isFirst;
    private boolean isLast;
    private boolean isInitialzed;
    private int level;
    final private SlotEJB slotEJB;
    private String cableNumber;

    /** Simpler constructor, used in the new Hierarchy builder.
     * @param slot the {@link Slot} to create the UI view object for
     * @param parentNode a reference to the SlotView object of a parent in the hierarchy tree
     * @param order the ordinal number of the SlotView object - defines the order in the hierarchy tree
     * @param slotEJB the {@link Slot} Enterprise bean
     */
    public SlotView(Slot slot, SlotView parentNode, int order, SlotEJB slotEJB) {
        this.name = slot.getName();
        this.description = slot.getDescription();
        this.id = slot.getId();
        this.parentNode = parentNode;
        this.isHostingSlot = slot.isHostingSlot();
        this.deviceTypeName = slot.getComponentType().getName();
        this.order = order;
        this.slotEJB = slotEJB;
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

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    public SlotView getParentNode() {
        return parentNode;
    }

    public Slot getSlot() {
        return slotEJB != null ? slotEJB.findById(id) : null;
    }

    /** This method sets the name and description at the same time as the slot.
     * @param slot the slot
     */
    public void setSlot(Slot slot) {
        name = slot.getName();
        description = slot.getDescription();
        deviceTypeName = slot.getComponentType().getName();
    }

    public boolean isHostingSlot() {
        return isHostingSlot;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public Device getInstalledDevice() {
        return installedDevice;
    }

    public void setInstalledDevice(Device device) {
        this.installedDevice = device;
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

    public boolean isInitialzed() {
        return isInitialzed;
    }

    public void setInitialzed(boolean isInitialzed) {
        this.isInitialzed = isInitialzed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the cableNumber
     */
    public String getCableNumber() {
        return cableNumber;
    }

    /**
     * @param cableNumber the cableNumber to set
     */
    public void setCableNumber(String cableNumber) {
        this.cableNumber = cableNumber;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SlotView) return id.equals((((SlotView)obj).id));
        return false;
    }
}
