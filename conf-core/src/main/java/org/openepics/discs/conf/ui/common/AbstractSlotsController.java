/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ui.SlotsTreeBuilder;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.TreeNode;

public abstract class AbstractSlotsController implements Serializable{

    @Inject protected SlotsTreeBuilder slotsTreeBuilder;
    @Inject protected SlotEJB slotEJB;
    @Inject protected SlotPairEJB slotPairEJB;
    @Inject protected SlotRelationEJB slotRelationEJB;
    @Inject protected ComptypeEJB comptypeEJB;

    protected TreeNode rootNode;
    protected TreeNode selectedNode;
    protected SlotView selectedSlotView;

    protected Set<Long> collapsedNodes;

    protected String name;
    protected String description;
    protected SlotView parentSlotView;

    protected Slot newSlot;

    /**
     * Prepares fields that are used in pop up for adding new container
     */
    public void prepareAddPopup() {
        if (selectedNode != null) {
            parentSlotView = (SlotView) selectedNode.getData();
        } else {
            parentSlotView = null;
        }
        name = null;
        description = null;
    }

    /**
     * From fields populated in pop up creates new container and saves it.
     */
    protected void onSlotAdd() {
        if (selectedNode != null) {
            slotPairEJB.add(new SlotPair(newSlot, ((SlotView) selectedNode.getData()).getSlot(), slotRelationEJB.findBySlotRelationName(SlotRelationName.CONTAINS)));
        }

        List<ComptypePropertyValue> propertyDefinitions = comptypeEJB.findPropertyDefinitions(newSlot.getComponentType());
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            final SlotPropertyValue slotPropertyValue = new SlotPropertyValue(false);
            slotPropertyValue.setProperty(propertyDefinition.getProperty());
            slotPropertyValue.setSlot(newSlot);
            slotEJB.addChild(slotPropertyValue);
        }
        updateRootNode();
    }

    /**
     * Deletes selected container
     */
    public void onDelete() {
        slotEJB.delete(selectedSlotView.getSlot());
        updateRootNode();
    }

    /**
     * Sets selected {@link SlotView} and prepares fields that are used in pop up for container modification
     *
     * @param selectedSlotView selected {@link SlotView} node
     */
    public void setSelectedSlotViewToModify(SlotView selectedSlotView) {
        this.selectedSlotView = selectedSlotView;
        prepareModifyPopup();
    }
    public SlotView getSelectedSlotViewToModify() { return selectedSlotView; }

    protected void prepareModifyPopup() {
        name = selectedSlotView.getName();
        description = selectedSlotView.getDescription();
        parentSlotView = selectedSlotView.getParentNode();
    }

    /**
     * Returns root node of a tree of containers
     *
     * @return root {@link TreeNode} of tree of containers
     */
    public TreeNode getRootNode() { return rootNode; }

    public TreeNode getSelectedNode() { return selectedNode; }
    public void setSelectedNode(TreeNode selectedNode) { this.selectedNode = selectedNode; }

    /**
     * Adds collapsed node to the set of collapsed nodes which is used to preserve the state of tree
     * throughout the nodes manipulation.
     *
     * @param event Event triggered on node collapse action
     */
    public void onNodeCollapse(NodeCollapseEvent event) {
        if (event != null && event.getTreeNode() != null) {
            if (collapsedNodes == null) {
                collapsedNodes = new HashSet<>();
            }
            collapsedNodes.add(((SlotView)event.getTreeNode().getData()).getId());
            event.getTreeNode().setExpanded(false);
        }
    }

    /**
     * Removes expanded node from list of collapsed nodes which is used to preserve the state of tree
     * throughout the nodes manipulation.
     *
     * @param event Event triggered on node expand action
     */
    public void onNodeExpand(NodeExpandEvent event) {
        if (event != null && event.getTreeNode() != null) {
            if (collapsedNodes != null) {
                collapsedNodes.remove(((SlotView)event.getTreeNode().getData()).getId());
            }
        }
    }

    protected abstract void updateRootNode();

    public abstract void onSlotModify();

    public abstract String redirectToAttributes(Long id);

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SlotView getParentSlotView() { return parentSlotView; }
    public void setParentSlotView(SlotView parentContainer) { this.parentSlotView = parentContainer; }

    public SlotView getSelectedSlotView() { return selectedSlotView; }
    public void setSelectedSlotView(SlotView selectedSlotView) { this.selectedSlotView = selectedSlotView; }



}
