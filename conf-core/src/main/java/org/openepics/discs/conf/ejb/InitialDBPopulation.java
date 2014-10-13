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
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;
import org.openepics.discs.conf.ent.Role;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.User;
import org.openepics.discs.conf.ent.UserRole;
import org.openepics.discs.conf.util.PropertyDataType;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.core.Seds;

/**
 * This EJB populates an empty database with mandatory data (data-types etc) and optional
 * (default login for DB security).
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
@Stateless
public class InitialDBPopulation {
    @PersistenceContext private EntityManager em;

    /**
     * Fills out initial data for empty database
     *
     */
    public void initialPopulation() {
        final User user = new User("admin", "admin");
        em.persist(user);

        final Role role = new Role("admin");
        role.setDescription("test role");
        em.persist(role);

        final UserRole userRole = new UserRole(true, true, new Date(), new Date());
        userRole.setRole(role);
        userRole.setUser(user);
        em.persist(userRole);

        Privilege privilege = new Privilege(EntityType.COMPONENT_TYPE, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.COMPONENT_TYPE, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.COMPONENT_TYPE, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.UNIT, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.UNIT, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.UNIT, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.UNIT, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.PROPERTY, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.PROPERTY, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.PROPERTY, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.PROPERTY, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.SLOT, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.SLOT, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.SLOT, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.SLOT, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.DEVICE, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.DEVICE, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.DEVICE, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.ALIGNMENT_RECORD, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.ALIGNMENT_RECORD, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.ALIGNMENT_RECORD, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.ALIGNMENT_RECORD, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.DATA_TYPE, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.DATA_TYPE, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.DATA_TYPE, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.DATA_TYPE, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        privilege = new Privilege(EntityType.INSTALLATION_RECORD, EntityTypeOperation.CREATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.INSTALLATION_RECORD, EntityTypeOperation.UPDATE);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.INSTALLATION_RECORD, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.INSTALLATION_RECORD, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        em.persist(createDataType(PropertyDataType.INTEGER.toString(), "Integer number", true, null));
        em.persist(createDataType(PropertyDataType.DOUBLE.toString(), "Double precision floating point", true, null));
        em.persist(createDataType(PropertyDataType.STRING.toString(), "String of characters (text)", true, null));
        em.persist(createDataType(PropertyDataType.TIMESTAMP.toString(), "Date and time", true, null));
        em.persist(createDataType(PropertyDataType.URL.toString(), "String of characters which is known to contain an URL", true, null));
        em.persist(createDataType(PropertyDataType.INT_VECTOR.toString(), "Vector of integer numbers (1D array)", false, null));
        em.persist(createDataType(PropertyDataType.DBL_VECTOR.toString(), "Vector of double precision numbers (1D array)", false, null));
        em.persist(createDataType(PropertyDataType.STRING_LIST.toString(), "List of strings (1D array)", false, null));
        em.persist(createDataType(PropertyDataType.DBL_TABLE.toString(), "Table of double precision numbers (2D array)", false, null));

        final SedsEnum testEnum = Seds.newFactory().newEnum("TEST1", new String[] {"TEST1", "TEST2", "TEST3", "TEST4"});
        JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);
        em.persist(createDataType("Test enums", "Testing of enums", false, jsonEnum.toString()));

        em.persist(createSlotRelation(SlotRelationName.CONTAINS));
        em.persist(createSlotRelation(SlotRelationName.POWERS));
        em.persist(createSlotRelation(SlotRelationName.CONTROLS));

        em.persist(createContainerType(SlotEJB.ROOT_COMPONENT_TYPE));
        em.persist(createContainerType(SlotEJB.GRP_COMPONENT_TYPE));
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

        result.setModifiedBy("system");
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

        slotRelation.setModifiedBy("system");
        slotRelation.setModifiedAt(new Date());

        return slotRelation;
    }

    private ComponentType createContainerType(String containerTypeName) {
        final ComponentType containerType = new ComponentType(containerTypeName);
        containerType.setDescription(containerTypeName);
        containerType.setModifiedBy("system");
        containerType.setModifiedAt(new Date());

        return containerType;
    }
}
