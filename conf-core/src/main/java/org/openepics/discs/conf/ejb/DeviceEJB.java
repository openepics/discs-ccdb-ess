package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless public class DeviceEJB {
    private static final Logger logger = Logger.getLogger(DeviceEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @Inject private ConfigurationEntityUtility entityUtility;

    // ---------------- Physical Devices -------------------------

    public List<Device> findDevice() {
        final CriteriaQuery<Device> cq = em.getCriteriaBuilder().createQuery(Device.class);
        cq.from(Device.class);
        final List<Device> comps = em.createQuery(cq).getResultList();

        logger.log(Level.FINE, "Number of devices: {0}", comps.size());

        return comps;
    }

    public Device findDevice(Long id) {
        return em.find(Device.class, id);
    }

    public Device findDeviceBySerialNumber(String serialNumber) {
        Device device;
        try {
            device = em.createNamedQuery("Device.findBySerialNumber", Device.class).setParameter("serialNumber", serialNumber).getSingleResult();
        } catch (NoResultException e) {
            device = null;
        }
        return device;
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

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addDevice(Device device) {
        entityUtility.setModified(device);
        em.persist(device);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveDevice(Device device) {
        entityUtility.setModified(device);
        em.merge(device);
    }

    /** Deletes the device.
     * @param device - the device to delete.
     */
    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteDevice(Device device) {
        final Device mergedDevice = em.merge(device);
        em.remove(mergedDevice);
    }

    // ------------------ Device Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addDeviceProperty(DevicePropertyValue propertyValue) {
        final Device parent = propertyValue.getDevice();

        entityUtility.setModified(parent, propertyValue);

        parent.getDevicePropertyList().add(propertyValue);
        em.merge(parent);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveDeviceProp(DevicePropertyValue propertyValue) {
        final DevicePropertyValue mergedPropertyValue = em.merge(propertyValue);

        entityUtility.setModified(mergedPropertyValue.getDevice(), mergedPropertyValue);

        logger.log(Level.FINE, "Device Property: id " + mergedPropertyValue.getId() + " name " + mergedPropertyValue.getProperty().getName());
    }

    /** Deletes the device property value.
     * @param propertyValue - the device property value to delete.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteDeviceProp(DevicePropertyValue propertyValue) {
        logger.log(Level.FINE, "deleting comp type property id " + propertyValue.getId() + " name " + propertyValue.getProperty().getName());

        final DevicePropertyValue mergedPropertyValue = em.merge(propertyValue);
        final Device parent = mergedPropertyValue.getDevice();

        entityUtility.setModified(parent);

        parent.getDevicePropertyList().remove(mergedPropertyValue);
        em.remove(mergedPropertyValue);
    }


    // ---------------- Device Artifact ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addDeviceArtifact(DeviceArtifact artifact) {
        final Device parent = artifact.getDevice();

        entityUtility.setModified(parent, artifact);

        parent.getDeviceArtifactList().add(artifact);
        em.merge(parent);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveDeviceArtifact(DeviceArtifact artifact) {
        final DeviceArtifact mergedArtifact = em.merge(artifact);

        entityUtility.setModified(mergedArtifact.getDevice(), mergedArtifact);

        logger.log(Level.FINE, "Device Type Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    /** Deletes the device artifact.
     * @param artifact - the device artifact to delete.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteDeviceArtifact(DeviceArtifact artifact) {
        final DeviceArtifact mergedArtifact = em.merge(artifact);
        final Device parent = mergedArtifact.getDevice();

        entityUtility.setModified(parent);

        parent.getDeviceArtifactList().remove(mergedArtifact);
        em.remove(mergedArtifact);
    }
}