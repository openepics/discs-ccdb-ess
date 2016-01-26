package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

/**
 * Tree class contains global info about the tree.
 * Additionally it has an "extrinsic" method to find tree node's children.
 * 
 * Theoretically this could be inside tree's root node, but then we couldn't use RootNodeWithChildren for all: powers, controls and connects trees.
 * 
 * @author ilist
 *
 * @param <D>
 */
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
	
    public void unselectAllTreeNodes() {        
    	for (final TreeNode node : selectedNodes) {
    		node.setSelected(false);
        }
        selectedNodes = new ArrayList<>();
    }

    
    public void refreshIds(HashSet<Long> ids) {
    	refreshIds(ids, getRootNode());
    }
    
    private void refreshIds(HashSet<Long> ids, FilteredTreeNode<D> node) {
    	   if (ids.contains(((SlotView)node.getData()).getId())) {
    		   node.refreshCache();
    	   } else {
    		   for (final FilteredTreeNode<D> child : node.getBufferedAllChildren()) {
    			   refreshIds(ids, child);
    		   }
    	   }
    }
}
