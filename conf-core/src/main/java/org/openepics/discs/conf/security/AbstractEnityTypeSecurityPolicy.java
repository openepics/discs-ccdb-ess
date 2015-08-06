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
    private static final long serialVersionUID = 1869933525057076928L;

    @Override
    public abstract void login(String userName, String password);

    @Override
    public abstract void logout();

    @Override
    public abstract String getUserId();

    /**
     * Checks if the user has access to the given entityType using operation operationType
     *
     * @param entityType
     * @param operationType
     * @return <code>true</code> if permission exists, <code>false</code> otherwise
     */
    protected abstract boolean hasPermission(EntityType entityType, EntityTypeOperation operationType);

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
            case "SIGNALS" :
                return hasPermission(EntityType.SLOT, EntityTypeOperation.UPDATE);
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
}
