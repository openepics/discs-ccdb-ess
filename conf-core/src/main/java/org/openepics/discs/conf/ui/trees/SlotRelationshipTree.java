package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;

public class SlotRelationshipTree extends Tree<SlotView> {
	private SlotRelationName relationship;
	final private SlotEJB slotEJB;
	final FilteredTreeNode<SlotView> rootNode;
	 
	public SlotRelationshipTree(SlotView data, SlotRelationName relationship, SlotEJB slotEJB) {
		rootNode = new FilteredTreeNode<SlotView>(data, null, this);
		this.relationship = relationship;
		this.slotEJB = slotEJB;
	}
	
	@Override
	public List<? extends BasicTreeNode<SlotView>> getAllChildren(BasicTreeNode<SlotView> parent) {
		final SlotView slotView = parent.getData();
		final List<BasicTreeNode<SlotView>> allChildren = new ArrayList<>();
		
		final List<SlotPair> slotChildren = slotView.getSlot().getPairsInWhichThisSlotIsAParentList();    
	    
		for (SlotPair pair : slotChildren) {
	    	if (pair.getSlotRelation().getName().equals(relationship)) {
	    		final Slot childSlot = pair.getChildSlot();
	            final SlotView childSlotView = new SlotView(childSlot, slotView, pair.getSlotOrder(), slotEJB);
	            allChildren.add(new FilteredTreeNode<SlotView>(childSlotView, parent, this));
	    	}
	    }
		return allChildren;
	}

	@Override
	public BasicTreeNode<SlotView> getRootNode() {
		return rootNode;
	}

}
