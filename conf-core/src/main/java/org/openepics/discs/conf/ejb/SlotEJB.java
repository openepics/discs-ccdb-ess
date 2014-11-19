/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 * DAO Service for accessing Installation Slot entities ( {@link Slot} )
 * .
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class SlotEJB extends DAO<Slot> {
    /**
     * Special {@link ComponentType} name for root components
     */
    public static final String ROOT_COMPONENT_TYPE = "_ROOT";

    /**
     * Special {@link ComponentType} name for group components
     */
    public static final String GRP_COMPONENT_TYPE = "_GRP";

    @Inject private SlotPairEJB slotPairEJB;
    @Inject private SlotRelationEJB slotRelationEJB;
    @Inject private ComptypeEJB comptypeEJB;

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

    /**
     * Queries database for slots by partial name
     *
     * @param namePart partial name
     * @return {@link List} of slots
     */
    public List<Slot> findSlotByNameContainingString(String namePart) {
        return em.createNamedQuery("Slot.findByNameContaining", Slot.class)
                .setParameter("name", namePart).getResultList();
    }

    /**
     * Retrieves the special implicit root container from the database.
     *
     * @return the implicit root {@link Slot} of the contains hierarchy
     */
    public Slot getRootNode() {
        ComponentType rootComponentType = comptypeEJB.findByName(ROOT_COMPONENT_TYPE);
        return em.createNamedQuery("Slot.findByComponentType", Slot.class)
                .setParameter("componentType", rootComponentType).getSingleResult();
    }

    /**
     * Queries for all parent slots with a {@link SlotRelationName#CONTAINS} of a
     * given component type ( {@link ComponentType} ) name
     *
     * @param compName The {@link ComponentType} name
     * @return {@link List} of slots matching the query
     */
    public List<Slot> relatedChildren(String compName) {
        // XXX Optimization opportunity - find "component type" and "slot relation" objects first, then scan the table
        return em.createQuery("SELECT cp.childSlot FROM SlotPair cp "
                              + "WHERE cp.parentSlot.name = :compname AND cp.slotRelation.name = :relname", Slot.class)
               .setParameter("compname", compName)
               .setParameter("relname", SlotRelationName.CONTAINS).getResultList();
    }

    /**
     * All {@link Slot}s for given {@link ComponentType}
     * @param componentType the {@link ComponentType}
     * @return list of slots with specific {@link ComponentType}
     */
    public List<Slot> findByComponentType(ComponentType componentType) {
        return em.createNamedQuery("Slot.findByComponentType", Slot.class)
                .setParameter("componentType", componentType).getResultList();
    }

    /**
     * All hosting or non-hosting {@link Slot}s.
     *
     * @param isHostingSlot is slot hosting or not
     * @return List of all hosting or non-hosting {@link Slot}s
     */
    public List<Slot> findByIsHostingSlot(boolean isHostingSlot) {
        return em.createNamedQuery("Slot.findByIsHostingSlot", Slot.class)
                .setParameter("isHostingSlot", isHostingSlot).getResultList();
    }

    /** The method takes care of adding a new Slot and all its dependencies in one transaction. Called from Container
     * and iInstallation slot managed beans.
     * @param newSlot the Container or Installation slot to be added
     * @param parentSlot its parent. <code>null</code> if the container is a new root.
     * @param propertyDefinitions a list of property definitions that are automatically added to this slot.
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addSlotToParentWithPropertyDefs(Slot newSlot, @Nullable Slot parentSlot, List<ComptypePropertyValue> propertyDefinitions) {
        add(newSlot);
        if (parentSlot != null) {
            slotPairEJB.add(new SlotPair(newSlot, parentSlot, slotRelationEJB.findBySlotRelationName(SlotRelationName.CONTAINS)));
        } else {
            slotPairEJB.add(new SlotPair(newSlot, getRootNode(), slotRelationEJB.findBySlotRelationName(SlotRelationName.CONTAINS)));
        }
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            if (propertyDefinition.isDefinitionTargetSlot()) {
                final SlotPropertyValue slotPropertyValue = new SlotPropertyValue(false);
                slotPropertyValue.setProperty(propertyDefinition.getProperty());
                slotPropertyValue.setSlot(newSlot);
                addChild(slotPropertyValue);
            }
        }
    }
}
