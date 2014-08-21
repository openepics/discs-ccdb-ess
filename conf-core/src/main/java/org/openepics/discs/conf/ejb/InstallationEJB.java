package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * 
 */
@Stateless public class InstallationEJB {
    private static final Logger logger = Logger.getLogger(InstallationEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @Inject private ConfigurationEntityUtility entityUtility;

    // ----- Installation Records

    public List<InstallationRecord> findInstallationRec() {
        final CriteriaQuery<InstallationRecord> cq = em.getCriteriaBuilder().createQuery(InstallationRecord.class);
        cq.from(InstallationRecord.class);

        final List<InstallationRecord> installationRecs = em.createQuery(cq).getResultList();
        logger.log(Level.INFO, "Number of installation records: {0}", installationRecs.size());

        return installationRecs;
    }
    
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addIRecord(InstallationRecord irec) {
        entityUtility.setModified(irec);
        em.persist(irec);        
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveIRecord(InstallationRecord irec) {
        entityUtility.setModified(irec);
        em.merge(irec);        
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteIRecord(InstallationRecord irec) {
        final InstallationRecord mergedIRec = em.merge(irec);
        em.remove(mergedIRec);
    }

    // ---------------- Installation Record Artifact ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addInstallationArtifact(InstallationArtifact artifact) {
        final InstallationRecord parent = artifact.getInstallationRecord();
        
        entityUtility.setModified(parent, artifact);
        
        parent.getInstallationArtifactList().add(artifact);
        em.merge(parent);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveInstallationArtifact(InstallationArtifact artifact) {
        final InstallationArtifact mergedArtifact = em.merge(artifact);
        
        entityUtility.setModified(mergedArtifact.getInstallationRecord(), mergedArtifact);
        
        logger.log(Level.INFO, "Installation Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteInstallationArtifact(InstallationArtifact artifact) {
        final InstallationArtifact mergedArtifact = em.merge(artifact);
        final InstallationRecord parent = mergedArtifact.getInstallationRecord();
        
        entityUtility.setModified(parent);
        
        parent.getInstallationArtifactList().remove(mergedArtifact);
        em.remove(mergedArtifact);
    }
}
