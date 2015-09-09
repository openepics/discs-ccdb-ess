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

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;

import com.google.common.base.Preconditions;

/**
 * DAO Service for accessing Component Types ( {@link ComponentType} )
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */

@Stateless
public class ComptypeEJB extends DAO<ComponentType> {
    /**
     * @return A list of all device types ordered by name.
     */    public List<ComponentType> findComponentTypeOrderedByName() {
        return em.createNamedQuery("ComponentType.findAllOrdered", ComponentType.class).getResultList();
    }

    /**
     * @param componentType - the device type
     * @return A list of all property definitions for the selected device type.
     */
    public List<ComptypePropertyValue> findPropertyDefinitions(ComponentType componentType) {
        return em.createNamedQuery("ComptypePropertyValue.findPropertyDefs", ComptypePropertyValue.class)
               .setParameter("componentType", componentType).getResultList();
    }

    @Override
    public List<ComponentType> findAll() {
        return em.createNamedQuery("ComponentType.findUserTypesOnly", ComponentType.class)
                .setParameter("internalType1", SlotEJB.ROOT_COMPONENT_TYPE)
                .setParameter("internalType2", SlotEJB.GRP_COMPONENT_TYPE).getResultList();
    }

    @Override
    protected Class<ComponentType> getEntityClass() {
        return ComponentType.class;
    }

    @Override
    protected boolean isPropertyValueTypeUnique(PropertyValue child, ComponentType parent) {
        // each component type can have only one property value for some property. This is true by definition.
        return true;
    }

    /** If the property value fails the uniqueness check, this method throws an unchecked
     * {@link PropertyValueNotUniqueException}
     * @param pv the property value to check for
     */
    public void checkPropertyValueUnique(final ComptypePropertyValue pv) {
        Preconditions.checkNotNull(pv);
        uniquePropertyValueCheck(pv, (ComponentType)pv.getPropertiesParent());
    }

    /**
     * @param compType the device type to search for
     * @return <code>true</code> if the device type is used in the database, <code>false</code> otherwise
     */
    public boolean isComponentTypeUsed(final ComponentType compType) {
        List<? extends ConfigurationEntity> entitiesWithComponentType =
                em.createNamedQuery("Slot.findByComponentType", Slot.class).setParameter("componentType", compType)
                    .setMaxResults(1).getResultList();
        if (entitiesWithComponentType.isEmpty()) {
            entitiesWithComponentType = em.createNamedQuery("Device.findByComponentType", Device.class)
                        .setParameter("componentType", compType).setMaxResults(1).getResultList();
            if (entitiesWithComponentType.isEmpty()) {
                // utility join table, not part of the model, native query needs to be used.
                long uses = ((Number)
                        em.createNativeQuery("SELECT COUNT(1) FROM filter_by_type WHERE type_id = ? LIMIT 1;")
                            .setParameter(1, compType.getId()).getSingleResult()).longValue();
                if (uses == 0) {
                    // two columns need to be checked with as single query. Native query needs to be used.
                    uses = ((Number)
                            em.createNativeQuery("SELECT COUNT(1) FROM comptype_asm "
                                    + "WHERE child_type = ? OR parent_type = ? LIMIT 1")
                                    .setParameter(1, compType.getId()).setParameter(2, compType.getId())
                                    .getSingleResult()).longValue();
                    return (uses != 0);
                }
            }
        }
        return true;
    }

    /**
     * This method duplicates selected device types. This method actually copies
     * selected device type name, description, tags, artifacts and properties
     * into new device type. If property has set universally unique value,
     * copied property value is set to null.
     */
    public void duplicate(final ComponentType selectedDeviceType, final String newName) {
        ComponentType newDeviceType = new ComponentType(newName);
        newDeviceType.setDescription(selectedDeviceType.getDescription());
        add(newDeviceType);

        duplicateProperties(newDeviceType, selectedDeviceType);
        newDeviceType.getTags().addAll(selectedDeviceType.getTags());
        duplicateArtifactsFromSource(newDeviceType, selectedDeviceType);

        save(newDeviceType);
    }

    private void duplicateArtifactsFromSource(final ComponentType newDeviceType, final ComponentType copyDeviceType) {
        for (final ComptypeArtifact artifact : copyDeviceType.getComptypeArtifactList()) {
            if (!artifact.isInternal()) {
                ComptypeArtifact newArtifact = new ComptypeArtifact(artifact.getName(),
                        false, artifact.getDescription(), artifact.getUri());
                newArtifact.setComponentType(newDeviceType);
                addChild(newArtifact);
            }
        }
    }

    private void duplicateProperties(final ComponentType newDeviceType, final ComponentType copyDeviceType) {
        for(final ComptypePropertyValue propertyValue : copyDeviceType.getComptypePropertyList()) {
            final Property property = propertyValue.getProperty();
            final ComptypePropertyValue pv = new ComptypePropertyValue();
            pv.setComponentType(newDeviceType);
            pv.setProperty(property);
            pv.setDefinitionTargetDevice(propertyValue.isDefinitionTargetDevice());
            pv.setDefinitionTargetSlot(propertyValue.isDefinitionTargetSlot());
            if (property.getValueUniqueness() != PropertyValueUniqueness.UNIVERSAL) {
                pv.setPropValue(propertyValue.getPropValue());
            }
            addChild(pv);
        }
    }
}