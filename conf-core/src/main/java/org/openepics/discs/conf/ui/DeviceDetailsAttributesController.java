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
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.views.EntityAttributeView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

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
    @Inject private PropertyEJB propertyEJB;

    private Device device;

    @PostConstruct
    public void init() {
        final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
        device = deviceEJB.findById(id);
        super.setArtifactClass(DeviceArtifact.class);
        super.setPropertyValueClass(DevicePropertyValue.class);
        super.setDao(deviceEJB);

        parentProperties = device.getComponentType().getComptypePropertyList();

        populateAttributesList();
        filterProperties();
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
        filteredProperties = ImmutableList.copyOf(Collections2.filter(propertyEJB.findAll(), new Predicate<Property>() {
            @Override
            public boolean apply(Property property) {
                final PropertyAssociation propertyAssociation = property.getAssociation();
                return propertyAssociation == PropertyAssociation.ALL || propertyAssociation == PropertyAssociation.DEVICE
                       || propertyAssociation == PropertyAssociation.SLOT_DEVICE
                       || propertyAssociation == PropertyAssociation.TYPE_DEVICE;
            }
        }));
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<EntityAttributeView>();

        // refresh the device from database. This refreshes all related collections as well.
        device = deviceEJB.findById(device.getId());

        for (ComptypePropertyValue parentProp : parentProperties) {
            if (parentProp.getPropValue() != null) attributes.add(new EntityAttributeView(parentProp));
        }

        for (DevicePropertyValue propVal : device.getDevicePropertyList()) {
            attributes.add(new EntityAttributeView(propVal));
        }

        // TODO check whether to show inherited artifacts and prevent their deletion
        for (DeviceArtifact artf : device.getDeviceArtifactList()) {
            attributes.add(new EntityAttributeView(artf));
        }

        // TODO solve and add inherited tags.
        for (Tag tag : device.getTags()) {
            attributes.add(new EntityAttributeView(tag));
        }
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }

}
