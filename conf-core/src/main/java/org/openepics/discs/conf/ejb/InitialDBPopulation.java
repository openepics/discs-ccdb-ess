package org.openepics.discs.conf.ejb;

import java.util.Date;

import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;
import org.openepics.discs.conf.ent.Role;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.User;
import org.openepics.discs.conf.ent.UserRole;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.core.Seds;

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
        
        privilege = new Privilege(EntityType.MENU, EntityTypeOperation.AUTHORIZED);
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
        
        em.persist(createDataType("Integer", "Integer number", true, null));
        em.persist(createDataType("Double", "Double precision floating point", true, null));
        em.persist(createDataType("String", "String of characters (text)", true, null));
        em.persist(createDataType("Timestamp", "Date and time", true, null));
        em.persist(createDataType("URL", "string of characters which is known to contain URL", true, null));
        em.persist(createDataType("Integers Vector", "ector of integer numbers (1D array)", false, null));
        em.persist(createDataType("Doubles Vector", "Vector of double precision numbers (1D array)", false, null));
        em.persist(createDataType("Strings List", "List of strings (1D array)", false, null));
        em.persist(createDataType("Doubles Table", "Table of double precision numbers (2D array)", false, null));

        final SedsEnum testEnum = Seds.newFactory().newEnum("TEST1", new String[]{"TEST1", "TEST2", "TEST3", "TEST4"});
        JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);


        em.persist(createDataType("Test enums", "Testing of enums", false, jsonEnum.toString()));

        em.persist(createSlotRelation(SlotRelationName.CONTAINS));
        em.persist(createSlotRelation(SlotRelationName.POWERS));
        em.persist(createSlotRelation(SlotRelationName.CONTROLS));
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
}