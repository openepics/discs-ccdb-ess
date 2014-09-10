package org.openepics.discs.conf.ejb;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class SlotRelationEJB extends ReadOnlyDAO<SlotRelation> {
    @Override
    protected void defineEntity() {
        defineEntityClass(SlotRelation.class);
    }

    @Override
    public SlotRelation findByName(String name) {
        throw new UnsupportedOperationException("findByName with String parametar method not aplicable to SlotRelation class");
    }

    public SlotRelation findBySlotRelationName(SlotRelationName name) {
        try {
            return em.createNamedQuery("SlotRelation.findByName", SlotRelation.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
