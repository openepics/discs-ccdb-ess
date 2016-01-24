package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.util.ConnectsManager;
import org.openepics.discs.conf.views.SlotView;

public class ConnectsTree extends Tree<SlotView> {
	private final ConnectsManager connectsManager;	
	 
	public ConnectsTree(SlotEJB slotEJB, ConnectsManager connectsManager) {
		super(slotEJB);						
		this.connectsManager = connectsManager;
	}
	
	
	@Override
	public List<? extends BasicTreeNode<SlotView>> getAllChildren(BasicTreeNode<SlotView> parent) {
		final SlotView parentSlotView = parent.getData();
		
		List<Slot> childSlots = connectsManager.getSlotConnects(parentSlotView.getSlot());
		  
		final List<BasicTreeNode<SlotView>> allChildren = new ArrayList<>();
		
		for (Slot child : childSlots) {
			if (hasCycle(parentSlotView, child.getId())) continue;
			final SlotView childSlotView = new SlotView(child, parentSlotView, 0, slotEJB);
	        childSlotView.setLevel(parentSlotView.getLevel()+1);
	        allChildren.add(new FilteredTreeNode<SlotView>(childSlotView, parent, this));
	    	//TREE check cycleas
	    }
		return allChildren;
	}


	private boolean hasCycle(SlotView parentSlotView, Long id) {
		while (parentSlotView != null) {
			if (id.equals(parentSlotView.getId())) return true;
			parentSlotView = parentSlotView.getParentNode();
		}
		return false;
	}
	
}
