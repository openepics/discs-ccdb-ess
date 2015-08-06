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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

import se.esss.ics.rbac.loginmodules.service.Message;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Implementation of simple security policy (checking for entity-type access only) using DISCS RBAC.
 *
 * Please note that RBAC Login Module is assumed configured on the Application Server.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@RequestScoped
@Named("securityPolicy")
@Alternative
public class RBACEntityTypeSecurityPolicy extends AbstractEnityTypeSecurityPolicy
    implements SecurityPolicy, Serializable {

    @Inject private SSOSessionService sessionService;

    private static final long serialVersionUID = 7573725310824284483L;

    private static final Logger LOGGER = Logger.getLogger(RBACEntityTypeSecurityPolicy.class.getCanonicalName());

    private static final Map<EntityType, String> PERMISSION_MAPPING;

    static {
        final Builder<EntityType, String> permissionMappingBuilder = ImmutableMap.builder();

        permissionMappingBuilder.put(EntityType.ALIGNMENT_RECORD, "WriteAlignmentRecords");
        permissionMappingBuilder.put(EntityType.COMPONENT_TYPE, "WriteComponentTypes");
        permissionMappingBuilder.put(EntityType.DATA_TYPE, "WriteDataTypes");
        permissionMappingBuilder.put(EntityType.DEVICE, "WriteDevices");
        permissionMappingBuilder.put(EntityType.INSTALLATION_RECORD, "WriteInstallationRecords");
        permissionMappingBuilder.put(EntityType.PROPERTY, "WriteProperties");
        permissionMappingBuilder.put(EntityType.SLOT, "WriteSlots");
        permissionMappingBuilder.put(EntityType.UNIT, "WriteUnits");

        PERMISSION_MAPPING = permissionMappingBuilder.build();
    }

    /** Default no-params constructor */
    public RBACEntityTypeSecurityPolicy() {}

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
    protected boolean hasPermission(EntityType entityType, EntityTypeOperation operationType) {
        return PERMISSION_MAPPING.containsKey(entityType)
                    && sessionService.hasPermission(PERMISSION_MAPPING.get(entityType));
    }
}
