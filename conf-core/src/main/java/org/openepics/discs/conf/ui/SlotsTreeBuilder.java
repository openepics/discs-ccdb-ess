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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Tree builder for tree presentation of {@link Slot}s
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class SlotsTreeBuilder implements Serializable {
    private static final long serialVersionUID = -2249157032475929350L;

    @Inject transient protected InstallationEJB installationEJB;
    private TreeNode initiallySelectedTreeNode;

    /**
     * Builds a tree of {@link SlotView}s from the provided lists of slots.
     *
     * @param slots the list of all slots
     * @param collapsedNodes set of collapsed nodes
     * @param withInstallationSlots <code>true</code>: containers and installation slots in the tree,
     * <code>false</code> containers only
     * @return the root node of the tree
     */
    public TreeNode newSlotsTree(List<Slot> slots, Set<Long> collapsedNodes, boolean withInstallationSlots) {
        return newSlotsTree(slots, null, collapsedNodes, withInstallationSlots, null);
    }

    /**
     * Builds a tree of {@link SlotView}s from the provided lists of slots.
     *
     * @param slots the list of all slots
     * @param selected the {@link Slot} in the resulting tree that should be preselected.
     * @param collapsedNodes set of collapsed nodes
     * @param withInstallationSlots <code>true</code>: containers and installation slots in the tree, <code>false</code> containers only
     * @return the root node of the tree
     */
    public TreeNode newSlotsTree(List<Slot> slots, Slot selected, Set<Long> collapsedNodes, boolean withInstallationSlots) {
        return newSlotsTree(slots, selected, collapsedNodes, withInstallationSlots, null);
    }


    /**
     * Builds a tree of {@link SlotView}s from the provided lists of slots.
     *
     * @param slots the list of all slots
     * @param selected the {@link Slot} in the resulting tree that should be preselected
     * @param collapsedNodes set of collapsed nodes
     * @param withInstallationSlots <code>true</code>: containers and installation slots in the returned tree,
     *           <code>false</code>: containers only
     * @param installationSlotType <code>null</code> the tree contains all possible installation slots,
     *          <code>non-null</code> only the installation slots of the type are shown
     * @return the root node of the tree
     */
    public TreeNode newSlotsTree(List<Slot> slots, Slot selected, Set<Long> collapsedNodes,
                                    boolean withInstallationSlots, ComponentType installationSlotType) {
        Preconditions.checkArgument(withInstallationSlots || (!withInstallationSlots && installationSlotType == null),
                "Installation slots not included in the resulting tree, but installation slot type selected.");

        final String requestedComponentTypeName = installationSlotType == null ? "" : installationSlotType.getName();

        // the filteredList can only contain the slots that _MUST_ be in the final tree. The 'addSlotNode' method
        // will add the parents directly from the database, which means that the final tree will also contain all
        // the required slots, even if they are not part of the original filteredList
        final List<Slot> filteredList = filterSlots(slots, requestedComponentTypeName, withInstallationSlots);

        final SlotTree slotsTree = new SlotTree(getRootNode(filteredList), selected, collapsedNodes);
        // use the sorted list to build the tree. This guarantees that the layout of the tree is always the same.
        for (Slot slot : filteredList) {
            addSlotNode(slotsTree, slot, installationSlotType);
        }

        initiallySelectedTreeNode = slotsTree.getSelectedTreeNode();

        return slotsTree.asViewTree();
    }

    private void addSlotNode(SlotTree slotTree, Slot slot, ComponentType installationSlotType) {
        if (!slotTree.hasNode(slot)) {
            if (slot.getPairsInWhichThisSlotIsAChildList().isEmpty()) {
                throw new IllegalStateException("Illegal state in the database. "
                        + "Please contact the administrator.\nDetails: Slot " + slot.getName() + " is orphaned "
                                + "(not a child, not involved in any parent-child relationships)");
            }
            final List<SlotPair> parentSlotPairs = slot.getPairsInWhichThisSlotIsAChildList();
            for (SlotPair parentSlotPair : parentSlotPairs) {
                if (parentSlotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                    final Slot parentSlot = parentSlotPair.getParentSlot();
                    // first recursively add parents
                    addSlotNode(slotTree, parentSlot, installationSlotType);
                    final Device installedDevice = getInstalledDeviceForSlot(slot);
                    // then add the child you're working on at the moment
                    slotTree.addChildToParent(parentSlot.getId(), slot, installedDevice,
                            isNodeSelectable(slot, installationSlotType, installedDevice),
                            parentSlotPair.getSlotOrder());
                }
            }
        }
    }

    private static class SlotTree {
        /**
         * A helper class for building the tree. This helps by keeping the SlotTree API to minimum.
         */
        private static class TreeNodeBuilder {
            private final TreeNode root;
            /**
            * Contains all the nodes which have already been added to the tree. This helps with building the tree,
            * since when adding a new child to the parent, you do need to traverse the whole tree to find it.
            */
            private final Map<Long, List<TreeNode>> cache;
            private final Slot selectedSlot;
            private TreeNode selectedTreeNode;
            private Set<Long> collapsedNodes;

            private TreeNodeBuilder(Slot rootSlot, Slot selected, Set<Long> collapsedNodes) {
                root = new DefaultTreeNode(new SlotView(rootSlot, null, new ArrayList<SlotPair>(), null, 1), null);
                cache = new HashMap<>();
                addToCache(root);
                this.selectedSlot = selected;
                this.collapsedNodes = collapsedNodes != null ? collapsedNodes : new HashSet<Long>();
            }

            private void addNewNodeToParent(Long parentId, Slot slot, Device installedDevice,
                    boolean selectable, int order) {
                for (TreeNode parentNode : cache.get(parentId)) {
                    addNewNode(slot, (SlotView)parentNode.getData(), parentNode, installedDevice, selectable, order);
                }
            }

            private void addNewNode(Slot slot, SlotView parent, TreeNode parentNode, Device installedDevice,
                    boolean selectable, int order) {
                final TreeNode newNode = new DefaultTreeNode(
                        new SlotView(slot, parent, slot.getPairsInWhichThisSlotIsAParentList(), installedDevice, order));

                orderedInsert(parentNode, newNode);
                addToCache(newNode);
                newNode.setExpanded(!collapsedNodes.contains(slot.getId()));
                newNode.setSelectable(selectable);
                if (isSelected(newNode)) {
                    newNode.setSelected(true);
                    selectedTreeNode = newNode;
                }
            }

            private void addToCache(TreeNode newNode) {
                final SlotView slotView = (SlotView) newNode.getData();
                final Long slotId = slotView.getId();
                final List<TreeNode> treeNodesWithSameSlot;
                if (cache.containsKey(slotId)) {
                    treeNodesWithSameSlot = cache.get(slotId);
                } else {
                    treeNodesWithSameSlot = new ArrayList<>();
                    cache.put(slotId, treeNodesWithSameSlot);
                }
                treeNodesWithSameSlot.add(newNode);
            }

            private void orderedInsert(TreeNode parent, TreeNode child) {
                final SlotView childSlotView = (SlotView) child.getData();

                if (parent.getChildCount() == 0) {
                    childSlotView.setFirst(true);
                    childSlotView.setLast(true);
                    parent.getChildren().add(child);
                    return;
                }

                final int childOrder = childSlotView.getOrder();

                ListIterator<TreeNode> siblings = parent.getChildren().listIterator();
                while (siblings.hasNext()) {
                    SlotView sibling = (SlotView) siblings.next().getData();
                    final int siblingOrder = sibling.getOrder();
                    if (childOrder < siblingOrder) {
                        // we have to insert before the element we just processed
                        // and the processed element is definitely no longer the first
                        sibling.setFirst(false);
                        siblings.previous();
                        break;
                    }
                    // the new element will be added after this one, so this one is definitely not the last
                    sibling.setLast(false);
                }
                childSlotView.setFirst(!siblings.hasPrevious());
                childSlotView.setLast(!siblings.hasNext());
                siblings.add(child);
            }

            private boolean isSelected(TreeNode node) {
                // if there is a slot defined for selection, and there is no tree node already selected and if the
                // current slot matches
                return (selectedSlot != null) && (selectedTreeNode == null)
                        && (selectedSlot.equals( ((SlotView) node.getData()).getSlot() ));
            }

            private TreeNode getSelectedTreeNode() {
                return selectedTreeNode;
            }
        }

        private final TreeNodeBuilder treeBuilder;

        private SlotTree(Slot rootSlot, Slot selected, Set<Long> collapsedNodes) {
            treeBuilder = new TreeNodeBuilder(rootSlot, selected, collapsedNodes);
        }

        private boolean hasNode(Slot slot) {
            return treeBuilder.cache.containsKey(slot.getId());
        }

        private void addChildToParent(Long parentId, Slot slot, Device installedDevice, boolean selectable,
                int order) {
            treeBuilder.addNewNodeToParent(parentId, slot, installedDevice, selectable, order);
        }

        private TreeNode asViewTree() {
            return treeBuilder.root;
        }

        private TreeNode getSelectedTreeNode() {
            return treeBuilder.getSelectedTreeNode();
        }
    }

    private Slot getRootNode(List<Slot> slots) {
        for (Slot slot : slots) {
            if (slot.getComponentType().getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                return slot;
            }
        }
        throw new RuntimeException("_ROOT node does not exist in the database.");
    }

    /** This method prepares the list of slots that will be used for building a tree. Only the slots in the returned
     * list will be shown in the tree (plus any parents that are required, even if they are not part of the
     * filtered list).
     * @param allSlotList the list of all slots in the database.
     * @param requestedComponentTypeName the component (device) type to filter the allSlotList by.
     * @param withInstallationSlots <code>true</code> if the installation slots should be included in the built tree,
     *              <code>false</code> otherwise.
     * @return The filtered list to build the tree out of.
     */
    protected List<Slot> filterSlots(final List<Slot> allSlotList, final String requestedComponentTypeName,
            boolean withInstallationSlots) {
        // this is the default implementation for containers only. The 'requestedComponentTypeName' is not needed.
        Builder<Slot> listBuilder = ImmutableList.builder();

        for (Slot slot : allSlotList) {
            if (withInstallationSlots || !slot.isHostingSlot()) {
                listBuilder.add(slot);
            }
        }

        return listBuilder.build();
    }

    /** This method is only called with the appropriate type of installation slot or a container which can never have
     * a device installed.
     * @param slot the slot to inspect.
     * @return The device that is installed in the current slot. <code>null</code> means that the installation slot
     * is empty.
     */
    protected Device getInstalledDeviceForSlot(final Slot slot) {
        // in the basic container/slot tree we are not interested in the installed devices
        return null;
    }

    /** This method determines whether the node is selectable based on the slot the node is for, the device type the
     * user has requested and device that is installed in this slot.
     * @param slot the slot this node is for.
     * @param installationSlotType the device type the user requested.
     * @param installedDevice the device that is installed in this slot.
     * @return <code>true</code> if the node is selectable, <code>false</code> otherwise.
     */
    protected boolean isNodeSelectable(Slot slot, ComponentType installationSlotType, Device installedDevice) {
        return true;
    }

    /**
     * @return If there was a {@link Slot} preselected when the tree was built, this method returns a {@link SlotView}
     * containing this {@link Slot}, <code>null</code> otherwise.
     */
    public SlotView getInitiallySelectedSlotView() {
        if (initiallySelectedTreeNode == null) {
            return null;
        }
        return (SlotView) initiallySelectedTreeNode.getData();
    }

    public TreeNode getInitiallySelectedTreeNode() {
        return initiallySelectedTreeNode;
    }
}
