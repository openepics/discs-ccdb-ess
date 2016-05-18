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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComponentType_;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.fields.DeviceTypeFields;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.CRUDOperation;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.SortOrder;
import org.openepics.discs.conf.util.Utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * DAO Service for accessing Component Types ( {@link ComponentType} )
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */

@Stateless
public class ComptypeEJB extends DAO<ComponentType> {
    @Inject private BlobStore blobStore;


    /**
     * @return A list of all device types ordered by name.
     */
    public List<ComponentType> findComponentTypeOrderedByName() {
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
     * @param maxResults the maximum number of entities returned by the database
     * @return <code>true</code> if the device type is used in the database, <code>false</code> otherwise
     */
    public List<String> findWhereIsComponentTypeUsed(final ComponentType compType, int maxResults) {
        List<String> usedBy = Lists.newArrayList();

        List<Slot> slots = em.createNamedQuery("Slot.findByComponentType", Slot.class).setParameter("componentType", compType)
                .setMaxResults(maxResults).getResultList();
        usedBy.addAll(slots.stream().map(Slot::getName).collect(Collectors.toList()));

        List<Device> devices = em.createNamedQuery("Device.findByComponentType", Device.class)
                .setParameter("componentType", compType).setMaxResults(maxResults).getResultList();
        usedBy.addAll(devices.stream().map(Device::getName).collect(Collectors.toList()));


        // utility join table, not part of the model, native query needs to be used.
        long uses = ((Number)em.createNativeQuery("SELECT COUNT(1) FROM filter_by_type WHERE type_id = ? LIMIT 1;")
                    .setParameter(1, compType.getId()).getSingleResult()).longValue();
        if (uses>0) usedBy.add("filter_by_type");


        // two columns need to be checked with as single query. Native query needs to be used.
        uses = ((Number)em.createNativeQuery("SELECT COUNT(1) FROM comptype_asm "
                + "WHERE child_type = ? OR parent_type = ? LIMIT 1")
                .setParameter(1, compType.getId()).setParameter(2, compType.getId())
                .getSingleResult()).longValue();
        if (uses>0) usedBy.add("comptype_asm");
        return usedBy;
    }


    /**
     * The method creates a new copy of the selected {@link ComponentType}s
     * @param deviceTypesToDuplicate a {@link List} of {@link ComponentType}s to create a copy of
     * @return the number of copies created
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Authorized
    public int duplicate(List<ComponentType> deviceTypesToDuplicate) {
        if (Utility.isNullOrEmpty(deviceTypesToDuplicate)) return 0;

        int duplicated = 0;
        for (final ComponentType deviceType : deviceTypesToDuplicate) {
            String newName = Utility.findFreeName(deviceType.getName(), this);
            ComponentType newDeviceType = new ComponentType(newName);
            duplicate(newDeviceType, deviceType);
            explicitAuditLog(newDeviceType, EntityTypeOperation.CREATE);
            ++duplicated;
        }
        return duplicated;
    }

    /**
     * This method duplicates selected device types. This method actually copies
     * selected device type description, tags, artifacts and properties
     * into the new device type. If property has set universally unique value,
     * copied property value is set to <code>null</code>.
     *
     * @param newDeviceType the newly created device type
     * @param originalDeviceType the device type to copy the attributes from
     */
    private void duplicate(final ComponentType newDeviceType, final ComponentType originalDeviceType) {
        newDeviceType.setDescription(originalDeviceType.getDescription());
        add(newDeviceType);

        duplicateProperties(newDeviceType, originalDeviceType);
        newDeviceType.getTags().addAll(originalDeviceType.getTags());
        duplicateArtifactsFromSource(newDeviceType, originalDeviceType);

        save(newDeviceType);
    }

    @Override
    public <S> void addChild(S child) {
        if (child instanceof ComptypePropertyValue) {
            final ComptypePropertyValue ctpv = (ComptypePropertyValue) child;
            // by business logic property definitions that are UNIQUE cannot have a default value
            if (ctpv.isPropertyDefinition()
                    && (ctpv.getProperty().getValueUniqueness() != PropertyValueUniqueness.NONE)) {
                ctpv.setPropValue(null);
            }
        }
        super.addChild(child);
    }

    private void duplicateArtifactsFromSource(final ComponentType newDeviceType, final ComponentType copyDeviceType) {
        for (final ComptypeArtifact artifact : copyDeviceType.getComptypeArtifactList()) {
            String uri = artifact.getUri();
            if (artifact.isInternal()) {
                try {
                    uri = blobStore.storeFile(blobStore.retreiveFile(uri));
                } catch (IOException e) {
                    throw new PersistenceException(e);
                }
            }
            ComptypeArtifact newArtifact = new ComptypeArtifact(artifact.getName(),
                        artifact.isInternal(), artifact.getDescription(), artifact.getUri());
            newArtifact.setComponentType(newDeviceType);
            addChild(newArtifact);

        }
    }

    private void duplicateProperties(final ComponentType newDeviceType, final ComponentType copyDeviceType) {
        for (final ComptypePropertyValue propertyValue : copyDeviceType.getComptypePropertyList()) {
            final Property property = propertyValue.getProperty();
            final ComptypePropertyValue pv = new ComptypePropertyValue();
            pv.setComponentType(newDeviceType);
            pv.setPropertyDefinition(propertyValue.isDefinitionTargetDevice() || propertyValue.isDefinitionTargetSlot());
            pv.setProperty(property);
            pv.setDefinitionTargetDevice(propertyValue.isDefinitionTargetDevice());
            pv.setDefinitionTargetSlot(propertyValue.isDefinitionTargetSlot());
            if (property.getValueUniqueness() != PropertyValueUniqueness.UNIVERSAL) {
                pv.setPropValue(propertyValue.getPropValue());
            }
            addChild(pv);
        }
    }

    /**
     * If the name does not exist, the {@link NoResultException} will get thrown.
     *
     * @param name the name MUST exist
     * @return the position of this entity if ordered b name
     */
    public long getNamedPosition(String name) {
        return em.createQuery("SELECT COUNT(*) FROM ComponentType c WHERE c.name <= :name AND c.name <> :internalType1 "
                + "AND c.name <> :internalType2", Long.class).
                setParameter("internalType1", SlotEJB.ROOT_COMPONENT_TYPE).
                setParameter("internalType2", SlotEJB.GRP_COMPONENT_TYPE).
                setParameter("name", name).getSingleResult() - 1;
    }

    /**
     * Returns only a subset of data based on sort column, sort order and filtered by all the fields.
     *
     * @param first the index of the first result to return
     * @param pageSize the number of results
     * @param sortField the field by which to sort
     * @param sortOrder ascending/descending
     * @param name the {@link ComponentType} name
     * @param description the {@link ComponentType} description
     * @return The required entities.
     */
    public List<ComponentType> findLazy(final int first, final int pageSize,
            final @Nullable DeviceTypeFields sortField, final @Nullable SortOrder sortOrder,
            final @Nullable String name, final @Nullable String description) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<ComponentType> cq = cb.createQuery(getEntityClass());
        final Root<ComponentType> deviceTypeRecord = cq.from(getEntityClass());

        addSortingOrder(sortField, sortOrder, cb, cq, deviceTypeRecord);

        final Predicate[] predicates = buildPredicateList(cb, deviceTypeRecord, name, description);
        cq.where(predicates);

        final TypedQuery<ComponentType> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    private void addSortingOrder(final DeviceTypeFields sortField, final SortOrder sortOrder, final CriteriaBuilder cb,
            final CriteriaQuery<ComponentType> cq, final Root<ComponentType> deviceTypeRecord) {
        if ((sortField != null) && (sortOrder != null) && (sortOrder != SortOrder.UNSORTED)) {
            switch (sortField) {
            case NAME:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(cb.lower(deviceTypeRecord.get(ComponentType_.name)))
                                : cb.desc(cb.lower(deviceTypeRecord.get(ComponentType_.name))));
                break;
            case DESCRIPTION:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(cb.lower(deviceTypeRecord.get(ComponentType_.description)))
                                : cb.desc(cb.lower(deviceTypeRecord.get(ComponentType_.description))));
                break;
            default:
                break;
            }
        }
    }

    private Predicate[] buildPredicateList(final CriteriaBuilder cb, final Root<ComponentType> deviceTypeRecord,
            final @Nullable String name, final @Nullable String description) {
        final List<Predicate> predicates = Lists.newArrayList();

        predicates.add(cb.notEqual(deviceTypeRecord.get(ComponentType_.name), SlotEJB.ROOT_COMPONENT_TYPE));
        predicates.add(cb.notEqual(deviceTypeRecord.get(ComponentType_.name), SlotEJB.GRP_COMPONENT_TYPE));

        if (name != null) {
            predicates.add(cb.like(cb.lower(deviceTypeRecord.get(ComponentType_.name)),
                                                        "%" + escapeDbString(name).toLowerCase() + "%", '\\'));
        }
        if (description != null) {
            predicates.add(cb.like(cb.lower(deviceTypeRecord.get(ComponentType_.description)),
                                                        "%" + escapeDbString(description).toLowerCase() + "%", '\\'));
        }

        return predicates.toArray(new Predicate[] {});
    }

}