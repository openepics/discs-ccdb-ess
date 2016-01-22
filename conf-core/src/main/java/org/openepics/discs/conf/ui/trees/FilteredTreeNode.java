package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

public class FilteredTreeNode<D> extends TreeNodeWithTree<D> {

	protected List<FilteredTreeNode<D>> bufferedAllChildren = null;
	protected List<TreeNode> bufferedFilteredChildren = null;
	
	public FilteredTreeNode(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent, tree);
	}

	@Override
	public List<TreeNode> getChildren() {		
		if (bufferedFilteredChildren == null) {
			getAllChildren();
			if ("".equals(getTree().getAppliedFilter())) {
				bufferedFilteredChildren = (List<TreeNode>)(Object)bufferedAllChildren;				
			} else {				
				bufferedFilteredChildren = new ArrayList<>();
				for (FilteredTreeNode<D> node : bufferedAllChildren) {					
					if (node.isThisNodeAbsolutelyInFilter()) {
						bufferedFilteredChildren.add(node);
					} else if (!node.isLeaf()) {
						bufferedFilteredChildren.add(node);
					} // else remove the leafs					
				}				
				//SlotView view = (SlotView)getData(); // TREE clean up this code
				//System.out.println("Filtered " + view.getName() + " " + bufferedAllChildren.size() + " " + bufferedFilteredChildren.size() );
			}
			updateRowKeys();			
		}
		return bufferedFilteredChildren;
	}
	
	@Override
	public List<? extends BasicTreeNode<D>> getAllChildren() {
		if (bufferedAllChildren == null) {
			bufferedAllChildren = (List<FilteredTreeNode<D>>) super.getAllChildren();
		}
		return bufferedAllChildren;
	}
	
    public boolean isThisNodeAbsolutelyInFilter() {
    	SlotView view = (SlotView)getData(); // TREE clean up this code
    	Slot slot = view.getSlot();
    	return !slot.isHostingSlot() || slot.getName().toUpperCase().contains(getTree().getAppliedFilter().toUpperCase());
    }
    
	public void cleanCache() {
		bufferedAllChildren = null;
		bufferedFilteredChildren = null;
	}
	
	public void cleanFilterCache() {
		bufferedFilteredChildren = null;
		if (bufferedAllChildren != null) {
			for (FilteredTreeNode<D> node : bufferedAllChildren) {
				node.cleanFilterCache();
			}
		}
		
	}
}