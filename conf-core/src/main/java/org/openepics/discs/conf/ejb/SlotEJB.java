package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * 
 */
@Stateless public class SlotEJB {
    private static final Logger logger = Logger.getLogger(SlotEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // ---------------- Layout Slot -------------------------

    public List<Slot> findLayoutSlot() {
        final CriteriaQuery<Slot> cq = em.getCriteriaBuilder().createQuery(Slot.class);
        cq.from(Slot.class);

        final List<Slot> slots = em.createQuery(cq).getResultList();
        logger.log(Level.INFO, "Number of installation slots: {0}", slots.size());

        return slots;
    }

    public Slot findSlotByName(String name) {
        Slot slot;
        try {
            slot = em.createNamedQuery("Slot.findByName", Slot.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            slot = null;
        }
        return slot;
    }

    public List<Slot> findSlotByNameContainingString(String namePart) {
        return em.createNamedQuery("Slot.findByNameContaining", Slot.class).setParameter("name", namePart).getResultList();
    }

    public SlotRelation findSlotRelationByName(SlotRelationName name) {
        SlotRelation slotRelation;
        try {
            slotRelation = em.createNamedQuery("SlotRelation.findByName", SlotRelation.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            slotRelation = null;
        }
        return slotRelation;
    }

    public Slot findLayoutSlot(Long id) {
        return em.find(Slot.class, id);
    }

    public List<SlotPair> findSlotPairsByParentChildRelation(String childName, String parentName, SlotRelationName relationName) {
        return em.createNamedQuery("SlotPair.findByParentChildRelation", SlotPair.class).setParameter("childName", childName).setParameter("parentName", parentName).setParameter("relationName", relationName).getResultList();
    }
    
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addSlot(Slot slot) {
        slot.setModifiedAt(new Date());
        em.persist(slot);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveLayoutSlot(Slot slot) {
        slot.setModifiedAt(new Date());
        em.merge(slot);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteLayoutSlot(Slot slot) {
        final Slot mergedSlot = em.merge(slot);
        em.remove(mergedSlot);
    }
    

    // ------------------ Slot Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void addSlotProperty(SlotPropertyValue propertyValue) {
        final SlotPropertyValue mergedPropertyValue = em.merge(propertyValue);
        final Slot parent = mergedPropertyValue.getSlot();
        
        DateUtility.setModifiedAt(parent, mergedPropertyValue);
        
        parent.getSlotPropertyList().add(mergedPropertyValue);        
        em.merge(parent);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveSlotProp(SlotPropertyValue propertyValue) {
        final SlotPropertyValue mergedPropertyValue = em.merge(propertyValue);
        
        DateUtility.setModifiedAt(mergedPropertyValue.getSlot(), mergedPropertyValue);
        
        logger.log(Level.INFO, "Slot Property: id " + mergedPropertyValue.getId() + " name " + mergedPropertyValue.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteSlotProp(SlotPropertyValue propertyValue) {
        logger.log(Level.INFO, "deleting slot property id " + propertyValue.getId() + " name " + propertyValue.getProperty().getName());
        
        final SlotPropertyValue mergedPropertyValue = em.merge(propertyValue);
        final Slot parent = mergedPropertyValue.getSlot();
        
        parent.setModifiedAt(new Date());
        
        parent.getSlotPropertyList().remove(mergedPropertyValue);
        em.remove(mergedPropertyValue);
    }


    // ---------------- Slot Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addSlotArtifact(SlotArtifact artifact) {
        final SlotArtifact mergedArtifact = em.merge(artifact);
        final Slot parent = mergedArtifact.getSlot();
        
        DateUtility.setModifiedAt(parent, mergedArtifact);
        
        parent.getSlotArtifactList().add(mergedArtifact);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveSlotArtifact(SlotArtifact artifact) {
        final SlotArtifact mergedArtifact = em.merge(artifact);
        
        DateUtility.setModifiedAt(mergedArtifact.getSlot(), mergedArtifact);
        
        logger.log(Level.INFO, "Slot Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteSlotArtifact(SlotArtifact artifact) { 
        final SlotArtifact mergedArtifact = em.merge(artifact);
        final Slot parent = mergedArtifact.getSlot();

        parent.setModifiedAt(new Date());
        
        parent.getSlotArtifactList().remove(mergedArtifact);
        em.remove(mergedArtifact);
    }
    
   
    // ---------------- Related Slots ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addSlotPair(SlotPair slotPair) {
        em.persist(slotPair);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveSlotPair(SlotPair slotPair) {
        em.merge(slotPair);

        logger.log(Level.INFO, "saved slot pair: child " + slotPair.getChildSlot().getName() + " parent " + slotPair.getParentSlot().getName() + " relation ");
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteSlotPair(SlotPair slotPair) {
        final SlotPair mergedSlotPair = em.merge(slotPair);
        em.remove(mergedSlotPair);
    }


    private final String ROOT_COMPONENT_TYPE = "_ROOT"; // ToDo: Get the root
                                                        // type from
                                                        // configuration (JNDI,
                                                        // config table etc)

    public List<Slot> getRootNodes(SlotRelationName relationName) {
        return em.createQuery("SELECT cp.childSlot FROM SlotPair cp "
                + "WHERE cp.parentSlot.componentType.name = :ctype AND cp.slotRelation.name = :relname "
                + "ORDER BY cp.childSlot.name", 
                Slot.class).
                setParameter("ctype", ROOT_COMPONENT_TYPE).
                setParameter("relname", relationName).getResultList();
    }

    public List<Slot> getRootNodes() {
        return getRootNodes(SlotRelationName.CONTAINS); // ToDo: get the relation name from
                                         // configuration
    }

    public List<Slot> relatedChildren(String compName) {
        return em.createQuery("SELECT cp.childSlot FROM SlotPair cp "
                + "WHERE cp.parentSlot.name = :compname AND cp.slotRelation.name = :relname", Slot.class).
                setParameter("compname", compName).
                setParameter("relname", SlotRelationName.CONTAINS).getResultList();
    }
}