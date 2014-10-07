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
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class HierarchiesController implements Serializable {
    private static final Logger logger = Logger.getLogger(HierarchiesController.class.getCanonicalName());

    @Inject private SlotsTreeBuilder slotsTreeBuilder;
    @Inject private SlotEJB slotEJB;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private InstallationEJB installationEJB;

    private TreeNode rootNode;
    private TreeNode selectedNode;
    private InstallationRecord installationRecord;
    private Device deviceToInstall;
    private Slot selectedSlot;

    @PostConstruct
    public void init() {
        rootNode = slotsTreeBuilder.newSlotsTree(slotEJB.findAll(), null, true);
    }

    /**
     * @return The list of attributes (property values, artifacts and tags) for at all levels:
     * <ul>
     * <li>device type properties</li>
     * <li>container or installation slot properties</li>
     * <li>device instance properties (for installation slots and if device is installed)</li>
     * </ul>
     */
    public List<EntityAttributeView> getAttributes() {
        final List<EntityAttributeView> attributesList = new ArrayList<>();

        if (selectedNode != null) {
            final String slotType = selectedSlot.isHostingSlot() ? "Installation slot" : "Container";

            for (ComptypePropertyValue value : selectedSlot.getComponentType().getComptypePropertyList()) {
                attributesList.add(new EntityAttributeView(value, "Type property"));
            }

            for (SlotPropertyValue value : selectedSlot.getSlotPropertyList()) {
                attributesList.add(new EntityAttributeView(value, slotType + " property"));
            }

            if (installationRecord != null) {
                for (DevicePropertyValue value : installationRecord.getDevice().getDevicePropertyList()) {
                    attributesList.add(new EntityAttributeView(value, "Device property"));
                }
            }

            for (ComptypeArtifact artifact : selectedSlot.getComponentType().getComptypeArtifactList()) {
                attributesList.add(new EntityAttributeView(artifact, "Type artifact"));
            }

            for (SlotArtifact artifact : selectedSlot.getSlotArtifactList()) {
                attributesList.add(new EntityAttributeView(artifact, slotType + " artifact"));
            }

            if (installationRecord != null) {
                for (DeviceArtifact artifact : installationRecord.getDevice().getDeviceArtifactList()) {
                    attributesList.add(new EntityAttributeView(artifact, "Device artifact"));
                }
            }

            for (Tag tag : selectedSlot.getComponentType().getTags()) {
                attributesList.add(new EntityAttributeView(tag, "Type tag"));
            }

            for (Tag tag : selectedSlot.getTags()) {
                attributesList.add(new EntityAttributeView(tag, slotType + " tag"));
            }

            if (installationRecord != null) {
                for (Tag tag : installationRecord.getDevice().getTags()) {
                    attributesList.add(new EntityAttributeView(tag, "Device tag"));
                }
            }
        }
        return attributesList;
    }

    /**
     * @return The slot (container or installation slot) that is currently selected in the tree.
     */
    public Slot getSelectedNodeSlot() {
        return selectedSlot;
    }

    /**
     * @return The list of relationships for the currently selected slot.
     */
    public List<SlotRelationshipView> getRelationships() {
        final List<SlotRelationshipView> relationships = new ArrayList<>();
        if (selectedNode != null) {
            final List<SlotPair> slotPairs = slotPairEJB.getSlotRleations(selectedSlot);

            for (SlotPair slotPair : slotPairs) {
                relationships.add(new SlotRelationshipView(slotPair, selectedSlot));
            }
        }
        return relationships;
    }

    /**
     * @return The root node of (and consequently the entire) hierarchy tree.
     */
    public TreeNode getRootNode() {
        return rootNode;
    }

    /**
     * @return Getter for the currently selected node in a tree (required by PrimeFaces).
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /** With this method PrimeFaces sets the currently selected node. The method has two side effects:
     * <ul>
     * <li>it also sets the container or installation slot associated with the currently selected tree node</li>
     * <li>it also searches id there is an installation record associated with the currently selected tree node</li>
     * </ul>
     * @param selectedNode The PrimeFaces tree node to that is selected.
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
        this.selectedSlot = selectedNode == null ? null : ((SlotView)selectedNode.getData()).getSlot();
        this.installationRecord = selectedNode == null ? null
                                    : installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
    }

    /**
     * @return The device that is installed in the currently selected slot, <code>null</code> if no device is installed
     * or this is a <i>container</i> or no node is selected.
     */
    public Device getInstalledDevice() {
        return installationRecord == null ? null : installationRecord.getDevice();
    }


    /**
     * @return Based on the device type of the currently selected slot, this returns a list of all appropriate device
     * instances that are not installed.
     */
    public List<Device> getUninstalledDevices() {
        if (selectedSlot == null || !selectedSlot.isHostingSlot()) {
            return new ArrayList<Device>();
        }
        return installationEJB.getUninstalledDevices(selectedSlot.getComponentType());
    }

    /**
     * @return the deviceToInstall
     */
    public Device getDeviceToInstall() {
        return deviceToInstall;
    }

    /**
     * @param deviceToInstall the deviceToInstall to set
     */
    public void setDeviceToInstall(Device deviceToInstall) {
        this.deviceToInstall = deviceToInstall;
    }

    /**
     * This method creates a new installation record for the device selected in the installation dialog. Before that
     * it checks whether a device slot and device are both selected, and that both are also uninstalled at the moment.
     * This method creates a new installation record containing:
     * <ul>
     * <li>record number</li>
     * <li>install date</li>
     * <li>device</li>
     * <li>installation slot</li>
     * </ul>
     * The uninstall date is left empty (NULL).
     */
    public void installDevice() {
        Preconditions.checkNotNull(deviceToInstall);
        Preconditions.checkNotNull(selectedSlot);
        if (!selectedSlot.getComponentType().equals(deviceToInstall.getComponentType())) {
            logger.log(Level.WARNING, "The device and installation slot device types do not match.");
            throw new RuntimeException("The device and installation slot device types do not match.");
        }
        // we must check whether the selected slot is already occupied or selected device is already installed
        final InstallationRecord slotCheck = installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
        if (slotCheck != null) {
            logger.log(Level.WARNING, "An attempt was made to install a device in an already occupied slot.");
            throw new RuntimeException("Slot already occupied.");
        }
        final InstallationRecord deviceCheck = installationEJB.getActiveInstallationRecordForDevice(deviceToInstall);
        if (deviceCheck != null) {
            logger.log(Level.WARNING, "An attempt was made to install a device that is already installed.");
            throw new RuntimeException("Device already installed.");
        }

        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        newRecord.setDevice(deviceToInstall);
        newRecord.setSlot(selectedSlot);
        installationEJB.add(newRecord);

        deviceToInstall = null;
        installationRecord = installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
    }
}