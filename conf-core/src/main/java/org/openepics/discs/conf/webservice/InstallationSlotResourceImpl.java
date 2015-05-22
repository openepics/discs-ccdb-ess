package org.openepics.discs.conf.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.jaxb.InstallationSlot;
import org.openepics.discs.conf.jaxb.InstallationSlotBasic;
import org.openepics.discs.conf.jaxb.PropertyValue;
import org.openepics.discs.conf.jaxrs.InstallationSlotResource;

/**
 * An implementation of the InstallationSlotResource interface.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
public class InstallationSlotResourceImpl implements InstallationSlotResource {

    @Inject private SlotEJB slotEJB;

    private interface RelatedSlotExtractor {
        public Slot getRelatedSlot(final SlotPair pair);
    }

    @Override
    public List<InstallationSlot> getAllSlots() {
        return new ArrayList<InstallationSlot>();
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
        installationSlot.setParents(getRelatedSlots(slot.getPairsInWhichThisSlotIsAChildList(), new RelatedSlotExtractor() {
            @Override
            public Slot getRelatedSlot(SlotPair pair) {
                return pair.getParentSlot();
            }
        }));
        installationSlot.setChildren(getRelatedSlots(slot.getPairsInWhichThisSlotIsAParentList(), new RelatedSlotExtractor() {
            @Override
            public Slot getRelatedSlot(SlotPair pair) {
                return pair.getChildSlot();
            }
        }));
        installationSlot.setProperties(getPropertyValues(slot));
        return installationSlot;
    }

    private List<InstallationSlotBasic> getRelatedSlots(final List<SlotPair> relatedSlots, final RelatedSlotExtractor extractor) {
        final List<InstallationSlotBasic> list = new ArrayList<InstallationSlotBasic>();
        for (final SlotPair pair : relatedSlots) {
            final Slot relatedSlot = extractor.getRelatedSlot(pair);
            if (relatedSlot.isHostingSlot()) {
                list.add(InstallationSlotBasicResourceImpl.getInstallationSlotBasic(relatedSlot));
            }
        }
        return list;
    }

    private List<PropertyValue> getPropertyValues(final Slot slot) {
        final List<PropertyValue> values = new ArrayList<PropertyValue>();

        for (final ComptypePropertyValue propertyValue : slot.getComponentType().getComptypePropertyList()) {
            if (!propertyValue.isPropertyDefinition()) {
                values.add(createPropertyValue(propertyValue));
            }
        }

        for (final SlotPropertyValue propertyValue : slot.getSlotPropertyList()) {
            values.add(createPropertyValue(propertyValue));
        }

        return values;
    }

    private PropertyValue createPropertyValue(final org.openepics.discs.conf.ent.PropertyValue slotPropertyValue) {
        final PropertyValue propertyValue = new PropertyValue();
        final Property parentProperty = slotPropertyValue.getProperty();
        propertyValue.setName(parentProperty.getName());
        propertyValue.setDataType(parentProperty.getDataType().getName());
        propertyValue.setUnit(parentProperty.getUnit() == null ? null : parentProperty.getUnit().getName());
        propertyValue.setValue(slotPropertyValue.getPropValue().toString());
        return propertyValue;
    }
}
