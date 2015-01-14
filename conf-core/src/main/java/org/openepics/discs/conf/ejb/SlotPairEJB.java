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
package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

import com.google.common.base.Preconditions;

/**
 * DAO Service for accessing slots in a relation {@link SlotPair}
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class SlotPairEJB extends DAO<SlotPair> {
    /**
     * Queries for a {@link SlotPair} given parent, child slot names and a relation type
     * @param childName child slot name (optional with wild card character)
     * @param parentName parent slot name
     * @param relationName relation type {@link SlotRelationName}
     * @return {@link List} of {@link SlotPair}s satisfying the query condition
     */
    public List<SlotPair> findSlotPairsByParentChildRelation(String childName,
            String parentName, SlotRelationName relationName) {
        return em.createNamedQuery("SlotPair.findByParentChildRelation", SlotPair.class)
                .setParameter("childName", childName)
                .setParameter("parentName", parentName).setParameter("relationName", relationName).getResultList();
    }

    /**
     * {@link DAO#findByName(String)} not applicable to {@link SlotPair}s
     *
     * Calling this method throws {@link UnsupportedOperationException}
     */
    @Override
    public SlotPair findByName(String name) {
        throw new UnsupportedOperationException("findByName method not aplicable to SlotPairEJB class");
    }

    /**
     * @param childSlot the {@link Slot} to check for
     * @return <code>true</code> if a slot is contained in more than one parent slot, <code>false</code> otherwise
     */
    public boolean slotHasMoreThanOneContainsRelation(Slot childSlot) {
        final List<SlotPair> slotPairs = em.createNamedQuery("SlotPair.findSlotPairsByChildAndRelation", SlotPair.class)
	            .setParameter("childSlot", childSlot)
	            .setParameter("relationName", SlotRelationName.CONTAINS)
	            .setMaxResults(2).getResultList();
        return slotPairs != null && slotPairs.size() == 2;
	}

    @Override
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void delete(SlotPair entity) {
        Preconditions.checkNotNull(entity);
        entity.getChildSlot().getPairsInWhichThisSlotIsAChildList().remove(entity);
        entity.getParentSlot().getPairsInWhichThisSlotIsAParentList().remove(entity);
        em.merge(entity.getChildSlot());
        em.merge(entity.getParentSlot());
        super.delete(entity);
	}


    @Override
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void add(SlotPair entity) {
        Preconditions.checkNotNull(entity);
        final Slot parentSlot = em.find( Slot.class, entity.getParentSlot().getId());
        final Slot childSlot = em.find(Slot.class, entity.getChildSlot().getId());
        entity.setParentSlot(parentSlot);
        entity.setChildSlot(childSlot);
        final int highestOrderNumberForNewPair;
        final Integer maxResult = em.createNamedQuery("SlotPair.findMaxPairOrder", Integer.class)
                .setParameter("parentSlot", parentSlot).getSingleResult();
        if (maxResult == null) {
            highestOrderNumberForNewPair = 1;
        } else {
            highestOrderNumberForNewPair = maxResult.intValue() + 1;
        }
        entity.setSlotOrder(highestOrderNumberForNewPair);
        childSlot.getPairsInWhichThisSlotIsAChildList().add(entity);
        parentSlot.getPairsInWhichThisSlotIsAParentList().add(entity);
        super.add(entity);
	}

    /**
     * Adds new slot pair but bypasses interceptors that create audit log and check
     * if user is authorized.
     *
     * @param entity
     */
    public void addWithoutInterceptors(SlotPair entity) {
        add(entity);
    }

    /**
     * Check if by adding new slot pair with {@link SlotRelationName} CONTAINS
     * a loop will be created.
     *
     * @param slotPair {@link SlotPair} that should be added
     * @param childSlot child {@link Slot} in this relationship
     * @return {@link Boolean} true if by adding this {@link SlotPair} loop will be created, false otherwise
     */
    public boolean slotPairCreatesLoop(SlotPair slotPair, Slot childSlot) {
        final boolean loopDetected;
        if (slotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
            if (slotPair.getParentSlot().equals(childSlot)) {
                loopDetected = true;
            } else {
                for (SlotPair parentSlotPair : slotPair.getParentSlot().getPairsInWhichThisSlotIsAChildList()) {
                    if (slotPairCreatesLoop(parentSlotPair, childSlot)) {
                        return true;
                    }
                }
                loopDetected = false;
            }
        } else {
            loopDetected = false;
        }
        return loopDetected;
    }

	/**
	 * @param slot - the slot to use in query.
	 * @return The list of all {@link SlotPair}s where the slot is either a parent or a child.
	 */
    public List<SlotPair> getSlotRleations(Slot slot) {
        return em.createNamedQuery("SlotPair.findSlotRelations", SlotPair.class).setParameter("slot", slot)
                .getResultList();
    }

    /** The method performs an order swap between the the current <code>slot</code> and the previous slot in the
     * context of the <code>parentSlot</code>. The swap is only possible for the <code>CONTAINS</code> relationship.
     * The previous slot is the one that has the largest order number that is still smaller than the current
     * <code>slot</code> order number. If there is no previous slot, the method doesn't do anything.
     * @param parentSlot the parent slot in context of which to perform the move
     * @param slot the slot to move "up"
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void moveUp(Slot parentSlot, Slot slot) {
        moveUpOrDown(parentSlot, slot, "SlotPair.findPrecedingPairs");
    }

    /** The method performs an order swap between the the current <code>slot</code> and the next slot in the
     * context of the <code>parentSlot</code>. The swap is only possible for the <code>CONTAINS</code> relationship.
     * The next slot is the one that has the smallest order number that is still larger than the <code>slot</code> order
     * number. If there is no next slot, the method doesn't do anything.
     * @param parentSlot the parent slot in context of which to perform the move
     * @param slot the slot to move "down"
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void moveDown(Slot parentSlot, Slot slot) {
        moveUpOrDown(parentSlot, slot, "SlotPair.findSucceedingPairs");
    }

    @Override
    protected Class<SlotPair> getEntityClass() {
        return SlotPair.class;
    }

    private void moveUpOrDown(Slot parentSlot, Slot slot, String queryName) {
        SlotPair mySlotPair = null;
        for (SlotPair pair : slot.getPairsInWhichThisSlotIsAChildList()) {
            if (pair.getParentSlot().equals(parentSlot) &&
                    pair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                // when moving the slot, this "pair" information gets stale very fast. We need to fetch from DB.
                mySlotPair = findById(pair.getId());
                break;
            }
        }
        if (mySlotPair == null) {
            throw new IllegalArgumentException("No CONTAINS relationship between parent ("
                    + parentSlot.getName() + ") and child (" + slot.getName() + ").");
        }

        // get all preceding/succeeding elements "conveniently" ordered in the right way
        List<SlotPair> relevantPairs = em.createNamedQuery(queryName, SlotPair.class)
                                            .setParameter("parentSlot", parentSlot)
                                            .setParameter("slotRelation", mySlotPair.getSlotRelation())
                                            .setParameter("order", mySlotPair.getSlotOrder())
                                            .getResultList();
        if (relevantPairs.isEmpty()) {
            return; // nothing to do
        }

        SlotPair swapPair = relevantPairs.get(0);

        int swapPairOrder = swapPair.getSlotOrder();
        int myPairOrder = mySlotPair.getSlotOrder();
        mySlotPair.setSlotOrder(-myPairOrder);
        swapPair.setSlotOrder(myPairOrder);
        em.flush();
        mySlotPair.setSlotOrder(swapPairOrder);
	}
}
