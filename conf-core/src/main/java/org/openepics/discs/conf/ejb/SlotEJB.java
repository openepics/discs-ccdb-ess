package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

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
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class SlotEJB extends DAO<Slot> {
    private static final Logger logger = Logger.getLogger(SlotEJB.class.getCanonicalName());

    // ---------------- Layout Slot -------------------------

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

    public List<SlotPair> findSlotPairsByParentChildRelation(String childName, String parentName, SlotRelationName relationName) {
        return em.createNamedQuery("SlotPair.findByParentChildRelation", SlotPair.class).setParameter("childName", childName)
                .setParameter("parentName", parentName).setParameter("relationName", relationName).getResultList();
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

        logger.log(Level.FINE, "saved slot pair: child " + slotPair.getChildSlot().getName() + " parent " + slotPair.getParentSlot().getName() + " relation ");
    }

    /** Deletes the slot pair.
     * @param slotPair - the slot pair to delete.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteSlotPair(SlotPair slotPair) {
        final SlotPair mergedSlotPair = em.merge(slotPair);
        em.remove(mergedSlotPair);
    }


    private final String ROOT_COMPONENT_TYPE = "_ROOT"; // ToDo: Get the root type from configuration (JNDI, config table etc)

    public List<Slot> getRootNodes(SlotRelationName relationName) {
        return em.createQuery("SELECT cp.childSlot FROM SlotPair cp "
                + "WHERE cp.parentSlot.componentType.name = :ctype AND cp.slotRelation.name = :relname "
                + "ORDER BY cp.childSlot.name",
                Slot.class).
                setParameter("ctype", ROOT_COMPONENT_TYPE).
                setParameter("relname", relationName).getResultList();
    }

    public List<Slot> getRootNodes() {
        return getRootNodes(SlotRelationName.CONTAINS);
    }

    public List<Slot> relatedChildren(String compName) {
        return em.createQuery("SELECT cp.childSlot FROM SlotPair cp "
                + "WHERE cp.parentSlot.name = :compname AND cp.slotRelation.name = :relname", Slot.class).
                setParameter("compname", compName).
                setParameter("relname", SlotRelationName.CONTAINS).getResultList();
    }

    @Override
    protected void defineEntity() {
        defineEntityClass(Slot.class);

        defineParentChildInterface(SlotPropertyValue.class, new ParentChildInterface<Slot, SlotPropertyValue>() {
            @Override
            public List<SlotPropertyValue> getChildCollection(Slot slot) {
                return slot.getSlotPropertyList();
            }

            @Override
            public Slot getParentFromChild(SlotPropertyValue child) {
                return child.getSlot();
            }
        });

        defineParentChildInterface(SlotArtifact.class, new ParentChildInterface<Slot, SlotArtifact>() {
            @Override
            public List<SlotArtifact> getChildCollection(Slot slot) {
                return slot.getSlotArtifactList();
            }

            @Override
            public Slot getParentFromChild(SlotArtifact child) {
                return child.getSlot();
            }
        });
    }
}