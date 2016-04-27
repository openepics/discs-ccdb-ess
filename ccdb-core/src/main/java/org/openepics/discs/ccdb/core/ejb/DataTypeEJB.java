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
package org.openepics.discs.ccdb.core.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.ccdb.model.AlignmentPropertyValue;
import org.openepics.discs.ccdb.model.ComptypePropertyValue;
import org.openepics.discs.ccdb.model.DataType;
import org.openepics.discs.ccdb.model.DevicePropertyValue;
import org.openepics.discs.ccdb.model.EntityTypeOperation;
import org.openepics.discs.ccdb.model.Property;
import org.openepics.discs.ccdb.model.PropertyValue;
import org.openepics.discs.ccdb.model.SlotPropertyValue;
import org.openepics.discs.ccdb.core.security.Authorized;
import org.openepics.discs.ccdb.core.util.CRUDOperation;
import org.openepics.discs.ccdb.core.util.Utility;

import com.google.common.base.Preconditions;

/**
 * DAO service for accesing {@link DataType} entities
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
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
}
