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
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.AppProperties;

/**
 *
 * @author vuppala
 */
@Stateless
public class ComptypeEJB implements ComptypeEJBLocal {
    @EJB
    private AuthEJBLocal authEJB;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;
    @Inject
    private LoginManager loginManager;
    
    // ----------- Audit record ---------------------------------------
    private void makeAuditEntry(char oper, String key, String entry) {
        AuditRecord arec = new AuditRecord(null, new Date(), oper, loginManager.getUserid(), entry);
        arec.setEntityType(AppProperties.EN_COMPTYPE);
        arec.setEntityKey(key);
        em.persist(arec);
    }
    
    // ----------------  Component Type -------------------------
    @Override
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

    @Override
    public ComponentType findComponentType(int id) {
        return em.find(ComponentType.class, id);
    }

    @Override
    public void saveComponentType(ComponentType ctype) throws Exception {
        if (ctype == null) {
            logger.log(Level.SEVERE, "Comp Type is null!");
            throw new Exception("Comp Type is null");
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ctype.setModifiedAt(new Date());

        em.merge(ctype);
        makeAuditEntry(AppProperties.OPER_UPDATE,ctype.getName(),"Updated component type");
    }

    @Override
    public void addComponentType(ComponentType ctype) throws Exception {
        if (ctype == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_CREATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ctype.setModifiedAt(new Date());
        em.persist(ctype);
        makeAuditEntry(AppProperties.OPER_CREATE,ctype.getName(),"Created component type");
    }

    @Override
    public void deleteComponentType(ComponentType ctype) throws Exception {
        if (ctype == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_DELETE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        ComponentType ct = em.find(ComponentType.class, ctype.getComponentTypeId());
        em.remove(ct);
        makeAuditEntry(AppProperties.OPER_DELETE,ctype.getName(),"Deleted component type");
    }

    // ---------------- Component Type Property ---------------------
    @Override
    public void saveCompTypeProp(ComptypeProperty ctprop, boolean create) throws Exception {
        if (ctprop == null) {
            logger.log(Level.SEVERE, "saveCompTypeProp: property is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
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
        makeAuditEntry(AppProperties.OPER_UPDATE,ctprop.getComponentType().getName(),"Updated property " + ctprop.getProperty().getName());
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getCtypePropId() + " name " + newProp.getProperty().getName());
    }

    @Override
    public void deleteCompTypeProp(ComptypeProperty ctp) throws Exception {
        if (ctp == null) {
            logger.log(Level.SEVERE, "deleteCompTypeProp: property is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting comp type property id " + ctp.getCtypePropId() + " name " + ctp.getProperty().getName());
        ComptypeProperty property = em.find(ComptypeProperty.class, ctp.getCtypePropId());
        ComponentType arec = property.getComponentType();
        arec.getComptypePropertyList().remove(property);
        em.remove(property);
        makeAuditEntry(AppProperties.OPER_UPDATE,ctp.getComponentType().getName(),"Deleted property " + ctp.getProperty().getName());
    }

    // ---------------- Component Type Artifact ---------------------
    @Override
    public void saveCompTypeArtifact(ComptypeArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "saveCompTypeArtifact: artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
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
        makeAuditEntry(AppProperties.OPER_UPDATE,art.getComponentType().getName(),"Updated artifact " + art.getName());
        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.getIsInternal());
        // logger.log(Level.INFO, "device serial " + device.getSerialNumber());
        // return newArt;

    }

    @Override
    public void deleteCompTypeArtifact(ComptypeArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteCompTypeArtifact: alignment artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getArtifactId() + " des " + art.getDescription());
        ComptypeArtifact artifact = em.find(ComptypeArtifact.class, art.getArtifactId());
        ComponentType arec = artifact.getComponentType();
        arec.getComptypeArtifactList().remove(artifact);
        em.remove(artifact);
        makeAuditEntry(AppProperties.OPER_UPDATE,art.getComponentType().getName(),"Deleted artifact " + art.getName());
    }

    // ---------------- Component Type Assmebly ---------------------
    @Override
    public void saveComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
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
            makeAuditEntry(AppProperties.OPER_UPDATE,ctype.getName(),"Updated component type. Added an assembly part");
        }
    }

    @Override
    public void deleteComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_COMPTYPE, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        if (prt != null) {
            ComptypeAsm entity = em.find(ComptypeAsm.class, prt.getComptypeAsmId());
            em.remove(entity);
            em.merge(ctype);
            makeAuditEntry(AppProperties.OPER_UPDATE,ctype.getName(),"Updated component type. Deleted a part from assembly.");
        }
    }

}
