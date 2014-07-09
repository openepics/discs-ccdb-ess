/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;

/**
 *
 * @author vuppala
 */
@Stateless
public class AuthEJB {

    private static final Logger logger = Logger.getLogger(AuthEJB.class.getCanonicalName());
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;

    // todo: implement autorization using RBAC.
    public boolean userHasAuth(String principal, EntityType resource, EntityTypeOperation operation) {
        boolean auth = false;

        if (principal == null || principal.isEmpty()) {
            return false;
        }

        List<Privilege> privs;
        TypedQuery<Privilege> query;
        //query = em.createQuery("SELECT p FROM UserRole ur JOIN ur.role r JOIN r.privilegeList p WHERE ur.user.userId = :user AND LOCATE(:resource,p.resource) = 1", Privilege.class)
        query = em.createQuery("SELECT p FROM UserRole ur JOIN ur.role r JOIN r.privilegeList p WHERE ur.user.userId = :user", Privilege.class)
                .setParameter("user", principal);

        privs = query.getResultList();
        logger.info("AuthEJB: found privileges: " + privs.size());

        for (Privilege p : privs) {
            if (resource.equals(p.getResource())) {
                logger.info("AuthEJB: matched privileges: " + p);
                if (p.getOper().equals(operation)) {
                    auth = true;
                    break;
                }
            }
        }
        return auth;
    }
}
