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

import static org.openepics.discs.conf.ent.EntityTypeOperation.CREATE;
import static org.openepics.discs.conf.ent.EntityTypeOperation.DELETE;
import static org.openepics.discs.conf.ent.EntityTypeOperation.RENAME;
import static org.openepics.discs.conf.ent.EntityTypeOperation.UPDATE;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import se.esss.ics.rbac.loginmodules.service.Message;
import se.esss.ics.rbac.loginmodules.service.RBACSSOSessionService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;


/**
 * Implementation of simple security policy (checking for entity-type access only) using DISCS RBAC.
 *
 * Please note that RBAC Login Module is assumed configured on the Application Server.
 *
 * Stateful EJB, caches all permissions from database on first access.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@SessionScoped
@Named("securityPolicy")
@Alternative
public class RBACEntityTypeSecurityPolicy extends AbstractEnityTypeSecurityPolicy
    implements SecurityPolicy, Serializable {

    @Inject private RBACSSOSessionService sessionService;

    private static final long serialVersionUID = 7573725310824284483L;

    private static final Logger LOGGER = Logger.getLogger(RBACEntityTypeSecurityPolicy.class.getCanonicalName());

    private static final Map<String, EntityType> PERMISSION_MAPPING;

    static {
        final Builder<String , EntityType> permissionMappingBuilder = ImmutableMap.builder();

        permissionMappingBuilder.put("WriteAlignmentRecords", EntityType.ALIGNMENT_RECORD);
        permissionMappingBuilder.put("WriteComponentTypes", EntityType.COMPONENT_TYPE);
        permissionMappingBuilder.put("WriteDataTypes", EntityType.DATA_TYPE);
        permissionMappingBuilder.put("WriteDevices", EntityType.DEVICE);
        permissionMappingBuilder.put("WriteInstallationRecords", EntityType.INSTALLATION_RECORD);
        permissionMappingBuilder.put("WriteProperties", EntityType.PROPERTY);
        permissionMappingBuilder.put("WriteSlots", EntityType.SLOT);
        permissionMappingBuilder.put("WriteUnits", EntityType.UNIT);

        PERMISSION_MAPPING = permissionMappingBuilder.build();
    }

    /**
     * Default no-params constructor
     */
    public RBACEntityTypeSecurityPolicy() {
        super();
    }

    @Override
    public void login(String userName, String password) {
        Message message = sessionService.login(userName, password);
        if (!message.isSuccessful()) {
            LOGGER.log(Level.FINE, message.getMessage());
            throw new SecurityException(message.getMessage());
        }
    }

    @Override
    public void logout() {
        Message message = sessionService.logout();
        if (!message.isSuccessful()) {
            LOGGER.log(Level.FINE, message.getMessage());
            throw new SecurityException(message.getMessage());
        }
    }

    @Override
    public String getUserId() {
        return sessionService.getLoggedInName();
    }

    @Override
    protected void populateCachedPermissions() {
        Preconditions.checkArgument(cachedPermissions==null,
                                    "populateCachedPermissions called when cached data was already available");

        final Set<EntityTypeOperation> allWritePermissions = EnumSet.of(UPDATE, CREATE, DELETE, RENAME);

        cachedPermissions = new EnumMap<EntityType, Set<EntityTypeOperation>>(EntityType.class);
        for (String perm : PERMISSION_MAPPING.keySet()) {
            if (sessionService.hasPermission(perm)) {
                cachedPermissions.put(PERMISSION_MAPPING.get(perm), allWritePermissions);
            }
        }
    }
}
