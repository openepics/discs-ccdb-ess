/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypeProperty;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.AppProperties;

/**
 *
 * @author vuppala
 */
@Stateless
public class ComptypeEJB {
    @EJB
    private AuthEJB authEJB;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    @PersistenceContext
    private EntityManager em;
    @Inject
    private LoginManager loginManager;
    
    // ----------- Audit record ---------------------------------------
    private void makeAuditEntry(EntityTypeOperation oper, String key, String entry) {
        AuditRecord arec = new AuditRecord(null, new Date(), oper, loginManager.getUserid(), entry);
        arec.setEntityType(EntityType.COMPONENT_TYPE);
        arec.setEntityKey(key);
        em.persist(arec);
    }
    
    // ----------------  Component Type -------------------------
    
    public List<ComponentType> findComponentType() {
        List<ComponentType> comptypes;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ComponentType> cq = cb.createQuery(ComponentType.class);
        Root<ComponentType> prop = cq.from(ComponentType.class);

        TypedQuery<ComponentType> query = em.createQuery(cq);
        comptypes = query.getResultList();
        logger.log(Level.INFO, "Number of component types: {0}", comptypes.size());

        return comptypes;
    }

    
    public ComponentType findComponentType(int id) {
        return em.find(ComponentType.class, id);
    }

    
    public void saveComponentType(ComponentType ctype) throws Exception {
        if (ctype == null) {
            logger.log(Level.SEVERE, "Comp Type is null!");
            throw new Exception("Comp Type is null");
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ctype.setModifiedAt(new Date());

        em.merge(ctype);
        makeAuditEntry(EntityTypeOperation.UPDATE,ctype.getName(),"Updated component type");
    }

    
    public void addComponentType(ComponentType ctype) throws Exception {
        if (ctype == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.CREATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ctype.setModifiedAt(new Date());
        em.persist(ctype);
        makeAuditEntry(EntityTypeOperation.CREATE,ctype.getName(),"Created component type");
    }

    
    public void deleteComponentType(ComponentType ctype) throws Exception {
        if (ctype == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.DELETE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ComponentType ct = em.find(ComponentType.class, ctype.getComponentTypeId());
        em.remove(ct);
        makeAuditEntry(EntityTypeOperation.DELETE,ctype.getName(),"Deleted component type");
    }

    // ---------------- Component Type Property ---------------------
    
    public void saveCompTypeProp(ComptypeProperty ctprop, boolean create) throws Exception {
        if (ctprop == null) {
            logger.log(Level.SEVERE, "saveCompTypeProp: property is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ctprop.setModifiedAt(new Date());
        // ctprop.setType("a");
        ctprop.setModifiedBy("user");
        ComptypeProperty newProp = em.merge(ctprop);

        if (create) { // create instead of update
            ComponentType ctype = ctprop.getComponentType();
            ctype.getComptypePropertyList().add(newProp);
            em.merge(ctype);
        }
        makeAuditEntry(EntityTypeOperation.UPDATE,ctprop.getComponentType().getName(),"Updated property " + ctprop.getProperty().getName());
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getCtypePropId() + " name " + newProp.getProperty().getName());
    }

    
    public void deleteCompTypeProp(ComptypeProperty ctp) throws Exception {
        if (ctp == null) {
            logger.log(Level.SEVERE, "deleteCompTypeProp: property is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting comp type property id " + ctp.getCtypePropId() + " name " + ctp.getProperty().getName());
        ComptypeProperty property = em.find(ComptypeProperty.class, ctp.getCtypePropId());
        ComponentType arec = property.getComponentType();
        arec.getComptypePropertyList().remove(property);
        em.remove(property);
        makeAuditEntry(EntityTypeOperation.UPDATE,ctp.getComponentType().getName(),"Deleted property " + ctp.getProperty().getName());
    }

    // ---------------- Component Type Artifact ---------------------
    
    public void saveCompTypeArtifact(ComptypeArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "saveCompTypeArtifact: artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        art.setModifiedAt(new Date());
        // ctprop.setType("a");
        art.setModifiedBy("user");
        ComptypeArtifact newArt = em.merge(art);

        if (create) { // create instead of update
            ComponentType ctype = art.getComponentType();
            ctype.getComptypeArtifactList().add(newArt);
            em.merge(ctype);
        }
        makeAuditEntry(EntityTypeOperation.UPDATE,art.getComponentType().getName(),"Updated artifact " + art.getName());
        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.getIsInternal());
        // logger.log(Level.INFO, "device serial " + device.getSerialNumber());
        // return newArt;

    }

    
    public void deleteCompTypeArtifact(ComptypeArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteCompTypeArtifact: alignment artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getArtifactId() + " des " + art.getDescription());
        ComptypeArtifact artifact = em.find(ComptypeArtifact.class, art.getArtifactId());
        ComponentType arec = artifact.getComponentType();
        arec.getComptypeArtifactList().remove(artifact);
        em.remove(artifact);
        makeAuditEntry(EntityTypeOperation.UPDATE,art.getComponentType().getName(),"Deleted artifact " + art.getName());
    }

    // ---------------- Component Type Assmebly ---------------------
    
    public void saveComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        if (prt != null) {
            prt.setModifiedAt(new Date());
            // ctprop.setType("a");
            prt.setModifiedBy("user");
            // ctprop.setComponentType1(findComponentType(ctprop.getDevicePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getDevicePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}", ctprop.getDevicePropertyPK().getComponentType(), ctprop.getDevicePropertyPK().getProperty(), ctprop.getType());
            prt.setParentType(ctype);
            em.merge(prt);
            em.merge(ctype);
            makeAuditEntry(EntityTypeOperation.UPDATE,ctype.getName(),"Updated component type. Added an assembly part");
        }
    }

    
    public void deleteComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        if (prt != null) {
            ComptypeAsm entity = em.find(ComptypeAsm.class, prt.getComptypeAsmId());
            em.remove(entity);
            em.merge(ctype);
            makeAuditEntry(EntityTypeOperation.UPDATE,ctype.getName(),"Updated component type. Deleted a part from assembly.");
        }
    }

}
