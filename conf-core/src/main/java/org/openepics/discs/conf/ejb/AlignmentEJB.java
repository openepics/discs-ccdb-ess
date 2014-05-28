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
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentProperty;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.AppProperties;

/**
 *
 * @author vuppala
 */
@Stateless
public class AlignmentEJB implements AlignmentEJBLocal {
private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");   
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private AuthEJBLocal authEJB;
    
    @Inject 
    private LoginManager loginManager;
    
    // ----------- Audit record ---------------------------------------
    private void makeAuditEntry(char oper, String key, String entry) {
        AuditRecord arec = new AuditRecord(null, new Date(), oper, loginManager.getUserid(), entry);
        arec.setEntityType(AppProperties.EN_ALIGNREC);
        arec.setEntityKey(key);
        em.persist(arec);
    }
    
    // ----------------  Alignment Record -------------------------
    @Override
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
    
    @Override
    public AlignmentRecord findAlignmentRec(int id) {
        return em.find(AlignmentRecord.class, id);
    }
    
    
    @Override
    public void saveAlignment(AlignmentRecord arec) throws Exception  {
        if (arec == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            return;
            // throw new Exception("property is null");        
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_ALIGNREC, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        arec.setModifiedAt(new Date());   
        logger.log(Level.INFO, "Preparing to save device");
        em.persist(arec);   
        makeAuditEntry(AppProperties.OPER_UPDATE,arec.getRecordNumber(),"updated alignment record");
    }
    
    @Override
    public void deleteAlignment(AlignmentRecord arec) throws Exception  {
        if (arec == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            return;        
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_ALIGNREC, AppProperties.OPER_DELETE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        AlignmentRecord ct = em.find(AlignmentRecord.class,arec.getAlignmentRecordId());
        em.remove(ct);       
        makeAuditEntry(AppProperties.OPER_DELETE,arec.getRecordNumber(),"deleted alignment record");
    }
    
    // ------------------ Property ---------------
    @Override
    public void saveAlignmentProp(AlignmentProperty prop, boolean create) throws Exception  {
        if (prop == null) {
            logger.log(Level.SEVERE, "saveDeviceProp: property is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_ALIGNREC, AppProperties.OPER_UPDATE)) {
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
        makeAuditEntry(AppProperties.OPER_UPDATE,prop.getAlignmentRecord().getRecordNumber(),"updated alignment property " + prop.getProperty().getName());
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getAlignPropId() + " name " + newProp.getProperty().getName());
    }
    
    @Override
    public void deleteAlignmentProp(AlignmentProperty prop) throws Exception  {
        if (prop == null) {
            logger.log(Level.SEVERE, "deleteAlignmentArtifact: dev-artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_ALIGNREC, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        AlignmentProperty property = em.find(AlignmentProperty.class, prop.getAlignPropId());
        AlignmentRecord arec = property.getAlignmentRecord();
        arec.getAlignmentPropertyList().remove(property);
        em.remove(property);
        makeAuditEntry(AppProperties.OPER_DELETE,prop.getAlignmentRecord().getRecordNumber(),"deleted alignment property " + prop.getProperty().getName());
    } 
    
    // ---------------- Artifact ---------------------
    
    @Override
    public void saveAlignmentArtifact(AlignmentArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "saveAlignmentArtifact: artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_ALIGNREC, AppProperties.OPER_UPDATE)) {
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
        makeAuditEntry(AppProperties.OPER_UPDATE,art.getAlignmentRecord().getRecordNumber(),"updated alignment artifact " + art.getName());
        // art.setAlignmentRecord(em.merge(arec)); // todo: improve this code. this is not the right way.
        logger.log(Level.INFO, "Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri() + "is int " + newArt.getIsInternal());
        // logger.log(Level.INFO, "device serial " + device.getSerialNumber());       
    }
    
    @Override
    public void deleteAlignmentArtifact(AlignmentArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteAlignmentArtifact: alignment artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (! authEJB.userHasAuth(user, AppProperties.EN_ALIGNREC, AppProperties.OPER_UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getArtifactId() + " des " + art.getDescription());
        AlignmentArtifact artifact = em.find(AlignmentArtifact.class, art.getArtifactId());
        AlignmentRecord arec = artifact.getAlignmentRecord();
        arec.getAlignmentArtifactList().remove(artifact);
        em.remove(artifact);   
        makeAuditEntry(AppProperties.OPER_UPDATE,art.getAlignmentRecord().getRecordNumber(),"deleted alignment artifact " + art.getName());
    }
}
