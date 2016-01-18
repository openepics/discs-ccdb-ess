package org.openepics.discs.conf.ui.trees;

import java.util.List;

public abstract class Tree<D> {
	public abstract BasicTreeNode<D> getRootNode();

	public abstract List<? extends BasicTreeNode<D>> getAllChildren(BasicTreeNode<D> data);	
}
