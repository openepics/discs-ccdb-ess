/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Cable Database.
 * Cable Database is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.jaxb.DeviceType;
import org.openepics.discs.conf.jaxrs.DeviceTypeResource;

public class DeviceTypeResourceImpl implements DeviceTypeResource {

    @Inject private ComptypeEJB comptypeEJB;

    @Override
    public List<DeviceType> getAllDeviceTypes() {
        final List<DeviceType> allTypes = new ArrayList<DeviceType>();

        for (final ComponentType componentType : comptypeEJB.findAll()) {
            allTypes.add(getDeviceType(componentType));
        }

        return allTypes;
    }

    @Override
    public DeviceType getDeviceType(Long id) {
        return getDeviceType(comptypeEJB.findById(id));
    }

    private DeviceType getDeviceType(ComponentType componentType) {
        final DeviceType deviceType = new DeviceType();
        deviceType.setId(componentType.getId());
        deviceType.setName(componentType.getName());
        deviceType.setDescription(componentType.getDescription());
        deviceType.setModifiedBy(componentType.getModifiedBy());
        deviceType.setModifiedAt(componentType.getModifiedAt());
        return deviceType;
    }
}
