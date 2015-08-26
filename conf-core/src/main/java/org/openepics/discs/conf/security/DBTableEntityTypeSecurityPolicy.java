/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.security;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;

/**
 * Implementation of simple security policy (checking for entity-type access only) using the DB {@link Privilege} table
 * and the Java EE security module, as was in Configuration Module v. 1.0.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@RequestScoped
@Named("securityPolicy")
public class DBTableEntityTypeSecurityPolicy extends AbstractEnityTypeSecurityPolicy
                implements SecurityPolicy, Serializable {
    private static final long serialVersionUID = 108664734022549860L;

    private static final Logger LOGGER = Logger.getLogger(DBTableEntityTypeSecurityPolicy.class.getCanonicalName());

    @PersistenceContext private transient EntityManager em;

    @Inject private HttpServletRequest servletRequest;

    /** Default no-params constructor */
    public DBTableEntityTypeSecurityPolicy() {}

    @Override
    public void login(String userName, String password) {
        try {
            if (servletRequest.getUserPrincipal() == null) {
                servletRequest.login(userName, password);
                LOGGER.log(Level.INFO, "Login successful for " + userName);
            }
        } catch (Exception e) {
            throw new SecurityException("Login Failed !", e);
        }
    }

    @Override
    public void logout() {
        try {
            servletRequest.logout();
            servletRequest.getSession().invalidate();
        } catch (Exception e) {
            throw new SecurityException("Error while logging out!", e);
        }
    }

    @Override
    public String getUserId() {
        return servletRequest.getUserPrincipal() != null ? servletRequest.getUserPrincipal().getName() : null;
    }

    @Override
    protected boolean hasPermission(EntityType entityType, EntityTypeOperation operationType) {
        final String principal = getUserId();
        if (principal == null || principal.isEmpty()) {
            return false;
        }

        final List<Privilege> privs = em.createQuery(
                "SELECT p FROM UserRole ur JOIN ur.role r JOIN r.privilegeList p " +
                "WHERE ur.user.userId = :user AND p.resource = :entityType", Privilege.class).
                setParameter("user", principal).setParameter("entityType", entityType).getResultList();
        LOGGER.log(Level.FINE, "Found privileges for user \"" + principal + "\": " + privs);

        for (Privilege p : privs) {
            if (p.getOper() == operationType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLoggedIn() {
        return servletRequest.getUserPrincipal() != null;
    }
}
