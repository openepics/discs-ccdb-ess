/**
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
import org.openepics.discs.conf.util.SlotPairLoopException;
import org.openepics.discs.conf.util.names.Names;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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


    @PostConstruct
    public void init() {
        updateRootNode();
        fillNamesAutocomplete();

        final List<SlotRelation> slotRelations = slotRelationEJB.findAll();
        slotRelationBySlotRelationStringName = new HashMap<>();
        for (SlotRelation slotRelation : slotRelations) {
            slotRelationBySlotRelationStringName.put(slotRelation.getNameAsString(), slotRelation);
            slotRelationBySlotRelationStringName.put(slotRelation.getIname(), slotRelation);
        }
        relationshipTypes = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
    }

    @Override
    protected void updateRootNode() {
        rootNode = slotsTreeBuilder.newSlotsTree(slotEJB.findAll(), collapsedNodes, true);
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
        slotEJB.add(newSlot);
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

    public void setSelectedSlotViewForRelationships(SlotView selectedSlotView) {
        this.selectedSlotView = selectedSlotView;
        prepareRelationshipsPopup();
    }
    public SlotView getSelectedSlotViewForRelationships() { return selectedSlotView; }

    private void prepareRelationshipsPopup() {
        selectedRelationship = null;
        relationships = Lists.newArrayList();
        for (SlotPair slotPair : selectedSlotView.getSlot().getChildrenSlotsPairList()) {
            relationships.add(new SlotRelationshipView(slotPair, selectedSlotView.getSlot()));
        }

        for (SlotPair slotPair : selectedSlotView.getSlot().getParentSlotsPairList()) {
            relationships.add(new SlotRelationshipView(slotPair, selectedSlotView.getSlot()));
        }
    }

    public void onRelationshipDelete() {
        if (canRelationshipBeDeleted()) {
            slotPairEJB.delete(selectedRelationship.getSlotPair());
        } else {
            RequestContext.getCurrentInstance().execute("PF('cantDeleteRelation').show()");
        }
        prepareRelationshipsPopup();
    }

    private boolean canRelationshipBeDeleted() {
        return !(selectedRelationship.getSlotPair().getSlotRelation().getName() == SlotRelationName.CONTAINS && !slotPairEJB.slotHasMoreThanOneContainsRelation(selectedRelationship.getSlotPair().getChildSlot()));
    }

    public void prepareAddRelationshipPopup() {
        selectedTreeNodeForRelationshipAdd = null;
        selectedRelationshipType = SlotRelationName.CONTAINS.toString().toLowerCase();
    }

    public void slotForRelationshipChanged() {
        if (((SlotView)selectedTreeNodeForRelationshipAdd.getData()).getIsHostingSlot()) {
            relationshipTypes = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
            selectedRelationshipType = SlotRelationName.CONTAINS.toString().toLowerCase();
        } else {
            relationshipTypes = ImmutableList.of("contained in");
            selectedRelationshipType = "contained in";
        }

        prepareRelationshipsPopup();
    }

    public void onRelationshipAdd() {
        final SlotRelation slotRelation = slotRelationBySlotRelationStringName.get(selectedRelationshipType);
        final Slot parentSlot;
        final Slot childSlot;
        if (slotRelation.getNameAsString().equals(selectedRelationshipType)) {
            childSlot = ((SlotView) selectedTreeNodeForRelationshipAdd.getData()).getSlot();
            parentSlot = selectedSlotView.getSlot();
        } else {
            childSlot = selectedSlotView.getSlot();
            parentSlot = ((SlotView) selectedTreeNodeForRelationshipAdd.getData()).getSlot();
        }

        try {
            slotPairEJB.add(new SlotPair(childSlot, parentSlot, slotRelation));
        } catch (SlotPairLoopException e) {

                RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show()");

        }
        prepareRelationshipsPopup();
    }

    @Override
    public String redirectToAttributes(Long id) { return "installation-slot-attributes-manager.xhtml?faces-redirect=true&id=" + id; }

    private void fillNamesAutocomplete() {
        namesForAutoComplete = ImmutableList.copyOf(names.getAllNames());
    }

    public List<SlotRelationshipView> getRelationships() {
        return relationships;
    }

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

    public List<String> nameAutocompleteText(String query) {
        final List<String> resultList = new ArrayList<String>();
        final String queryUpperCase = query.toUpperCase();
        for (String element : namesForAutoComplete) {
            if (element.toUpperCase().startsWith(queryUpperCase))
                resultList.add(element);
        }

        return resultList;
    }

    public SlotRelationshipView getSelectedRelationship() { return selectedRelationship; }
    public void setSelectedRelationship(SlotRelationshipView selectedRelationship) { this.selectedRelationship = selectedRelationship; }

    public void setSelectedSlotViewForRelationshipAdd(TreeNode selectedTreeNode) { selectedTreeNodeForRelationshipAdd = selectedTreeNode; }
    public TreeNode getSelectedSlotViewForRelationshipAdd() { return selectedTreeNodeForRelationshipAdd; }

    public String getSelectedRelationshipType() { return selectedRelationshipType; }
    public void setSelectedRelationshipType(String relationshipType) { this.selectedRelationshipType = relationshipType; }

    public List<String> getRelationshipTypes() { return relationshipTypes; }



}
