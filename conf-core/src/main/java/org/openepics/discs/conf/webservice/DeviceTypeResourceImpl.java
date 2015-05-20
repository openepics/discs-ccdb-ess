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
