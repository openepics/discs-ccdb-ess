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
    final private String userName = "init-import";

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
        
        em.persist(new DataType("Integer", "Integer number", true, null, userName));
        em.persist(new DataType("Double", "Double precision floating point", true, null, userName));
        em.persist(new DataType("String", "String of characters (text)", true, null, userName));
        em.persist(new DataType("Timestamp", "Date and time", true, null, userName));
        em.persist(new DataType("URL", "string of characters which is known to contain URL", true, null, userName));
        em.persist(new DataType("Integers Vector", "ector of integer numbers (1D array)", false, null, userName));
        em.persist(new DataType("Doubles Vector", "Vector of double precision numbers (1D array)", false, null, userName));
        em.persist(new DataType("Strings List", "List of strings (1D array)", false, null, userName));
        em.persist(new DataType("Doubles Table", "Table of double precision numbers (2D array)", false, null, userName));

        final SedsEnum testEnum = Seds.newFactory().newEnum("TEST1", new String[]{"TEST1", "TEST2", "TEST3", "TEST4"});
        JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);


        em.persist(new DataType("Test enums", "Testing of enums", false, jsonEnum.toString(), userName));

        em.persist(new SlotRelation(SlotRelationName.CONTAINS, userName));
        em.persist(new SlotRelation(SlotRelationName.POWERS, userName));
        em.persist(new SlotRelation(SlotRelationName.CONTROLS, userName));
    }
}