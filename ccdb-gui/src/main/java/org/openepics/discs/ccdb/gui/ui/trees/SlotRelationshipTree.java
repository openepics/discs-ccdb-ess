/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.ccdb.gui.ui.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.openepics.discs.ccdb.core.ejb.InstallationEJB;
import org.openepics.discs.ccdb.core.ejb.SlotEJB;
import org.openepics.discs.ccdb.model.InstallationRecord;
import org.openepics.discs.ccdb.model.Slot;
import org.openepics.discs.ccdb.model.SlotPair;
import org.openepics.discs.ccdb.model.SlotRelationName;
import org.openepics.discs.ccdb.gui.views.SlotView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Implements extrinsic method, that return's tree node's children based on given relationship name.
 *
 * @author ilist
 */
public class SlotRelationshipTree extends Tree<SlotView> {
	protected SlotRelationName relationship;
	protected InstallationEJB installationEJB;

	/**
	 * Initializes the tree.
	 * Relationship determines the type of hierarchy.
	 * SlotEJB and installationEJB are needed to generate the children.
	 * @param relationship the type of hierarchy
	 * @param slotEJB slotEJB
	 * @param installationEJB installationEJB
	 */
	public SlotRelationshipTree(SlotRelationName relationship, SlotEJB slotEJB, InstallationEJB installationEJB) {
		super(slotEJB);
		this.relationship = relationship;
		this.installationEJB = installationEJB;
	}

	/**
	 * Containers and nodes containing the filter string are present.
	 * @param node the node
	 * @return should the node be displayed
	 */
	@Override
	public boolean isNodeInFilter(BasicTreeNode<SlotView> node) {
		final SlotView slotView = node.getData();
		return !slotView.isHostingSlot() || slotView.getName().toUpperCase().contains(getAppliedFilter());
	}

	/**
	 * Returns all children. Takes care of correct order and initialization of them.
	 * @param parent the parent node
	 */
	@Override
	public List<? extends BasicTreeNode<SlotView>> getAllChildren(BasicTreeNode<SlotView> parent) {
		final SlotView slotView = parent.getData();
		final List<BasicTreeNode<SlotView>> allChildren = new ArrayList<>();

		final List<SlotPair> slotChildren = slotView.getSlot().getPairsInWhichThisSlotIsAParentList();

		for (SlotPair pair : slotChildren) {
	    	if (pair.getSlotRelation().getName() == relationship) {
	    		final Slot childSlot = pair.getChildSlot();
	            final SlotView childSlotView = new SlotView(childSlot, slotView, pair.getSlotOrder(), slotEJB);

	            assignInstalledDeviceToView(childSlotView);
	            allChildren.add(new FilteredTreeNode<SlotView>(childSlotView, parent, this));
	    	}
	    }
		if (!allChildren.isEmpty()) {
			allChildren.sort((o1, o2) -> {return o1.getData().getOrder() - o2.getData().getOrder();});
			allChildren.get(0).getData().setFirst(true);
			allChildren.get(allChildren.size()-1).getData().setLast(true);
		}
		return allChildren;
	}

	private void assignInstalledDeviceToView(final SlotView slotView) {
        if (slotView.isHostingSlot() && installationEJB != null) {
            final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slotView.getSlot());
            if (record != null) {
                slotView.setInstalledDevice(record.getDevice());
            }
        }
	}

	/**
	 * Finds a one instance of the slot in the tree. Only works for "contains" tree, but it could work for any entity
	 * based trees.
	 * TODO This code could be simplified by turning everything into recursion. Or we could use a similar map mentioned next to Tree.refreshNodeIds
	 *
	 * @param slot the slot to find
	 * @return one of it's tree nodes
	 */
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
                final SlotView slotView = child.getData();
                if (slotView.getId().equals(soughtSlot.getId())) {
                    // the sought TreeNode found. Process it.
                    soughtChildFound = true;
                    node = child;
                    if (!node.isLeaf() && (soughtSlot != slot)) {
                        node.setExpanded(true);
                    }
                    break;
                }
            }
            if (!soughtChildFound) {
                // the tree does not contain a slot in the path
                throw new IllegalStateException("Slot " + node.getData().getName() +
                        " does not CONTAINS slot " + soughtSlot.getName());
            }
        }
        return node;
    }


    /**
     * The method generates the path from the requested node to the root of the contains hierarchy. If an element has
     * multiple parents, this method always chooses the first parent it encounters.
     *
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
