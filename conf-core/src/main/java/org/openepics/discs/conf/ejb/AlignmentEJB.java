package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
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
        record.setModifiedAt(new Date());
        em.persist(record);
    }    
        
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveAlignment(AlignmentRecord record) {
        record.setModifiedAt(new Date());
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
    public void addAlignmentProp(AlignmentPropertyValue propertyValye) {
        final AlignmentPropertyValue mergedPropertyValue = em.merge(propertyValye);      
        final AlignmentRecord parent = mergedPropertyValue.getAlignmentRecord();
        
        DateUtility.setModifiedAt(parent, mergedPropertyValue);

        parent.getAlignmentPropertyList().add(propertyValye);
        em.merge(parent);
    }        
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveAlignmentProp(AlignmentPropertyValue prop){
        final AlignmentPropertyValue mergedPropValue = em.merge(prop);
        
        final Date now = new Date();
        mergedPropValue.getAlignmentRecord().setModifiedAt(now);
        mergedPropValue.setModifiedAt(now);
        
        logger.log(Level.INFO, "Alignment Record Property: id " + mergedPropValue.getId() + " name " + mergedPropValue.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteAlignmentProp(AlignmentPropertyValue prop) {
        logger.log(Level.INFO, "deleting alignment type property id " + prop.getId() + " name " + prop.getProperty().getName());
                
        final AlignmentPropertyValue mergedProperty = em.merge(prop);
        final AlignmentRecord parent = prop.getAlignmentRecord();
        
        parent.setModifiedAt(new Date());
        
        parent.getAlignmentPropertyList().remove(mergedProperty);        
        em.remove(mergedProperty);
    }

    // ---------------- Alignment Record Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void addAlignmentArtifact(AlignmentArtifact artifact) {
        final AlignmentArtifact mergedArtifact = em.merge(artifact);
        final AlignmentRecord parent = mergedArtifact.getAlignmentRecord();

        DateUtility.setModifiedAt(parent, mergedArtifact);
        
        parent.getAlignmentArtifactList().add(mergedArtifact); 
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveAlignmentArtifact(AlignmentArtifact artifact) {
        final AlignmentArtifact mergedArtifact = em.merge(artifact);
        
        DateUtility.setModifiedAt(mergedArtifact.getAlignmentRecord(), mergedArtifact);
        
        logger.log(Level.INFO, "Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void deleteAlignmentArtifact(AlignmentArtifact artifact) {
        final AlignmentArtifact mergedArtifact = em.merge(artifact);
        final AlignmentRecord parent = mergedArtifact.getAlignmentRecord();
        
        parent.setModifiedAt(new Date());
        
        parent.getAlignmentArtifactList().remove(mergedArtifact);
        em.remove(mergedArtifact);
    }


}
