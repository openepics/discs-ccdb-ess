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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.common.AbstractSlotsController;
import org.openepics.discs.conf.util.names.Names;

import com.google.common.collect.ImmutableList;

@Named
@ViewScoped
public class InstallationSlotsController extends AbstractSlotsController {

    @Inject private Names names;

    private ComponentType deviceType;
    private Double beamlinePosition;
    private Double globalX;
    private Double globalY;
    private Double globalZ;
    private Double globalPitch;
    private Double globalRoll;
    private Double globalYaw;

    List<String> namesForAutoComplete;

    @PostConstruct
    public void init() {
        updateRootNode();
        fillNamesAutocomplete();
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

    @Override
    public String redirectToAttributes(Long id) { return "installation-slot-attributes-manager.xhtml?faces-redirect=true&id=" + id; }

    private void fillNamesAutocomplete() {
        namesForAutoComplete = ImmutableList.copyOf(names.getAllNames());
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

}
