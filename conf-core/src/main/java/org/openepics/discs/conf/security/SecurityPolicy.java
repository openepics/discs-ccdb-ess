/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.security;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;


/**
 * Abstract SecurityPolicy interface. Implementations should contain all needed a&a functionality.
 * 
 * @author mpavleski
 *
 */
public interface SecurityPolicy {
    public final String MANAGE_ALIGNMENT_RECORDS    = "manage-alignment-records";
    public final String MANAGE_COMPONENT_TYPES      = "manage-component-types";
    public final String MANAGE_DATA_TYPES           = "manage-data-types";
    public final String MANAGE_DEVICE_ASSEMBLIES    = "manage-device-assemblies";
    public final String MANAGE_INSTALLATION_RECORDS = "manage-installation-records";
    public final String MANAGE_PROPERTIES           = "manage-properties";
    public final String MANAGE_LAYOUT_SLOTS         = "manage-layout-slots";
    public final String MANAGE_UNITS                = "manage-units";
    public final String MANAGE_DEVICES              = "manage-devices";
    
    
    /**
     * Checks if user is authorized to do opeeration operationType on entity of entityType
     * 
     * @param entity The target entity
     * @param operationType The operation type
     */
    public void checkAuth(Object entity, EntityTypeOperation operationType);
    
    /**
     * Returns UI hints for the JSF/ManagedBeans layer
     * 
     * @param param
     * @return
     */
    public boolean getUIHint(String param);
}
