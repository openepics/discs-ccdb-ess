package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

public abstract class Tree<D> {
	private List<FilteredTreeNode<D>> selectedNodes = new ArrayList<>();
	private String filter = "";
	private String appliedFilter = "";
	final protected SlotEJB slotEJB;
	private FilteredTreeNode<D> rootNode;

	public Tree(SlotEJB slotEJB) {
		this.slotEJB = slotEJB;
	}
	
	public void setRootNode(FilteredTreeNode<D> rootNode) {
		this.rootNode = rootNode;
	}
	
	public FilteredTreeNode<D> getRootNode() {
		return rootNode;
	}


	public abstract List<? extends BasicTreeNode<D>> getAllChildren(BasicTreeNode<D> data);	
	
	public List<FilteredTreeNode<D>> getSelectedNodes()
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
			this.selectedNodes.addAll((List<FilteredTreeNode<D>>)(List<?>)Arrays.asList(selectedNodes));
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

	public void print(int i, TreeNode rootNode) {
		System.out.println(i+" "+((SlotView)rootNode.getData()).getName());
		for (TreeNode node : rootNode.getChildren())
			print(i+1, node);
		
	}

}
