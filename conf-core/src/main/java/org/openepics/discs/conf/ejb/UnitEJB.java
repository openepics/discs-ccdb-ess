package org.openepics.discs.conf.ejb;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.Unit;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class UnitEJB extends DAO<Unit> {
    @Override
    protected void defineEntity() {
        defineEntityClass(Unit.class);
    }
}
