/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.EntityAttributeViewKind;
import org.openepics.discs.conf.views.EntityAttributeView;

/**
 * Controller bean for manipulation of {@link Device} attributes
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class DeviceDetailsAttributesController extends AbstractAttributesController<DevicePropertyValue, DeviceArtifact> {

    @Inject private DeviceEJB deviceEJB;

    private Device device;

    @PostConstruct
    public void init() {
        try {
            final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
            device = deviceEJB.findById(id);
            super.setArtifactClass(DeviceArtifact.class);
            super.setPropertyValueClass(DevicePropertyValue.class);
            super.setDao(deviceEJB);

            parentProperties = device.getComponentType().getComptypePropertyList();
            parentArtifacts = device.getComponentType().getComptypeArtifactList();
            populateParentTags();
            
            populateAttributesList();
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

        for (ComptypePropertyValue parentProp : parentProperties) {
            if (parentProp.getPropValue() != null) attributes.add(new EntityAttributeView(parentProp, EntityAttributeViewKind.DEVICE_TYPE.toString() + " " + EntityAttributeViewKind.PROPERTY.toString()));
        }
        
        for (ComptypeArtifact parentArtifact : parentArtifacts) {
            attributes.add(new EntityAttributeView(parentArtifact, EntityAttributeViewKind.DEVICE_TYPE.toString() + " " + EntityAttributeViewKind.ARTIFACT.toString()));
        }
        
        for (Tag parentTag : parentTags) {
            attributes.add(new EntityAttributeView(parentTag, EntityAttributeViewKind.DEVICE_TYPE.toString() + " " + EntityAttributeViewKind.TAG.toString())); 
        }

        for (DevicePropertyValue propVal : device.getDevicePropertyList()) {
            attributes.add(new EntityAttributeView(propVal, EntityAttributeViewKind.DEVICE.toString() + " " + EntityAttributeViewKind.PROPERTY.toString()));
        }

        for (DeviceArtifact artf : device.getDeviceArtifactList()) {
            attributes.add(new EntityAttributeView(artf, EntityAttributeViewKind.DEVICE.toString() + " " + EntityAttributeViewKind.ARTIFACT.toString()));
        }

        for (Tag tag : device.getTags()) {
            attributes.add(new EntityAttributeView(tag, EntityAttributeViewKind.DEVICE.toString() + " " + EntityAttributeViewKind.TAG.toString()));
        }
    }
    
    @Override
    protected void populateParentTags() {
        parentTags = new HashSet<Tag>();
        for (Tag parentTag : device.getComponentType().getTags()) {
            if (!device.getTags().contains(parentTag)) {
                parentTags.add(parentTag);
            }
        }
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }
}
