/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.common.AbstractSlotsController;

/**
 * Controller for manipulation of container {@link Slot}s
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class ContainerController extends AbstractSlotsController {

    @PostConstruct
    public void init() {
        updateRootNode();
    }

    @Override
    protected void updateRootNode() {
        rootNode = slotsTreeBuilder.newSlotsTree(slotEJB.findByIsHostingSlot(false), collapsedNodes, false);
    }

    /**
     * Saves modifications to the container
     */
    @Override
    public void onSlotModify() {
        final Slot slotToModify = selectedSlotView.getSlot();
        slotToModify.setName(name);
        slotToModify.setDescription(description);
        slotEJB.save(slotToModify);

        updateRootNode();
    }

    @Override
    public void onSlotAdd() {
        newSlot = new Slot(name, false);
        newSlot.setDescription(description);
        if (selectedNode != null) {
            newSlot.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        } else {
            newSlot.setComponentType(comptypeEJB.findByName(SlotEJB.ROOT_COMPONENT_TYPE));
        }
        slotEJB.add(newSlot);
        super.onSlotAdd();
    }

    /**
     * Returns relative path for redirection to attribute manipulation view for id of selected container
     *
     * @param id of selected container
     * @return relative path to which vew should be redirected
     */
    @Override
    public String redirectToAttributes(Long id) { return "container-attributes-manager.xhtml?faces-redirect=true&id=" + id; }
}
