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

import org.openepics.discs.conf.ent.Slot;

/**
 * View of container used to compose and manipulate with container presentation in tree view
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class ContainerView {
    private Long id;
    private String name;
    private String description;
    private boolean hasChildren;
    private ContainerView parentNode;
    private Slot slot;

    public ContainerView (Slot slot, ContainerView parentNode, boolean hasChildren) {
        this.slot = slot;
        this.name = slot.getName();
        this.description = slot.getDescription();
        this.id = slot.getId();
        this.hasChildren = hasChildren;
        this.parentNode = parentNode;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public boolean getHasChildren() { return hasChildren; }

    public ContainerView getParentNode() { return parentNode; }

    public Slot getSlot() { return slot; }


}
