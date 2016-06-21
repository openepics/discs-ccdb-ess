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
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ui.trees.FilteredTreeNode;
import org.openepics.discs.conf.ui.trees.SlotRelationshipTree;
import org.openepics.discs.conf.ui.util.ConnectsManager;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class RelationshipController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private SlotEJB slotEJB;
    @Inject private InstallationEJB installationEJB;
    @Inject private SlotRelationEJB slotRelationEJB;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private ConnectsManager connectsManager;
    @Inject private HierarchiesController hierarchiesController;

    private List<SlotRelationshipView> relationships;
    private List<SlotRelationshipView> filteredRelationships;
    private List<SelectItem> relationshipTypes;

    private List<SlotRelationshipView> selectedRelationships;

    private SlotRelationshipView editedRelationshipView;
    private List<String> relationshipTypesForDialog;
    private Map<String, SlotRelation> slotRelationBySlotRelationStringName;

    private SlotRelationshipTree containsTree;

    private boolean editExistingRelationship;

    public RelationshipController() {}

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        relationshipTypes = buildRelationshipTypeList();

    	SlotView rootView = new SlotView(slotEJB.getRootNode(), null, 1, slotEJB);
    	containsTree = new SlotRelationshipTree(SlotRelationName.CONTAINS, slotEJB, installationEJB);
    	containsTree.setRootNode(new FilteredTreeNode<SlotView>(rootView, null, containsTree));
    }

    /** Tell this bean which {@link HierarchiesController} is its "master"
     * @param hierarchiesController the {@link HierarchiesController} master bean
     */
    protected void setUIParent(HierarchiesController hierarchiesController) {
        this.hierarchiesController = hierarchiesController;
    }

    private List<SelectItem> buildRelationshipTypeList() {
        Builder<SelectItem> immutableListBuilder = ImmutableList.builder();
        immutableListBuilder.add(new SelectItem("", "Select one"));

        final List<SlotRelation> slotRelations = slotRelationEJB.findAll();
        slotRelations.sort(new Comparator<SlotRelation>() {
            @Override
            public int compare(SlotRelation o1, SlotRelation o2) {
                return o1.getNameAsString().compareTo(o2.getNameAsString());
            }});
        // LinkedHashMap preserves the ordering. Important for UI.
        slotRelationBySlotRelationStringName = new LinkedHashMap<>();
        for (final SlotRelation slotRelation : slotRelations) {
            immutableListBuilder.add(new SelectItem(slotRelation.getNameAsString(), slotRelation.getNameAsString()));
            immutableListBuilder.add(new SelectItem(slotRelation.getIname(), slotRelation.getIname()));
            slotRelationBySlotRelationStringName.put(slotRelation.getNameAsString(), slotRelation);
            slotRelationBySlotRelationStringName.put(slotRelation.getIname(), slotRelation);
        }
        if (connectsManager.getCableDBStatus()) {
            immutableListBuilder.add(new SelectItem(connectsManager.getRelationshipName(),
                    connectsManager.getRelationshipName()));
        }

        relationshipTypesForDialog = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
        return immutableListBuilder.build();
    }

    /** Add the relationship information connected to one {@link Slot} to the relationship list.
     * @param slot the {@link Slot} to add the information for
     * @param forceInit if <code>true</code> recreate the relationship list even if it already contains some information
     * and add just the one {@link Slot} info to the new list. If <code>false</code> add the relationship information to
     * the already existing list.
     */
    protected void initRelationshipList(final Slot slot, final boolean forceInit) {
        if (forceInit || relationships == null) {
            relationships = Lists.newArrayList();
        }
        addToRelationshipList(slot);
    }

    private void addToRelationshipList(final Slot slot) {
        final Slot rootSlot = slotEJB.getRootNode();
        final List<SlotPair> slotPairs = slotPairEJB.getSlotRelations(slot);

        for (final SlotPair slotPair : slotPairs) {
            if (!slotPair.getParentSlot().equals(rootSlot)) {
                relationships.add(new SlotRelationshipView(slotPair, slot));
            }
        }

        final List<Slot> connectedSlots = connectsManager.getSlotConnects(slot);
        for (final Slot targetSlot : connectedSlots) {
            relationships.add(new SlotRelationshipView(slot.getId()+"c"+targetSlot.getId(), slot, targetSlot,
                    connectsManager.getRelationshipName()));
        }
    }

    /** Remove the relationships connected to one {@link Slot} from the relationship list and the
     * list of selected relationships if necessary.
     * @param slot the {@link Slot} to remove the information for
     */
    protected void removeRelatedRelationships(final Slot slot) {
        final ListIterator<SlotRelationshipView> relationsList = relationships.listIterator();
        while (relationsList.hasNext()) {
            final SlotRelationshipView relationshipView = relationsList.next();
            if (slot.getName().equals(relationshipView.getSourceSlotName())) {
                relationsList.remove();
            }
        }
        if (selectedRelationships!= null) {
            Iterator<SlotRelationshipView> i = selectedRelationships.iterator();
            while (i.hasNext()) {
                SlotRelationshipView selectedRelationship = i.next();
                if (selectedRelationship.getSourceSlotName().equals(slot.getName())) i.remove();
            }
        }
    }

    /** Called from {@link HierarchiesController} when the user deselects everything. */
    protected void clearRelationshipInformation() {
        relationships = null;
        filteredRelationships = null;
        selectedRelationships = null;
    }

    /**
     * This method gets called when the relationship processing is complete or dialog is simply closed. It properly
     * refreshes the selected node state so that the hierarchy behaves consistently.
     * The currently selected slot is also refreshed, so that new realtionship data is displayed.
     */
    public void onRelationshipPopupClose() {
        // clear the previous dialog selection for the next use
        if (editedRelationshipView != null) {
            if (editedRelationshipView.getTargetNode() != null) {
                editedRelationshipView.getTargetNode().setSelected(false);
            }

            editedRelationshipView = null;
        }
    }

    /** Called when button to delete relationship is clicked */
    public void onRelationshipDelete() {
        if (canRelationshipBeDeleted()) {
            for (SlotRelationshipView selectedRelationship : selectedRelationships) {
                final SlotPair slotPairToBeRemoved = selectedRelationship.getSlotPair();
                final Long parentSlotId = slotPairToBeRemoved.getParentSlot().getId();
                final Long childSlotId = slotPairToBeRemoved.getChildSlot().getId();
                relationships.remove(selectedRelationship);
                slotPairEJB.delete(slotPairToBeRemoved);
                UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                        "Relationship deleted.");
                selectedRelationship = null;
                hierarchiesController.refreshTrees(new HashSet<>(Arrays.asList(childSlotId, parentSlotId)));
            }
            selectedRelationships = null;
        } else {
            RequestContext.getCurrentInstance().execute("PF('cantDeleteRelation').show();");
        }
    }

    /** Tells the UI whether the relationship "Edit" button can be enabled.
     * @return <code>true</code> if only one relationship is selected and is of correct type,
     * <code>false</code> otherwise.
     */
    public boolean canRelationshipBeEdited() {
        if (selectedRelationships == null || selectedRelationships.size() != 1) return false;
        if (selectedRelationships.get(0).getSlotPair() == null) return false;
        if (selectedRelationships.get(0).getSlotPair().getSlotRelation().getName() == SlotRelationName.CONTAINS) {
            return false;
        }
        return true;
    }

    private boolean canRelationshipBeDeleted() {
        if (selectedRelationships == null || selectedRelationships.size() == 0) return false;
        for (SlotRelationshipView selectedRelationship : selectedRelationships) {
            if (selectedRelationship.getSlotPair() == null) return false;
            if (selectedRelationship.getSlotPair().getSlotRelation().getName().equals(SlotRelationName.CONTAINS)
                && !slotPairEJB.slotHasMoreThanOneContainsRelation(selectedRelationship.getSlotPair().getChildSlot())) return false;
        }
        return true;
    }

    /** Prepares data for editing new relationship */
    public void prepareEditRelationshipPopup() {
        Preconditions.checkState((selectedRelationships != null) && (selectedRelationships.size() == 1));

        containsTree.unselectAllTreeNodes();
        editExistingRelationship = true;
        // setups the dialog
        SlotRelationshipView v = selectedRelationships.get(0);
        editedRelationshipView = new SlotRelationshipView(v.getSlotPair(), v.getSourceSlot());

        FilteredTreeNode<SlotView> node = containsTree.findNode(editedRelationshipView.getTargetSlot());
        node.setSelected(true);
        editedRelationshipView.setTargetNode(node);

        // modify relationship types drop down menu
        relationshipTypesForDialog = slotRelationBySlotRelationStringName.entrySet().stream()
                .filter(e -> !e.getValue().getName().equals(SlotRelationName.CONTAINS)).map(Entry::getKey).collect(Collectors.toList());
    }

    /** Prepares data for adding new relationship(s) */
    public void prepareAddRelationshipPopup() {
        Preconditions.checkNotNull(hierarchiesController.getSelectedNodeSlot());
        Preconditions.checkState(hierarchiesController.isSingleNodeSelected());

        containsTree.unselectAllTreeNodes();
        editExistingRelationship = false;
        // clear the previous dialog selection in case the dialog was already used before
        editedRelationshipView = new SlotRelationshipView(null, hierarchiesController.getSelectedNodeSlot());
        editedRelationshipView.setRelationshipName(SlotRelationName.CONTAINS.toString());

        // modify relationship types drop down menu
        if (hierarchiesController.getSelectedNodeSlot().isHostingSlot()) {
            relationshipTypesForDialog = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());

        } else {
            relationshipTypesForDialog = ImmutableList.of(SlotRelationName.CONTAINS.toString(),
                    SlotRelationName.CONTAINS.inverseName());
        }
    }

    /**
     * Called when user clicks add button to add new relationship. Relationship is added if this does not
     * cause a loop on CONTAINS relationships
     */
    public void onRelationshipAdd() {
        try {
            if (editExistingRelationship) {
                editExistingRelationship();
            } else {
                addNewRelationships();
            }
        } finally {
            onRelationshipPopupClose();
        }
    }

    private void editExistingRelationship() {
        Preconditions.checkNotNull(editedRelationshipView.getSlotPair());
        final SlotRelation slotRelation =
                slotRelationBySlotRelationStringName.get(editedRelationshipView.getRelationshipName());
        final Slot parentSlot;
        final Slot childSlot;
        if (slotRelation.getNameAsString().equals(editedRelationshipView.getRelationshipName())) {
            childSlot =  editedRelationshipView.getTargetNode().getData().getSlot();
            parentSlot = editedRelationshipView.getSourceSlot();
        } else {
            childSlot = editedRelationshipView.getSourceSlot();
            parentSlot = editedRelationshipView.getTargetNode().getData().getSlot();
        }

        if (childSlot.equals(parentSlot)) {
            UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                    "The installation slot cannot be in relationship with itself.");
            return;
        }

        final SlotPair editedSlotPair = editedRelationshipView.getSlotPair();
        if (editedSlotPair.getChildSlot().equals(childSlot) &&
                editedSlotPair.getParentSlot().equals(parentSlot) &&
                editedSlotPair.getSlotRelation().equals(slotRelation)) {
            // nothing to do, relationship not modified
            return;
        }

        if (!slotPairEJB.findSlotPairsByParentChildRelation(childSlot.getName(), parentSlot.getName(),
                slotRelation.getName()).isEmpty()) {
            UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                    "This relationship already exists.");  // XXX why is this message not show?!
            return;
        }

        editedSlotPair.setChildSlot(childSlot);
        editedSlotPair.setParentSlot(parentSlot);
        editedSlotPair.setSlotRelation(slotRelation);

        if (slotPairEJB.slotPairCreatesLoop(editedSlotPair, childSlot)) {
            RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show();");
            return;
        }

        slotPairEJB.save(editedSlotPair);
        relationships.remove(selectedRelationships.get(0));
        relationships.add(new SlotRelationshipView(slotPairEJB.refreshEntity(editedSlotPair),
                                                selectedRelationships.get(0).getSourceSlot()));
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                "Relationship modified.");
        selectedRelationships = null;

        final boolean isContainsAdded = (slotRelation.getName() == SlotRelationName.CONTAINS);
        hierarchiesController.refreshTrees(new HashSet<>(Arrays.asList(childSlot.getId(), parentSlot.getId())));

        if (isContainsAdded && (parentSlot == hierarchiesController.getSelectedNodeSlot())) {
            hierarchiesController.expandFirstSelectedNode();
        }
    }

    private void addNewRelationships() {
        // we will create new entities in memory and check all of them. Then we will add them in a single transaction
        Preconditions.checkArgument(!containsTree.getSelectedNodes().isEmpty());
        final SlotRelation slotRelation =
                slotRelationBySlotRelationStringName.get(editedRelationshipView.getRelationshipName());
        final boolean isForwardRelation = slotRelation.getNameAsString().equals(editedRelationshipView.getRelationshipName());

        final Slot sourceSlot = hierarchiesController.getSelectedNodeSlot();

        final List<SlotPair> newRelationships = Lists.newArrayList();
        final HashSet<Long> refreshList = new HashSet<Long>();
        refreshList.add(editedRelationshipView.getSourceSlot().getId());

        for (final TreeNode selectedNode : containsTree.getSelectedNodes()) {
            final Slot parentSlot;
            final Slot childSlot;
            if (isForwardRelation) {
                childSlot = ((SlotView) selectedNode.getData()).getSlot();
                parentSlot = sourceSlot;
                refreshList.add(childSlot.getId());
            } else {
                childSlot = sourceSlot;
                parentSlot = ((SlotView) selectedNode.getData()).getSlot();
                refreshList.add(parentSlot.getId());
            }

            if (childSlot.equals(parentSlot)) {
                UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                        "The installation slot cannot be in relationship with itself.");
                return;
            }

            if (!slotPairEJB.findSlotPairsByParentChildRelation(childSlot.getName(), parentSlot.getName(),
                    slotRelation.getName()).isEmpty()) {
                UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                        "This relationship already exists.");  // XXX why is this message not show?!
                return;
            }
            final SlotPair newSlotPair = new SlotPair();

            newSlotPair.setChildSlot(childSlot);
            newSlotPair.setParentSlot(parentSlot);
            newSlotPair.setSlotRelation(slotRelation);

            if (slotPairEJB.slotPairCreatesLoop(newSlotPair, childSlot)) {
                RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show();");
                return;
               }

            newRelationships.add(newSlotPair);
        }
        slotPairEJB.add(newRelationships);

        relationships.addAll(newRelationships.stream().map(e -> new SlotRelationshipView(e, sourceSlot)).
                collect(Collectors.toList()));
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                "Added " + newRelationships.size() + " relationships.");

        hierarchiesController.refreshTrees(refreshList);

        // expand tree if a single node gained new CONTAINS child(ren)
        final boolean isContainsAdded = (slotRelation.getName() == SlotRelationName.CONTAINS);
        if (isContainsAdded && (newRelationships.size() == 1) && isForwardRelation) {
            hierarchiesController.expandFirstSelectedNode();
        }

        containsTree.unselectAllTreeNodes();
    }

    /** Expands the selected node or entire tree */
    public void expandTreeNodes() {
        if (editExistingRelationship) {
            final FilteredTreeNode<SlotView> selectedNode = editedRelationshipView.getTargetNode();
            if (selectedNode == null) {
                expandOrCollapseNode(containsTree.getRootNode(), true);
            } else {
                expandOrCollapseNode(selectedNode, true);
            }
        } else if (!Utility.isNullOrEmpty(containsTree.getSelectedNodes())) {
            for (final FilteredTreeNode<SlotView> selectedSlotView : containsTree.getSelectedNodes()) {
                expandOrCollapseNode(selectedSlotView, true);
            }
        } else {
            expandOrCollapseNode(containsTree.getRootNode(), true);
        }
    }

    /** Collapses the selected node or entire tree */
    public void collapseTreeNodes() {
        if (editExistingRelationship) {
            final FilteredTreeNode<SlotView> selectedNode = editedRelationshipView.getTargetNode();
            if (selectedNode == null) {
                expandOrCollapseNode(containsTree.getRootNode(), false);
            } else {
                expandOrCollapseNode(selectedNode, false);
            }
        } else if (!Utility.isNullOrEmpty(containsTree.getSelectedNodes())) {
            for (final FilteredTreeNode<SlotView> selectedSlotView : containsTree.getSelectedNodes()) {
                expandOrCollapseNode(selectedSlotView, false);
            }
        } else {
            expandOrCollapseNode(containsTree.getRootNode(), false);
        }
    }

    private void expandOrCollapseNode(final FilteredTreeNode<SlotView> parent, final boolean expand) {
        parent.setExpanded(expand);
        for (final  FilteredTreeNode<SlotView> node : parent.getFilteredChildren()) {
            expandOrCollapseNode(node, expand);
        }
    }

    /** @return The list of relationships for the currently selected slot. */
    public List<SlotRelationshipView> getRelationships() {
        return relationships;
    }
    public void setRelationships(List<SlotRelationshipView> relationships) {
        this.relationships = relationships;
    }

    /** @return the {@link List} of relationship types to display in the filter drop down selection. */
    public List<SelectItem> getRelationshipTypes() {
        return relationshipTypes;
    }

    /** @return the filteredRelationships */
    public List<SlotRelationshipView> getFilteredRelationships() {
        return filteredRelationships;
    }
    /** @param filteredRelationships the filteredRelationships to set */
    public void setFilteredRelationships(List<SlotRelationshipView> filteredRelationships) {
        this.filteredRelationships = filteredRelationships;
    }

    /** @return the selectedRelationships */
    public List<SlotRelationshipView> getSelectedRelationships() {
        return selectedRelationships;
    }
    /** @param selectedRelationships the selectedRelationships to set */
    public void setSelectedRelationships(List<SlotRelationshipView> selectedRelationships) {
        this.selectedRelationships = selectedRelationships;
    }

    /** @return the relationshipTypesForDialog */
    public List<String> getRelationshipTypesForDialog() {
        return relationshipTypesForDialog;
    }
    /** @param relationshipTypesForDialog the relationshipTypesForDialog to set */
    public void setRelationshipTypesForDialog(List<String> relationshipTypesForDialog) {
        this.relationshipTypesForDialog = relationshipTypesForDialog;
    }

    /** @return the editedRelationshipView */
    public SlotRelationshipView getEditedRelationshipView() {
        return editedRelationshipView;
    }

    /**@return the contains tree */
	public SlotRelationshipTree getContainsTree() {
		return containsTree;
	}

	/** @return is existing relationship being edited */
	public boolean isEditExistingRelationship() {
	    return editExistingRelationship;
	}
}
