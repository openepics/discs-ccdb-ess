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
package org.openepics.discs.ccdb.gui.ui.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openepics.discs.ccdb.core.ejb.SlotEJB;
import org.openepics.discs.ccdb.gui.views.SlotView;
import org.primefaces.model.TreeNode;

/**
 * Most importantly Tree class has an "extrinsic" method to find tree node's children.
 * Theoretically this could be inside tree's root node, but then we couldn't use RootNodeWithChildren for all: powers, controls and connects trees.
 *
 * Additionally Tree class contains global info about the tree: current selection of the nodes, current filter.
 *
 * @author ilist
 *
 * @param <D> the class of the elements used to build a tree of
 */
public abstract class Tree<D> {
    private static final Logger LOGGER = Logger.getLogger(Tree.class.getCanonicalName());
	private List<FilteredTreeNode<D>> selectedNodes = new ArrayList<>();
	private String filter = "";
	private String appliedFilter = "";
	final protected SlotEJB slotEJB;
	private FilteredTreeNode<D> rootNode;

	/**
	 * Constructs the tree.
	 * Tree knows how to generate new descendant nodes, so it needs slotEJB.
	 *
	 * @param slotEJB slotEJB
	 */
	public Tree(SlotEJB slotEJB) {
		this.slotEJB = slotEJB;
	}

	/**
	 * The master method to create/get children of the tree.
	 *
	 * @param parent the parent node
	 * @return children
	 */
	public abstract List<? extends BasicTreeNode<D>> getAllChildren(BasicTreeNode<D> parent);

	/**
	 * The root node of this tree.
	 *
	 * @param rootNode root node
	 */
	public void setRootNode(FilteredTreeNode<D> rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * Returns the root node.
	 *
	 * @return the root node
	 */
	public FilteredTreeNode<D> getRootNode() {
		return rootNode;
	}

	/**
	 * Returns currently selected nodes.
	 * @return selected nodes
	 */
	public List<FilteredTreeNode<D>> getSelectedNodes()	{
		return selectedNodes;
	}

	/**
	 * Returns currently selected nodes as an array. Used for primefaces.
	 * @return array of selected nodes
	 */
	public TreeNode[] getSelectedNodesArray() {
		return selectedNodes.toArray(new TreeNode[selectedNodes.size()]);
	}

	/**
	 * Sets selected nodes from an array. Used for primefaces.
	 * Unchecked typecast is needed.
	 *
	 * @param selectedNodes array of selected nodes
	 */
	@SuppressWarnings("unchecked")
	public void setSelectedNodesArray(TreeNode[] selectedNodes)	{
		this.selectedNodes.clear();
		if (selectedNodes != null) {
			for (TreeNode node : selectedNodes) {
				if (node != null) {
					this.selectedNodes.add((FilteredTreeNode<D>)node);
				}
			}
		}
		print(0, (FilteredTreeNode<SlotView>)getRootNode());
	}

	/** Clears the selection globally. */
    public void unselectAllTreeNodes() {
    	for (final TreeNode node : selectedNodes) {
    		node.setSelected(false);
        }
        selectedNodes.clear();
    }

	/**
	 * Gets current filter field. Which might not be applied. Used for textboxes.
	 * @return filter text
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Sets current filter field but it doesn't apply it. Used for textboxes.
	 * @param filter filter text
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Returns currently applied filter.
	 * @return the applied filter
	 */
	public String getAppliedFilter() {
		return appliedFilter;
	}

	/** Applies the filter. AppliedFilter filed becomes filter field. */
	public void applyFilter() {
		appliedFilter = filter == null ? "" : filter.toUpperCase();
		getRootNode().cleanFilterCache();
		unselectAllTreeNodes();
		// TODO old selection could be kept by fixing getSelectedNodesArray not to return filtered nodes
	}

	/**
	 * Returns whether the node should be displayed when filtering or not.
	 * Implementation depends on the data in the tree.
	 *
	 * @param node the node
	 * @return should the node be displayed
	 */
    public abstract boolean isNodeInFilter(BasicTreeNode<D> node);

    /**
     * Refreshes nodes which contain slot with the given IDs.
     *
     * @param ids set of IDs
     */
    public void refreshIds(HashSet<Long> ids) {
    	refreshIds(getRootNode(), ids);
    }

    /**
     * Traverses the tree and refreshes given IDs.
     * TODO possible optimizations:
     *   1. keep a map of lists of tree nodes id -> list treenode
     *   2. getBufferedAllChildren also buffers / better if just skip unbuffered nodes
     *
     * @param node currently visited node
     * @param ids set of IDs
     */
    private void refreshIds(FilteredTreeNode<D> node, HashSet<Long> ids) {
	   if (ids.contains(((SlotView)node.getData()).getId())) {
		   node.refreshCache();
	   } else {
		   for (final FilteredTreeNode<D> child : node.getBufferedAllChildren()) {
			   refreshIds(child, ids);
		   }
	   }
    }

    /**
     * Prints the tree with level numbers. Used for the debugging purposes.
     * @param i the starting level
     * @param rootNode the starting node
     */
	private static void print(int i, FilteredTreeNode<SlotView> rootNode) {
		LOGGER.log(Level.FINEST, String.format("%" + (i + 1) + "s%s", "", rootNode.getData().getName()));
		if (rootNode.bufferedAllChildren == null) {
			LOGGER.log(Level.FINEST, String.format("%" + (i + 2) + "s%s", "", "children not yet loaded"));
		} else {
			for (FilteredTreeNode<SlotView> node : rootNode.getFilteredChildren()) {
				print(i + 1, node);
			}
		}
	}
}
