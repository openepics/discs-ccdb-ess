package org.openepics.discs.conf.ejb;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;
import org.openepics.discs.conf.ent.Role;
import org.openepics.discs.conf.ent.User;
import org.openepics.discs.conf.ent.UserRole;

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
        privilege = new Privilege(EntityType.DEVICE, EntityTypeOperation.RENAME);
        privilege.setRole(role);
        em.persist(privilege);
        privilege = new Privilege(EntityType.DEVICE, EntityTypeOperation.DELETE);
        privilege.setRole(role);
        em.persist(privilege);

        em.persist(new DataType("boolean", "True or False", true, null, userName));
        em.persist(new DataType("float", "Float", true, null, userName));
        em.persist(new DataType("String", "String", true, null, userName));
        em.persist(new DataType("int", "Integer", true, null, userName));

        final ComponentType compAP = new ComponentType("AP", userName);
        em.persist(compAP);
        final ComponentType compATP = new ComponentType("ATP", userName);
        em.persist(compATP);
        final ComponentType compBGV = new ComponentType("BGV", userName);
        em.persist(compBGV);
        final ComponentType compCAM = new ComponentType("CAM", userName);
        em.persist(compCAM);

        Device device = new Device("SC251", userName);
        device.setComponentType(compAP);
        em.persist(device);
        device = new Device("SC252", userName);
        device.setComponentType(compATP);
        em.persist(device);
        device = new Device("SC256", userName);
        device.setComponentType(compBGV);
        em.persist(device);
        device = new Device("SC253", userName);
        device.setComponentType(compCAM);
        em.persist(device);



    }
}