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
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentPropertyValue;
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class AlignmentEJB {

    private static final Logger logger = Logger.getLogger(AlignmentEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // ---------------- Alignment Record -------------------------

    public List<AlignmentRecord> findAlignmentRec() {
        final List<AlignmentRecord> comps;
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<AlignmentRecord> cq = cb.createQuery(AlignmentRecord.class);

        final TypedQuery<AlignmentRecord> query = em.createQuery(cq);
        comps = query.getResultList();
        logger.log(Level.INFO, "Number of physical components: {0}", comps.size());

        return comps;
    }

    public AlignmentRecord findAlignmentRec(Long id) {
        return em.find(AlignmentRecord.class, id);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveAlignment(AlignmentRecord arec) {
        arec.setModifiedAt(new Date());
        logger.log(Level.INFO, "Preparing to save device");
        em.persist(arec);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteAlignment(AlignmentRecord arec) throws Exception {
        final AlignmentRecord ct = em.find(AlignmentRecord.class, arec.getId());
        ct.setModifiedAt(new Date());
        em.remove(ct);
    }

    // ------------------ Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveAlignmentProp(AlignmentPropertyValue prop){
        final AlignmentPropertyValue newProp = em.merge(prop);
        newProp.setModifiedAt(new Date());
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addAlignmentProp(AlignmentPropertyValue prop) {
        final AlignmentPropertyValue newProp = em.merge(prop);
        final AlignmentRecord slot = prop.getAlignmentRecord();
        slot.getAlignmentPropertyList().add(newProp);
        em.merge(slot);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteAlignmentProp(AlignmentPropertyValue prop) {
        final AlignmentPropertyValue property = em.find(AlignmentPropertyValue.class, prop.getId());
        final AlignmentRecord arec = property.getAlignmentRecord();
        prop.setModifiedAt(new Date());
        arec.getAlignmentPropertyList().remove(property);
        em.remove(property);
    }

    // ---------------- Artifact ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveAlignmentArtifact(AlignmentArtifact art) {
        final AlignmentArtifact newArt = em.merge(art);
        newArt.setModifiedAt(new Date());
        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteAlignmentArtifact(AlignmentArtifact art) {
        final AlignmentArtifact artifact = em.find(AlignmentArtifact.class, art.getId());
        final AlignmentRecord arec = artifact.getAlignmentRecord();
        arec.setModifiedAt(new Date());
        arec.getAlignmentArtifactList().remove(artifact);
        em.remove(artifact);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addAlignmentArtifact(AlignmentArtifact art) {
        final AlignmentArtifact newArt = em.merge(art);
        final AlignmentRecord arec = art.getAlignmentRecord();
        arec.setModifiedAt(new Date());
        arec.getAlignmentArtifactList().add(newArt);
        em.merge(arec);
    }
}
