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
import java.security.Principal;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import se.lu.esss.ics.rbac.loginmodules.RBACPrincipal;

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
 * @author Miroslav Pavleski &lt;miroslav.pavleski@cosylab.com&gt;
 *
 */
@SessionScoped
@Named("securityPolicy")
@Alternative
public class RBACEntityTypeSecurityPolicy extends AbstractEnityTypeSecurityPolicy
    implements SecurityPolicy, Serializable {

    private static final long serialVersionUID = 7573725310824284483L;

    private static final Logger LOGGER = Logger.getLogger(RBACEntityTypeSecurityPolicy.class.getCanonicalName());

    private static final String RBAC_RESOURCE = "ControlsDatabase";

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
        super.login(userName, password);

        final Principal principal = servletRequest.getUserPrincipal();
        if (principal==null || !(principal instanceof RBACPrincipal)) {
            throw new SecurityException(RBACEntityTypeSecurityPolicy.class.getName() + " Could not get RBAC user "
                    + "Principal. Is RBAC Login Module installed on the server?");
        }

        final RBACPrincipal rbacPrincipal = (RBACPrincipal) principal;
        if (!RBAC_RESOURCE.equals(rbacPrincipal.getResource())) {
            throw new SecurityException(RBACEntityTypeSecurityPolicy.class.getName() + " ControlsDatabase resource not "
                    + "available in the principal. Is the RBAC login module configured properly?");
        }
        for (String roleName : rbacPrincipal.getRoles()) {
            LOGGER.log(Level.FINE, "User role: " + roleName);
        }
        for (String permissionName : rbacPrincipal.getPermissions()) {
            LOGGER.log(Level.FINE, "User permission: " + permissionName);
        }
    }

    @Override
    protected void populateCachedPermissions() {
        Preconditions.checkArgument(cachedPermissions==null,
                                    "populateCachedPermissions called when cached data was already available");

        cachedPermissions = new EnumMap<EntityType, Set<EntityTypeOperation>>(EntityType.class);

        RBACPrincipal principal = (RBACPrincipal) servletRequest.getUserPrincipal();

        final Set<EntityTypeOperation> allWritePermissions = EnumSet.of(UPDATE, CREATE, DELETE, RENAME);

        for (String rbacPermission : principal.getPermissions()) {
            final EntityType entityType = PERMISSION_MAPPING.get(rbacPermission);
            if (entityType != null) {
                cachedPermissions.put(entityType, allWritePermissions);
            }
        }
    }
}
