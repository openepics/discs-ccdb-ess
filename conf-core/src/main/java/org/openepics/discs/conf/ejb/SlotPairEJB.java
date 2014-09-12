package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.util.SlotPairLoopException;

import com.google.common.base.Preconditions;

@Stateless
public class SlotPairEJB extends DAO<SlotPair> {
	@Override
	protected void defineEntity() {
		defineEntityClass(SlotPair.class);
	}

	/**
	 * Finds all {@link SlotPair}s with given parent name, child name and {@link SlotRelationName}. Child name can contain wild card.
	 *
	 * @param childName Name (optional with wild card character) of the child in the {@link SlotPair}
	 * @param parentName Name of the parent in the {@link SlotPair}
	 * @param relationName {@link SlotRelationName}
	 * @return {@link List}<SlotPair> of all {@link SlotPair}s for given parent name, child name and {@link SlotRelationName}
	 */
    public List<SlotPair> findSlotPairsByParentChildRelation(String childName, String parentName, SlotRelationName relationName) {
        return em.createNamedQuery("SlotPair.findByParentChildRelation", SlotPair.class).setParameter("childName", childName)
                .setParameter("parentName", parentName).setParameter("relationName", relationName).getResultList();
    }

	@Override
	public SlotPair findByName(String name) {
		throw new UnsupportedOperationException("findByName method not aplicable to SlotPairEJB class");
	}

	public boolean slotHasMoreThanOneContainsRelation(Slot childSlot) {
	    final List<SlotPair> slotPairs = em.createNamedQuery("SlotPair.findFirstTwoSlotPairsByChildAndRelation", SlotPair.class).setParameter("childSlot", childSlot).setParameter("relationName", SlotRelationName.CONTAINS).setMaxResults(2).getResultList();
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
	    if (!hasLoop(entity, entity.getChildSlot())) {
    	    super.add(entity);
    	    entity.getChildSlot().getChildrenSlotsPairList().add(entity);
            entity.getParentSlot().getParentSlotsPairList().add(entity);
            em.merge(entity.getChildSlot());
            em.merge(entity.getParentSlot());
	    } else {
	        throw new SlotPairLoopException();
	    }
	}

	private boolean hasLoop(SlotPair slotPair, Slot childSlot) {
	    if (slotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
	        if (slotPair.getParentSlot().equals(childSlot)) {
	            return true;
	        } else {
	            for (SlotPair parentSlotPair : slotPair.getParentSlot().getChildrenSlotsPairList()) {
	                if (hasLoop(parentSlotPair, childSlot)) {
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
