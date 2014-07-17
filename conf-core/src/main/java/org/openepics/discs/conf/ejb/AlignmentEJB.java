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

import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentProperty;
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ui.LoginManager;

/**
 *
 * @author vuppala
 */
@Stateless public class AlignmentEJB {

    private static final Logger logger = Logger.getLogger(AlignmentEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @EJB private AuthEJB authEJB;

    @Inject private LoginManager loginManager;

    // ----------- Audit record ---------------------------------------
    private void makeAuditEntry(EntityTypeOperation oper, String key, String entry) {
        AuditRecord arec = new AuditRecord(new Date(), oper, loginManager.getUserid(), entry);
        arec.setEntityType(EntityType.ALIGNMENT_RECORD);
        arec.setEntityKey(key);
        em.persist(arec);
    }

    // ---------------- Alignment Record -------------------------

    public List<AlignmentRecord> findAlignmentRec() {
        List<AlignmentRecord> comps;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AlignmentRecord> cq = cb.createQuery(AlignmentRecord.class);
        Root<AlignmentRecord> prop = cq.from(AlignmentRecord.class);

        TypedQuery<AlignmentRecord> query = em.createQuery(cq);
        comps = query.getResultList();
        logger.log(Level.INFO, "Number of physical components: {0}", comps.size());

        return comps;
    }

    public AlignmentRecord findAlignmentRec(Long id) {
        return em.find(AlignmentRecord.class, id);
    }

    public void saveAlignment(AlignmentRecord arec) throws Exception {
        if (arec == null) {
            logger.log(Level.SEVERE, "Property is null!");
            return;
            // throw new Exception("property is null");
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.ALIGNMENT_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        arec.setModifiedAt(new Date());
        logger.log(Level.INFO, "Preparing to save device");
        em.persist(arec);
        makeAuditEntry(EntityTypeOperation.UPDATE, arec.getRecordNumber(), "updated alignment record");
    }

    public void deleteAlignment(AlignmentRecord arec) throws Exception {
        if (arec == null) {
            logger.log(Level.SEVERE, "Property is null!");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.ALIGNMENT_RECORD, EntityTypeOperation.DELETE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        AlignmentRecord ct = em.find(AlignmentRecord.class, arec.getId());
        em.remove(ct);
        makeAuditEntry(EntityTypeOperation.DELETE, arec.getRecordNumber(), "deleted alignment record");
    }

    // ------------------ Property ---------------

    public void saveAlignmentProp(AlignmentProperty prop, boolean create) throws Exception {
        if (prop == null) {
            logger.log(Level.SEVERE, "saveDeviceProp: property is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.ALIGNMENT_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        prop.setModifiedAt(new Date());
        // ctprop.setType("a");
        prop.setModifiedBy("user");
        AlignmentProperty newProp = em.merge(prop);

        if (create) { // create instead of update
            AlignmentRecord slot = prop.getAlignmentRecord();
            slot.getAlignmentPropertyList().add(newProp);
            em.merge(slot);
        }
        makeAuditEntry(EntityTypeOperation.UPDATE, prop.getAlignmentRecord().getRecordNumber(), "updated alignment property " + prop.getProperty().getName());
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    public void deleteAlignmentProp(AlignmentProperty prop) throws Exception {
        if (prop == null) {
            logger.log(Level.SEVERE, "deleteAlignmentArtifact: dev-artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.ALIGNMENT_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        AlignmentProperty property = em.find(AlignmentProperty.class, prop.getId());
        AlignmentRecord arec = property.getAlignmentRecord();
        arec.getAlignmentPropertyList().remove(property);
        em.remove(property);
        makeAuditEntry(EntityTypeOperation.DELETE, prop.getAlignmentRecord().getRecordNumber(), "deleted alignment property " + prop.getProperty().getName());
    }

    // ---------------- Artifact ---------------------

    public void saveAlignmentArtifact(AlignmentArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "saveAlignmentArtifact: artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.ALIGNMENT_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        art.setModifiedAt(new Date());
        art.setModifiedBy("user");
        AlignmentArtifact newArt = em.merge(art);

        if (create) { // create instead of update
            AlignmentRecord arec = art.getAlignmentRecord();
            arec.getAlignmentArtifactList().add(newArt);
            em.merge(arec);
        }
        makeAuditEntry(EntityTypeOperation.UPDATE, art.getAlignmentRecord().getRecordNumber(), "updated alignment artifact " + art.getName());
        // art.setAlignmentRecord(em.merge(arec)); // todo: improve this code.
        // this is not the right way.
        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.getIsInternal());
        // logger.log(Level.INFO, "device serial " + device.getSerialNumber());
    }

    public void deleteAlignmentArtifact(AlignmentArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteAlignmentArtifact: alignment artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.ALIGNMENT_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        AlignmentArtifact artifact = em.find(AlignmentArtifact.class, art.getId());
        AlignmentRecord arec = artifact.getAlignmentRecord();
        arec.getAlignmentArtifactList().remove(artifact);
        em.remove(artifact);
        makeAuditEntry(EntityTypeOperation.UPDATE, art.getAlignmentRecord().getRecordNumber(), "deleted alignment artifact " + art.getName());
    }
}
