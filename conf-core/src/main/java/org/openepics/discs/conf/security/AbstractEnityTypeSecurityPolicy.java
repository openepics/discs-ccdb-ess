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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;


/**
 * Abstract implementation of "simple" security policy
 *
 * Simple security policy checks for access only on entity type, not per instance (e.g. check whether user can add new
 * Properties, not whether a user can modify a particular property instance owned by her or another user).
 *
 * Complex security policy needs additional data in the CCDB to associate CCDB entity instances with ownership
 * and/or permission information.
 *
 * The class assumes that an Java EE {@link LoginModule} is integrated with the Servlet Container so
 * {@link HttpServletRequest} methods getUserPrincipal, login and logout work.
 *
 * Stateful EJB, caches all permissions from database on first access.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */

public abstract class AbstractEnityTypeSecurityPolicy implements SecurityPolicy, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AbstractEnityTypeSecurityPolicy.class.getCanonicalName());

    @Inject protected HttpServletRequest servletRequest;

    /**
     * Contains cached permissions
     */
    protected Map<EntityType, Set<EntityTypeOperation> > cachedPermissions;

    /**
     * Default constructor.
     */
    public AbstractEnityTypeSecurityPolicy() {
        LOGGER.log(Level.INFO, "Creating " + this.getClass().getCanonicalName());
    }

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
        return servletRequest.getUserPrincipal()!=null ? servletRequest.getUserPrincipal().getName() : null;
    }

    @Override
    public void checkAuth(Object entity, EntityTypeOperation operationType) {
        final EntityType entityType = EntityTypeResolver.resolveEntityType(entity);

        if (!hasPermission(entityType , operationType)) {
            throw SecurityException.generateExceptionMessage(entity, entityType, operationType);
        }
    }

    @Override
    public boolean getUIHint(String param) {
        switch(param) {
            case "MOVE_SLOT" :
                return hasPermission(EntityType.SLOT, EntityTypeOperation.CREATE)
                        || hasPermission(EntityType.SLOT, EntityTypeOperation.UPDATE);
            default:
                return hasAnyModifyPermission( EntityType.valueOf(param) );
        }
    }

    /**
     * Will allow UI element to be shown for given entity type
     *
     * @param entityType the {@link EntityType} for which to check for
     * @return <code>true</code> if has any modify permissions, <code>false</code> otherwise
     */
    protected boolean hasAnyModifyPermission(EntityType entityType) {
        return hasPermission(entityType, EntityTypeOperation.CREATE) ||
               hasPermission(entityType, EntityTypeOperation.DELETE) ||
               hasPermission(entityType, EntityTypeOperation.UPDATE) ||
               hasPermission(entityType, EntityTypeOperation.RENAME);
    }

    /**
     * Checks if the user has access to the given entityType using operation operationType
     *
     * @param entityType
     * @param operationType
     * @return true if permission exists
     */
    private boolean hasPermission(EntityType entityType, EntityTypeOperation operationType) {

        final String principal = getUserId();

        // Handle the non-logged case
        if (principal == null) {
            return false;
        }

        if (cachedPermissions == null)
            populateCachedPermissions();

        final Set<EntityTypeOperation> entityTypeOperations = cachedPermissions.get(entityType);

        if (entityTypeOperations == null) {
            return false;
        } else {
            return entityTypeOperations.contains(operationType);
        }
    }

    /**
     * Populates the map of cached privileges from the database
     */
    protected abstract void populateCachedPermissions();
}
