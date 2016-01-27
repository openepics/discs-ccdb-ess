package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.util.ConnectsManager;
import org.openepics.discs.conf.views.SlotView;

/**
 * Implements extrinsic method, that returns tree node's children based on connects database.
 * Takes care of removing cycles.
 * 
 * @author ilist
 *
 */
public class ConnectsTree extends Tree<SlotView> {
	private final ConnectsManager connectsManager;
	 
	/**
	 * Constructs the connects tree.
	 * 
	 * @param slotEJB slotEJB
	 * @param connectsManager connects manager
	 */
	public ConnectsTree(SlotEJB slotEJB, ConnectsManager connectsManager) {
		super(slotEJB);						
		this.connectsManager = connectsManager;
	}
	
	/**
	 * Containers and nodes containing the filter string are present.
	 * @param node the node
	 * @return should the node be displayed
	 */
	@Override
	public boolean isNodeInFilter(BasicTreeNode<SlotView> node) {
		Slot slot = node.getData().getSlot();
		return !slot.isHostingSlot() || slot.getName().toUpperCase().contains(getAppliedFilter());
	}
	
	/**
	 * Returns "cable" children of current tree node. Takes care of cycles.
	 * Takes care of cable numbers.
	 * 
	 * @param parent the parent node
	 * @return it children
	 */
	@Override
	public List<? extends BasicTreeNode<SlotView>> getAllChildren(BasicTreeNode<SlotView> parent) {
		final SlotView parentSlotView = parent.getData();
		final Slot parentSlot = parentSlotView.getSlot();
		
		List<Slot> childSlots = connectsManager.getSlotConnects(parentSlot);
		  
		final List<BasicTreeNode<SlotView>> allChildren = new ArrayList<>();
		
		for (Slot child : childSlots) {
			if (hasCycle(parentSlotView, child.getId())) {
				// This sets the default for parent if the only connection from the slot is to itself.
                // If there are any other connections, this will get set to correct value after the loop.
				parentSlotView.setCableNumber(connectsManager.getCobles(parentSlot, child).get(0).getNumber());
				continue;
			}
			final SlotView childSlotView = new SlotView(child, parentSlotView, 0, slotEJB);
	        childSlotView.setLevel(parentSlotView.getLevel()+1);
	        allChildren.add(new FilteredTreeNode<SlotView>(childSlotView, parent, this));
	    	
	        childSlotView.setCableNumber(connectsManager.getCobles(parentSlot, child).get(0).getNumber()); // points to parent
	    }
		
		if (allChildren.size() > 0) {  // points to first child // TODO lazy loading may introduce bugs here
			parentSlotView.setCableNumber(connectsManager.getCobles(parentSlot, allChildren.get(0).getData().getSlot()).get(0).getNumber());
		}
		return allChildren;
	}

	/**
	 * Check's if there is a cycle formed with node ID.
	 * 
	 * @param parentSlotView the parent slot view
	 * @param id to search for
	 * @return true if there is a cycle
	 */
	private boolean hasCycle(SlotView parentSlotView, Long id) {
		while (parentSlotView != null) {
			if (id.equals(parentSlotView.getId())) return true;
			parentSlotView = parentSlotView.getParentNode();
		}
		return false;
	}
	
}
