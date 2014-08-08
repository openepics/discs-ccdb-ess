package org.openepics.discs.conf.util;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;

/**
 * Just a mock, to be removed and replaced with actual authorization checking logic
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless
public class RBACMock {

    final static String user = "admin";

    public boolean isAuthorized(EntityTypeOperation operation, Object entity) {
        if (entity instanceof Property) {
            if (user.equals("admin") && operation == EntityTypeOperation.CREATE) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
