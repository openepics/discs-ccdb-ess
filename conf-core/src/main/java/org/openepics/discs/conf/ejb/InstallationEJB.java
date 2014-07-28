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
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.LoginManager;

/**
 *
 * @author vuppala
 */
@Stateless public class InstallationEJB {

    private static final Logger logger = Logger.getLogger(InstallationEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @EJB private AuthEJB authEJB;

    @Inject private LoginManager loginManager;

    // ----------- Audit record ---------------------------------------
    private void makeAuditEntry(EntityTypeOperation oper, String key, String entry, Long id) {
        AuditRecord arec = new AuditRecord(new Date(), oper, loginManager.getUserid(), entry);
        arec.setEntityType(EntityType.INSTALLATION_RECORD);
        arec.setEntityKey(key);
        em.persist(arec);
    }

    // ----- Installation

    public List<InstallationRecord> findInstallationRec() {
        List<InstallationRecord> irecs;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<InstallationRecord> cq = cb.createQuery(InstallationRecord.class);
        Root<InstallationRecord> prop = cq.from(InstallationRecord.class);

        TypedQuery<InstallationRecord> query = em.createQuery(cq);
        irecs = query.getResultList();
        logger.log(Level.INFO, "Number of physical components: {0}", irecs.size());

        return irecs;
    }

    public void saveIRecord(InstallationRecord irec, boolean create) throws Exception {
        if (irec == null) {
            logger.log(Level.SEVERE, "Installation Record is null!");
            return;
            // throw new Exception("property is null");
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.INSTALLATION_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        irec.setModifiedAt(new Date());

        logger.log(Level.INFO, "Preparing to save installation record");

        if (irec.getId() == null) { // new record
            em.persist(irec);
            Slot slot = irec.getSlot();
            slot.getInstallationRecordList().add(irec);
            // em.merge(slot);
            // todo: add to the list for the device
        } else {
            em.merge(irec);
        }
        makeAuditEntry(EntityTypeOperation.UPDATE, irec.getRecordNumber(), "updated installation record ", irec.getId());
    }

    public void deleteIRecord(InstallationRecord irec) throws Exception {
        if (irec == null) {
            logger.log(Level.SEVERE, "Installation Record is null!");
            throw new Exception("Installation Record is null");
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.INSTALLATION_RECORD, EntityTypeOperation.DELETE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        InstallationRecord ct = em.find(InstallationRecord.class, irec.getId());
        em.remove(ct);
        makeAuditEntry(EntityTypeOperation.DELETE, irec.getRecordNumber(), "deleted installation record ", irec.getId());
    }

    // ---------------- Artifact ---------------------

    public void saveInstallationArtifact(InstallationArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteInstallationArtifact: Installation Record is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.INSTALLATION_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        art.setModifiedAt(new Date());
        art.setModifiedBy("user");
        InstallationArtifact newArt = em.merge(art);
        if (create) { // create instead of update
            InstallationRecord arec = art.getInstallationRecord();
            arec.getInstallationArtifactList().add(newArt);
            em.merge(arec);
        }
        makeAuditEntry(EntityTypeOperation.UPDATE, art.getInstallationRecord().getRecordNumber(), "updated installation artifact " + art.getName(), art.getInstallationRecord().getId());
        logger.log(Level.INFO, "Artifact: name " + art.getName() + " description " + art.getDescription() + " uri " + art.getUri() + "is int " + art.getIsInternal());
        // logger.log(Level.INFO, "device serial " + device.getSerialNumber());
    }

    public void deleteInstallationArtifact(InstallationArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteInstallationArtifact: irec-artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.INSTALLATION_RECORD, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        InstallationArtifact artifact = em.find(InstallationArtifact.class, art.getId());
        InstallationRecord irec = artifact.getInstallationRecord();

        em.remove(artifact);
        irec.getInstallationArtifactList().remove(artifact);
        makeAuditEntry(EntityTypeOperation.UPDATE, art.getInstallationRecord().getRecordNumber(), "deleted installation artifact " + art.getName(), art.getInstallationRecord().getId());
    }
}
