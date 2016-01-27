package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.views.SlotView;

/**
 * Adds filtering and buffering support to tree nodes.
 * It takes care to implement filtering without generating new tree nodes.
 * Buffering is done on all children and when needed. It's also done on filtered children.
 * The call to cleanCache() causes the buffers to clear and reload.
 * More elegant is call to refreshCache() which updates added/moved/removed nodes keeping their state,
 * i.e. expanded.
 * 
 * @author ilist
 *
 * @param <D> type of data
 */
public class FilteredTreeNode<D> extends TreeNodeWithTree<D> {

	protected List<FilteredTreeNode<D>> bufferedAllChildren = null;
	protected List<FilteredTreeNode<D>> bufferedFilteredChildren = null;
	
	/**
	 * Same construction as for the parent class.
	 * 
	 * @param data data
	 * @param parent the parent
	 * @param tree the tree
	 */
	public FilteredTreeNode(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent, tree);
	}

	/**
	 * Returns children available in current filter.
	 * @return filtered children
	 */
	@Override
	public List<? extends FilteredTreeNode<D>> getFilteredChildren() {
		if (bufferedFilteredChildren == null) {
			getBufferedAllChildren();
			if ("".equals(getTree().getAppliedFilter())) {
				bufferedFilteredChildren = bufferedAllChildren;
			} else {				
				bufferedFilteredChildren = new ArrayList<>();
				for (FilteredTreeNode<D> node : bufferedAllChildren) {
					if (getTree().isNodeInFilter(node)) {
						bufferedFilteredChildren.add(node);
					} else if (!node.isLeaf()) { // isLeaf actually calls getFilteredChildren
						bufferedFilteredChildren.add(node);
					} // else remove the leafs
				}
			}
			updateRowKeys();
		}
		return bufferedFilteredChildren;
	}
		
	/**
	 * Returns and buffers children.
	 * @return the children
	 */
	@SuppressWarnings("unchecked")
	public List<? extends FilteredTreeNode<D>> getBufferedAllChildren() {
		if (bufferedAllChildren == null) {
			bufferedAllChildren = (List<FilteredTreeNode<D>>)getAllChildren();
		}
		return bufferedAllChildren;
	}
    
    /**
     * Cleans the cache, so the next time data are reloaded from source.
     */
	public void cleanCache() {
		bufferedAllChildren = null;
		bufferedFilteredChildren = null;
	}
	
	/**
	 * Cleans filter cache on this node and all descendants. This way next time the filter is reapplied.
	 */
	public void cleanFilterCache() {
		bufferedFilteredChildren = null;
		if (bufferedAllChildren != null) {
			for (FilteredTreeNode<D> node : bufferedAllChildren) {
				node.cleanFilterCache();
			}
		}
		
	}

	/**
	 * Refreshes cache in a smart way to keep the old state of the nodes.
	 * TODO SlotView cast is used on data to compare the Slots.
	 */
	@SuppressWarnings("unchecked")
	public void refreshCache() {
		List<FilteredTreeNode<D>> oldBuffer = bufferedAllChildren;
		if (oldBuffer == null) return;
		ArrayList<FilteredTreeNode<D>> newBuffer = (ArrayList<FilteredTreeNode<D>>)getAllChildren();
		for (int i = 0; i<newBuffer.size(); i++) {
			Long id = ((SlotView)newBuffer.get(i).getData()).getId();
			for (FilteredTreeNode<D> oldNode : oldBuffer) {
				if (((SlotView)oldNode.getData()).getId().equals(id)) {
					newBuffer.set(i, oldNode);
				}
			}
			((SlotView)newBuffer.get(i).getData()).setFirst(i == 0);
			((SlotView)newBuffer.get(i).getData()).setLast(i == newBuffer.size()-1);
		}
		bufferedAllChildren = newBuffer;
		bufferedFilteredChildren = null;
	}
}