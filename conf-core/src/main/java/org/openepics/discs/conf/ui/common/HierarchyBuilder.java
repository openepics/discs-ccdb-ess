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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

public abstract class HierarchyBuilder {

    /** This method is called when the user expands the tree node in the UI. If the tree node is still not initialized
     * (its subtree only contains the stub to show expansion mark), then the subtree is initialized, and the node is
     * also marked initialized.
     * @param node the {@link TreeNode} to add children to.
     */
    public abstract void expandNode(TreeNode node);

    public void removeRedundantRoots(TreeNode root) {
        final Set<Long> levelOneIds = new HashSet<>();
        for (TreeNode node : root.getChildren()) {
            levelOneIds.add(((SlotView)node.getData()).getId());
        }

        // find redundant roots
        final Set<Long> visited = new HashSet<>();
        for (TreeNode levelOne : root.getChildren()) {
            removeRedundantRoots(levelOne, levelOneIds, visited);
        }

        // remove them
        Iterator<TreeNode> i = root.getChildren().iterator();
        while (i.hasNext()) {
            TreeNode n = i.next();
            if (!levelOneIds.contains(((SlotView)n.getData()).getId())) {
                i.remove();
            }
        }
    }

    private void removeRedundantRoots(TreeNode node, Set<Long> levelOne, Set<Long> visited) {
        final SlotView nodeSlotView = (SlotView) node.getData();
        expandNode(node);

        if (visited.contains(nodeSlotView.getId())) return;
        visited.add(nodeSlotView.getId());

        if (nodeSlotView.getLevel() > 1) {
            if (levelOne.contains(nodeSlotView.getId())) {
                levelOne.remove(nodeSlotView.getId());
                // after removal, we still need to visit the subtree of this node
            }
        }
        for (TreeNode child : node.getChildren()) {
            removeRedundantRoots(child, levelOne, visited);
        }
    }


}
