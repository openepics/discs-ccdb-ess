package org.openepics.discs.conf.ui.trees;

import java.util.List;

import org.primefaces.model.TreeNode;

public class FilteredTreeNode<D> extends TreeNodeWithTree<D> {

	private List<TreeNode> bufferedChildren = null;
	
	public FilteredTreeNode(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent, tree);
	}

	@Override
	public List<TreeNode> getChildren() {
		if (bufferedChildren == null)
			bufferedChildren = (List<TreeNode>)(Object)getAllChildren(); 
		return bufferedChildren;
	}

	public void cleanCache() {
		bufferedChildren = null;
	}
}
