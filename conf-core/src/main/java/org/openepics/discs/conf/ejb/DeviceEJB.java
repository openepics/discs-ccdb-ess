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

    /** Deletes the device and returns <code>true</code> if deletion was successful.
     * @param device - the device to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the device is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public boolean deleteDevice(Device device) {
        final Device deviceToDelete = em.find(Device.class, device.getId());
        em.remove(deviceToDelete);
        return true;
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

    /** Deletes the device property value and returns <code>true</code> if deletion was successful.
     * @param propertyValue - the device property value to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the device property value is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public boolean deleteDeviceProp(DevicePropertyValue propertyValue) {
        logger.log(Level.FINE, "deleting comp type property id " + propertyValue.getId() + " name " + propertyValue.getProperty().getName());

        final DevicePropertyValue propertyValueToDelete = em.find(DevicePropertyValue.class, propertyValue.getId());
        final Device parent = propertyValueToDelete.getDevice();

        entityUtility.setModified(parent);

        parent.getDevicePropertyList().remove(propertyValueToDelete);
        em.remove(propertyValueToDelete);
        return true;
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

    /** Deletes the device artifact and returns <code>true</code> if deletion was successful.
     * @param artifact - the device artifact to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the device artifact is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public boolean deleteDeviceArtifact(DeviceArtifact artifact) {
        final DeviceArtifact artifactToDelete = em.find(DeviceArtifact.class, artifact.getId());
        final Device parent = artifactToDelete.getDevice();

        entityUtility.setModified(parent);

        parent.getDeviceArtifactList().remove(artifactToDelete);
        em.remove(artifactToDelete);
        return true;
    }
}