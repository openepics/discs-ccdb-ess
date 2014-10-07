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

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;

import com.google.common.base.Preconditions;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
@Stateless
public class InstallationEJB extends DAO<InstallationRecord> {

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

}
