package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.views.SlotView;

import com.google.common.collect.Lists;

public class RootNodeWithChildren extends FilteredTreeNode<SlotView> {		
	public RootNodeWithChildren(SlotView data, Tree<SlotView> tree) {
		super(data, null, tree);
		bufferedAllChildren = new ArrayList<>();
	}
	
	public void initHierarchy(final List<BasicTreeNode<SlotView>> selectedNodes) {
		bufferedAllChildren = new ArrayList<>();
        
        final List<Slot> childrenSlots = Lists.newArrayList();

        // find root nodes for the selected sub-tree        
        for (BasicTreeNode<SlotView> selectedNode : selectedNodes) {
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
                
        cleanCache();       
	}
	
    private void findRelationRootsForSelectedNode(final BasicTreeNode<SlotView> containsNode, final List<Slot> rootSlots) {        
        final Slot nodeSlot = containsNode.getData().getSlot();
                
        if (getTree().getAllChildren(containsNode).size() > 0   // TREE could be optimized to hasChildren()
        		&& !rootSlots.contains(nodeSlot)) {
        	rootSlots.add(nodeSlot);
        }
        
        // this node is not a root
        for (final BasicTreeNode<SlotView> childNode : containsNode.getAllChildren()) {
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
        for (BasicTreeNode<SlotView> levelOne : bufferedAllChildren) {
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
    
    
    private void removeRedundantRoots(BasicTreeNode<SlotView> node, Set<Long> levelOne, Set<Long> visited) {
        final SlotView nodeSlotView = node.getData();        

        if (visited.contains(nodeSlotView.getId())) return;
        visited.add(nodeSlotView.getId());

        if (nodeSlotView.getLevel() > 1) {
            if (levelOne.contains(nodeSlotView.getId())) {
                levelOne.remove(nodeSlotView.getId());
                // after removal, we still need to visit the subtree of this node
            }
        }
        for (BasicTreeNode<SlotView> child : node.getAllChildren()) {
            removeRedundantRoots(child, levelOne, visited);
        }
    }
	
	@Override
	public void cleanCache() {
		bufferedFilteredChildren = null;
	}
}
