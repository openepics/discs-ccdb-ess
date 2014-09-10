package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelationName;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class SlotEJB extends DAO<Slot> {
    public static final String ROOT_COMPONENT_TYPE = "_ROOT"; // TODO Get the root type from configuration (JNDI, config table etc)
    public static final String GRP_COMPONENT_TYPE = "_GRP";

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SlotEJB.class.getCanonicalName());

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

    public List<Slot> findSlotByNameContainingString(String namePart) {
        return em.createNamedQuery("Slot.findByNameContaining", Slot.class).setParameter("name", namePart).getResultList();
    }

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

    /**
     * All {@link Slot}s for given {@link ComponentType}
     * @param componentType the {@link ComponentType}
     * @return list of slots with specific {@link ComponentType}
     */
    public List<Slot> findByComponentType(ComponentType componentType) {
        return em.createNamedQuery("Slot.findByComponentType", Slot.class).setParameter("componentType", componentType).getResultList();
    }

    /**
     * All hosting or non-hosting {@link Slot}s.
     *
     * @param isHostingSlot is slot hosting or not
     * @return List of all hosting or non-hosting {@link Slot}s
     */
    public List<Slot> findByIsHostingSlot(boolean isHostingSlot) {
        return em.createNamedQuery("Slot.findByIsHostingSlot", Slot.class).setParameter("isHostingSlot", isHostingSlot).getResultList();
    }
}