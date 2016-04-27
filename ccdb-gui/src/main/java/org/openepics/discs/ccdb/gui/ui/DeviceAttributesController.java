/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.ccdb.gui.ui;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.ccdb.core.ejb.DeviceEJB;
import org.openepics.discs.ccdb.core.ejb.InstallationEJB;
import org.openepics.discs.ccdb.model.ComponentType;
import org.openepics.discs.ccdb.model.ComptypeArtifact;
import org.openepics.discs.ccdb.model.ComptypePropertyValue;
import org.openepics.discs.ccdb.model.Device;
import org.openepics.discs.ccdb.model.DeviceArtifact;
import org.openepics.discs.ccdb.model.DevicePropertyValue;
import org.openepics.discs.ccdb.model.InstallationRecord;
import org.openepics.discs.ccdb.model.Slot;
import org.openepics.discs.ccdb.model.SlotArtifact;
import org.openepics.discs.ccdb.model.SlotPropertyValue;
import org.openepics.discs.ccdb.model.Tag;
import org.openepics.discs.ccdb.gui.ui.common.AbstractAttributesController;
import org.openepics.discs.ccdb.gui.views.DeviceView;
import org.openepics.discs.ccdb.gui.views.EntityAttrArtifactView;
import org.openepics.discs.ccdb.gui.views.EntityAttrPropertyValueView;
import org.openepics.discs.ccdb.gui.views.EntityAttrTagView;
import org.openepics.discs.ccdb.gui.views.EntityAttributeView;
import org.openepics.discs.ccdb.gui.views.EntityAttributeViewKind;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class DeviceAttributesController
        extends AbstractAttributesController<Device, DevicePropertyValue, DeviceArtifact> {
    private static final long serialVersionUID = 1L;

    @Inject private DeviceEJB deviceEJB;
    @Inject private InstallationEJB installationEJB;

    @Inject private DevicesController devicesController;

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        setDao(deviceEJB);
    }

    @Override
    protected void filterProperties() {
        // nothing to do
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<>();

        for (final DeviceView deviceView : devicesController.getSelectedDevices()) {
            final Device attrDevice = deviceEJB.findById(deviceView.getDevice().getId());
            final ComponentType parent = attrDevice.getComponentType();

            for (final ComptypePropertyValue parentProp : parent.getComptypePropertyList()) {
                if (!parentProp.isPropertyDefinition()) {
                    attributes.add(new EntityAttrPropertyValueView<Device>(parentProp, attrDevice, parent));
                }
            }

            for (final ComptypeArtifact parentArtifact : parent.getComptypeArtifactList()) {
                attributes.add(new EntityAttrArtifactView<Device>(parentArtifact, attrDevice, parent));
            }

            for (final Tag parentTag : parent.getTags()) {
                attributes.add(new EntityAttrTagView<Device>(parentTag, attrDevice, parent));
            }

            for (final DevicePropertyValue propVal : attrDevice.getDevicePropertyList()) {
                attributes.add(new EntityAttrPropertyValueView<Device>(propVal, attrDevice));
            }

            for (final DeviceArtifact artf : attrDevice.getDeviceArtifactList()) {
                attributes.add(new EntityAttrArtifactView<Device>(artf, attrDevice));
            }

            for (final Tag tagAttr : attrDevice.getTags()) {
                attributes.add(new EntityAttrTagView<Device>(tagAttr, attrDevice));
            }

            final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForDevice(attrDevice);
            final Slot slot = installationRecord != null ? installationRecord.getSlot() : null;

            if (slot != null) {
                for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
                    attributes.add(new EntityAttrPropertyValueView<Device>(value, attrDevice, slot));
                }
                for (final SlotArtifact value : slot.getSlotArtifactList()) {
                    attributes.add(new EntityAttrArtifactView<Device>(value, attrDevice, slot));
                }
                for (final Tag tag : slot.getTags()) {
                    attributes.add(new EntityAttrTagView<Device>(tag, attrDevice, slot));
                }
            } else {
                for (final ComptypePropertyValue parentProp : parent.getComptypePropertyList()) {
                    if (parentProp.isDefinitionTargetSlot())
                        attributes.add(new EntityAttrPropertyValueView<Device>(parentProp,
                                                                    EntityAttributeViewKind.INSTALL_SLOT_PROPERTY,
                                                                    attrDevice, parent));
                }
            }
        }
    }

    @Override
    public boolean canEdit(EntityAttributeView<Device> attributeView) {
        final EntityAttributeViewKind attributeKind = attributeView.getKind();
        return EntityAttributeViewKind.DEVICE_PROPERTY.equals(attributeKind)
                || EntityAttributeViewKind.DEVICE_ARTIFACT.equals(attributeKind);
    }

    @Override
    protected boolean canDelete(EntityAttributeView<Device> attributeView) {
        final EntityAttributeViewKind attributeKind = attributeView.getKind();
        return EntityAttributeViewKind.DEVICE_PROPERTY.equals(attributeKind)
                || EntityAttributeViewKind.DEVICE_ARTIFACT.equals(attributeKind)
                || EntityAttributeViewKind.DEVICE_TAG.equals(attributeKind);
    }

    @Override
    protected Device getSelectedEntity() {
        if (devicesController.isSingleDeviceSelected()) {
            return deviceEJB.findById(devicesController.getSelectedDevices().get(0).getDevice().getId());
        }
        throw new IllegalArgumentException("No device selected");
    }

    @Override
    protected DevicePropertyValue newPropertyValue() {
        return new DevicePropertyValue();
    }

    @Override
    protected DeviceArtifact newArtifact() {
        return new DeviceArtifact();
    }
}
