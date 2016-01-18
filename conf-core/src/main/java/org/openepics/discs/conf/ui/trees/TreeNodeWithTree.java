package org.openepics.discs.conf.ui.trees;

import java.util.List;

import org.primefaces.model.TreeNode;

public abstract class TreeNodeWithTree<D> extends BasicTreeNode<D> {
	private Tree<D> tree;
	
	public TreeNodeWithTree(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent);
		this.tree = tree;
	}
	
	public List<? extends TreeNode> getAllChildren() {
		return tree.getAllChildren(this);
	}
	
}
