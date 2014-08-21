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
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentPropertyValue;
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * 
 */
@Stateless public class AlignmentEJB {
    private static final Logger logger = Logger.getLogger(AlignmentEJB.class.getCanonicalName());   
    @PersistenceContext private EntityManager em;
    @Inject private ConfigurationEntityUtility entityUtility;
    
    // ---------------- Alignment Record -------------------------
    
    public List<AlignmentRecord> findAlignmentRec() {
        final CriteriaQuery<AlignmentRecord> cq =  em.getCriteriaBuilder().
                createQuery(AlignmentRecord.class);
        cq.from(AlignmentRecord.class);
                
        final List<AlignmentRecord> records = em.createQuery(cq).getResultList();
        logger.log(Level.INFO, "Number of alignment records: {0}", records.size());

        return records;
    }

    public AlignmentRecord findAlignmentRec(Long id) {
        return em.find(AlignmentRecord.class, id);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addAlignment(AlignmentRecord record) {
        entityUtility.setModified(record);
        em.persist(record);
    }    
        
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveAlignment(AlignmentRecord record) {
        entityUtility.setModified(record);
        em.merge(record);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteAlignment(AlignmentRecord record) throws Exception {
        final AlignmentRecord mergedRecord = em.merge(record);
        em.remove(mergedRecord);
    }
    
    
    // ------------------ Alignment Property ---------------
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addAlignmentProp(AlignmentPropertyValue propertyValue) {
        final AlignmentRecord parent = propertyValue.getAlignmentRecord();
        
        entityUtility.setModified(parent, propertyValue);

        parent.getAlignmentPropertyList().add(propertyValue);
        em.merge(parent);
    }        
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveAlignmentProp(AlignmentPropertyValue prop){
        final AlignmentPropertyValue mergedPropValue = em.merge(prop);

        entityUtility.setModified(mergedPropValue.getAlignmentRecord(), mergedPropValue);
        
        logger.log(Level.INFO, "Alignment Record Property: id " + mergedPropValue.getId() + " name " + mergedPropValue.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteAlignmentProp(AlignmentPropertyValue prop) {
        logger.log(Level.INFO, "deleting alignment type property id " + prop.getId() + " name " + prop.getProperty().getName());
                
        final AlignmentPropertyValue mergedProperty = em.merge(prop);
        final AlignmentRecord parent = prop.getAlignmentRecord();
        
        entityUtility.setModified(parent);
        
        parent.getAlignmentPropertyList().remove(mergedProperty);        
        em.remove(mergedProperty);
    }

    // ---------------- Alignment Record Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addAlignmentArtifact(AlignmentArtifact artifact) {
        final AlignmentRecord parent = artifact.getAlignmentRecord();

        entityUtility.setModified(parent, artifact);
        
        parent.getAlignmentArtifactList().add(artifact);
        em.merge(parent);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveAlignmentArtifact(AlignmentArtifact artifact) {
        final AlignmentArtifact mergedArtifact = em.merge(artifact);
        
        entityUtility.setModified(mergedArtifact.getAlignmentRecord(), mergedArtifact);
        
        logger.log(Level.INFO, "Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteAlignmentArtifact(AlignmentArtifact artifact) {
        final AlignmentArtifact mergedArtifact = em.merge(artifact);
        final AlignmentRecord parent = mergedArtifact.getAlignmentRecord();
        
        entityUtility.setModified(parent);
        
        parent.getAlignmentArtifactList().remove(mergedArtifact);
        em.remove(mergedArtifact);
    }
}
