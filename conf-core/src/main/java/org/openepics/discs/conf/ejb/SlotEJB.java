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
import org.openepics.discs.conf.dl.SlotsAndSlotPairsDataLoader;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

import com.google.common.base.Preconditions;

/**
 * DAO Service for accessing Installation Slot entities ( {@link Slot} )
 * .
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
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

    /**
     * The method takes care of adding a new Slot and all its dependencies in one transaction. Called from Container
     * and iInstallation slot managed beans.
     *
     * @param newSlot the Container or Installation slot to be added
     * @param parentSlot its parent. <code>null</code> if the container is a new root.
     * @param fromDataLoader is data being imported via {@link SlotsAndSlotPairsDataLoader}. If it is slot pair should
     *          never be created since it is separately created from data loader.
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addSlotToParentWithPropertyDefs(Slot newSlot, @Nullable Slot parentSlot, boolean fromDataLoader) {
        super.add(newSlot);
        if (!fromDataLoader) {
            if (parentSlot != null) {
                slotPairEJB.addWithoutInterceptors(new SlotPair(newSlot, parentSlot,
                            slotRelationEJB.findBySlotRelationName(SlotRelationName.CONTAINS)));
            } else {
                slotPairEJB.addWithoutInterceptors(new SlotPair(newSlot, getRootNode(),
                            slotRelationEJB.findBySlotRelationName(SlotRelationName.CONTAINS)));
            }
        }

        final List<ComptypePropertyValue> propertyDefinitions =
                                                comptypeEJB.findPropertyDefinitions(newSlot.getComponentType());
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            if (propertyDefinition.isDefinitionTargetSlot()) {
                final SlotPropertyValue slotPropertyValue = new SlotPropertyValue(false);
                slotPropertyValue.setProperty(propertyDefinition.getProperty());
                slotPropertyValue.setSlot(newSlot);
                addChild(slotPropertyValue);
            }
        }
    }

    /** This method removed all needless property values in a single transaction.
     * Also, all the removals are only logged once.
     * @param slot the {@link Slot} to work on
     * @param deleteList property values to delete
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void removePropertyDefinitionsForTypeChange(final Slot slot, final List<SlotPropertyValue> deleteList) {
        // delete all properties marked for removal
        for (SlotPropertyValue propertyValueToDelete : deleteList) {
            deleteChild(propertyValueToDelete);
        }
    }


    /** This adds all the new properties in a single transaction.
     * @param slot the {@link Slot} to work on
     * @param newComponentType the new {@link ComponentType} to add the properties on
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addPropertyDefinitionsForTypeChange(final Slot slot, final ComponentType newComponentType) {
        // add all property values to the new type
        final Slot slotFromDatabase = findById(slot.getId());
        slotFromDatabase.setComponentType(newComponentType);
        final List<SlotPropertyValue> existingPropertyValues = slotFromDatabase.getSlotPropertyList();
        for (ComptypePropertyValue newPropDefinition : newComponentType.getComptypePropertyList()) {
            if (newPropDefinition.isDefinitionTargetSlot()
                    && !isPropertyInParentList(newPropDefinition.getProperty(), existingPropertyValues)) {
                final SlotPropertyValue newPropertyValue = new SlotPropertyValue(false);
                newPropertyValue.setProperty(newPropDefinition.getProperty());
                newPropertyValue.setPropValue(null);
                newPropertyValue.setSlot(slotFromDatabase);
                addChild(newPropertyValue);
                // this is for logging only, since the application is logging the slot, instead the new one
                slot.getSlotPropertyList().add(newPropertyValue);
            }
        }
    }

    private boolean isPropertyInParentList(Property prop, List<SlotPropertyValue> parentPropertyValues) {
        for (SlotPropertyValue propertyValueChild : parentPropertyValues) {
            if (propertyValueChild.getProperty().equals(prop)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param newSlotName the installation slot name to check
     * @return <code>true</code> if the installation slot name is unique, <code>false</code> otherwise
     */
    public boolean isInstallationSlotNameUnique(String newSlotName) {
        return em.createNamedQuery("Slot.findByNameAndHosting", Slot.class).setParameter("name", newSlotName).
                setParameter("isHostingSlot", true).getResultList().isEmpty();
    }

    @Override
    protected Class<Slot> getEntityClass() {
        return Slot.class;
    }

    @Override
    protected boolean isPropertyValueTypeUnique(PropertyValue child, Slot parent) {
        Preconditions.checkNotNull(child);
        Preconditions.checkNotNull(parent);
        final Value value = child.getPropValue();
        if (value == null) {
            return true;
        }
        final List<PropertyValue> results = em.createNamedQuery("SlotPropertyValue.findSamePropertyValueByType",
                                                        PropertyValue.class)
                    .setParameter("componentType", parent.getComponentType())
                    .setParameter("property", child.getProperty())
                    .setParameter("propValue", value).setMaxResults(2).getResultList();
        // value is unique if there is no property value with the same value, or the only one found us the entity itself
        return (results.size() < 2) && (results.isEmpty() || results.get(0).equals(child));
    }
}
