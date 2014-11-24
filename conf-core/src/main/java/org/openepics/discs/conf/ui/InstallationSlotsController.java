/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ui.common.AbstractSlotsController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.names.Names;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class InstallationSlotsController extends AbstractSlotsController {

    @Inject private Names names;
    @Inject private SlotRelationEJB slotRelationEJB;

    private ComponentType deviceType;
    private Double beamlinePosition;
    private Double globalX;
    private Double globalY;
    private Double globalZ;
    private Double globalPitch;
    private Double globalRoll;
    private Double globalYaw;

    private List<String> namesForAutoComplete;
    private List<SlotRelationshipView> relationships;
    private SlotRelationshipView selectedRelationship;
    private TreeNode selectedTreeNodeForRelationshipAdd;
    private String selectedRelationshipType;
    private List<String> relationshipTypes;
    private Map<String, SlotRelation> slotRelationBySlotRelationStringName;
    private SlotView selectedSlotForRelationships;

    @PostConstruct
    public void init() {
        try {
            updateRootNode();
            fillNamesAutocomplete();

            final List<SlotRelation> slotRelations = slotRelationEJB.findAll();
            slotRelationBySlotRelationStringName = new HashMap<>();
            for (SlotRelation slotRelation : slotRelations) {
                slotRelationBySlotRelationStringName.put(slotRelation.getNameAsString(), slotRelation);
                slotRelationBySlotRelationStringName.put(slotRelation.getIname(), slotRelation);
            }
            relationshipTypes = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
        } catch(Exception e) {
            throw new UIException("Installation slot display initialization fialed: " + e.getMessage(), e);
        }
    }

    @Override
    protected void updateRootNode() {
        final Slot selectedSlot;
        if (parentSlotView != null) {
            selectedSlot = parentSlotView.getSlot();
        } else {
            selectedSlot = null;
        }

        rootNode = slotsTreeBuilder.newSlotsTree(slotEJB.findAll(), selectedSlot, collapsedNodes, true);
        selectedNode = slotsTreeBuilder.getInitiallySelectedTreeNode();
        parentSlotView = slotsTreeBuilder.getInitiallySelectedSlotView();
    }

    @Override
    public void prepareAddPopup() {
        super.prepareAddPopup();
        deviceType = null;
        beamlinePosition = null;
        globalX = null;
        globalY = null;
        globalZ = null;
        globalPitch = null;
        globalRoll = null;
        globalYaw = null;
    }

    @Override
    public void onSlotAdd() {
        newSlot = new Slot(name, true);
        newSlot.setDescription(description);
        newSlot.setComponentType(deviceType);
        newSlot.setBeamlinePosition(beamlinePosition);
        newSlot.getPositionInformation().setGlobalX(globalX);
        newSlot.getPositionInformation().setGlobalY(globalY);
        newSlot.getPositionInformation().setGlobalZ(globalZ);
        newSlot.getPositionInformation().setGlobalPitch(globalPitch);
        newSlot.getPositionInformation().setGlobalRoll(globalRoll);
        newSlot.getPositionInformation().setGlobalYaw(globalYaw);
        super.onSlotAdd();
    }

    @Override
    protected void prepareModifyPopup() {
        super.prepareModifyPopup();
        deviceType = selectedSlotView.getDeviceType();
        beamlinePosition = selectedSlotView.getBeamlinePosition();
        globalX = selectedSlotView.getGlobalX();
        globalY = selectedSlotView.getGlobalY();
        globalZ = selectedSlotView.getGlobalZ();
        globalPitch = selectedSlotView.getGlobalPitch();
        globalRoll = selectedSlotView.getGlobalRoll();
        globalYaw = selectedSlotView.getGlobalYaw();
    }

    @Override
    public void onSlotModify() {
        final Slot slotToModify = selectedSlotView.getSlot();
        slotToModify.setName(name);
        slotToModify.setDescription(description);
        slotToModify.setComponentType(deviceType);
        slotToModify.setBeamlinePosition(beamlinePosition);
        slotToModify.getPositionInformation().setGlobalX(globalX);
        slotToModify.getPositionInformation().setGlobalY(globalY);
        slotToModify.getPositionInformation().setGlobalZ(globalZ);
        slotToModify.getPositionInformation().setGlobalPitch(globalPitch);
        slotToModify.getPositionInformation().setGlobalRoll(globalRoll);
        slotToModify.getPositionInformation().setGlobalYaw(globalYaw);
        slotEJB.save(slotToModify);

        updateRootNode();
    }

    private void prepareRelationshipsPopup() {
        Preconditions.checkNotNull(selectedSlotForRelationships);
        selectedRelationship = null;
        final Slot freshSlot = slotEJB.findById(selectedSlotForRelationships.getSlot().getId());
        relationships = Lists.newArrayList();
        for (SlotPair slotPair : freshSlot.getChildrenSlotsPairList()) {
            relationships.add(new SlotRelationshipView(slotPair, selectedSlotForRelationships.getSlot()));
        }

        for (SlotPair slotPair : freshSlot.getParentSlotsPairList()) {
            relationships.add(new SlotRelationshipView(slotPair, selectedSlotForRelationships.getSlot()));
        }
    }

    /**
     * This method rebuilds the tree so that all displayed slots are refreshed. This is important for refreshing the
     * relationship status of all displayed slots. Otherwise the information can get out of synch with the database.
     */
    public void onRelationshipPopupClose() {
        if (selectedNode != null) {
            selectedNode.setSelected(true);
        }
        updateRootNode();
    }

    /**
     * Called when button to delete relationship is clicked
     */
    public void onRelationshipDelete() {
        if (canRelationshipBeDeleted()) {
            slotPairEJB.delete(selectedRelationship.getSlotPair());
        } else {
            RequestContext.getCurrentInstance().execute("PF('cantDeleteRelation').show()");
        }
        prepareRelationshipsPopup();
    }

    private boolean canRelationshipBeDeleted() {
        return !(selectedRelationship.getSlotPair().getSlotRelation().getName() == SlotRelationName.CONTAINS
                && !slotPairEJB.slotHasMoreThanOneContainsRelation(selectedRelationship.getSlotPair().getChildSlot()));
    }

    /**
     * Prepares data for adding new relationship
     */
    public void prepareAddRelationshipPopup() {
        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        if (selectedTreeNodeForRelationshipAdd != null) {
            selectedTreeNodeForRelationshipAdd.setSelected(false);
        }
        selectedTreeNodeForRelationshipAdd = null;
        selectedRelationshipType = SlotRelationName.CONTAINS.toString().toLowerCase();
    }

    /**
     * Called when slot to be in relationship selected from tree of installation slots is changed.
     * This method is needed to modify relationship types drop down menu so that if user selects
     * container slot the only relationship that can be created is "contained in".
     */
    public void slotForRelationshipChanged() {
        if (((SlotView)selectedTreeNodeForRelationshipAdd.getData()).getIsHostingSlot()) {
            relationshipTypes = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
            if (selectedRelationshipType == null) {
                selectedRelationshipType = SlotRelationName.CONTAINS.toString().toLowerCase();
            }
        } else {
            relationshipTypes = ImmutableList.of("contained in");
            selectedRelationshipType = "contained in";
        }
    }

    /**
     * Called when user clicks add button to add new relationship. Relationship is added if this does not
     * cause a loop on CONTAINS relationships
     */
    public void onRelationshipAdd() {
        final SlotRelation slotRelation = slotRelationBySlotRelationStringName.get(selectedRelationshipType);
        final Slot parentSlot;
        final Slot childSlot;
        if (slotRelation.getNameAsString().equals(selectedRelationshipType)) {
            childSlot = ((SlotView) selectedTreeNodeForRelationshipAdd.getData()).getSlot();
            parentSlot = selectedSlotForRelationships.getSlot();
        } else {
            childSlot = selectedSlotForRelationships.getSlot();
            parentSlot = ((SlotView) selectedTreeNodeForRelationshipAdd.getData()).getSlot();
        }
        // TODO do not create the same relationship twice
        final SlotPair newSlotPair = new SlotPair(childSlot, parentSlot, slotRelation);
        if (!slotPairEJB.slotPairCreatesLoop(newSlotPair, childSlot)) {
            slotPairEJB.add(newSlotPair);
        } else {
            RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show()");
        }
        prepareRelationshipsPopup();
    }

    @Override
    public String redirectToAttributes(Long id) { return "installation-slot-attributes-manager.xhtml?faces-redirect=true&id=" + id; }

    private void fillNamesAutocomplete() {
        namesForAutoComplete = ImmutableList.copyOf(names.getAllNames());
    }

    public List<SlotRelationshipView> getRelationships() { return relationships; }

    public ComponentType getDeviceType() { return deviceType; }
    public void setDeviceType(ComponentType deviceType) { this.deviceType = deviceType; }

    public Double getBeamlinePosition() { return beamlinePosition; }
    public void setBeamlinePosition(Double beamlinePosition) { this.beamlinePosition = beamlinePosition; }

    public Double getGlobalX() { return globalX; }
    public void setGlobalX(Double globalX) { this.globalX = globalX; }

    public Double getGlobalY() { return globalY; }
    public void setGlobalY(Double globalY) { this.globalY = globalY; }

    public Double getGlobalZ() { return globalZ; }
    public void setGlobalZ(Double globalZ) { this.globalZ = globalZ; }

    public Double getGlobalPitch() { return globalPitch; }
    public void setGlobalPitch(Double globalPitch) { this.globalPitch = globalPitch; }

    public Double getGlobalRoll() { return globalRoll; }
    public void setGlobalRoll(Double globalRoll) { this.globalRoll = globalRoll; }

    public Double getGlobalYaw() { return globalYaw; }
    public void setGlobalYaw(Double globalYaw) { this.globalYaw = globalYaw; }

    /**
     * Helper method for auto complete when entering a name for new installation {@link Slot}.
     *
     * @param query Text that was entered so far
     * @return {@link List} of strings with suggestions
     */
    public List<String> nameAutocompleteText(String query) {
        final List<String> resultList = new ArrayList<>();
        final String queryUpperCase = query.toUpperCase();
        for (String element : namesForAutoComplete) {
            if (element.toUpperCase().startsWith(queryUpperCase))
                resultList.add(element);
        }

        return resultList;
    }

    /**
     * Sets {@link SlotView} on which button to present relationships was clicked
     *
     * @param selectedSlotView selected {@link SlotView}
     */
    public void setSelectedSlotViewForRelationships(SlotView selectedSlotView) {
        this.selectedSlotForRelationships = selectedSlotView;
        prepareRelationshipsPopup();
    }
    public SlotView getSelectedSlotViewForRelationships() { return selectedSlotForRelationships; }

    public SlotRelationshipView getSelectedRelationship() { return selectedRelationship; }
    public void setSelectedRelationship(SlotRelationshipView selectedRelationship) { this.selectedRelationship = selectedRelationship; }

    public void setSelectedSlotViewForRelationshipAdd(TreeNode selectedTreeNode) { selectedTreeNodeForRelationshipAdd = selectedTreeNode; }
    public TreeNode getSelectedSlotViewForRelationshipAdd() { return selectedTreeNodeForRelationshipAdd; }

    public String getSelectedRelationshipType() { return selectedRelationshipType; }
    public void setSelectedRelationshipType(String relationshipType) { this.selectedRelationshipType = relationshipType; }

    public List<String> getRelationshipTypes() { return relationshipTypes; }
}
