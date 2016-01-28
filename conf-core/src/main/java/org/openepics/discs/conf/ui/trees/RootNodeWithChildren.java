/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

/**
 * Some hierarchies need a root node with specially listed children.
 * Such are controls, powers and connects.
 *
 * The children are searched through "contains" hierarchy. Only the children acting in the new hierarchy are used.
 * If a root appears somewhere in the new tree as a subnode it is removed.
 *
 * @author ilist
 *
 */
public class RootNodeWithChildren extends FilteredTreeNode<SlotView> {
	/** Current state of this node. Initialized is with initHierarchy() call, and deinitialized by reset(). */
	private boolean initialized = false;

	/**
	 * Creates an uninitialized root node.
	 *
	 * @param data a fake slot view with the root
	 * @param tree the tree of underlying hierarchy
	 */
	public RootNodeWithChildren(SlotView data, Tree<SlotView> tree) {
		super(data, null, tree);
		bufferedAllChildren = new ArrayList<>();
	}

	/**
	 * Method returns list of children we determined during initHierarchy call.
	 * @return list of children
	 */
	@Override
	public List<? extends BasicTreeNode<SlotView>> getAllChildren() {
		return bufferedAllChildren;
	}

	/**
	 * Given a set of selected nodes in "contains" hierarchy this method:
	 * 1. searches which of their descendants are also in "our" hierarchy
	 * 2. adds them to the list
	 * 3. prunes the list so that those nodes don't appear as children in "our" hierarchy
	 *
	 * @param selectedNodes The nodes from "contains" hierarchy to be searched for in "our" hierarchy.
	 */
	public void initHierarchy(final List<FilteredTreeNode<SlotView>> selectedNodes) {
		if (initialized) return;

		bufferedAllChildren = new ArrayList<>();
        final List<Slot> childrenSlots = Lists.newArrayList();

        // 1. find root nodes for the selected sub-tree
        for (FilteredTreeNode<SlotView> selectedNode : selectedNodes) {
            findRelationRootsForSelectedNode(selectedNode, childrenSlots);
        }

        // 2. build the tree
        int order = 0;
        for (final Slot slot : childrenSlots) {
            final SlotView levelOneView = new SlotView(slot, getData(), ++order, getTree().slotEJB);
            levelOneView.setLevel(1);
            bufferedAllChildren.add(new FilteredTreeNode<SlotView>(levelOneView, this, getTree()));
        }

        // 3. prune the tree
        removeRedundantRoots();

        // 4. reset the state
        getTree().setSelectedNodesArray(new TreeNode[0]);
        cleanCache();
        initialized = true;
	}

	/** Resets the state of the tree. Initial state is: no children, no selected nodes. */
	public void reset() {
		initialized = false;
		bufferedAllChildren = new ArrayList<>();
		bufferedFilteredChildren = null;
		getTree().setSelectedNodesArray(new TreeNode[0]);
	}

	/**
	 * Traverses using containsNode parameter and collects candidate nodes into rootSlots list.
	 *
	 * @param containsNode a node from "contains" hierarchy to be traversed
	 * @param rootSlots a return list
	 */
    private void findRelationRootsForSelectedNode(final FilteredTreeNode<SlotView> containsNode,
                                                                                    final List<Slot> rootSlots) {
        final Slot nodeSlot = containsNode.getData().getSlot();

        // getTree().getAllChildren returns children in our hierarchy
        // this is the condition for the node to appear in the new tree
        if (getTree().getAllChildren(containsNode).size() > 0   // TREE could be optimized to hasChildren()
        		&& !rootSlots.contains(nodeSlot)) {
        	rootSlots.add(nodeSlot);
        }

        // traverse the rest of the nodes
        for (final FilteredTreeNode<SlotView> childNode : containsNode.getBufferedAllChildren()) {
            findRelationRootsForSelectedNode(childNode, rootSlots);
        }
    }

    /**
     * Removes redundant roots from the tree.
     *
     * A root is redundant when it already appears in the tree.
     */
    private void removeRedundantRoots() {
        // visit all subtrees
        final Set<Long> visited = new HashSet<>();
        for (FilteredTreeNode<SlotView> levelOne : bufferedAllChildren) {
        	if (!visited.contains(levelOne.getData().getId())) { // this prevents {a->b, b->a} to be both removed
        		for (FilteredTreeNode<SlotView> levelTwo : levelOne.getBufferedAllChildren()) {
        			depthFirstSearch(levelTwo, visited);
        		}
        	}
        }

        // remove them
        Iterator<? extends BasicTreeNode<SlotView>> i = bufferedAllChildren.iterator();
        while (i.hasNext()) {
            BasicTreeNode<SlotView> n = i.next();
            if (visited.contains(n.getData().getId())) {
                i.remove();
            }
        }
    }

    /**
     * Traverses starting in argument "node", marking visited nodes.
     * @param node currently inspected node
     * @param visited already visited nodes
     */
    private void depthFirstSearch(FilteredTreeNode<SlotView> node, Set<Long> visited) {
        final SlotView nodeSlotView = node.getData();

        if (visited.contains(nodeSlotView.getId())) return;
        visited.add(nodeSlotView.getId());

        for (FilteredTreeNode<SlotView> child : node.getBufferedAllChildren()) {
            depthFirstSearch(child, visited);
        }
    }

    /**
     * Normally this method would clean whole cache.
     * Here it's extended to prevent clearing of prepared list of roots.
     */
	@Override
	public void cleanCache() {
		bufferedFilteredChildren = null;
	}
}
