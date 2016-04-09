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
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.openepics.discs.conf.ejb.ComptypeEJB;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.jaxb.ResDevice;
import org.openepics.discs.conf.jaxrs.DeviceResource;

/**
 * An implementation of the DeviceTypeResource interface.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
public class DeviceResourceImpl implements DeviceResource {

    @Inject
    private DeviceEJB deviceEJB;
    @Inject
    private ComptypeEJB compTypeEJB;

    @Override
    public List<ResDevice> getAllDevices(String deviceType) {
        if ("undefined".equals(deviceType)) {
            return deviceEJB.findAll().stream().
                    map(dev -> getDevice(dev)).
                    collect(Collectors.toList());
        } else {
            // Get them filtered by deviceType
            return getDevicesOfType(deviceType);
        }
    }

    private List<ResDevice> getDevicesOfType(String deviceType) {
        if (StringUtils.isEmpty(deviceType)) {
            return new ArrayList<>();
        }

        final ComponentType ct = compTypeEJB.findByName(deviceType);
        if (ct == null) {
            return new ArrayList<>();
        }

        return deviceEJB.findDevicesByComponentType(ct).stream().
                map(device -> getDevice(device)).
                collect(Collectors.toList());
    }

    @Override
    public ResDevice getDevice(String iid) {
        return getDevice(deviceEJB.findDeviceBySerialNumber(iid));
    }

    /**
     * Transforms a CCDB database entity into a REST DTO object. Called from
     * other web service classes as well.
     *
     * @param device the CCDB database entity to wrap
     * @return REST DTO object
     */
    protected static ResDevice getDevice(Device device) {
        if (device == null) {
            return null;
        } else {
            final ResDevice resDevice = new ResDevice();
            resDevice.setInventoryId(device.getSerialNumber());
            resDevice.setDeviceType(DeviceTypeResourceImpl.getDeviceType(device.getComponentType()));
            return resDevice;
        }
    }
}
