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

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.ejb.ConnectsEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class ConnectsHierarchyBuilder extends HierarchyBuilder {

    ConnectsEJB connectsEJB;
    SlotEJB slotEJB;

    public ConnectsHierarchyBuilder(ConnectsEJB connectsEJB, SlotEJB slotEJB) {
        this.connectsEJB = connectsEJB;
        this.slotEJB = slotEJB;
    }

    @Override
    public void expandNode(TreeNode node) {
        final SlotView slotView = (SlotView) node.getData();
        if (!slotView.isInitialzed()) {
            List<Long> parentIds = new ArrayList<Long>();
            TreeNode parent = node;
            while (parent != null) {
                parentIds.add(((SlotView)parent.getData()).getId());
                parent = parent.getParent();
            }

            rebuildSubTree(node, parentIds, false);
        }
    }

    public void rebuildSubTree(final TreeNode node, List<Long> parentIds, boolean onlyHasChildren) {
        Preconditions.checkNotNull(node);
        // 1. Remove all existing children
        node.getChildren().clear();
        final SlotView parentSlotView = (SlotView) node.getData();
        final Slot parentSlot = parentSlotView.getSlot();

        parentIds.add(parentSlotView.getId());

        List<Slot> slots = connectsEJB.getSlotConnects(parentSlot);
        int order = 1;
        for (Slot child : slots) {
            if (parentIds.contains(child.getId())) continue;
            if (onlyHasChildren) {
                new DefaultTreeNode(null, node);
                return;
            }
          //  new DefaultTreeNode(new SlotView(slot, nodeView, 1, null), c);
            final SlotView childSlotView = new SlotView(child, parentSlotView, order++, slotEJB);
            childSlotView.setLevel(parentSlotView.getLevel() + 1);
            childSlotView.setInitialzed(false);
            childSlotView.setDeletable(true);
            final TreeNode addedTreeNode = new DefaultTreeNode(childSlotView, node);
            addedTreeNode.setExpanded(false);


            rebuildSubTree(addedTreeNode, parentIds, true);
        }
        parentSlotView.setInitialzed(true);
    }

    public List<TreeNode> initHierarchy(List<TreeNode> selectedNodes, TreeNode root) {
        final SlotView rootSlotView = (SlotView) root.getData();
        if (rootSlotView.isInitialzed()) return new ArrayList<>(root.getChildren());

        root.getChildren().clear();
        rootSlotView.setLevel(0);

        if (selectedNodes == null) {
            return new ArrayList<>(0);
        }

        final List<Slot> levelOneSlots;

        // find root nodes for the selected sub-tree
        levelOneSlots = Lists.newArrayList();

        for (TreeNode selectedNode : selectedNodes)
            findRelationRootsForSelectedNode(selectedNode, levelOneSlots);

        for (Slot node : levelOneSlots) {
            SlotView nodeView = new SlotView(node, rootSlotView, 1, slotEJB);
            nodeView.setLevel(1);
            TreeNode c = new DefaultTreeNode(nodeView, root);
            expandNode(c);
        }

        removeRedundantRoots(root);

        rootSlotView.setInitialzed(true);

        return new ArrayList<>(root.getChildren());
    }


    private void findRelationRootsForSelectedNode(final TreeNode node, final List<Slot> rootSlots) {
        final SlotView nodeSlotView = (SlotView) node.getData();
        final Slot nodeSlot = nodeSlotView.getSlot();

        List<Slot> slots = connectsEJB.getSlotConnects(nodeSlot);
        if (slots.size() > 0 && !rootSlots.contains(nodeSlot))
            rootSlots.add(nodeSlot);

        // this node is not a root
        for (final TreeNode childNode : node.getChildren()) {
            findRelationRootsForSelectedNode(childNode, rootSlots);
        }
    }


}
