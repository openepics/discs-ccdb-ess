package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

/**
 * Some hierarchies need a root node with predefined children.
 * Such are controls, powers and connects.
 * 
 * The children are searched through contains hierarchy. Only the children acting in the new hierarchy are used.
 * If a root appears somewhere in the new tree as a subnode it is removed.
 * 
 * @author ilist
 *
 */
public class RootNodeWithChildren extends FilteredTreeNode<SlotView> {
	private boolean initialized = false;
	
	public RootNodeWithChildren(SlotView data, Tree<SlotView> tree) {
		super(data, null, tree);
		bufferedAllChildren = new ArrayList<>();
	}
	
	@Override
	public List<? extends BasicTreeNode<SlotView>> getAllChildren() {
		return bufferedAllChildren;
	}
	
	public void initHierarchy(final List<FilteredTreeNode<SlotView>> selectedNodes) {
		if (initialized) return;
		
		bufferedAllChildren = new ArrayList<>();
        
        final List<Slot> childrenSlots = Lists.newArrayList();

        // find root nodes for the selected sub-tree
        for (FilteredTreeNode<SlotView> selectedNode : selectedNodes) {
            findRelationRootsForSelectedNode(selectedNode, childrenSlots);
        }

        // build the tree
        int order = 0;
        for (final Slot slot : childrenSlots) {
            final SlotView levelOneView = new SlotView(slot, getData(), ++order, getTree().slotEJB);
            levelOneView.setLevel(1);
            bufferedAllChildren.add(new FilteredTreeNode<SlotView>(levelOneView, this, getTree()));
        }

        removeRedundantRoots();
        
        getTree().setSelectedNodesArray(new TreeNode[0]);
        cleanCache();

        initialized = true;
	}
	
	public void reset() {
		initialized = false;
		bufferedAllChildren = new ArrayList<>();
		bufferedFilteredChildren = null;
		getTree().setSelectedNodesArray(new TreeNode[0]);
	}
	
    private void findRelationRootsForSelectedNode(final FilteredTreeNode<SlotView> containsNode, final List<Slot> rootSlots) {
        final Slot nodeSlot = containsNode.getData().getSlot();
        
        if (getTree().getAllChildren(containsNode).size() > 0   // TREE could be optimized to hasChildren()
        		&& !rootSlots.contains(nodeSlot)) {
        	rootSlots.add(nodeSlot);
        }
        
        // this node is not a root
        for (final FilteredTreeNode<SlotView> childNode : containsNode.getBufferedAllChildren()) {
            findRelationRootsForSelectedNode(childNode, rootSlots);
        }
    }

    public void removeRedundantRoots() {
        final Set<Long> levelOneIds = new HashSet<>();
        for (BasicTreeNode<SlotView> node : bufferedAllChildren) {
            levelOneIds.add(node.getData().getId());
        }

        // find redundant roots
        final Set<Long> visited = new HashSet<>();
        for (FilteredTreeNode<SlotView> levelOne : bufferedAllChildren) {
            removeRedundantRoots(levelOne, levelOneIds, visited);
        }

        // remove them
        Iterator<? extends BasicTreeNode<SlotView>> i = bufferedAllChildren.iterator();
        while (i.hasNext()) {
            BasicTreeNode<SlotView> n = i.next();
            if (!levelOneIds.contains(n.getData().getId())) {
                i.remove();
            }
        }
    }
    
    
    private void removeRedundantRoots(FilteredTreeNode<SlotView> node, Set<Long> levelOne, Set<Long> visited) {
        final SlotView nodeSlotView = node.getData();        

        if (visited.contains(nodeSlotView.getId())) return;
        visited.add(nodeSlotView.getId());

        if (nodeSlotView.getLevel() > 1) {
            if (levelOne.contains(nodeSlotView.getId())) {
                levelOne.remove(nodeSlotView.getId());
                // after removal, we still need to visit the subtree of this node
            }
        }
        for (FilteredTreeNode<SlotView> child : node.getBufferedAllChildren()) {
            removeRedundantRoots(child, levelOne, visited);
        }
    }
	
	@Override
	public void cleanCache() {
		bufferedFilteredChildren = null;
	}
}
