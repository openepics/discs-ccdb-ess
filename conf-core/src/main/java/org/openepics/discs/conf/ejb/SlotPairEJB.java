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

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;

import com.google.common.base.Preconditions;

/**
 * DAO Service for accessing slots in a relation {@link SlotPair}
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class SlotPairEJB extends DAO<SlotPair> {
    @Override
    protected void defineEntity() {
        defineEntityClass(SlotPair.class);
    }

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

	public boolean slotHasMoreThanOneContainsRelation(Slot childSlot) {
	    final List<SlotPair> slotPairs = em.createNamedQuery("SlotPair.findSlotPairsByChildAndRelation", SlotPair.class)
	            .setParameter("childSlot", childSlot)
	            .setParameter("relationName", SlotRelationName.CONTAINS)
	            .setMaxResults(2).getResultList();
	    return slotPairs != null && slotPairs.size() == 2;
	}

	@Override
	public void delete(SlotPair entity) {
	    Preconditions.checkNotNull(entity);
        entity.getChildSlot().getChildrenSlotsPairList().remove(entity);
        entity.getParentSlot().getParentSlotsPairList().remove(entity);
        em.merge(entity.getChildSlot());
        em.merge(entity.getParentSlot());
        super.delete(entity);
	}


    @Override
    public void add(SlotPair entity) {
        Preconditions.checkNotNull(entity);
        final Slot parentSlot = em.merge(entity.getParentSlot());
        final Slot childSlot = em.merge(entity.getChildSlot());
        entity.setParentSlot(parentSlot);
        entity.setChildSlot(childSlot);
        childSlot.getChildrenSlotsPairList().add(entity);
        parentSlot.getParentSlotsPairList().add(entity);
        super.add(entity);
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
	    if (slotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
	        if (slotPair.getParentSlot().equals(childSlot)) {
	            return true;
	        } else {
	            for (SlotPair parentSlotPair : slotPair.getParentSlot().getChildrenSlotsPairList()) {
	                if (slotPairCreatesLoop(parentSlotPair, childSlot)) {
	                    return true;
	                }
	            }
	            return false;
	        }
	    } else {
	        return false;
	    }
	}
}
