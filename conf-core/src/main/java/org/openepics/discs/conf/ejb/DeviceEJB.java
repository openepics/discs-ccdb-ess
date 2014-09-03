package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;

/**
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class DeviceEJB extends DAO<Device> {

    public Device findDeviceBySerialNumber(String serialNumber) {
        return findByName(serialNumber);
    }

    /** Finds a list of device instances of a specified component type.
     * @param componentType - the component type to search for.
     * @return The list of instances of a specified component type.
     */
    public List<Device> findDevicesByComponentType(ComponentType componentType) {
        if (componentType == null) return null;

        final List<Device> devices = em.createNamedQuery("Device.findByComponentType", Device.class).setParameter("componentType", componentType).getResultList();
        return devices;
    }

    @Override
    protected void defineEntity() {
        defineEntityClass(Device.class);

        defineParentChildInterface(DevicePropertyValue.class, new ParentChildInterface<Device, DevicePropertyValue>() {
            @Override
            public List<DevicePropertyValue> getChildCollection(Device device) {
                return device.getDevicePropertyList();
            }
            @Override
            public Device getParentFromChild(DevicePropertyValue child) {
                return child.getDevice();
            }
        });

        defineParentChildInterface(DeviceArtifact.class, new ParentChildInterface<Device, DeviceArtifact>() {

            @Override
            public List<DeviceArtifact> getChildCollection(Device device) {
                return device.getDeviceArtifactList();

            }

            @Override
            public Device getParentFromChild(DeviceArtifact child) {
                return child.getDevice();
            }
        });
    }
}