package org.openepics.discs.conf.ejb;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.DataType;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class DataTypeEJB extends ReadOnlyDAO<DataType> {
    @Override
    protected void defineEntity() {
        defineEntityClass(DataType.class);
    }
}
