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

import java.util.List;

import org.openepics.discs.conf.ejb.ConnectsEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class ConnectsHierarchyBuilder {

    ConnectsEJB connectsEJB;

    public ConnectsHierarchyBuilder(ConnectsEJB connectsEJB) {
        this.connectsEJB = connectsEJB;
    }

    public void expandNode(TreeNode expandedNode) {
    }

    public void initHierarchy(List<TreeNode> selectedNodes, TreeNode rootNode) {
        List<TreeNode> children = rootNode.getChildren();
        children.clear();

        if (selectedNodes == null) {
            return;
        }

        final List<Slot> levelOneSlots;

        // find root nodes for the selected sub-tree
        levelOneSlots = Lists.newArrayList();

        for (TreeNode selectedNode : selectedNodes)
            findRelationRootsForSelectedNode(selectedNode, levelOneSlots);

        for (Slot node : levelOneSlots) {
            SlotView nodeView = new SlotView(node, (SlotView)rootNode.getData(), 1, null);
            TreeNode c = new DefaultTreeNode(nodeView, rootNode);
            List<Slot> slots = connectsEJB.getSlotConnects(node);
            for (Slot slot : slots) {
                new DefaultTreeNode(new SlotView(slot, nodeView, 1, null), c);
            }
        }
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
