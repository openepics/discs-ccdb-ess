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
import org.openepics.discs.conf.util.UnhandledCaseException;

import com.google.common.base.Objects;

/**
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class SlotRelationshipView {

    private final String id;
    private final String sourceSlotName;
    private final String relationshipName;
    private final String targetSlotName;
    private final Slot targetSlot;
    private final SlotPair slotPair;

    /** Constructs a new slot pair UI view object for the selected {@link Slot} object. The method checks that the
     * <code>selectedSlot</code> is either a parent or a child in the <code>slotPair</code> relationship object.
     * @param slotPair a {@link SlotPair} object defining a relationship
     * @param selectedSlot the selected {@link Slot}
     * @throws UnhandledCaseException if the <code>selectedSlot</code> is neither a parent nor a child of the
     * <code>slotPair</code> object.
     */
    public SlotRelationshipView(SlotPair slotPair, Slot selectedSlot) {
        if (Objects.equal(slotPair.getChildSlot(), selectedSlot)) {
            relationshipName = slotPair.getSlotRelation().getIname();
            targetSlot = slotPair.getParentSlot();
            targetSlotName = slotPair.getParentSlot().getName();
            sourceSlotName = slotPair.getChildSlot().getName();
        } else if (Objects.equal(slotPair.getParentSlot(), selectedSlot)) {
            relationshipName = slotPair.getSlotRelation().getNameAsString();
            targetSlot = slotPair.getChildSlot();
            targetSlotName = slotPair.getChildSlot().getName();
            sourceSlotName = slotPair.getParentSlot().getName();
        } else {
            throw new UnhandledCaseException();
        }

        this.slotPair = slotPair;
        this.id = slotPair.getId().toString();
    }

    public SlotRelationshipView(String id, Slot sourceSlot, Slot targetSlot, String relationshipName) {
        this.sourceSlotName = sourceSlot.getName();
        this.targetSlotName = targetSlot.getName();
        this.targetSlot = targetSlot;
        this.relationshipName = relationshipName;
        slotPair = null;
        this.id = id;
    }

    public String getName() {
        return getTargetSlotName();
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public String getTargetSlotName() {
        return targetSlotName;
    }

    public Slot getSlot() {
        return targetSlot;
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
}
