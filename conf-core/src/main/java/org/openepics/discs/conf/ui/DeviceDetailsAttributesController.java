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
package org.openepics.discs.conf.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;

/**
 * Controller bean for manipulation of {@link Device} attributes
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Named
@ViewScoped
public class DeviceDetailsAttributesController extends
                        AbstractAttributesController<DevicePropertyValue, DeviceArtifact> {
    private static final long serialVersionUID = -2881746639197321061L;

    @Inject private transient DeviceEJB deviceEJB;

    private Device device;

    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            setArtifactClass(DeviceArtifact.class);
            setPropertyValueClass(DevicePropertyValue.class);
            setDao(deviceEJB);
        } catch(Exception e) {
            throw new UIException("Device details display initialization fialed: " + e.getMessage(), e);
        }
    }

    @Override
    protected void setPropertyValueParent(DevicePropertyValue child) {
        child.setDevice(device);
    }

    @Override
    protected void setArtifactParent(DeviceArtifact child) {
        child.setDevice(device);
    }

    @Override
    protected void setTagParent(Tag tag) {
        final Set<Tag> existingTags = device.getTags();
        if (!existingTags.contains(tag)) {
            existingTags.add(tag);
            deviceEJB.save(device);
        }
    }

    /** The method prepares all the UI related data to show to the user, when the user selects a new device instance
     * in the table.
     * @param device the device instance user selected in the UI
     * @see #clearDeviceInstance()
     */
    public void prepareDeviceInstance(Device device) {
        this.device = device;
        parentProperties = device.getComponentType().getComptypePropertyList();
        parentArtifacts = device.getComponentType().getComptypeArtifactList();
        entityName = device.getSerialNumber();
        selectedAttribute = null;
        populateParentTags();
        populateAttributesList();
    }

    /** The method clears all the device instance related information when the user deselects the device instance in
     * the table.
     * @see #prepareDeviceInstance(Device)
     */
    public void clearDeviceInstance() {
        device = null;
        parentProperties = null;
        parentArtifacts = null;
        entityName = null;
        parentTags = null;
        attributes = null;
        selectedAttribute = null;
    }

    @Override
    protected void deleteTagFromParent(Tag tag) {
        device.getTags().remove(tag);
        deviceEJB.save(device);
    }

    @Override
    protected void filterProperties() {
        // nothing to do
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<EntityAttributeView>();

        // refresh the device from database. This refreshes all related collections as well.
        device = deviceEJB.findById(device.getId());

        for (final ComptypePropertyValue parentProp : parentProperties) {
            if (parentProp.getPropValue() != null) {
                attributes.add(new EntityAttributeView(parentProp, EntityAttributeViewKind.DEVICE_TYPE_PROPERTY));
            }
        }

        for (final ComptypeArtifact parentArtifact : parentArtifacts) {
            attributes.add(new EntityAttributeView(parentArtifact, EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT));
        }

        for (final Tag parentTag : parentTags) {
            attributes.add(new EntityAttributeView(parentTag, EntityAttributeViewKind.DEVICE_TYPE_TAG));
        }

        for (final DevicePropertyValue propVal : device.getDevicePropertyList()) {
            attributes.add(new EntityAttributeView(propVal, EntityAttributeViewKind.DEVICE_PROPERTY));
        }

        for (final DeviceArtifact artf : device.getDeviceArtifactList()) {
            attributes.add(new EntityAttributeView(artf, EntityAttributeViewKind.DEVICE_ARTIFACT));
        }

        for (final Tag tagAttr : device.getTags()) {
            attributes.add(new EntityAttributeView(tagAttr, EntityAttributeViewKind.DEVICE_TAG));
        }
    }

    @Override
    protected void populateParentTags() {
        parentTags = new HashSet<Tag>();
        for (final Tag parentTag : device.getComponentType().getTags()) {
            if (!device.getTags().contains(parentTag)) {
                parentTags.add(parentTag);
            }
        }
    }

    @Override
    public void modifyBuiltInProperty() {
        populateAttributesList();
    }
}
