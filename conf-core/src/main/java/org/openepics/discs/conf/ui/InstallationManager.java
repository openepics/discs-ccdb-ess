/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
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
    private static final Logger logger = Logger.getLogger(InstallationManager.class.getCanonicalName());

    @EJB private InstallationEJB installationEJB;
    @EJB private SlotEJB slotEJB;
    @Inject private SlotsTreeBuilder slotsTreeBuilder;

    private Device installedDevice;
    private InstallationRecord installationRecord;
    private Slot installationSlot;
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
            setInstallationRecord(installationEJB.getActiveInstallationRecordForDevice(device));
        }
        return this.installedDevice != null;
    }

    /** Used in the listing of device instances screen.
     * @param device the device to query for.
     * @return The name os the installation slot the device is currently installed in.
     */
    public String getInstalledSlotForDevice(Device device) {
        final InstallationRecord record = installationEJB.getActiveInstallationRecordForDevice(device);
        return record == null ? "-" : record.getSlot().getName();
    }

    private void setInstallationRecord(InstallationRecord installationRecord) {
        this.installationRecord = installationRecord;
        if (installationRecord != null) {
            this.installedDevice = installationRecord.getDevice();
            this.installationSlot = installationRecord.getSlot();
        } else {
            this.installedDevice = null;
            this.installationSlot = null;
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

    /** Used in the device details screen. This method creates a new installation record for a device. The
     * installation record contains:
     * <ul>
     * <li>record number</li>
     * <li>install date</li>
     * <li>device</li>
     * <li>installation slot</li>
     * </ul>
     * The uninstall date is left empty (NULL).
     * @param device The device to create installation record for.
     */
    public void installDevice(Device device) {
        // we must check whether the selected slot is already occupied or selected device is already installed
        final InstallationRecord slotCheck = installationEJB
                .getActiveInstallationRecordForSlot(((SlotView)selectedSlot.getData()).getSlot());
        if (slotCheck != null) {
            logger.log(Level.WARNING, "An attempt was made to install a device in an already occupied slot.");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Slot already occupied.", ""));
        }
        final InstallationRecord deviceCheck = installationEJB.getActiveInstallationRecordForDevice(device);
        if (deviceCheck != null) {
            logger.log(Level.WARNING, "An attempt was made to install a device that is already installed.");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Device already installed.", ""));
        }

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
            installationInformation = buildInstalledSlotInformation(record.getSlot());
        }
        return installationInformation;
    }

    private List<String> buildInstalledSlotInformation(Slot slot) {
        if (slot.getComponentType().getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
            final List<String> list = new ArrayList<>();
            list.add(slot.getName());
            return list;
        } else {
            final List<String> list = new ArrayList<>();
            for (SlotPair pair : slot.getChildrenSlotsPairList()) {
                if (pair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                    for (String parentPath : buildInstalledSlotInformation(pair.getParentSlot())) {
                        list.add(parentPath + "\u00A0\u00A0\u00BB\u00A0\u00A0" + slot.getName());
                    }
                }
            }
            return list;
        }
    }

    /** Used in the device installation dialog.
     * @param type1 First device type
     * @param type2 Second device type
     * @return <code>true</code> if both device types are equal, <code>false</code> otherwise.
     */
    public boolean isSameDeviceType(ComponentType type1, ComponentType type2) {
        return type1 != null && type2 != null && type1.equals(type2);
    }

    /** Used in the device details screen. This method creates a new installation record for a device.
     * @param device
     */
    public void uninstallDevice(Device device) {
        Preconditions.checkNotNull(device);
        final InstallationRecord deviceInstallationRecord = installationEJB.getActiveInstallationRecordForDevice(device);
        if (deviceInstallationRecord == null) {
            logger.log(Level.WARNING, "The device appears installed, but no active installation record for "
                    + "it could be retrieved. Device db ID: " + device.getId()
                    + ", serial number: " + device.getSerialNumber());
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No installation record for the device exists.", ""));
        }
        deviceInstallationRecord.setUninstallDate(new Date());
        installationEJB.save(deviceInstallationRecord);
        // the device is not installed any more.
        this.installationRecord = null;
        this.installedDevice = null;
        this.installationSlot = null;
    }
}
