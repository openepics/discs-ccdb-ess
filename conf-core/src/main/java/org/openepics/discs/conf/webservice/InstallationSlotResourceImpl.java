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
package org.openepics.discs.conf.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.jaxb.InstallationSlot;
import org.openepics.discs.conf.jaxb.PropertyValue;
import org.openepics.discs.conf.jaxrs.InstallationSlotResource;

/**
 * An implementation of the InstallationSlotResource interface.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
public class InstallationSlotResourceImpl implements InstallationSlotResource {
    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB compTypeEJB;

    @FunctionalInterface
    private interface RelatedSlotExtractor {
        public Slot getRelatedSlot(final SlotPair pair);
    }

    @Override
    public List<InstallationSlot> getInstallationSlots(String deviceType) {
        // Get all slots
        if ("undefined".equals(deviceType)) {
            return slotEJB.findAll().stream().
                filter(slot -> slot!=null && slot.isHostingSlot()).
                map(slot -> createInstallationSlot(slot)).
                collect(Collectors.toList());
        } else {
            // Get them filtered by deviceType
            return getInstallationSlotsForType(deviceType);
        }
    }

    @Override
    public InstallationSlot getInstallationSlot(String name) {
        final Slot installationSlot = slotEJB.findByName(name);
        if (installationSlot == null || !installationSlot.isHostingSlot()) {
            return null;
        }
        return createInstallationSlot(installationSlot);
    }

    private List<InstallationSlot> getInstallationSlotsForType(String deviceType) {
        if (StringUtils.isEmpty(deviceType)) {
            return new ArrayList<>();
        }

        final ComponentType ct = compTypeEJB.findByName(deviceType);
        if (ct == null) {
            return new ArrayList<>();
        }

        return slotEJB.findByComponentType(ct).stream().
                map(slot -> createInstallationSlot(slot)).
                collect(Collectors.toList());
    }

    private InstallationSlot createInstallationSlot(final Slot slot) {
        if (slot==null) {
            return null;
        }

        final InstallationSlot installationSlot = new InstallationSlot();
        installationSlot.setName(slot.getName());
        installationSlot.setDesription(slot.getDescription());
        installationSlot.setDeviceType(DeviceTypeResourceImpl.getDeviceType(slot.getComponentType()));

        installationSlot.setParents(
                getRelatedSlots(slot.getPairsInWhichThisSlotIsAChildList().stream(),
                        SlotRelationName.CONTAINS,
                        pair -> pair.getParentSlot()));
        installationSlot.setChildren(
                getRelatedSlots(slot.getPairsInWhichThisSlotIsAParentList().stream(),
                        SlotRelationName.CONTAINS,
                        pair -> pair.getChildSlot()));

        installationSlot.setPoweredBy(
                getRelatedSlots(slot.getPairsInWhichThisSlotIsAChildList().stream(),
                        SlotRelationName.POWERS,
                        pair -> pair.getParentSlot()));
        installationSlot.setPowers(
                getRelatedSlots(slot.getPairsInWhichThisSlotIsAParentList().stream(),
                        SlotRelationName.POWERS,
                        pair -> pair.getChildSlot()));

        installationSlot.setControlledBy(
                getRelatedSlots(slot.getPairsInWhichThisSlotIsAChildList().stream(),
                        SlotRelationName.CONTROLS,
                        pair -> pair.getParentSlot()));
        installationSlot.setControls(
                getRelatedSlots(slot.getPairsInWhichThisSlotIsAParentList().stream(),
                        SlotRelationName.CONTROLS,
                        pair -> pair.getChildSlot()));

        installationSlot.setProperties(getPropertyValues(slot));
        return installationSlot;
    }

    private List<String> getRelatedSlots(final Stream<SlotPair> relatedSlotPairs,
            final SlotRelationName relationName,
            final RelatedSlotExtractor extractor) {
        return relatedSlotPairs.
                filter(slotPair -> relationName.equals(slotPair.getSlotRelation().getName())).
                map(relatedSlotPair -> extractor.getRelatedSlot(relatedSlotPair)).
                filter(slot -> slot.isHostingSlot()).
                map(slot -> slot.getName()).
                collect(Collectors.toList());
    }

    private List<PropertyValue> getPropertyValues(final Slot slot) {
        return Stream.concat(
                slot.getComponentType().getComptypePropertyList().stream().
                        filter(propValue -> !propValue.isPropertyDefinition()).
                        map(propValue -> createPropertyValue(propValue)),
                slot.getSlotPropertyList().stream().
                        map(propValue -> createPropertyValue(propValue))).
                collect(Collectors.toList());
    }

    private PropertyValue createPropertyValue(final org.openepics.discs.conf.ent.PropertyValue slotPropertyValue) {
        final PropertyValue propertyValue = new PropertyValue();
        final Property parentProperty = slotPropertyValue.getProperty();
        propertyValue.setName(parentProperty.getName());
        propertyValue.setDataType(parentProperty.getDataType() != null ? parentProperty.getDataType().getName() : null);
        propertyValue.setUnit(parentProperty.getUnit() != null ? parentProperty.getUnit().getName() : null);
        propertyValue.setValue(Objects.toString(slotPropertyValue.getPropValue()));
        return propertyValue;
    }
}
