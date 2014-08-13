package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class ComptypeEJB {

    @EJB private AuthEJB authEJB;
    private static final Logger logger = Logger.getLogger(ComptypeEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @Inject private LoginManager loginManager;

    // ---------------- Component Type -------------------------

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

    public ComponentType findComponentType(Long id) {
        return em.find(ComponentType.class, id);
    }

    public ComponentType findComponentTypeByName(String name) {
        ComponentType componentType;
        try {
            componentType = em.createNamedQuery("ComponentType.findByName", ComponentType.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            componentType = null;
        }
        return componentType;
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveComponentType(ComponentType ctype) {
        ctype.setModifiedAt(new Date());
        em.merge(ctype);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addComponentType(ComponentType ctype) {
        em.persist(ctype);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteComponentType(ComponentType ctype) {
        final ComponentType merged = em.merge(ctype);
        em.remove(merged);
    }

    // ---------------- Component Type Property ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveCompTypeProp(ComptypePropertyValue ctprop, boolean create) {

        ctprop.setModifiedAt(new Date());
        ComptypePropertyValue newProp = em.merge(ctprop);

        if (create) { // create instead of update
            ComponentType ctype = ctprop.getComponentType();
            ctype.getComptypePropertyList().add(newProp);
            em.merge(ctype);
        }
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteCompTypeProp(ComptypePropertyValue ctp) {

        logger.log(Level.INFO, "deleting comp type property id " + ctp.getId() + " name " + ctp.getProperty().getName());
        ComptypePropertyValue property = em.find(ComptypePropertyValue.class, ctp.getId());
        ComponentType arec = property.getComponentType();
        arec.getComptypePropertyList().remove(property);
        em.remove(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addCompTypeProp(ComptypePropertyValue ctp) {
        em.persist(ctp);
    }

    // ---------------- Component Type Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveCompTypeArtifact(ComptypeArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "saveCompTypeArtifact: artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
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

        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.isInternal());

        // logger.log(Level.INFO, "device serial " + device.getSerialNumber());
        // return newArt;

    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteCompTypeArtifact(ComptypeArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteCompTypeArtifact: alignment artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        ComptypeArtifact artifact = em.find(ComptypeArtifact.class, art.getId());
        ComponentType arec = artifact.getComponentType();
        arec.getComptypeArtifactList().remove(artifact);
        em.remove(artifact);
    }

    // ---------------- Component Type Assmebly ---------------------

    public void saveComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        if (prt != null) {
            prt.setModifiedAt(new Date());
            // ctprop.setType("a");
            prt.setModifiedBy("user");
            // ctprop.setComponentType1(findComponentType(ctprop.getDevicePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getDevicePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}",
            // ctprop.getDevicePropertyPK().getComponentType(),
            // ctprop.getDevicePropertyPK().getProperty(), ctprop.getType());
            prt.setParentType(ctype);
            em.merge(prt);
            em.merge(ctype);
        }
    }

    public void deleteComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        if (prt != null) {
            ComptypeAsm entity = em.find(ComptypeAsm.class, prt.getId());
            em.remove(entity);
            em.merge(ctype);
        }
    }

}
