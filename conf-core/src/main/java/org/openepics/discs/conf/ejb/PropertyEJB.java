package org.openepics.discs.conf.ejb;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.Property;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class PropertyEJB extends DAO<Property> {
    @Override
    protected void defineEntity() {
        defineEntityClass(Property.class);
    }
}
