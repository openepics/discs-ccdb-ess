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
package org.openepics.discs.conf.ejb;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;
import org.openepics.discs.conf.ent.Role;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.User;
import org.openepics.discs.conf.ent.UserRole;
import org.openepics.discs.conf.util.BuiltInDataType;

/**
 * This EJB populates an empty database with mandatory data (data-types etc) and optional
 * (default login for DB security).
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
@Stateless
public class InitialDBPopulation {
    private static final String SYSTEM_USER = "system";
    private static final String ADMIN = "admin";

    @PersistenceContext private EntityManager em;

    /** Fills out initial data for empty database */
    public void initialPopulation() {
        final User user = new User(ADMIN, ADMIN);
        em.persist(user);

        final Role role = new Role(ADMIN);
        role.setDescription("test role");
        em.persist(role);

        final UserRole userRole = new UserRole(true, true, new Date(), new Date());
        userRole.setRole(role);
        userRole.setUser(user);
        em.persist(userRole);

        createPrivilegesForRole(role, EntityType.COMPONENT_TYPE);
        createPrivilegesForRole(role, EntityType.UNIT);
        createPrivilegesForRole(role, EntityType.PROPERTY);
        createPrivilegesForRole(role, EntityType.SLOT);
        createPrivilegesForRole(role, EntityType.DEVICE);
        createPrivilegesForRole(role, EntityType.ALIGNMENT_RECORD);
        createPrivilegesForRole(role, EntityType.DATA_TYPE);
        createPrivilegesForRole(role, EntityType.INSTALLATION_RECORD);

        em.persist(createDataType(BuiltInDataType.INTEGER.toString(), "Integer number", true, null));
        em.persist(createDataType(BuiltInDataType.DOUBLE.toString(), "Double precision floating point", true, null));
        em.persist(createDataType(BuiltInDataType.STRING.toString(), "String of characters (text)", true, null));
        em.persist(createDataType(BuiltInDataType.TIMESTAMP.toString(), "Date and time", true, null));
        em.persist(createDataType(BuiltInDataType.INT_VECTOR.toString(),
                "Vector of integer numbers (1D array)", false, null));
        em.persist(createDataType(BuiltInDataType.DBL_VECTOR.toString(),
                "Vector of double precision numbers (1D array)", false, null));
        em.persist(createDataType(BuiltInDataType.STRING_LIST.toString(), "List of strings (1D array)", false, null));
        em.persist(createDataType(BuiltInDataType.DBL_TABLE.toString(),
                "Table of double precision numbers (2D array)", false, null));

        em.persist(createSlotRelation(SlotRelationName.CONTAINS));
        em.persist(createSlotRelation(SlotRelationName.POWERS));
        em.persist(createSlotRelation(SlotRelationName.CONTROLS));

        final ComponentType rootComponentType = createContainerType(SlotEJB.ROOT_COMPONENT_TYPE);
        em.persist(rootComponentType);
        em.persist(createContainerType(SlotEJB.GRP_COMPONENT_TYPE));

        final Slot rootContainer = new Slot("_ROOT", false);
        rootContainer.setComponentType(rootComponentType);
        rootContainer.setDescription("Implicit CCDB type.");
        rootContainer.setModifiedBy(SYSTEM_USER);
        rootContainer.setModifiedAt(new Date());
        em.persist(rootContainer);
    }

    private void createPrivilegesForRole(Role role, EntityType entityType) {
        Privilege privilege = new Privilege(entityType, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(entityType, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(entityType, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(entityType, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);
    }

    /**
     * Helper function to create data type entity and fill out mandatory modifiedAt and modifiedBy fields
     *
     * @param name
     * @param description
     * @param scalar
     * @param definition
     * @return
     */
    private DataType createDataType(String name, String description, boolean scalar, String definition) {
        final DataType result = new DataType(name, description, scalar, definition);
        result.setModifiedBy(SYSTEM_USER);
        result.setModifiedAt(new Date());

        return result;
    }

    /**
     * Helper function to create a slot relation entity and fill out mandatory modifietAt and modifiedBy fields
     * @param relationName
     * @return
     */
    private SlotRelation createSlotRelation(SlotRelationName relationName) {
        final SlotRelation slotRelation = new SlotRelation(relationName);
        slotRelation.setModifiedBy(SYSTEM_USER);
        slotRelation.setModifiedAt(new Date());

        return slotRelation;
    }

    private ComponentType createContainerType(String containerTypeName) {
        final ComponentType containerType = new ComponentType(containerTypeName);
        containerType.setDescription(containerTypeName);
        containerType.setModifiedBy(SYSTEM_USER);
        containerType.setModifiedAt(new Date());

        return containerType;
    }
}
