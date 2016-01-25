package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;

/**
 * Implements extrinsic method, that return's tree node's children based on given relationship name. 
 * 
 * @author ilist
 *
 */
public class SlotRelationshipTree extends Tree<SlotView> {
	protected SlotRelationName relationship;
	protected InstallationEJB installationEJB;

	public SlotRelationshipTree(SlotRelationName relationship, SlotEJB slotEJB, InstallationEJB installationEJB) {
		super(slotEJB);		
		this.relationship = relationship;		
		this.installationEJB = installationEJB;
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
	            childSlotView.setDeletable(true);
	            if (childSlotView.isHostingSlot() && installationEJB != null) {
	                final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slotView.getSlot());
	                if (record != null) {
	                    slotView.setInstalledDevice(record.getDevice());
	                }
	            }
	            allChildren.add(new FilteredTreeNode<SlotView>(childSlotView, parent, this));
	    	}
	    }
		if (!allChildren.isEmpty()) {
			allChildren.sort(new Comparator<BasicTreeNode<SlotView>>() {
				@Override
				public int compare(BasicTreeNode<SlotView> o1, BasicTreeNode<SlotView> o2) {
					return o1.getData().getOrder() - o2.getData().getOrder();
				}
			});
			allChildren.get(0).getData().setFirst(true);
			allChildren.get(allChildren.size()-1).getData().setLast(true);
		}
		return allChildren;
	}
}
