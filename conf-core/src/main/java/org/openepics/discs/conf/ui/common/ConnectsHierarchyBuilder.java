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
        for (TreeNode node : selectedNodes) {
            TreeNode c = new DefaultTreeNode(node.getData(), rootNode);
            List<Slot> slots = connectsEJB.getSlotConnects(((SlotView)node.getData()).getSlot());
            for (Slot slot : slots) {
                new DefaultTreeNode(new SlotView(slot, (SlotView)node.getData(), 1, null), c);
            }
        }
    }
}
