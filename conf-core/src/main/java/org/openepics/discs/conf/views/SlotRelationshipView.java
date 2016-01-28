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
package org.openepics.discs.conf.views;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ui.trees.FilteredTreeNode;
import org.openepics.discs.conf.util.UnhandledCaseException;

import com.google.common.base.Objects;

/**
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class SlotRelationshipView {

    private final String id;
    private final Slot sourceSlot;
    private final String sourceSlotName;
    private  Slot targetSlot;
    private String targetSlotName;
    private String relationshipName;
    private final SlotPair slotPair;
    private final String usedBy = "";
    private FilteredTreeNode<SlotView> targetNode;

    /** Constructs a new slot pair UI view object for the selected {@link Slot} object. The method checks that the
     * <code>selectedSlot</code> is either a parent or a child in the <code>slotPair</code> relationship object.
     * @param slotPair a {@link SlotPair} object defining a relationship
     * @param selectedSlot the selected {@link Slot}
     * @throws UnhandledCaseException if the <code>selectedSlot</code> is neither a parent nor a child of the
     * <code>slotPair</code> object.
     */
    public SlotRelationshipView(SlotPair slotPair, Slot selectedSlot) {
        sourceSlot = selectedSlot;
        sourceSlotName = selectedSlot.getName();
        this.slotPair = slotPair;
        if (slotPair == null) {
            id = null;
            targetSlot = null;
            targetSlotName = null;
            return;
        }
        if (Objects.equal(slotPair.getChildSlot(), selectedSlot)) {
            relationshipName = slotPair.getSlotRelation().getIname();
            targetSlot = slotPair.getParentSlot();
            targetSlotName = slotPair.getParentSlot().getName();
        } else if (Objects.equal(slotPair.getParentSlot(), selectedSlot)) {
            relationshipName = slotPair.getSlotRelation().getNameAsString();
            targetSlot = slotPair.getChildSlot();
            targetSlotName = slotPair.getChildSlot().getName();
        } else {
            throw new UnhandledCaseException();
        }

        this.id = slotPair.getId().toString();
    }

    /** Constructs a SlotRelationshipView
     * @param id the unique identifier for this relationship (not DB id)
     * @param sourceSlot source {@link Slot}
     * @param targetSlot target {@link Slot}
     * @param relationshipName the name of the Relationship
     */
    public SlotRelationshipView(String id, Slot sourceSlot, Slot targetSlot, String relationshipName) {
        this.sourceSlot = sourceSlot;
        this.sourceSlotName = sourceSlot.getName();
        this.targetSlotName = targetSlot.getName();
        this.targetSlot = targetSlot;
        this.relationshipName = relationshipName;
        slotPair = null;
        this.id = id;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getTargetSlotName() {
        return targetSlotName;
    }

    public Slot getSourceSlot() {
        return sourceSlot;
    }

    public Slot getTargetSlot() {
        return targetSlot;
    }

    public FilteredTreeNode<SlotView> getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(FilteredTreeNode<SlotView> targetNode) {
        if (targetNode != null) {
            this.targetNode = targetNode;
            targetSlot = targetNode.getData().getSlot();
            targetSlotName = targetSlot.getName();
        }
    }

    public SlotPair getSlotPair() {
        return slotPair;
    }

    public String getSourceSlotName() {
        return sourceSlotName;
    }

    public String getId() {
        return id;
    }

    /**
     * @return the usedBy
     */
    public String getUsedBy() {
        return usedBy;
    }
}
