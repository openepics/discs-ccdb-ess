package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class SlotEJB {
    @EJB private AuthEJB authEJB;

    @Inject private LoginManager loginManager;
    private static final Logger logger = Logger.getLogger(SlotEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;


    // ---------------- Layout Slot -------------------------

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

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveLayoutSlot(Slot slot) {
        slot.setModifiedAt(new Date());
        em.merge(slot);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addSlot(Slot slot) {
        em.persist(slot);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteLayoutSlot(Slot slot) {
        Slot ct = em.find(Slot.class, slot.getId());
        em.remove(ct);
    }

    // ------------------ Slot Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveSlotProp(SlotPropertyValue prop, boolean create) {
        prop.setModifiedAt(new Date());
        // ctprop.setType("a");
        prop.setModifiedBy("user");
        SlotPropertyValue newProp = em.merge(prop);

        if (create) { // create instead of update
            Slot slot = prop.getSlot();
            slot.getSlotPropertyList().add(newProp);
            em.merge(slot);
        }
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteSlotProp(SlotPropertyValue prop) {
        SlotPropertyValue property = em.find(SlotPropertyValue.class, prop.getId());
        Slot slot = property.getSlot();
        slot.getSlotPropertyList().remove(property);
        em.remove(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addSlotProperty(SlotPropertyValue property) {
        em.persist(property);
    }

    // ---------------- Slot Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveSlotArtifact(SlotArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "saveSlotArtifact: artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.SLOT, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        art.setModifiedAt(new Date());
        art.setModifiedBy("user");
        SlotArtifact newArt = em.merge(art);
        if (create) { // create instead of update
            Slot slot = art.getSlot();
            slot.getSlotArtifactList().add(newArt);
            em.merge(slot);
        }
        logger.log(Level.INFO, "slot Artifact: name " + art.getName() + " description " + art.getDescription() + " uri " + art.getUri());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteSlotArtifact(SlotArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteAlignmentArtifact: alignment artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.SLOT, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        SlotArtifact artifact = em.find(SlotArtifact.class, art.getId());
        Slot slot = artifact.getSlot();
        slot.getSlotArtifactList().remove(artifact);
        em.remove(artifact);
    }

    // ---------------- Related Slots ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveSlotPair(SlotPair spair, boolean create) throws Exception {
        if (spair == null) {
            logger.log(Level.SEVERE, "saveSlotPair: slot pair is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.SLOT, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        SlotPair newArt = em.merge(spair);
        if (create) { // create instead of update
            Slot pslot = spair.getParentSlot(); // get the parent. Todo: update
                                                // the child too
            pslot.getParentSlotsPairList().add(newArt);
            em.merge(pslot);
        }
        logger.log(Level.INFO, "saved slot pair: child " + spair.getChildSlot().getName() + " parent " + spair.getParentSlot().getName() + " relation ");
    }


    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addSlotPair(SlotPair slotPair) {
        em.persist(slotPair);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void removeSlotPair(SlotPair slotPair) {
        em.merge(slotPair);
        em.remove(slotPair);
    }


    private final String ROOT_COMPONENT_TYPE = "_ROOT"; // ToDo: Get the root
                                                        // type from
                                                        // configuration (JNDI,
                                                        // config table etc)

    public List<Slot> getRootNodes(SlotRelationName relationName) {
        List<Slot> components;
        TypedQuery<Slot> queryComp;

        // queryComp =
        // em.createQuery("SELECT cp.childLogicalComponent FROM ComponentPair cp WHERE cp.componentPairPK.parentLogicalComponentId = :compid AND cp.componentRelation.name = :relname ORDER BY cp.childLogicalComponent.name",
        // LogicalComponent.class).setParameter("compid",
        // ROOTID).setParameter("relname", relationName);
        queryComp = em.createQuery("SELECT cp.childSlot FROM SlotPair cp WHERE cp.parentSlot.componentType.name = :ctype AND cp.slotRelation.name = :relname ORDER BY cp.childSlot.name", Slot.class).setParameter("ctype", ROOT_COMPONENT_TYPE).setParameter("relname", relationName);
        components = queryComp.getResultList();

        return components;
    }

    public List<Slot> getRootNodes() {
        return getRootNodes(SlotRelationName.CONTAINS); // ToDo: get the relation name from
                                         // configuration
    }

    public List<Slot> relatedChildren(String compName) {
        List<Slot> components;
        TypedQuery<Slot> queryComp;

        // ToDO; remove 'contains' and move it to configuration
        queryComp = em.createQuery("SELECT cp.childSlot FROM SlotPair cp WHERE cp.parentSlot.name = :compname AND cp.slotRelation.name = :relname", Slot.class).setParameter("compname", compName).setParameter("relname", SlotRelationName.CONTAINS);
        // queryComp =
        // em.createQuery("SELECT l FROM LogicalComponent l, IN (l.childList) c WHERE c.parentLogicalComponent.name = :compname AND c.componentRelation.name = :relname ORDER BY l.name",
        // LogicalComponent.class).setParameter("compname",
        // compName).setParameter("relname", "contains");
        components = queryComp.getResultList();

        return components;
    }
}