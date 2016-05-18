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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.AlignmentPropertyValue;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.DataType_;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.fields.EnumFields;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;
import org.openepics.discs.conf.util.SortOrder;
import org.openepics.discs.conf.util.Utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * DAO service for accessing {@link DataType} entities
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Stateless
public class DataTypeEJB extends DAO<DataType> {
    @Override
    protected Class<DataType> getEntityClass() {
        return DataType.class;
    }

    /**
     * The method checks whether a data type is used in any property value in the database.
     *
     * @param dataType the data type to check for
     * @return <code>true</code> if the data type is used in any property value, <code>false</code> otherwise.
     */
    public boolean isDataTypeUsed(final DataType dataType) {
        return isDataTypeUsed(dataType, false);
    }


    /**
     * The method checks whether a data type is used in any property value or {@link Property} in the database.
     *
     * @param dataType
     *              the data type to check for
     * @param checkProperties
     *              if <code>true</code> the method also checks whether the data type is used in any {@link Property}.
     *              <code>false</code> skips this check.
     * @return <code>true</code> if the data type is used in any property value or property,
     * <code>false</code> otherwise.
     */
    public boolean isDataTypeUsed(final DataType dataType, final boolean checkProperties) {
        Preconditions.checkNotNull(dataType);
        List<? extends PropertyValue> valuesWithDataType;

        if (checkProperties) {
            final List<Property> props = em.createNamedQuery("Property.findByDataType", Property.class)
                    .setParameter("dataType", dataType).setMaxResults(1).getResultList();
            if (!props.isEmpty()) {
                return true;
            }
        }

        valuesWithDataType = em.createNamedQuery("ComptypePropertyValue.findByDataType", ComptypePropertyValue.class)
                .setParameter("dataType", dataType).setMaxResults(1).getResultList();

        if (valuesWithDataType.isEmpty()) {
            valuesWithDataType = em.createNamedQuery("SlotPropertyValue.findByDataType", SlotPropertyValue.class)
                    .setParameter("dataType", dataType).setMaxResults(1).getResultList();
        }
        if (valuesWithDataType.isEmpty()) {
            valuesWithDataType = em.createNamedQuery("DevicePropertyValue.findByDataType", DevicePropertyValue.class)
                    .setParameter("dataType", dataType).setMaxResults(1).getResultList();
        }
        if (valuesWithDataType.isEmpty()) {
            valuesWithDataType = em.createNamedQuery("AlignmentPropertyValue.findByDataType", AlignmentPropertyValue.class)
                    .setParameter("dataType", dataType).setMaxResults(1).getResultList();
        }
        return !valuesWithDataType.isEmpty();
    }

    /**
     * The method returns list of {@link Property} in the database where a data type is used.
     *
     * @param dataType
     *              the data type to check for
     * @param maxResults
     *              maximum number of properties to return
     * @return the list of properties, where the data type is used
     */
    public List<Property> findProperties(final DataType dataType, int maxResults) {
        Preconditions.checkNotNull(dataType);
        return em.createNamedQuery("Property.findByDataType", Property.class)
                    .setParameter("dataType", dataType).setMaxResults(maxResults).getResultList();
    }

    /**
     * The method creates a new copy of the selected {@link DataType}s
     * @param enumsToDuplicate a {@link List} of {@link DataType}s to create a copy of
     * @return the number of copies created
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Authorized
    public int duplicate(final List<DataType> enumsToDuplicate) {
        int duplicated = 0;
        if (Utility.isNullOrEmpty(enumsToDuplicate)) return 0;

        for (final DataType enumToCopy : enumsToDuplicate) {
            final String newEnumName = Utility.findFreeName(enumToCopy.getName(), this);
            final DataType newEnum = new DataType(newEnumName, enumToCopy.getDescription(), enumToCopy.isScalar(),
                    enumToCopy.getDefinition());
            add(newEnum);
            explicitAuditLog(newEnum, EntityTypeOperation.CREATE);
            ++duplicated;
        }
        return duplicated;
    }

    /**
     * Returns only a subset of data based on sort column, sort order and filtered by all the fields.
     *
     * @param first the index of the first result to return
     * @param pageSize the number of results
     * @param sortField the field by which to sort
     * @param sortOrder ascending/descending
     * @param name the {@link DataType} name
     * @param description the {@link DataType} description
     * @param definition the {@link DataType} definition
     * @return The required entities.
     */
    public List<DataType> findLazy(final int first, final int pageSize,
            final @Nullable EnumFields sortField, final @Nullable SortOrder sortOrder,
            final @Nullable String name, final @Nullable String description, final @Nullable String definition) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<DataType> cq = cb.createQuery(getEntityClass());
        final Root<DataType> dataTypeRecord = cq.from(getEntityClass());

        addSortingOrder(sortField, sortOrder, cb, cq, dataTypeRecord);

        final Predicate[] predicates = buildPredicateList(cb, dataTypeRecord, name, description, definition);
        cq.where(predicates);

        final TypedQuery<DataType> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    private void addSortingOrder(final EnumFields sortField, final SortOrder sortOrder, final CriteriaBuilder cb,
            final CriteriaQuery<DataType> cq, final Root<DataType> enumRecord) {
        if ((sortField != null) && (sortOrder != null) && (sortOrder != SortOrder.UNSORTED)) {
            switch (sortField) {
            case NAME:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(cb.lower(enumRecord.get(DataType_.name)))
                                : cb.desc(cb.lower(enumRecord.get(DataType_.name))));
                break;
            case DESCRIPTION:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(cb.lower(enumRecord.get(DataType_.description)))
                                : cb.desc(cb.lower(enumRecord.get(DataType_.description))));
                break;
            case DEFINITION:
                cq.orderBy(sortOrder == SortOrder.ASCENDING
                                ? cb.asc(cb.lower(enumRecord.get(DataType_.definition)))
                                : cb.desc(cb.lower(enumRecord.get(DataType_.definition))));
                break;
            default:
                break;
            }
        }
    }

    private Predicate[] buildPredicateList(final CriteriaBuilder cb, final Root<DataType> enumRecord,
            final @Nullable String name, final @Nullable String description, final @Nullable String definition) {
        final List<Predicate> predicates = Lists.newArrayList();

        if (name != null) {
            predicates.add(cb.like(cb.lower(enumRecord.get(DataType_.name)),
                                                        "%" + escapeDbString(name).toLowerCase() + "%", '\\'));
        }
        if (description != null) {
            predicates.add(cb.like(cb.lower(enumRecord.get(DataType_.description)),
                                                        "%" + escapeDbString(description).toLowerCase() + "%", '\\'));
        }
        if (definition != null) {
            predicates.add(cb.like(cb.lower(enumRecord.get(DataType_.definition)),
                                                        "%" + escapeDbString(definition).toLowerCase() + "%", '\\'));
        }

        return predicates.toArray(new Predicate[] {});
    }
}
