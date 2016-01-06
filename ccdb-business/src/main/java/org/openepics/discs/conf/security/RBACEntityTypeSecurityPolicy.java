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
    private static final long serialVersionUID = 7573725310824284483L;

    private static final Logger LOGGER = Logger.getLogger(RBACEntityTypeSecurityPolicy.class.getCanonicalName());

    private static final String PERM_ALIGN_WRITE = "WriteAlignmentRecords";
    private static final String PERM_COMPTYPE_WRITE = "WriteComponentTypes";
    private static final String PERM_DATATYPE_WRITE = "WriteDataTypes";
    private static final String PERM_DEVICE_WRITE = "WriteDevices";
    private static final String PERM_INSTREC_WRITE = "WriteInstallationRecords";
    private static final String PERM_PROP_WRITE = "WriteProperties";
    private static final String PERM_SLOT_WRITE = "WriteSlots";
    private static final String PERM_UNIT_WRITE = "WriteUnits";

    private static final Map<EntityType, String> PERMISSION_MAPPING;

    static {
        final Builder<EntityType, String> permissionMappingBuilder = ImmutableMap.builder();

        permissionMappingBuilder.put(EntityType.ALIGNMENT_RECORD, PERM_ALIGN_WRITE);
        permissionMappingBuilder.put(EntityType.COMPONENT_TYPE, PERM_COMPTYPE_WRITE);
        permissionMappingBuilder.put(EntityType.DATA_TYPE, PERM_DATATYPE_WRITE);
        permissionMappingBuilder.put(EntityType.DEVICE, PERM_DEVICE_WRITE);
        permissionMappingBuilder.put(EntityType.INSTALLATION_RECORD, PERM_INSTREC_WRITE);
        permissionMappingBuilder.put(EntityType.PROPERTY, PERM_PROP_WRITE);
        permissionMappingBuilder.put(EntityType.SLOT, PERM_SLOT_WRITE);
        permissionMappingBuilder.put(EntityType.UNIT, PERM_UNIT_WRITE);

        PERMISSION_MAPPING = permissionMappingBuilder.build();
    }

    @Inject private SSOSessionService sessionService;

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

    @Override
    public boolean isLoggedIn() {
        return sessionService.isLoggedIn();
    }

    @Override
    public boolean getUIHint(String param) {
        if (!isLoggedIn()) return false;

        switch (param) {
            case SecurityPolicy.UI_HINT_HIERARCHY_CREATE:
            case SecurityPolicy.UI_HINT_HIERARCHY_DELETE:
            case SecurityPolicy.UI_HINT_HIERARCHY_MODIFY:
            case SecurityPolicy.UI_HINT_HIERARCHY_ALL:
            case SecurityPolicy.UI_HINT_INSTALLATION:
            case SecurityPolicy.UI_HINT_RELATIONSHIP:
            case SecurityPolicy.UI_HINT_MOVE_SLOT:
            case SecurityPolicy.UI_HINT_IMPORT_SIGNALS:
                return sessionService.hasPermission(PERM_SLOT_WRITE);
            case SecurityPolicy.UI_HINT_DEVICES_CREATE:
            case SecurityPolicy.UI_HINT_DEVICES_DELETE:
            case SecurityPolicy.UI_HINT_DEVICES_MODIFY:
            case SecurityPolicy.UI_HINT_DEVICES_ALL:
                return sessionService.hasPermission(PERM_DEVICE_WRITE);
            case SecurityPolicy.UI_HINT_DEVTYPE_CREATE:
            case SecurityPolicy.UI_HINT_DEVTYPE_DELETE:
            case SecurityPolicy.UI_HINT_DEVTYPE_MODIFY:
            case SecurityPolicy.UI_HINT_DEVTYPE_ALL:
                return sessionService.hasPermission(PERM_COMPTYPE_WRITE);
            case SecurityPolicy.UI_HINT_PROP_CREATE:
            case SecurityPolicy.UI_HINT_PROP_DELETE:
            case SecurityPolicy.UI_HINT_PROP_MODIFY:
            case SecurityPolicy.UI_HINT_PROP_ALL:
                return sessionService.hasPermission(PERM_PROP_WRITE);
            case SecurityPolicy.UI_HINT_ENUM_CREATE:
            case SecurityPolicy.UI_HINT_ENUM_DELETE:
            case SecurityPolicy.UI_HINT_ENUM_MODIFY:
            case SecurityPolicy.UI_HINT_ENUM_ALL:
                return sessionService.hasPermission(PERM_DATATYPE_WRITE);
            case SecurityPolicy.UI_HINT_UNIT_CREATE:
            case SecurityPolicy.UI_HINT_UNIT_DELETE:
            case SecurityPolicy.UI_HINT_UNIT_MODIFY:
            case SecurityPolicy.UI_HINT_UNIT_ALL:
                return sessionService.hasPermission(PERM_UNIT_WRITE);
            default:
                return super.getUIHint(param);
        }


    }
}
