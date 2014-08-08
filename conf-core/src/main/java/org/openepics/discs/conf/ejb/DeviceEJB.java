package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.Audit;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 */
@Stateless public class DeviceEJB {

    @EJB private AuthEJB authEJB;
    @Inject private LoginManager loginManager;
    private static final Logger logger = Logger.getLogger(DeviceEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // ---------------- Physical Component -------------------------

    public List<Device> findDevice() {
        List<Device> comps;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Device> cq = cb.createQuery(Device.class);
        Root<Device> prop = cq.from(Device.class);

        TypedQuery<Device> query = em.createQuery(cq);
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
        Device ct = em.find(Device.class, device.getId());
        em.remove(ct);
    }

    // ------------------ Property ---------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveDeviceProp(DevicePropertyValue prop, boolean create) {
        prop.setModifiedAt(new Date());
        DevicePropertyValue newProp = em.merge(prop);

        if (create) { // create instead of update
            Device device = prop.getDevice();
            device.getDevicePropertyList().add(newProp);
            em.merge(device);
        }
        logger.log(Level.INFO, "Comp Type Property: id " + newProp.getId() + " name " + newProp.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteDeviceProp(DevicePropertyValue prop) {
        DevicePropertyValue property = em.find(DevicePropertyValue.class, prop.getId());
        Device device = property.getDevice();
        device.getDevicePropertyList().remove(property);
        em.remove(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    public void addDeviceProperty(DevicePropertyValue property) {
        em.persist(property);
    }

    // ---------------- Artifact ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    public void saveDeviceArtifact(DeviceArtifact art, boolean create) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteDeviceArtifact: Device is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.DEVICE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        art.setModifiedAt(new Date());
        art.setModifiedBy("user");
        DeviceArtifact newArt = em.merge(art);
        if (create) { // create instead of update
            Device dev = art.getDevice();
            dev.getDeviceArtifactList().add(newArt);
            em.merge(dev);
        }
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    public void deleteDeviceArtifact(DeviceArtifact art) throws Exception {
        if (art == null) {
            logger.log(Level.SEVERE, "deleteDeviceArtifact: dev-artifact is null");
            return;
        }
        String user = loginManager.getUserid();
        if (!authEJB.userHasAuth(user, EntityType.DEVICE, EntityTypeOperation.UPDATE)) {
            logger.log(Level.SEVERE, "User is not authorized to perform this operation:  " + user);
            throw new Exception("User " + user + " is not authorized to perform this operation");
        }
        logger.log(Level.INFO, "deleting " + art.getName() + " id " + art.getId() + " des " + art.getDescription());
        DeviceArtifact artifact = em.find(DeviceArtifact.class, art.getId());
        Device device = artifact.getDevice();
        device.getDeviceArtifactList().remove(artifact);
        em.remove(artifact);
    }
}