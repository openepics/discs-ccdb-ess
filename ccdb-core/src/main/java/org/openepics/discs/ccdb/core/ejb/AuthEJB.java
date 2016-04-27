/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 *
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 *
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 *
 */
package org.openepics.discs.ccdb.core.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import org.openepics.discs.ccdb.model.Privilege;
import org.openepics.discs.ccdb.model.Rack;
import org.openepics.discs.ccdb.model.Role;
import org.openepics.discs.ccdb.model.User;
import org.openepics.discs.ccdb.model.UserRole;

/**
 *
 * @author vuppala
 *
 */
@Stateless
public class AuthEJB extends DAO<Role> {    
    private static final Logger logger = Logger.getLogger(AuthEJB.class.getName());
    
    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    /**
     * All the tacks
     * 
     * @return a list of all {@link Rack}s ordered by name.
     */
    public List<Role> findAllRoles() {
        return em.createNamedQuery("Role.findAll", Role.class).getResultList();
    }
    
    public List<Privilege> findAllPrivileges() {
        return em.createNamedQuery("Privilege.findAll", Privilege.class).getResultList();
    }
    
    public List<User> findAllUsers() {
        return em.createNamedQuery("User.findAll", User.class).getResultList();
    }
    
    public List<UserRole> findAllUserRoles() {
        return em.createNamedQuery("UserRole.findAll", UserRole.class).getResultList();
    }
}
