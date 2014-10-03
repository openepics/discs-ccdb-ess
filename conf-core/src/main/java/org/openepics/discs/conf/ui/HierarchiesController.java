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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class HierarchiesController implements Serializable {

    @Inject private SlotsTreeBuilder slotsTreeBuilder;
    @Inject private SlotEJB slotEJB;
    @Inject private SlotPairEJB slotPairEJB;

    private TreeNode rootNode;
    private TreeNode selectedNode;

    @PostConstruct
    public void init() {
        rootNode = slotsTreeBuilder.newSlotsTree(slotEJB.findAll(), null, true);
    }

    public List<EntityAttributeView> getAttributes() {
        final List<EntityAttributeView> attributesList = new ArrayList<>();

        if (selectedNode != null) {
            final Slot selectedSlot = ((SlotView)selectedNode.getData()).getSlot();
            final String slotType = selectedSlot.isHostingSlot() ? "Installation slot" : "Container";

            for (ComptypePropertyValue value : selectedSlot.getComponentType().getComptypePropertyList()) {
                attributesList.add(new EntityAttributeView(value, "Type property"));
            }

            for (SlotPropertyValue value : selectedSlot.getSlotPropertyList()) {
                attributesList.add(new EntityAttributeView(value, slotType + " property"));
            }

            for (ComptypeArtifact artifact : selectedSlot.getComponentType().getComptypeArtifactList()) {
                attributesList.add(new EntityAttributeView(artifact, "Type artifact"));
            }

            for (SlotArtifact artifact : selectedSlot.getSlotArtifactList()) {
                attributesList.add(new EntityAttributeView(artifact, slotType + " artifact"));
            }

            for (Tag tag : selectedSlot.getComponentType().getTags()) {
                attributesList.add(new EntityAttributeView(tag, "Type tag"));
            }

            for (Tag tag : selectedSlot.getTags()) {
                attributesList.add(new EntityAttributeView(tag, slotType + " tag"));
            }
        }
        return attributesList;
    }

    public List<SlotRelationshipView> getRelationships() {
        final List<SlotRelationshipView> relationships = new ArrayList<>();
        if (selectedNode != null) {
            final Slot selectedSlot = ((SlotView)selectedNode.getData()).getSlot();
            final List<SlotPair> slotPairs = slotPairEJB.getSlotRleations(selectedSlot);

            for (SlotPair slotPair : slotPairs) {
                relationships.add(new SlotRelationshipView(slotPair, selectedSlot));
            }
        }
        return relationships;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
}
