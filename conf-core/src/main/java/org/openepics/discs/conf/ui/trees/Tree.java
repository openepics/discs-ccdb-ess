package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

public abstract class Tree<D> {
	private List<BasicTreeNode<D>> selectedNodes = new ArrayList<>();
	
	public abstract BasicTreeNode<D> getRootNode();

	public abstract List<? extends BasicTreeNode<D>> getAllChildren(BasicTreeNode<D> data);	
	
	public List<BasicTreeNode<D>> getSelectedNodes()
	{
		return selectedNodes;
	}	
	
	public TreeNode[] getSelectedNodesArray()
	{
		return selectedNodes.toArray(new TreeNode[selectedNodes.size()]);
	}
	
	public void setSelectedNodesArray(TreeNode[] selectedNodes)
	{
		this.selectedNodes.clear();
		this.selectedNodes = (List<BasicTreeNode<D>>)(Object)Lists.newArrayList(selectedNodes);
	}
}
