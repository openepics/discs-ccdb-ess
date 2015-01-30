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
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.DeviceInstallation;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class InstallationManager implements Serializable {
    private static final long serialVersionUID = -16035870593037184L;

    private static final Logger LOGGER = Logger.getLogger(InstallationManager.class.getCanonicalName());

    @Inject transient private InstallationEJB installationEJB;
    @Inject transient private SlotEJB slotEJB;
    @Inject @DeviceInstallation private SlotsTreeBuilder slotsTreeBuilder;

    private Device installedDevice;
    private TreeNode installableSlots;
    private TreeNode selectedSlot;

    /**
     * Creates a new instance of InstallationManager
     */
    public InstallationManager() {
    }

    /** Used in the device details screen.
     * @param device the device instance to query for.
     * @return <code>true</code> if this device instance is currently installed, <code>false</code> otherwise.
     */
    public boolean isDeviceInstalled(Device device) {
        if (!device.equals(this.installedDevice)) {
            setInstalledDevice(installationEJB.getActiveInstallationRecordForDevice(device));
        }
        return this.installedDevice != null;
    }

    private void setInstalledDevice(InstallationRecord installationRecord) {
        if (installationRecord != null) {
            this.installedDevice = installationRecord.getDevice();
        } else {
            this.installedDevice = null;
        }
    }

    /** Used in the device details screen.
     * @param device the device to query for.
     * @return The containers and slots hierarchy, showing only the branches that contain the installatio0n slots
     * of appropriate device type. Also, only empty installation slots of appropriate device type are selectable.
     */
    public TreeNode getInstallationTree(Device device) {
        Preconditions.checkNotNull(device);
        if (installableSlots != null) {
            return installableSlots;
        }

        final List<Slot> allSlots = slotEJB.findAll();
        final ComponentType componentType = device.getComponentType();

        installableSlots = slotsTreeBuilder.newSlotsTree(allSlots, null, new HashSet<Long>(), true, componentType);
        return installableSlots;
    }

    /**
     * @return the selectedSlot
     */
    public TreeNode getSelectedSlot() {
        return selectedSlot;
    }

    /**
     * @param selectedSlot the selectedSlot to set
     */
    public void setSelectedSlot(TreeNode selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    /** Used in the device details screen. This method creates a new installation record for a device. Before that it
     * checks whether a device slot and device are both selected, and that both are also uninstalled at the moment.
     * The checks are performed in the EJB.
     * The installation record contains:
     * <ul>
     * <li>record number</li>
     * <li>install date</li>
     * <li>device</li>
     * <li>installation slot</li>
     * </ul>
     * The uninstall date is left empty (NULL).
     *
     * @param device The device to create installation record for.
     */
    public void installDevice(Device device) {
        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        newRecord.setDevice(device);
        newRecord.setSlot(((SlotView)selectedSlot.getData()).getSlot());
        installationEJB.add(newRecord);
    }

    /** Used in the device details screen.
     * @param device the device to create installation information for.
     * @return A list of strings. Each string is one path from the device installation slot to its root. This is a list
     * because the same installation slot can be included into multiple places within hierarchies and can appear more
     * than once and in more than one tree.
     */
    public List<String> getInstalledSlotInformation(Device device) {
        List<String> installationInformation = new ArrayList<>();
        final InstallationRecord record = installationEJB.getActiveInstallationRecordForDevice(device);
        if(record != null) {
            installationInformation = Utility.buildSlotPath(record.getSlot());
        }
        return installationInformation;
    }

    /** Used in the device installation dialog.
     * @param type1 First device type
     * @param type2 Second device type
     * @return <code>true</code> if both device types are equal, <code>false</code> otherwise.
     */
    public boolean isSameDeviceType(ComponentType type1, ComponentType type2) {
        return type1 != null && type2 != null && type1.equals(type2);
    }

    /** Used in the device details screen. This method updates the currently active installation record for a device.
     * The currently active installation record is the one which has the uninstall date set to <code>NULL</code>.
     * The method sets this field to the current date.
     * @param device the device which to uninstall.
     */
    public void uninstallDevice(Device device) {
        Preconditions.checkNotNull(device);
        final InstallationRecord deviceInstallationRecord = installationEJB.getActiveInstallationRecordForDevice(device);
        if (deviceInstallationRecord == null) {
            LOGGER.log(Level.WARNING, "The device appears installed, but no active installation record for "
                    + "it could be retrieved. Device db ID: " + device.getId()
                    + ", serial number: " + device.getSerialNumber());
            throw new RuntimeException("No installation record for the device exists.");
        }
        deviceInstallationRecord.setUninstallDate(new Date());
        installationEJB.save(deviceInstallationRecord);
        // the device is not installed any more. Clear the installation state information.
        installedDevice = null;
        installableSlots =  null;
    }
}
