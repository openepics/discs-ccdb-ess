package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

public abstract class Tree<D> {
	private List<BasicTreeNode<D>> selectedNodes = new ArrayList<>();
	private String filter = "";
	private String appliedFilter = "";
	
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
		if (selectedNodes != null)
			this.selectedNodes.addAll((List<BasicTreeNode<D>>)(Object)Arrays.asList(selectedNodes));
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getAppliedFilter() {
		return appliedFilter;
	}
	
	public void applyFilter() {
		this.appliedFilter = filter == null ? "" : filter.toUpperCase();
		((FilteredTreeNode<D>)getRootNode()).cleanFilterCache();
	//	print(0, getRootNode());
	}

	private void print(int i, TreeNode rootNode) {
		System.out.println(i+" "+((SlotView)rootNode.getData()).getName());
		for (TreeNode node : rootNode.getChildren())
			print(i+1, node);
		
	}

}
