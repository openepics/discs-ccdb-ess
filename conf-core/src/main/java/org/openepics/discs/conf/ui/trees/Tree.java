package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

/**
 * Most importantly Tree class has an "extrinsic" method to find tree node's children.
 * Theoretically this could be inside tree's root node, but then we couldn't use RootNodeWithChildren for all: powers, controls and connects trees.
 * 
 * Additionally Tree class contains global info about the tree: current selection of the nodes, current filter.
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
	public List<FilteredTreeNode<D>> getSelectedNodes()
	{
		return selectedNodes;
	}	
	
	/**
	 * Returns currently selected nodes as an array. Used for primefaces.
	 * @return array of selected nodes
	 */
	public TreeNode[] getSelectedNodesArray()
	{
		return selectedNodes.toArray(new TreeNode[selectedNodes.size()]);
	}
	
	/**
	 * Sets selected nodes from an array. Used for primefaces.
	 * Unchecked typecast is needed.
	 * 
	 * @param selectedNodes array of selected nodes
	 */
	@SuppressWarnings("unchecked")
	public void setSelectedNodesArray(TreeNode[] selectedNodes)
	{
		this.selectedNodes.clear();
		if (selectedNodes != null)
			this.selectedNodes.addAll((List<FilteredTreeNode<D>>)(List<?>)Arrays.asList(selectedNodes));
	}

	/**
	 * Clears the selection globally.
	 */
    public void unselectAllTreeNodes() {        
    	for (final TreeNode node : selectedNodes) {
    		node.setSelected(false);
        }
        selectedNodes = new ArrayList<>();
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
	
	/**
	 * Applies the filter. AppliedFilter filed becomes filter field.
	 */
	public void applyFilter() {
		this.appliedFilter = filter == null ? "" : filter.toUpperCase();
		((FilteredTreeNode<D>)getRootNode()).cleanFilterCache();
	//	print(0, getRootNode());
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
	public static void print(int i, TreeNode rootNode) {
		System.out.println(i+" "+((SlotView)rootNode.getData()).getName());
		for (TreeNode node : rootNode.getChildren()) {
			print(i+1, node);
		}
	}
}
