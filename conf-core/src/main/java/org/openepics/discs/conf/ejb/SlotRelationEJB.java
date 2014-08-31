package org.openepics.discs.conf.ejb;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.SlotRelation;

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
}
