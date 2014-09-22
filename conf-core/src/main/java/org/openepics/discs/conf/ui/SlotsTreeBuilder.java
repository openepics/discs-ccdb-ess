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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
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
        return newSlotsTree(slots, 0, null, collapsedNodes, withInstallationSlots);
    }

    /**
     * Builds a tree of {@link SlotView}s from the provided lists of slots.
     *
     * @param slots the list of all slots
     * @param selectableLevel the depth level starting from 0 below which (inclusively) node selection is made possible
     * @param selected the {@link Slot} in the resulting tree that should be preselected and the part of the tree containing
     * @param collapsedNodes set of collapsed nodes
     * @return the root node of the tree
     */
    public TreeNode newSlotsTree(List<Slot> slots, int selectableLevel, Slot selected, Set<Long> collapsedNodes, boolean withInstallationSlots) {
        final Map<Long, Slot> completeSlotList = new HashMap<>();

        for (Slot slot : slots) {
            completeSlotList.put(slot.getId(), slot);
        }

        final SlotTree slotsTree = new SlotTree(selectableLevel, selected, collapsedNodes);
        for (Slot slot : completeSlotList.values()) {
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
                    final Slot parentSlot = parentSlotPair.getParentSlot();
                    if ((withInstallationSlots  || !withInstallationSlots && !parentSlot.getIsHostingSlot()) && parentSlotPair.getSlotRelation().getName().equals(SlotRelationName.CONTAINS)) {
                        addSlotNode(nprt, allSlots.get(parentSlot.getId()), allSlots, withInstallationSlots);
                        nprt.addChildToParent(parentSlot.getId(), slot);
                    }
                }
            }
        }
    }

    private class SlotTree {
        private class SlotTreeNode {
            private final Slot node;
            private final List<SlotTreeNode> children;

            private SlotTreeNode(Slot slot) {
                node = slot;
                children = new ArrayList<>();
            }

            private void add(SlotTreeNode child) {
                ListIterator<SlotTreeNode> li = children.listIterator();
                while (li.hasNext()) {
                    SlotTreeNode currentNode = li.next();
                    if (child.node.getId() < currentNode.node.getId()) {
                        li.previous(); // insert before the current element
                        break;
                    }
                }
                // append at the end of the list
                li.add(child);
            }
        }

        private final SlotTreeNode root;
        private final HashMap<Long, SlotTreeNode> inventory;
        private final int selectableLevel;
        private final Slot selectedSlot;
        private TreeNode selectedTreeNode;
        private Set<Long> collapsedNodes;

        private SlotTree(int selectableLevel, Slot selected, Set<Long> collapsedNodes) {
            root = new SlotTreeNode(null);
            inventory = new HashMap<>();
            this.selectableLevel = selectableLevel;
            this.selectedSlot = selected;
            this.collapsedNodes = collapsedNodes != null ? collapsedNodes : new HashSet<Long>();
        }

        private boolean hasNode(Slot slot) {
            return inventory.containsKey(slot.getId());
        }

        private void addChildToParent(@Nullable Long parentId, Slot slot) {
            final SlotTreeNode newNode = new SlotTreeNode(slot);
            if (parentId != null) {
                inventory.get(parentId).add(newNode);
            } else {
                root.add(newNode);
            }
            inventory.put(slot.getId(), newNode);
        }

        private TreeNode asViewTree() {
            TreeNode treeRoot = asViewTree(new DefaultTreeNode(null, null), root, 0);
            TreeNode treeNode = selectedTreeNode;
            if (treeNode != null && selectedSlot != null) {
                while(treeNode.getParent() != null) {
                    treeNode.setExpanded(!collapsedNodes.contains(((Slot)treeNode.getData()).getId()));
                    treeNode = treeNode.getParent();
                }
            } else if ((treeNode != null && selectedSlot == null) || (treeNode == null && selectedSlot != null)) {
                throw new IllegalStateException();
            }
            return treeRoot;
        }

        private TreeNode asViewTree(TreeNode parentNode, SlotTreeNode nprNode, int level) {
            for (SlotTreeNode child : nprNode.children) {
                final TreeNode node = new DefaultTreeNode(new SlotView(child.node, (SlotView)parentNode.getData(), child.node.getParentSlotsPairList()), parentNode);
                node.setExpanded(!collapsedNodes.contains(((SlotView) node.getData()).getId()));
                node.setSelectable(level >= selectableLevel);
                if (isSelected(node)) {
                    node.setSelected(true);
                    selectedTreeNode = node;
                }
                asViewTree(node, child, level+1);
            }
            return parentNode;
        }

        private boolean isSelected(TreeNode node) {
            return (selectedSlot != null) && (selectedSlot.equals(node.getData()));
        }
    }

}
