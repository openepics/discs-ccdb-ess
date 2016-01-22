package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;

public class SlotRelationshipTree extends Tree<SlotView> {
	protected SlotRelationName relationship;
	private FilteredTreeNode<SlotView> rootNode;
	 
	public SlotRelationshipTree(SlotRelationName relationship, SlotEJB slotEJB) {
		super(slotEJB);		
		this.relationship = relationship;		
	}
	
	public void setRootNode(FilteredTreeNode<SlotView> rootNode) {
		this.rootNode = rootNode;
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
	            childSlotView.setLevel(slotView.getLevel()+1);
	            allChildren.add(new FilteredTreeNode<SlotView>(childSlotView, parent, this));
	    	}
	    }
		return allChildren;
	}

	@Override
	public FilteredTreeNode<SlotView> getRootNode() {
		return rootNode;
	}

}
