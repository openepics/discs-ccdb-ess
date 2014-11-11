/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

import com.google.common.base.Preconditions;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
@Stateless
public class InstallationEJB extends DAO<InstallationRecord> {
    private static final Logger logger = Logger.getLogger(InstallationEJB.class.getCanonicalName());

    @Override
    protected void defineEntity() {
        defineEntityClass(InstallationRecord.class);

        defineParentChildInterface(InstallationArtifact.class,
                new ParentChildInterface<InstallationRecord, InstallationArtifact>() {
            @Override
            public List<InstallationArtifact> getChildCollection(InstallationRecord iRecord) {
                return iRecord.getInstallationArtifactList();
            }

            @Override
            public InstallationRecord getParentFromChild(InstallationArtifact child) {
                return child.getInstallationRecord();
            }
        });
    }

    /**
     * @param slot the installation slot to find active installation record for.
     * @return The currently active installation slot (an installation slot which has uninstall date <code>NULL</code>),
     * <code>null</code> otherwise.
     */
    public InstallationRecord getActiveInstallationRecordForSlot(Slot slot) {
        Preconditions.checkNotNull(slot);
        try {
            return em.createNamedQuery("InstallationRecord.activeRecordForSlot", InstallationRecord.class)
                .setParameter("slot", slot).getSingleResult();
        } catch (NoResultException e) {
            // no result is not an exception
            return null;
        }
    }

    /**
     * @param device the device instance to find active installation record for.
     * @return The currently active installation slot (an installation slot which has uninstall date <code>NULL</code>),
     * <code>null</code> otherwise.
     */
    public InstallationRecord getActiveInstallationRecordForDevice(Device device) {
        Preconditions.checkNotNull(device);
        try {
            return em.createNamedQuery("InstallationRecord.activeRecordForDevice", InstallationRecord.class)
                .setParameter("device", device).getSingleResult();
        } catch (NoResultException e) {
            // no result is not an exception
            return null;
        }
    }

    /**
     * @param componentType the device type for which we are requesting information.
     * @return The list of all device instances which are not installed into any installation slot.
     */
    public List<Device> getUninstalledDevices(ComponentType componentType) {
        Preconditions.checkNotNull(componentType);
        return em.createNamedQuery("Device.uninstalledDevicesByType", Device.class).
                setParameter("componentType", componentType).getResultList();
    }

    @Override
    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void add(InstallationRecord record) {
        final Device device = record.getDevice();
        final Slot slot = record.getSlot();
        Preconditions.checkNotNull(device);
        Preconditions.checkNotNull(slot);
        if (!slot.getComponentType().equals(device.getComponentType())) {
            logger.log(Level.WARNING, "The device and installation slot device types do not match.");
            throw new RuntimeException("The device and installation slot device types do not match.");
        }
        // we must check whether the selected slot is already occupied or selected device is already installed
        final InstallationRecord slotCheck = getActiveInstallationRecordForSlot(slot);
        if (slotCheck != null) {
            logger.log(Level.WARNING, "An attempt was made to install a device in an already occupied slot.");
            throw new RuntimeException("Slot already occupied.");
        }
        final InstallationRecord deviceCheck = getActiveInstallationRecordForDevice(device);
        if (deviceCheck != null) {
            logger.log(Level.WARNING, "An attempt was made to install a device that is already installed.");
            throw new RuntimeException("Device already installed.");
        }

        super.add(record);
    }
}
