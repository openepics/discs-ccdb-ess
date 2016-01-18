package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;

import com.google.common.collect.Lists;

public class SlotRelationshipTreeWithChildren extends SlotRelationshipTree {
	private List<FilteredTreeNode<SlotView>> children = new ArrayList<>();
	
	public SlotRelationshipTreeWithChildren(SlotView data, SlotRelationName relationship, SlotEJB slotEJB) {
		super(data, relationship, slotEJB);
	}
	
	public void initHierarchy(final List<BasicTreeNode<SlotView>> selectedNodes) {
		children = new ArrayList<>();
        
        final List<Slot> childrenSlots = Lists.newArrayList();

        // find root nodes for the selected sub-tree        
        for (BasicTreeNode<SlotView> selectedNode : selectedNodes) {
            findRelationRootsForSelectedNode(selectedNode, childrenSlots);
        }

        // build the tree
        int order = 0;
        for (final Slot slot : childrenSlots) {        	
            final SlotView levelOneView = new SlotView(slot, getRootNode().getData(), ++order, slotEJB);
            levelOneView.setLevel(1);            
            children.add(new FilteredTreeNode<SlotView>(levelOneView, getRootNode(), this));
        }

        removeRedundantRoots();
                
        getRootNode().cleanCache();       
	}
	
    private void findRelationRootsForSelectedNode(final BasicTreeNode<SlotView> containsNode, final List<Slot> rootSlots) {        
        final Slot nodeSlot = containsNode.getData().getSlot();
        
        final List<SlotPair> relations = containsNode.getData().getSlot().getPairsInWhichThisSlotIsAParentList();                
        if (relations.stream().map(SlotPair::getSlotRelation).map(SlotRelation::getName).anyMatch(relationship::equals) 
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
        for (BasicTreeNode<SlotView> node : children) {
            levelOneIds.add(node.getData().getId());
        }

        // find redundant roots
        final Set<Long> visited = new HashSet<>();
        for (BasicTreeNode<SlotView> levelOne : children) {
            removeRedundantRoots(levelOne, levelOneIds, visited);
        }

        // remove them
        Iterator<? extends BasicTreeNode<SlotView>> i = children.iterator();
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
	public List<? extends BasicTreeNode<SlotView>> getAllChildren(BasicTreeNode<SlotView> parent) {		
		if (parent == getRootNode()) {
			return children;			
		}
		
		return super.getAllChildren(parent);
	}
}
