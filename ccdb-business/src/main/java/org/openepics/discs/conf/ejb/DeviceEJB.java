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
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.CRUDOperation;
import org.openepics.discs.conf.util.Utility;

import com.google.common.base.Preconditions;

/**
 * DAO service for accessing device instances.
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Stateless
public class DeviceEJB extends DAO<Device> {
    @Inject private BlobStore blobStore;
    @Inject private ComptypeEJB componentTypesEJB;

    /**
     * Searches for a device instance in the database, by its serial number
     *
     * @param serialNumber the {@link String} serial number
     * @return a device entity matching the criteria or <code>null</code>
     */
    public Device findDeviceBySerialNumber(String serialNumber) {
        return findByName(serialNumber);
    }

    /** Finds a list of device instances of a specified component type.
     * @param componentType - the component type to search for.
     * @return The list of instances of a specified component type.
     */
    public List<Device> findDevicesByComponentType(ComponentType componentType) {
        if (componentType == null) {
            return new ArrayList<>();
        }

        return em.createNamedQuery("Device.findByComponentType", Device.class)
                .setParameter("componentType", componentType).getResultList();
    }

    /**
     * Adds a new device and creates all its defined property values in a single transaction
     * @param device the {@link Device} the add
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addDeviceAndPropertyDefs(Device device) {
        add(device);
        List<ComptypePropertyValue> propertyDefinitions = componentTypesEJB.findPropertyDefinitions(device.getComponentType());
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            if (propertyDefinition.isDefinitionTargetDevice()) {
                final DevicePropertyValue devicePropertyValue = new DevicePropertyValue(false);
                devicePropertyValue.setProperty(propertyDefinition.getProperty());
                devicePropertyValue.setDevice(device);
                devicePropertyValue.setPropValue(propertyDefinition.getPropValue());
                addChild(devicePropertyValue);
            }
        }
    }

    /**
     * This method duplicates selected devices. This method actually copies
     * selected devices serial number, tags, artifacts and properties
     * into the new device type. If property value has uniqueness setting of any type,
     * copied property value is set to <code>null</code>.
     *
     * @param devicesToCopy a {@link List} of {@link Device}s to create a copy of
     * @return the number of copies created
     */
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Authorized
    public int duplicate(List<Device> devicesToCopy) {
        if (Utility.isNullOrEmpty(devicesToCopy)) {
            return 0;
        }

        int duplicated = 0;
        for (final Device deviceToCopy : devicesToCopy) {
            final String newDeviceSerial = Utility.findFreeName(deviceToCopy.getSerialNumber(), this);
            final Device newDevice = new Device(newDeviceSerial);
            newDevice.setComponentType(deviceToCopy.getComponentType());
            duplicate(newDevice, deviceToCopy);
            explicitAuditLog(newDevice, EntityTypeOperation.CREATE);
            ++duplicated;
        }
        return duplicated;
    }

    /**
     * If the type of the device has changed (the method performs this check), the method adds new property values
     * for this type of device from the type definition, and removes the existing ones. If both type definitions
     * contain the same properties, those values are preserved.
     *
     * @param device the {@link Device} to change type for
     * @param newDeviceType the new device type
     * @return the {@link Device} that was passed, fresh form the database if the type was changed
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public Device changeDeviceType(final Device device, final ComponentType newDeviceType) {
        Preconditions.checkNotNull(device);
        Preconditions.checkNotNull(newDeviceType);

        if (device.getComponentType().equals(newDeviceType))
            return device;

        Device freshDevice = refreshEntity(device);
        final List<DevicePropertyValue> deleteList = new ArrayList<>(freshDevice.getDevicePropertyList());
        for (final ComptypePropertyValue newPropDefinition : newDeviceType.getComptypePropertyList()) {
            final boolean isPropertyInDeleteList = isPropertyInPVList(newPropDefinition.getProperty(), deleteList);
            if (newPropDefinition.isDefinitionTargetDevice()) {
                if (!isPropertyInDeleteList) {
                    // old device does not have this property value
                    final DevicePropertyValue newPropertyValue = new DevicePropertyValue(false);
                    newPropertyValue.setProperty(newPropDefinition.getProperty());
                    newPropertyValue.setDevice(freshDevice);
                    addChild(newPropertyValue);
                } else {
                    // the property will remain with the current slot, so we remove it from the delete list
                    DevicePropertyValue valueToDelete = null;
                    for (final DevicePropertyValue dpv : deleteList) {
                        if (dpv.getProperty().equals(newPropDefinition.getProperty())) {
                            valueToDelete = dpv;
                            break;
                        }
                    }
                    if (valueToDelete != null) {
                        deleteList.remove(valueToDelete);
                    }
                }
                freshDevice = refreshEntity(device);
            }
        }
        removePropertyDefinitionsForTypeChange(deleteList);
        freshDevice = refreshEntity(device);
        freshDevice.setComponentType(newDeviceType);
        save(freshDevice);
        return refreshEntity(device);
    }

    private boolean isPropertyInPVList(final Property prop, final List<DevicePropertyValue> propertyValues) {
        for (final DevicePropertyValue propertyValue : propertyValues) {
            if (propertyValue.getProperty().equals(prop)) {
                return true;
            }
        }
        return false;
    }

    private void removePropertyDefinitionsForTypeChange(final List<DevicePropertyValue> deleteList) {
        // delete all properties marked for removal
        final List<DevicePropertyValue> deleteListCopy = new ArrayList<>(deleteList);
        for (final DevicePropertyValue propertyValueToDelete : deleteListCopy) {
            deleteChild(propertyValueToDelete);
        }
    }

    /** Creates a duplicate device, copying all the properties. For the {@link Device} property values
     * @param newCopy a new device that has not been persisted yet
     * @param deviceToCopy the device to copy the properties from
     */
    private void duplicate(Device newCopy, Device deviceToCopy) {
        addDeviceAndPropertyDefs(newCopy);
        transferValuesFromSource(newCopy, deviceToCopy);
        copyArtifactsFromSource(newCopy, deviceToCopy);
        newCopy.getTags().addAll(deviceToCopy.getTags());
        save(newCopy);
    }

    private void transferValuesFromSource(final Device newCopy, final Device copySource) {
        for (final DevicePropertyValue pv : newCopy.getDevicePropertyList()) {
            if (pv.getProperty().getValueUniqueness() == PropertyValueUniqueness.NONE) {
                final DevicePropertyValue parentPv = getPropertyValue(copySource, pv.getProperty().getName());
                if (parentPv != null) {
                    pv.setPropValue(parentPv.getPropValue());
                }
            }
        }
    }

    private void copyArtifactsFromSource(final Device newCopy, final Device copySource) {
        for (final DeviceArtifact artifact : copySource.getDeviceArtifactList()) {
            String uri = artifact.getUri();
            if (artifact.isInternal()) {
                try {
                    uri = blobStore.storeFile(blobStore.retreiveFile(uri));
                } catch (IOException e) {
                    throw new PersistenceException(e);
                }
            }
            final DeviceArtifact newArtifact =
                    new DeviceArtifact(artifact.getName(), artifact.isInternal(), artifact.getDescription(), uri);
            newArtifact.setDevice(newCopy);
            addChild(newArtifact);
        }
    }


    private DevicePropertyValue getPropertyValue(final Device device, final String pvName) {
        for (final DevicePropertyValue pv : device.getDevicePropertyList()) {
            if (pv.getProperty().getName().equals(pvName)) {
                return pv;
            }
        }
        return null;
    }

    @Override
    protected Class<Device> getEntityClass() {
        return Device.class;
    }

    @Override
    protected boolean isPropertyValueTypeUnique(PropertyValue child, Device parent) {
        Preconditions.checkNotNull(child);
        Preconditions.checkNotNull(parent);
        final Value value = child.getPropValue();
        if (value == null) {
            return true;
        }
        final List<PropertyValue> results = em.createNamedQuery("DevicePropertyValue.findSamePropertyValueByType",
                                                        PropertyValue.class)
                    .setParameter("componentType", parent.getComponentType())
                    .setParameter("property", child.getProperty())
                    .setParameter("propValue", value).setMaxResults(2).getResultList();
        // value is unique if there is no property value with the same value, or the only one found us the entity itself
        return (results.size() < 2) && (results.isEmpty() || results.get(0).equals(child));
    }
}
