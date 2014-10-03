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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;

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

    @Inject protected InstallationEJB installationEJB;

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

        final List<Slot> filteredList = filterSlotsAndPrepareCache(slots, requestedComponentTypeName,
                withInstallationSlots);

        Collections.sort(filteredList, new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                // TODO introduce additional sort criteria (user specified) later
                final long diff = o1.getId() - o2.getId();
                return (diff < 0) ? -1 : (diff > 0) ? 1 : 0; // avoid long to int conversion loss
            }
        });

        final SlotTree slotsTree = new SlotTree(selected, collapsedNodes);
        /*
         * use the slots list to build the tree because it is sorted. This guarantees that the layout of the tree is
         * always the same.
         */
        for (Slot slot : filteredList) {
            addSlotNode(slotsTree, slot, installationSlotType);
        }

        return slotsTree.asViewTree();
    }

    private void addSlotNode(SlotTree nprt, Slot slot, ComponentType installationSlotType) {
        if (!nprt.hasNode(slot)) {
            final List<SlotPair> parentSlotPairs = slot.getChildrenSlotsPairList().size() > 0
                    ? slot.getChildrenSlotsPairList() : null;
            if (parentSlotPairs == null) {
                nprt.addChildToParent(null, slot, null, isRootNodeSelectable());
            } else {
                for (SlotPair parentSlotPair : parentSlotPairs) {
                    if (parentSlotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                        final Slot parentSlot = parentSlotPair.getParentSlot();
                        // first recursively add parents
                        addSlotNode(nprt, parentSlot, installationSlotType);
                        final Device installedDevice = getInstalledDeviceForSlot(slot, installationSlotType);
                        // then add the child you're working on at the moment
                        nprt.addChildToParent(parentSlot.getId(), slot, installedDevice,
                                isNodeSelectable(slot, installationSlotType, installedDevice));
                    }
                }
            }
        }
    }

    private class SlotTree {
        /**
         * A helper class for building the tree. This helps by keeping the SlotTree API to minimum.
         */
        private class TreeNodeBuilder {
            private final TreeNode root;
            /**
            * Contains all the nodes which have already been added to the tree. This helps with building the tree,
            * since when adding a new child to the parent, you do need to traverse the whole tree to find it.
            */
            private final Map<Long, List<TreeNode>> cache;
            private final Slot selectedSlot;
            private TreeNode selectedTreeNode;
            private Set<Long> collapsedNodes;

            private TreeNodeBuilder(Slot selected, Set<Long> collapsedNodes) {
                root = new DefaultTreeNode(null, null);
                cache = new HashMap<>();
                this.selectedSlot = selected;
                this.collapsedNodes = collapsedNodes != null ? collapsedNodes : new HashSet<Long>();
            }

            private void addNewNodeToParent(@Nullable Long parentId, Slot slot, Device installedDevice,
                    boolean selectable) {
                if (parentId != null) {
                    for (TreeNode parentNode : cache.get(parentId)) {
                        addNewNode(slot, (SlotView)parentNode.getData(), parentNode, installedDevice, selectable);
                    }
                } else {
                    // this is one of the hierarchy roots
                    addNewNode(slot, null, root, installedDevice, selectable);
                }
            }

            private void addNewNode(Slot slot, SlotView parent, TreeNode parentNode, Device installedDevice,
                    boolean selectable) {
                final TreeNode newNode = new DefaultTreeNode(
                        new SlotView(slot, parent, slot.getParentSlotsPairList(), installedDevice), parentNode);
                final List<TreeNode> treeNodesWithSameSlot;
                final Long slotId = slot.getId();
                if (cache.containsKey(slotId)) {
                    treeNodesWithSameSlot = cache.get(slotId);
                } else {
                    treeNodesWithSameSlot = new ArrayList<>();
                    cache.put(slotId, treeNodesWithSameSlot);
                }
                treeNodesWithSameSlot.add(newNode);

                newNode.setExpanded(!collapsedNodes.contains(slotId));
                newNode.setSelectable(selectable);
                if (isSelected(newNode)) {
                    newNode.setSelected(true);
                    selectedTreeNode = newNode;
                }
            }

            private boolean isSelected(TreeNode node) {
                return (selectedSlot != null) && (selectedTreeNode != null) && (selectedSlot.equals(node.getData()));
            }
        }

        private final TreeNodeBuilder treeBuilder;

        private SlotTree(Slot selected, Set<Long> collapsedNodes) {
            treeBuilder = new TreeNodeBuilder(selected, collapsedNodes);
        }

        private boolean hasNode(Slot slot) {
            return treeBuilder.cache.containsKey(slot.getId());
        }

        private void addChildToParent(@Nullable Long parentId, Slot slot, Device installedDevice, boolean selectable) {
            treeBuilder.addNewNodeToParent(parentId, slot, installedDevice, selectable);
        }

        private TreeNode asViewTree() {
            return treeBuilder.root;
        }
    }

    /** This method populates the all slots cache and prepares the list of slots that will be used for building a tree.
     * Only the slots in the returned list will be shown in the tree (plus any parents that are required, even if they
     * are not part of the filtered list).
     * @param allSlotList the list of all slots in the database.
     * @param requestedComponentTypeName the component (device) type to filter the allSlotList by.
     * @param withInstallationSlots <code>true</code> if the installation slots should be included in the built tree,
     *              <code>false</code> otherwise.
     * @return The filtered list to build the tree out of.
     */
    protected List<Slot> filterSlotsAndPrepareCache(final List<Slot> allSlotList,
            final String requestedComponentTypeName, boolean withInstallationSlots) {
        List<Slot> filteredList = new ArrayList<>(allSlotList.size());

        for (Slot slot : allSlotList) {
            if (withInstallationSlots || !slot.isHostingSlot()) {
                filteredList.add(slot);
            }
        }

        return filteredList;
    }

    /**
     * @param slot the slot to inspect.
     * @param installationSlotType the type of the devices the user is interested in.
     * @return The device that is installed in the current slot.
     */
    protected Device getInstalledDeviceForSlot(final Slot slot, final ComponentType installationSlotType) {
        // in the basic container/slot tree we are not interested in the installed devices
        return null;
    }

    /**
     * @return <code>true</code> if the root node is selectable, <code>false</code> otherwise.
     */
    protected boolean isRootNodeSelectable() {
        return true;
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
}
