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
package org.openepics.discs.conf.ui.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.SlotsTreeBuilder;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.TreeNode;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public abstract class AbstractSlotsController implements Serializable{

    @Inject protected SlotsTreeBuilder slotsTreeBuilder;
    @Inject protected SlotEJB slotEJB;
    @Inject protected ComptypeEJB comptypeEJB;
    @Inject protected SlotPairEJB slotPairEJB;
    @Inject private InstallationEJB installationEJB;

    protected TreeNode rootNode;
    /** The currently selected PrimeFaces tree node */
    protected TreeNode selectedNode;
    /** Used in modify slot operations, this is usually set when the user clicks the pencil icon,
     * <code>null</code> otherwise. */
    protected SlotView selectedSlotView;

    protected Set<Long> collapsedNodes;

    protected String name;
    protected String description;
    /** Used in "add child to parent" operations. This usually reflects the <code>selectedNode</code>. */
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
     * From fields populated in pop up creates new container and saves it. This method implicitly works on the
     * <code>newSlot</code> field, which must be set by the descendants.
     */
    protected void onSlotAdd() {
        final Slot parentSlot = selectedNode != null ? ((SlotView) selectedNode.getData()).getSlot() : null;
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parentSlot, false);

        updateRootNode();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Slot created", "Slot has been successfully created");
    }

    /**
     * Deletes selected container
     */
    public void onDelete() {
        if (!selectedSlotView.getIsHostingSlot() || installationEJB.getActiveInstallationRecordForSlot(selectedSlotView.getSlot()) == null) {
            slotEJB.delete(selectedSlotView.getSlot());
            selectedSlotView = null;
            selectedNode = null;
            updateRootNode();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Slot deleted", "Slot has been successfully deleted");
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "Installation slot could not be deleted because it has a device installed on it.");
        }
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

    /** The action event to be called when the user presses the "move up" action icon. This action moves the current
     * container/installation slot up one space, if that is possible.
     * @param ev
     */
    public void moveUp(ActionEvent ev) {
        /* determined by the debugger:
         * action is invoked by the CommandLink
         * parent of CommandLink is Column
         * parent of Column is TreeTable
         */
        TreeNode currentNode = ((TreeTable)((CommandLink)ev.getSource()).getParent().getParent()).getRowNode();
        TreeNode parent = currentNode.getParent();

        ListIterator<TreeNode> listIterator = parent.getChildren().listIterator();
        while (listIterator.hasNext()) {
            TreeNode element = listIterator.next();
            if (element.equals(currentNode) && listIterator.hasPrevious()) {
                final SlotView movedSlotView = (SlotView) currentNode.getData();
                final SlotView parentSlotView = (SlotView) parent.getData();
                listIterator.remove();
                final SlotView affectedNode = (SlotView) listIterator.previous().getData();
                affectedNode.setLast(movedSlotView.isLast());
                affectedNode.setFirst(false);
                movedSlotView.setLast(false);
                movedSlotView.setFirst(!listIterator.hasPrevious());
                listIterator.add(currentNode);
                slotPairEJB.moveUp(parentSlotView.getSlot(), movedSlotView.getSlot());
                selectNodeAfterMove(currentNode);
                break;
            }
        }
    }

    /** The action event to be called when the user presses the "move down" action icon. This action moves the current
     * container/installation slot down one space, if that is possible.
     * @param ev
     */
    public void moveDown(ActionEvent ev) {
        /* determined by the debugger:
         * action is invoked by the CommandLink
         * parent of CommandLink is Column
         * parent of Column is TreeTable
         */
        TreeNode currentNode = ((TreeTable)((CommandLink)ev.getSource()).getParent().getParent()).getRowNode();
        TreeNode parent = currentNode.getParent();

        ListIterator<TreeNode> listIterator = parent.getChildren().listIterator();
        while (listIterator.hasNext()) {
            TreeNode element = listIterator.next();
            if (element.equals(currentNode) && listIterator.hasNext()) {
                final SlotView movedSlotView = (SlotView) currentNode.getData();
                final SlotView parentSlotView = (SlotView) parent.getData();
                listIterator.remove();
                final SlotView affectedNode = (SlotView) listIterator.next().getData();
                affectedNode.setFirst(movedSlotView.isFirst());
                affectedNode.setLast(false);
                movedSlotView.setFirst(false);
                movedSlotView.setLast(!listIterator.hasNext());
                listIterator.add(currentNode);
                slotPairEJB.moveDown(parentSlotView.getSlot(), movedSlotView.getSlot());
                selectNodeAfterMove(currentNode);
                break;
            }
        }
    }

    private void selectNodeAfterMove(TreeNode movedNode) {
        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        movedNode.setSelected(true);
        selectedNode = movedNode;
    }

    protected abstract void updateRootNode();

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
