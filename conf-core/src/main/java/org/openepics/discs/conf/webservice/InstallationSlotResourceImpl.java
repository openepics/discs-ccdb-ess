package org.openepics.discs.conf.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
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

    @FunctionalInterface
    private interface RelatedSlotExtractor {
        public Slot getRelatedSlot(final SlotPair pair);
    }

    @Override
    public List<InstallationSlot> getAllSlots() {
        return slotEJB.findAll().stream().
                filter(slot -> slot!=null && slot.isHostingSlot()).
                map(slot -> createInstallationSlot(slot)).
                collect(Collectors.toList());        
    }

    @Override
    public InstallationSlot getInstallationSlot(String name) {
        final Slot installationSlot = slotEJB.findByName(name);
        if (installationSlot == null || !installationSlot.isHostingSlot()) {
            return null;
        }
        return createInstallationSlot(installationSlot);
    }

    private InstallationSlot createInstallationSlot(final Slot slot) {
        final InstallationSlot installationSlot = new InstallationSlot();
        installationSlot.setName(slot.getName());
        installationSlot.setDesription(slot.getDescription());
        installationSlot.setDeviceType(DeviceTypeResourceImpl.getDeviceType(slot.getComponentType()));
        installationSlot.setParents(getRelatedSlots(slot.getPairsInWhichThisSlotIsAChildList(), 
                pair -> pair.getParentSlot()));
        installationSlot.setChildren(getRelatedSlots(slot.getPairsInWhichThisSlotIsAParentList(), 
                pair -> pair.getChildSlot()));
        installationSlot.setProperties(getPropertyValues(slot));
        return installationSlot;
    }

    private List<String> getRelatedSlots(final List<SlotPair> relatedSlots, final RelatedSlotExtractor extractor) {
        return relatedSlots.stream().
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
