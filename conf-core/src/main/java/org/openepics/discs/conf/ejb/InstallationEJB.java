package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class InstallationEJB {

    private static final Logger logger = Logger.getLogger(InstallationEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // ----- Installation

    public List<InstallationRecord> findInstallationRec() {
        final List<InstallationRecord> irecs;
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<InstallationRecord> cq = cb.createQuery(InstallationRecord.class);

        final TypedQuery<InstallationRecord> query = em.createQuery(cq);
        irecs = query.getResultList();
        logger.log(Level.INFO, "Number of physical components: {0}", irecs.size());

        return irecs;
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveIRecord(InstallationRecord irec) {
        irec.setModifiedAt(new Date());
        em.merge(irec);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addIRecord(InstallationRecord irec) {
        em.persist(irec);
        final Slot slot = irec.getSlot();
        slot.getInstallationRecordList().add(irec);
        em.merge(slot);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteIRecord(InstallationRecord irec) {
        final InstallationRecord ct = em.find(InstallationRecord.class, irec.getId());
        em.remove(ct);
    }

    // ---------------- Artifact ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveInstallationArtifact(InstallationArtifact art) {
        art.setModifiedAt(new Date());
        final InstallationArtifact newArt = em.merge(art);
        logger.log(Level.INFO, "Artifact: name " + art.getName() + " description " + art.getDescription() + " uri " + art.getUri() + "is int " + art.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteInstallationArtifact(InstallationArtifact art) {
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        final InstallationArtifact artifact = em.find(InstallationArtifact.class, art.getId());
        final InstallationRecord irec = artifact.getInstallationRecord();

        em.remove(artifact);
        irec.getInstallationArtifactList().remove(artifact);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addInstallationArtifact(InstallationArtifact art) {
        final InstallationArtifact newArt = em.merge(art);
        final InstallationRecord arec = art.getInstallationRecord();
        arec.getInstallationArtifactList().add(newArt);
        em.merge(arec);
    }
}
