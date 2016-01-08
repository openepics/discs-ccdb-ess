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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.jaxb.DeviceType;
import org.openepics.discs.conf.jaxrs.DeviceTypeResource;

/**
 * An implementation of the DeviceTypeResource interface.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
public class DeviceTypeResourceImpl implements DeviceTypeResource {

    @Inject private ComptypeEJB comptypeEJB;

    @Override
    public List<DeviceType> getAllDeviceTypes() {
        return comptypeEJB.findAll().stream().
                map(compType -> getDeviceType(compType)).
                collect(Collectors.toList());
    }

    @Override
    public DeviceType getDeviceType(String name) {
        return getDeviceType(comptypeEJB.findByName(name));
    }

    /** Transforms a CCDB database entity into a REST DTO object. Called from other web service classes as well.
     * @param componentType the CCDB database entity to wrap
     * @return REST DTO object
     */
    protected static DeviceType getDeviceType(ComponentType componentType) {
        if (componentType == null) {
            return null;
        } else {
            final DeviceType deviceType = new DeviceType();
            deviceType.setName(componentType.getName());
            deviceType.setDescription(componentType.getDescription());
            return deviceType;
        }
    }
}
