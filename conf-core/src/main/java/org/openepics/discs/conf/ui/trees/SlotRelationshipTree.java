package org.openepics.discs.conf.ui.trees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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
	
	
    public FilteredTreeNode<SlotView> findNode(final Slot slot) {
        Preconditions.checkNotNull(slot);

        FilteredTreeNode<SlotView> node = getRootNode();
        final List<Slot> pathToRoot = getPathToRoot(slot);
        final ListIterator<Slot> pathIterator = pathToRoot.listIterator(pathToRoot.size());
        // we're not interested in the root node. Skip it.
        pathIterator.previous();
        while (pathIterator.hasPrevious()) {
            final Slot soughtSlot = pathIterator.previous();
            boolean soughtChildFound = false;
            for (FilteredTreeNode<SlotView> child : node.getBufferedAllChildren()) {
                final SlotView slotView = (SlotView) child.getData();
                if (slotView.getSlot().equals(soughtSlot)) {
                    // the sought TreeNode found. Process it.
                    soughtChildFound = true;
                    node = child;
                    if (!node.isLeaf()) {
                        node.setExpanded(true);
                    }
                    break;
                }
            }
            if (!soughtChildFound) {
                // the tree does not contain a slot in the path
                throw new IllegalStateException("Slot " + ((SlotView)node.getData()).getName() +
                        " does not CONTAINS slot " + soughtSlot.getName());
            }
        }
        return node;        
    }

    
    /** The method generates the path from the requested node to the root of the contains hierarchy. If an element has
     * multiple parents, this method always chooses the first parent it encounters.
     * @param slotOnPath the slot to find the path for
     * @return the path from requested node (first element) to the root of the hierarchy (last element).
     */
    private List<Slot> getPathToRoot(Slot slot) {
        final List<Slot> path = Lists.newArrayList();
        final Slot rootSlot = slotEJB.getRootNode();
        Slot slotOnPath = slot;

        path.add(slotOnPath);

        while (!rootSlot.equals(slotOnPath)) {
            final List<SlotPair> parents = slotOnPath.getPairsInWhichThisSlotIsAChildList();
            boolean containsParentFound = false;
            for (final SlotPair pair : parents) {
                if (pair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                    containsParentFound = true;
                    slotOnPath = pair.getParentSlot();
                    path.add(slotOnPath);
                    break;
                }
            }
            if (!containsParentFound) {
                throw new IllegalStateException("Slot " + slotOnPath.getName() + " does not have a CONTAINS parent.");
            }
        }
        return path;
    }

}
