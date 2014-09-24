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
import javax.inject.Named;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Tree builder for tree presentation of {@link Slot}s
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class SlotsTreeBuilder implements Serializable {

    /**
     * Builds a tree of {@link SlotView}s from the provided lists of slots.
     *
     * @param slots the list of all slots
     * @param collapsedNodes set of collapsed nodes
     * @return the root node of the tree
     */
    public TreeNode newSlotsTree(List<Slot> slots, Set<Long> collapsedNodes, boolean withInstallationSlots) {
        return newSlotsTree(slots, null, collapsedNodes, withInstallationSlots);
    }

    /**
     * Builds a tree of {@link SlotView}s from the provided lists of slots.
     *
     * @param slots the list of all slots
     * @param selected the {@link Slot} in the resulting tree that should be preselected and the part of the tree containing
     * @param collapsedNodes set of collapsed nodes
     * @return the root node of the tree
     */
    public TreeNode newSlotsTree(List<Slot> slots, Slot selected, Set<Long> collapsedNodes, boolean withInstallationSlots) {
        Collections.sort(slots, new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                // TODO introduce additional sort criteria (user specified) later
                final long diff = o1.getId() - o2.getId();
                return (diff < 0) ? -1 : (diff > 0) ? 1 : 0; // avoid long to int conversion loss
            }
        });

        final Map<Long, Slot> completeSlotList = new HashMap<>();

        for (Slot slot : slots) {
            completeSlotList.put(slot.getId(), slot);
        }

        final SlotTree slotsTree = new SlotTree(selected, collapsedNodes);
        /*
         * use the slots list to build the tree because it is sorted. This guarantees that the layout of the tree is
         * always the same.
         */
        for (Slot slot : slots) {
            addSlotNode(slotsTree, slot, completeSlotList, withInstallationSlots);
        }

        return slotsTree.asViewTree();
    }

    private void addSlotNode(SlotTree nprt, Slot slot, Map<Long, Slot> allSlots, boolean withInstallationSlots) {
        if (!nprt.hasNode(slot)) {
            final List<SlotPair> parentSlotPairs = slot.getChildrenSlotsPairList().size() > 0 ? slot.getChildrenSlotsPairList() : null;
            if (parentSlotPairs == null) {
                nprt.addChildToParent(null, slot);
            } else {
                for (SlotPair parentSlotPair : parentSlotPairs) {
                    if (parentSlotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                        final Slot parentSlot = parentSlotPair.getParentSlot();
                        addSlotNode(nprt, allSlots.get(parentSlot.getId()), allSlots, withInstallationSlots); // first recursively add parents
                        if ((withInstallationSlots || !withInstallationSlots && !slot.getIsHostingSlot())) {
                            nprt.addChildToParent(parentSlot.getId(), slot); // then add the child you're working on at the moment
                        }
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

            private void addNewNodeToParent(@Nullable Long parentId, Slot slot) {
                if (parentId != null) {
                    for (TreeNode parentNode : cache.get(parentId)) {
                        addNewNode(slot, (SlotView)parentNode.getData(), parentNode);
                    }
                } else {
                    // this is one of the hierarchy roots
                    addNewNode(slot, null, root);
                }
            }

            private void addNewNode(Slot slot, SlotView parent, TreeNode parentNode) {
                final TreeNode newNode = new DefaultTreeNode(new SlotView(slot, parent, slot.getParentSlotsPairList()), parentNode);
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

        private void addChildToParent(@Nullable Long parentId, Slot slot) {
            treeBuilder.addNewNodeToParent(parentId, slot);
        }

        private TreeNode asViewTree() {
            return treeBuilder.root;
        }
    }
}
