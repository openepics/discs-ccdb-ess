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
package org.openepics.discs.ccdb.core.security;

import java.util.HashMap;
import java.util.Map;

import org.openepics.discs.ccdb.model.EntityType;

/**
 *
 * Helper class used to resolve the {@link EntityType} of configuration entities, relevant to audit logging and security
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
public class EntityTypeResolver {
    private static Map<String, EntityType> entityTypes = new HashMap<> ();

    static {
        entityTypes.put(org.openepics.discs.ccdb.model.Device.class.getCanonicalName(), EntityType.DEVICE);
        entityTypes.put(org.openepics.discs.ccdb.model.DevicePropertyValue.class.getCanonicalName(), EntityType.DEVICE);
        entityTypes.put(org.openepics.discs.ccdb.model.DeviceArtifact.class.getCanonicalName(), EntityType.DEVICE);

        entityTypes.put(org.openepics.discs.ccdb.model.Slot.class.getCanonicalName(), EntityType.SLOT);
        entityTypes.put(org.openepics.discs.ccdb.model.SlotPropertyValue.class.getCanonicalName(), EntityType.SLOT);
        entityTypes.put(org.openepics.discs.ccdb.model.SlotArtifact.class.getCanonicalName(), EntityType.SLOT);
        entityTypes.put(org.openepics.discs.ccdb.model.SlotPair.class.getCanonicalName(), EntityType.SLOT);

        entityTypes.put(org.openepics.discs.ccdb.model.ComponentType.class.getCanonicalName(), EntityType.COMPONENT_TYPE);
        entityTypes.put(org.openepics.discs.ccdb.model.ComptypePropertyValue.class.getCanonicalName(),
                EntityType.COMPONENT_TYPE);
        entityTypes.put(org.openepics.discs.ccdb.model.ComptypeArtifact.class.getCanonicalName(),
                EntityType.COMPONENT_TYPE);
        entityTypes.put(org.openepics.discs.ccdb.model.ComptypeAsm.class.getCanonicalName(),
                EntityType.COMPONENT_TYPE);

        entityTypes.put(org.openepics.discs.ccdb.model.InstallationRecord.class.getCanonicalName(),
                EntityType.INSTALLATION_RECORD);
        entityTypes.put(org.openepics.discs.ccdb.model.InstallationArtifact.class.getCanonicalName(),
                EntityType.INSTALLATION_RECORD);

        entityTypes.put(org.openepics.discs.ccdb.model.AlignmentRecord.class.getCanonicalName(),
                EntityType.ALIGNMENT_RECORD);
        entityTypes.put(org.openepics.discs.ccdb.model.AlignmentArtifact.class.getCanonicalName(),
                EntityType.ALIGNMENT_RECORD);
        entityTypes.put(org.openepics.discs.ccdb.model.AlignmentPropertyValue.class.getCanonicalName(),
                EntityType.ALIGNMENT_RECORD);

        entityTypes.put(org.openepics.discs.ccdb.model.Unit.class.getCanonicalName(), EntityType.UNIT);

        entityTypes.put(org.openepics.discs.ccdb.model.User.class.getCanonicalName(), EntityType.USER);
        entityTypes.put(org.openepics.discs.ccdb.model.Property.class.getCanonicalName(), EntityType.PROPERTY);

        entityTypes.put(org.openepics.discs.ccdb.model.DataType.class.getCanonicalName(), EntityType.DATA_TYPE);
    }

    /**
     * Explicit private constructor to forbid construction
     */
    private EntityTypeResolver() {}

    /**
     * Resolves the {@link EntityType} of an entity object, for security purposes.
     *
     * Child entities such as CompoenentTypePropertyValues are resolved as the security-relevant parent
     * (such as COMPONENT_TYPE)
     *
     * @param entity the entity
     * @return the {@link EntityType}
     */
    public static EntityType resolveEntityType(Object entity) {
        EntityType result = entityTypes.get(entity.getClass().getCanonicalName());
        if (result == null) {
            throw new SecurityException("Unhandled or invalid entity type in the security system: "
                                                + entity.getClass().getCanonicalName());
        }

        return result;
    }
}
