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

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class SlotEJB {
    private static final Logger logger = Logger.getLogger(SlotEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;


    // ---------------- Layout Slot -------------------------

    public List<Slot> findLayoutSlot() {
        final List<Slot> comps;
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Slot> cq = cb.createQuery(Slot.class);

        final TypedQuery<Slot> query = em.createQuery(cq);
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
        final Slot ct = em.find(Slot.class, slot.getId());
        em.remove(ct);
    }

    // ------------------ Slot Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveSlotProp(SlotPropertyValue prop) {
        prop.setModifiedAt(new Date());
        final SlotPropertyValue newProp = em.merge(prop);
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteSlotProp(SlotPropertyValue prop) {
        prop.setModifiedAt(new Date());
        final SlotPropertyValue property = em.find(SlotPropertyValue.class, prop.getId());
        final Slot slot = property.getSlot();
        slot.getSlotPropertyList().remove(property);
        em.remove(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addSlotProperty(SlotPropertyValue property) {
        final SlotPropertyValue newProp = em.merge(property);
        final Slot slot = property.getSlot();
        slot.getSlotPropertyList().add(newProp);
        em.merge(slot);
    }

    // ---------------- Slot Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveSlotArtifact(SlotArtifact art) {
        art.setModifiedAt(new Date());
        SlotArtifact newArt = em.merge(art);
        logger.log(Level.INFO, "slot Artifact: name " + newArt.getName() + " description " + newArt.getDescription() + " uri " + newArt.getUri());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteSlotArtifact(SlotArtifact art) {
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        final SlotArtifact artifact = em.find(SlotArtifact.class, art.getId());
        final Slot slot = artifact.getSlot();
        slot.getSlotArtifactList().remove(artifact);
        em.remove(artifact);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addSlotArtifact(SlotArtifact art) {
        final SlotArtifact newArt = em.merge(art);
        final Slot slot = art.getSlot();
        slot.getSlotArtifactList().add(newArt);
        em.merge(slot);
    }

    // ---------------- Related Slots ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveSlotPair(SlotPair spair) {
        final SlotPair newSpair = em.merge(spair);
        logger.log(Level.INFO, "saved slot pair: child " + newSpair.getChildSlot().getName() + " parent " + newSpair.getParentSlot().getName() + " relation ");
    }


    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addSlotPair(SlotPair slotPair) {
        final SlotPair newArt = em.merge(slotPair);
        final Slot pslot = slotPair.getParentSlot();
        pslot.getParentSlotsPairList().add(newArt);
        em.merge(pslot);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void removeSlotPair(SlotPair slotPair) {
        final SlotPair slotPairToRemove = em.merge(slotPair);
        em.remove(slotPairToRemove);
    }


    private final String ROOT_COMPONENT_TYPE = "_ROOT"; // ToDo: Get the root
                                                        // type from
                                                        // configuration (JNDI,
                                                        // config table etc)

    public List<Slot> getRootNodes(SlotRelationName relationName) {
        final List<Slot> components;
        final TypedQuery<Slot> queryComp;

        queryComp = em.createQuery("SELECT cp.childSlot FROM SlotPair cp WHERE cp.parentSlot.componentType.name = :ctype AND cp.slotRelation.name = :relname ORDER BY cp.childSlot.name", Slot.class).setParameter("ctype", ROOT_COMPONENT_TYPE).setParameter("relname", relationName);
        components = queryComp.getResultList();

        return components;
    }

    public List<Slot> getRootNodes() {
        return getRootNodes(SlotRelationName.CONTAINS); // ToDo: get the relation name from configuration
    }

    public List<Slot> relatedChildren(String compName) {
        final List<Slot> components;
        final TypedQuery<Slot> queryComp;

        // ToDO; remove 'contains' and move it to configuration
        queryComp = em.createQuery("SELECT cp.childSlot FROM SlotPair cp WHERE cp.parentSlot.name = :compname AND cp.slotRelation.name = :relname", Slot.class).setParameter("compname", compName).setParameter("relname", SlotRelationName.CONTAINS);
        components = queryComp.getResultList();

        return components;
    }
}