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
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * 
 */
@Stateless public class ComptypeEJB {
    private static final Logger logger = Logger.getLogger(ComptypeEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

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
    @Authorized
    public void saveComponentType(ComponentType ctype) {
        ctype.setModifiedAt(new Date());
        em.merge(ctype);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addComponentType(ComponentType ctype) {
        em.persist(ctype);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteComponentType(ComponentType ctype) {
        final ComponentType merged = em.merge(ctype);
        em.remove(merged);
    }

    // ---------------- Component Type Property ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveCompTypeProp(ComptypePropertyValue ctprop) {
        ComptypePropertyValue newProp = em.merge(ctprop);
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteCompTypeProp(ComptypePropertyValue ctp) {
        logger.log(Level.INFO, "deleting comp type property id " + ctp.getId() + " name " + ctp.getProperty().getName());
        ComptypePropertyValue property = em.find(ComptypePropertyValue.class, ctp.getId());
        ComponentType arec = property.getComponentType();
        arec.getComptypePropertyList().remove(property);
        em.remove(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addCompTypeProp(ComptypePropertyValue ctp) {
        final ComptypePropertyValue newProp = em.merge(ctp);
        final ComponentType ctype = ctp.getComponentType();
        ctype.getComptypePropertyList().add(newProp);
        em.merge(ctype);
    }

    // ---------------- Component Type Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveCompTypeArtifact(ComptypeArtifact art) throws Exception {
        ComptypeArtifact newArt = em.merge(art);
        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteCompTypeArtifact(ComptypeArtifact art) throws Exception {
        ComptypeArtifact artifact = em.find(ComptypeArtifact.class, art.getId());
        ComponentType arec = artifact.getComponentType();
        arec.getComptypeArtifactList().remove(artifact);
        em.remove(artifact);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addCompTypeArtifact(ComptypeArtifact art) {
        final ComptypeArtifact newArt = em.merge(art);
        final ComponentType ctype = art.getComponentType();
        ctype.getComptypeArtifactList().add(newArt);
        em.merge(ctype);
    }

    // ---------------- Component Type Assmebly ---------------------

    public void saveComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception {
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
        if (prt != null) {
            ComptypeAsm entity = em.find(ComptypeAsm.class, prt.getId());
            em.remove(entity);
            em.merge(ctype);
        }
    }

}
