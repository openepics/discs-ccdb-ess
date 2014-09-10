package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;

@Stateless
public class SlotPairEJB extends DAO<SlotPair> {
    @Override
    protected void defineEntity() {
        defineEntityClass(SlotPair.class);
    }

    public List<SlotPair> findSlotPairsByParentChildRelation(String childName, String parentName, SlotRelationName relationName) {
        return em.createNamedQuery("SlotPair.findByParentChildRelation", SlotPair.class).setParameter("childName", childName)
               .setParameter("parentName", parentName).setParameter("relationName", relationName).getResultList();
    }

    @Override
    public SlotPair findByName(String name) {
        throw new UnsupportedOperationException("findByName method not aplicable to SlotPairEJB class");
    }
}
