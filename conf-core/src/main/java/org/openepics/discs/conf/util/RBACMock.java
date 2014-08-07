package org.openepics.discs.conf.util;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.util.CRUDOperation.Operation;

@Stateless
public class RBACMock {

    final static String user = "admin";

    public boolean isAuthorized(Operation operation, Object entity) {
        if (entity instanceof Property) {
            if (user.equals("admin") && operation == Operation.CREATE) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
