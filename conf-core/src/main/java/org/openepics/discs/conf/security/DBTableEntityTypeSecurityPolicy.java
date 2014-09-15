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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;

import com.google.common.base.Preconditions;


/**
 * Implementation of simple security policy (checking for entity-type access only) using the DB {@link Privilege} table
 * and the Java EE security module, as was in Configuration Module v. 1.0.
 *
 * Stateful EJB, caches all permissions from database on first access.
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@SessionScoped
@Named("securityPolicy")
public class DBTableEntityTypeSecurityPolicy extends AbstractEnityTypeSecurityPolicy
                implements SecurityPolicy, Serializable {
    private static final Logger LOGGER = Logger.getLogger(DBTableEntityTypeSecurityPolicy.class.getCanonicalName());

    @PersistenceContext private transient EntityManager em;

    /**
     * Default no-params constructor
     */
    public DBTableEntityTypeSecurityPolicy() {
        super();
    }

    @Override
    protected void populateCachedPermissions() {
        Preconditions.checkArgument(cachedPermissions==null,
                                    "EntityTypeDBTableSecurityPolicy.populateCachedPermissions called when "
                                    + "cached data was already available");

        cachedPermissions = new EnumMap<EntityType, Set<EntityTypeOperation>>(EntityType.class);

        final String principal = getUserId();
        // The following should not happen for logged in user
        if (principal == null || principal.isEmpty()) {
            throw new SecurityException("Identity could not be established. Is user logged in");
        }

        final List<Privilege> privs = em.createQuery(
                "SELECT p FROM UserRole ur JOIN ur.role r JOIN r.privilegeList p " +
                "WHERE ur.user.userId = :user", Privilege.class).
                setParameter("user", principal).getResultList();
        LOGGER.finer("found privileges: " + privs.size());

        for (Privilege p : privs) {
            final EntityType entityType = p.getResource();

            Set<EntityTypeOperation> operationTypeSet = cachedPermissions.get(entityType);
            if (operationTypeSet == null) {
                operationTypeSet = EnumSet.noneOf(EntityTypeOperation.class);
                cachedPermissions.put(entityType, operationTypeSet);
            }

            operationTypeSet.add(p.getOper());
        }
    }
}
