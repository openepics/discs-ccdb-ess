/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
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
package org.openepics.discs.conf.ui.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class EntityHierarchyBuilder extends HierarchyBuilder {
    private int preloadLimit;

    private SlotRelationName relationship;
    private final SlotView fakeSlotView;
    private final InstallationEJB installationEJB;
    private final SlotEJB slotEJB;
    private Set<Long> expandedNodes = Collections.emptySet();

    /**
     * Constructs a new hierarchy builder.
     * @param preloadLimit the limit after which the builds the entire tree. If this value is e.g. 3, then the
     * builder will only create nodes for level one and create stubs for level 2. When the user actually expands the
     * node at level 1, it will build the tree for the expanded node at level 2, but again create stubs for level 3. But
     * when the node at level 3 is expanded the builds then build the entire for the expanded node.
     * @param installationEJB the DAO to use to read the installation configuration for each node
     * @param slotEJB the DAO to use to get the {@link Slot} information
     */
    public EntityHierarchyBuilder(int preloadLimit, InstallationEJB installationEJB, SlotEJB slotEJB) {
        this.relationship = SlotRelationName.CONTAINS;
        this.preloadLimit = preloadLimit;
        this.installationEJB = installationEJB;
        this.slotEJB = slotEJB;
        final Slot fakeSlot = new Slot("Fake slot", false);
        fakeSlot.setDescription("Fake slot");
        fakeSlot.setComponentType(new ComponentType("Fake type"));
        this.fakeSlotView = new SlotView(fakeSlot, null, 1, null);
    }



    public SlotRelationName getRelationship() {
        return relationship;
    }

    public void setRelationship(SlotRelationName relationship) {
        this.relationship = relationship;
    }


    /* (non-Javadoc)
     * @see org.openepics.discs.conf.ui.common.HierarchyBuilder#expandNode(org.primefaces.model.TreeNode)
     */
    @Override
    public void expandNode(final TreeNode node) {
        final SlotView slotView = (SlotView) node.getData();
        if (!slotView.isInitialzed()) {
            rebuildSubTree(node);
        }
    }

    /** This method adds new children to the node after removing all the existing children.
     * If the preload limit was already reached it calls itself recursively on all the children.
     * If the preload limit was not reached, it just checks whether the added children has any children of its own.
     * If it has, it adds a fake tree node to, since this marks the node as expandable in the UI.
     * @param node the {@link TreeNode} to add children to.
     * @see #expandNode(TreeNode)
     */
    public void rebuildSubTree(final TreeNode node) {
        expandedNodes = Collections.singleton(((SlotView)node.getData()).getId());
        rebuildSubTreeInternal(node);
        expandedNodes = Collections.emptySet();
    }

    private boolean rebuildSubTreeInternal(final TreeNode node) {
        Preconditions.checkNotNull(node);
        // 1. Remove all existing children
        node.getChildren().clear();

        return handleNodeSubtree(node);
    }

    /** <p>
     * Add the child to appropriate place in the children collection
     * </p><p>
     * If the level equals or is greater than preload limit call expand on all children
     * otherwise, check if the child has children of its own. If yes, add a fake node to it and exit.
     * </p><p>
     * This method adds a node unconditionally, disregarding the filters. If filtering is applied and the node is
     * an installation slot, then this method builds the subtree while applying the filter.
     * </p>
     *
     * @param parent The parent to add to
     * @param slotView The {@link SlotView}
     * @return <code>true</code> if the slot (or it's children) is accepted by filtering, <code>false</code> otherwise
     */
    public boolean addChildToParent(final TreeNode parent, final SlotView slotView) {
        final ListIterator<TreeNode> treeNodeChildren = parent.getChildren().listIterator();
        final SlotView parentData = (SlotView) parent.getData();

        // set basic slot information
        slotView.setLevel(parentData.getLevel() + 1);
        slotView.setInitialzed(false);
        slotView.setDeletable(true);
        if (slotView.isHostingSlot() && installationEJB != null) {
            final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slotView.getSlot());
            if (record != null) {
                slotView.setInstalledDevice(record.getDevice());
            }
        }
        // find correct position
        TreeNode manipulatedSibling = null;
        while (treeNodeChildren.hasNext()) {
            final TreeNode nextChild = treeNodeChildren.next();
            manipulatedSibling = nextChild;
            final SlotView childData = (SlotView) nextChild.getData();
            if (childData.getOrder() > slotView.getOrder()) {
                // move one back, because this slot needs to be after inserting slot
                manipulatedSibling = treeNodeChildren.previous();
                break;
            }
        }
        if (manipulatedSibling != null) {
            if (!treeNodeChildren.hasPrevious()) {
                // we're inserting at the start of the collection
                ((SlotView) manipulatedSibling.getData()).setFirst(false);
                slotView.setFirst(true);
            }
            if (!treeNodeChildren.hasNext()) {
                // we're appending to the end of the collection
                ((SlotView) manipulatedSibling.getData()).setLast(false);
                slotView.setLast(true);
            }
        } else {
            // the children collection was empty. This is the only slot.
            slotView.setFirst(true);
            slotView.setLast(true);
        }
        // Correct position found. Add the node to parent.
        final TreeNode addedTreeNode = new DefaultTreeNode(slotView);
        addedTreeNode.setParent(parent);
        addedTreeNode.setExpanded(false);
        treeNodeChildren.add(addedTreeNode);

        // avoid boolean evaluation short-cutting
        final boolean subtreeContainesValidChildren = handleNodeSubtree(addedTreeNode);
        final boolean isSlotAcceptedByFilter = isSlotAcceptedByFilter(slotView.getSlot());
        return isSlotAcceptedByFilter || subtreeContainesValidChildren;
    }

    private boolean handleNodeSubtree(TreeNode node) {
        final SlotView slotView = (SlotView) node.getData();
        final boolean isSlotContainer = !slotView.isHostingSlot();

        if (expandedNodes.contains(slotView.getId())) node.setExpanded(true);

        boolean includesFilteredNodes = false;

        // See if the added node needs to be marked "expandable".
        // get a list of all children
        List<SlotPair> slotChildren = slotView.getSlot().getPairsInWhichThisSlotIsAParentList();
        if (!slotChildren.isEmpty()) {
            // we add fake child or all children
            for (SlotPair pair : slotChildren) {
                if (pair.getSlotRelation().getName() == relationship) {
                    // relationship match is OK, but is the filter match OK
                    final Slot childSlot = pair.getChildSlot();
                    // There are two branches: no filtering or filtering
                    // no filtering branch: standard logic applies. Either the preload limit is in effect, and the node
                    //                      is just checked whether it has at least one child, so that it can be marked
                    //                      for expansion. Or preload limit is already passed, in which case the entire
                    //                      subtree is loaded.
                    // filtering branch:    if the node is a container, then the standard logic can be applied.
                    //                      Filtering does not apply to containers, only to installation slots.
                    //                      For container nodes the preload level is also applied, the same as for
                    //                      "no filtering" branch.
                    //                      If the node is and installation slot, then all children are loaded, but
                    //                      for each added child, it checks whether its tree contains any of the nodes
                    //                      that are accepted by the filter. If the child (and it's subtree) does not
                    //                      contain any such nodes, this tree is pruned from the current node.
                    if (!isFilteringApplied() || !childSlot.isHostingSlot()) {
                        includesFilteredNodes = true;
                        // the parent we're adding to can no longer be deleted
                        if (slotView.getLevel() >= preloadLimit || expandedNodes.contains(slotView.getId())) {
                            // add this child properly!
                            slotView.setInitialzed(true);
                            final SlotView childSlotView = new SlotView(childSlot, slotView, pair.getSlotOrder(),
                                    slotEJB);
                            addChildToParent(node, childSlotView);
                        } else {
                            // the preload limit still not reached. Just add one fake node.
                            slotView.setInitialzed(false);
                            new DefaultTreeNode(fakeSlotView, node);
                            break;
                        }
                    } else {
                        // we know that this is an installation slot, since a container is already covered above
                        final SlotView childSlotView = new SlotView(childSlot, slotView, pair.getSlotOrder(), slotEJB);
                        final boolean childContainsFilters = addChildToParent(node, childSlotView);
                        if (!childContainsFilters) {
                            pruneChild(node, childSlotView);
                        } else {
                            includesFilteredNodes = true;
                        }
                        slotView.setInitialzed(true);
                    }
                }
            }
        }
        slotView.setDeletable(node.isLeaf());
        return includesFilteredNodes;
    }

    private void pruneChild(final TreeNode parent, final SlotView slotView) {
        final ListIterator<TreeNode> children = parent.getChildren().listIterator();
        while (children.hasNext()) {
            TreeNode child = children.next();
            if (child.getData().equals(slotView)) {
                children.remove();
                return;
            }
        }
    }

    public List<TreeNode> initHierarchy(final List<TreeNode> selectedNodes, final TreeNode root) {
        final SlotView rootSlotView = (SlotView) root.getData();
        if (rootSlotView.isInitialzed()) return new ArrayList<>(root.getChildren());

        root.getChildren().clear();
        rootSlotView.setLevel(0);

        final List<Slot> levelOneSlots;

        // find root nodes for the selected sub-tree
        levelOneSlots = Lists.newArrayList();
        for (TreeNode selectedNode : selectedNodes)
            findRelationRootsForSelectedNode(selectedNode, levelOneSlots);


        // build the tree
        int order = 0;
        for (final Slot levelOne : levelOneSlots) {
            final SlotView levelOneView = new SlotView(levelOne, rootSlotView, ++order, slotEJB);
            levelOneView.setLevel(1);
            final TreeNode newLevelOneNode = new DefaultTreeNode(levelOneView, root);
            expandNode(newLevelOneNode);

        }

        removeRedundantRoots(root);

        rootSlotView.setInitialzed(true);

        return new ArrayList<>(root.getChildren());
    }


    private void findRelationRootsForSelectedNode(final TreeNode node, final List<Slot> rootSlots) {
        final SlotView nodeSlotView = (SlotView) node.getData();
        final Slot nodeSlot = nodeSlotView.getSlot();

        final List<SlotPair> relations = nodeSlot.getPairsInWhichThisSlotIsAParentList();
        for (final SlotPair relationCandidate : relations) {
            if (relationCandidate.getSlotRelation().getName() == relationship) {
                if (!rootSlots.contains(nodeSlot))
                        rootSlots.add(nodeSlot);
                break;
            }
        }
        // this node is not a root
        for (final TreeNode childNode : node.getChildren()) {
            findRelationRootsForSelectedNode(childNode, rootSlots);
        }
    }

    public void applyFilter(TreeNode root, List<TreeNode> children)
    {
        expandedNodes = new HashSet<Long>();
        for (TreeNode n : children) {
            collectExpandedNodes(n);
            if (n.isExpanded()) {
                expandedNodes.add(((SlotView)n.getData()).getId());
            }
        }
        for (TreeNode n : children) {
            SlotView nv = (SlotView)n.getData();
            if (rebuildSubTreeInternal(n) || isSlotAcceptedByFilter(nv.getSlot())) {
                root.getChildren().add(n);
                n.setExpanded(expandedNodes.contains(nv.getId()));
            } else {
                root.getChildren().remove(n);
            }
        }
        expandedNodes = Collections.emptySet();
    }

    private void collectExpandedNodes(TreeNode node) {
        if (node.isExpanded()) {
            SlotView modelSlotView = (SlotView)node.getData();
            expandedNodes.add(modelSlotView.getId());
        }
        for (TreeNode child : node.getChildren()) {
            collectExpandedNodes(child);
        }
    }
}
