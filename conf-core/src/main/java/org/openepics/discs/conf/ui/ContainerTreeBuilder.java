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
import org.openepics.discs.conf.ui.common.AlphanumComparator;
import org.openepics.discs.conf.views.ContainerView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

/**
 * Tree builder for tree presentation of {@link Slot}s
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class ContainerTreeBuilder implements Serializable {

    /**
     * Builds a tree of {@link ContainerView}s from the provided lists of containers.
     *
     * @param containers the list of all containers
     * @param collapsedNodes set of collapsed nodes
     * @return the root node of the tree
     */
    public TreeNode newContainerTree(List<Slot> containers, Set<Long> collapsedNodes) {
        return newContainerTree(containers, 0, null, collapsedNodes);
    }

    /**
     * Builds a tree of {@link ContainerView}s from the provided lists of containers.
     *
     * @param containers the list of all containers
     * @param selectableLevel the depth level starting from 0 below which (inclusively) node selection is made possible
     * @param selected the {@link Slot} in the resulting tree that should be preselected and the part of the tree containing
     * @param collapsedNodes set of collapsed nodes
     * expanded
     * @return the root node of the tree
     */
    public TreeNode newContainerTree(List<Slot> containers, int selectableLevel, Slot selected, Set<Long> collapsedNodes) {
        final Map<Long, Slot> completeContainerList = new HashMap<>();

        for (Slot slot : containers) {
            completeContainerList.put(slot.getId(), slot);
        }

        final ContainerTree containerTree = new ContainerTree(selectableLevel, selected, collapsedNodes);
        for (Slot slot : completeContainerList.values()) {
            addContainerNode(containerTree, slot, completeContainerList);
        }

        return containerTree.asViewTree();

    }

    private void addContainerNode(ContainerTree nprt, Slot slot, Map<Long, Slot> allSlots) {
        if (!nprt.hasNode(slot)) {
            final List<SlotPair> parentSlotPairs = slot.getChildrenSlotsPairList().size() > 0 ? slot.getChildrenSlotsPairList() : null;
            if (parentSlotPairs == null) {
                nprt.addChildToParent(null, slot);
            } else {
                for (SlotPair parentSlotPair : parentSlotPairs) {
                    final Slot parentSlot = parentSlotPair.getParentSlot();
                    if (!parentSlot.getIsHostingSlot()) {
                        addContainerNode(nprt, allSlots.get(parentSlot.getId()), allSlots);
                        nprt.addChildToParent(parentSlot.getId(), slot);
                    }
                }
            }
        }
    }

    private class ContainerTree {
        private class ContainerTreeNode {
            private final Slot node;
            private final List<ContainerTreeNode> children;

            private ContainerTreeNode(Slot slot) {
                node = slot;
                children = new ArrayList<>();
            }
        }

        private final ContainerTreeNode root;
        private final HashMap<Long, ContainerTreeNode> inventory;
        private final int selectableLevel;
        private final Slot selectedSlot;
        private TreeNode selectedTreeNode;
        private Set<Long> collapsedNodes;

        private ContainerTree(int selectableLevel, Slot selected, Set<Long> collapsedNodes) {
            root = new ContainerTreeNode(null);
            inventory = new HashMap<>();
            this.selectableLevel = selectableLevel;
            this.selectedSlot = selected;
            this.collapsedNodes = collapsedNodes != null ? collapsedNodes : new HashSet<Long>();
        }

        private boolean hasNode(Slot slot) {
            try {
                return inventory.containsKey(slot.getId());
            } catch (NullPointerException e) {
                throw e;
            }
        }

        private void addChildToParent(@Nullable Long parentId, Slot slot) {
            final ContainerTreeNode newNode = new ContainerTreeNode(slot);
            if (parentId != null) {
                inventory.get(parentId).children.add(newNode);
            } else {
                root.children.add(newNode);
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

        private TreeNode asViewTree(TreeNode parentNode, ContainerTreeNode nprNode, int level) {
            final List<TreeNode> children = Lists.newArrayList();
            for (ContainerTreeNode child : nprNode.children) {
                final TreeNode node = new DefaultTreeNode(new ContainerView(child.node, (ContainerView)parentNode.getData(), child.node.getParentSlotsPairList().size() > 0), parentNode);
                node.setExpanded(!collapsedNodes.contains(((ContainerView) node.getData()).getId()));
                node.setSelectable(level >= selectableLevel);
                if (isSelected(node)) {
                    node.setSelected(true);
                    selectedTreeNode = node;
                }
                asViewTree(node, child, level+1);
                children.add(node);
            }
            Collections.sort(children, new Comparator<TreeNode>() {
                @Override public int compare(TreeNode left, TreeNode right) {
                    final ContainerView leftView = (ContainerView) left.getData();
                    final ContainerView rightView = (ContainerView) right.getData();
                    final AlphanumComparator alphanumComparator = new AlphanumComparator();
                    return alphanumComparator.compare(leftView.getName(), rightView.getName());
                }
            });
            for (TreeNode child : children) {
                parentNode.getChildren().add(child);
                child.setParent(parentNode);
            }
            return parentNode;
        }

        private boolean isSelected(TreeNode node) {
            return (selectedSlot != null) && (selectedSlot.equals(node.getData()));
        }
    }

}
