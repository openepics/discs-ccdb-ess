package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class DeviceEJB {

    private static final Logger logger = Logger.getLogger(DeviceEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // ---------------- Physical Component -------------------------

    public List<Device> findDevice() {
        final List<Device> comps;
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Device> cq = cb.createQuery(Device.class);

        final TypedQuery<Device> query = em.createQuery(cq);
        comps = query.getResultList();
        logger.log(Level.INFO, "Number of devices: {0}", comps.size());

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

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveDevice(String token, Device device) {
        logger.log(Level.INFO, "Preparing to save device");
        device.setModifiedAt(new Date());
        em.merge(device);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addDevice(Device device) {
        em.persist(device);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteDevice(Device device) {
        device.setModifiedAt(new Date());
        final Device ct = em.find(Device.class, device.getId());
        em.remove(ct);
    }

    // ------------------ Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveDeviceProp(DevicePropertyValue prop) {
        prop.setModifiedAt(new Date());
        final DevicePropertyValue newProp = em.merge(prop);
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteDeviceProp(DevicePropertyValue prop) {
        prop.setModifiedAt(new Date());
        final DevicePropertyValue property = em.find(DevicePropertyValue.class, prop.getId());
        final Device device = property.getDevice();
        device.getDevicePropertyList().remove(property);
        em.remove(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addDeviceProperty(DevicePropertyValue property) {
        final DevicePropertyValue newProp = em.merge(property);
        final Device device = property.getDevice();
        device.getDevicePropertyList().add(newProp);
        em.merge(device);
    }

    // ---------------- Artifact ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveDeviceArtifact(DeviceArtifact art) {
        art.setModifiedAt(new Date());
        em.merge(art);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteDeviceArtifact(DeviceArtifact art) {
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        final DeviceArtifact artifact = em.find(DeviceArtifact.class, art.getId());
        final Device device = artifact.getDevice();
        device.getDeviceArtifactList().remove(artifact);
        em.remove(artifact);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addDeviceArtifact(DeviceArtifact art) {
        final DeviceArtifact newArt = em.merge(art);
        final Device dev = art.getDevice();
        dev.getDeviceArtifactList().add(newArt);
        em.merge(dev);
    }
}