package org.openepics.discs.conf.ui.trees;

import java.util.List;

public abstract class TreeNodeWithTree<D> extends BasicTreeNode<D> {
	private Tree<D> tree;
	
	public TreeNodeWithTree(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent);
		this.tree = tree;
	}
	
	public List<? extends BasicTreeNode<D>> getAllChildren() {
		return tree.getAllChildren(this);
	}

	protected Tree<D> getTree() {
		return tree;
	}
	
}
