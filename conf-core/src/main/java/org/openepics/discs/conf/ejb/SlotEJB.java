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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.openepics.discs.conf.ent.CtArtifact;
import org.openepics.discs.conf.ent.LsArtifact;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotProperty;

/**
 *
 * @author vuppala
 */
@Stateless
public class SlotEJB implements SlotEJBLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");   
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;
    
     // ----------------  Layout Slot  -------------------------
    @Override
    public List<Slot> findLayoutSlot() {
        List<Slot> comps;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Slot> cq = cb.createQuery(Slot.class);
        Root<Slot> prop = cq.from(Slot.class);       
        
        TypedQuery<Slot> query = em.createQuery(cq);
        comps = query.getResultList();
        logger.log(Level.INFO, "Number of logical components: {0}", comps.size());
        
        return comps;        
    }
    
    @Override
    public Slot findLayoutSlot(int id) {
        return em.find(Slot.class, id);
    }
    
    @Override
    public void saveLayoutSlot(Slot slot)  {
        if (slot == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            return;
            // throw new Exception("property is null");        
        }
        slot.setModifiedAt(new Date());   
        logger.log(Level.INFO, "Preparing to save slot");
        em.persist(slot);        
    }
    
    @Override
    public void deleteLayoutSlot(Slot slot) throws Exception {
        if (slot == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");        
        }
        Slot ct = em.find(Slot.class,slot.getSlotId());
        em.remove(ct);        
    }
    
    // ------------------ Slot Property ---------------
    @Override
    public void saveSlotProp(Slot slot, SlotProperty prop) {
        if (prop != null) {
            prop.setModifiedAt(new Date());
            // ctprop.setType("a");
            prop.setModifiedBy("user");
            // ctprop.setVersion(0);
            // ctprop.setComponentType1(findComponentType(ctprop.getComponentTypePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getComponentTypePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}", ctprop.getComponentTypePropertyPK().getComponentType(), ctprop.getComponentTypePropertyPK().getProperty(), ctprop.getType());
            em.merge(prop);
            em.merge(slot);
        }
    }
    
    @Override
    public void deleteSlotProp(Slot slot, SlotProperty prop) {
        if (prop != null) {            
            SlotProperty entity = em.find(SlotProperty.class, prop.getSlotPropertyPK());
            em.remove(entity);
            em.merge(slot);
        }
    }
    
    
    
    // ---------------- Slot Artifact ---------------------
    
    @Override
    public void saveSlotArtifact(Slot slot, LsArtifact art) {
        if (art != null) {
            art.setModifiedAt(new Date());
            // ctprop.setType("a");
            art.setModifiedBy("user");           
            // ctprop.setComponentType1(findComponentType(ctprop.getComponentTypePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getComponentTypePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}", ctprop.getComponentTypePropertyPK().getComponentType(), ctprop.getComponentTypePropertyPK().getProperty(), ctprop.getType());
            art.setSlot(slot);
            // ctype.getCtArtifactList().add(art);
            logger.log(Level.INFO, "CompType Artifact: name" + art.getName() + " description " + art.getDescription() + " uri " + art.getUri());
            em.merge(art);
            em.merge(slot);
        }
    }
    
    @Override
    public void deleteSlotArtifact(Slot ctype, LsArtifact art) {
        if (art != null) {            
            CtArtifact entity = em.find(CtArtifact.class, art.getArtifactId());
            em.remove(entity);
            em.merge(ctype);
        }
    }
}
