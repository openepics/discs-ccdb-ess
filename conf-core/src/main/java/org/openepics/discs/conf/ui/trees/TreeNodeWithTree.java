package org.openepics.discs.conf.ui.trees;

import java.util.List;

/**
 * This type of 'tree node' asks Tree what are its children. 
 * Extrinsic info about children let's us use the same nodes implementation for quite different trees.
 * 
 * @author ilist
 *
 * @param <D> type of data
 */
public abstract class TreeNodeWithTree<D> extends BasicTreeNode<D> {
	private Tree<D> tree;
	
	/**
	 * Besides data and parent, construction is also given the tree.
	 * 
	 * @param data data
	 * @param parent parent node
	 * @param tree the tree
	 */
	public TreeNodeWithTree(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent);
		this.tree = tree;
	}
	
	/**
	 * Returns the children it gets from the parent tree. No caching.
	 * 
	 * @return the children
	 */
	@Override
	public List<? extends BasicTreeNode<D>> getAllChildren() {
		return tree.getAllChildren(this);
	}

	/**
	 * Returns the tree.
	 * @return the tree.
	 */
	protected Tree<D> getTree() {
		return tree;
	}
	
}
