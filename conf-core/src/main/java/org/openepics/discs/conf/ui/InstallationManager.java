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
import org.primefaces.context.RequestContext;
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
    @Inject protected SlotsTreeBuilder slotsTreeBuilder;

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

    public boolean isDeviceInstalled(Device device) {
        if (!device.equals(this.installedDevice)) {
            setInstallationRecord(installationEJB.getActiveInstallationRecordForDevice(device));
        }
        return this.installedDevice != null;
    }

    public String getInstalledSlotForDevice(Device device) {
        final InstallationRecord record = installationEJB.getActiveInstallationRecordForDevice(device);
        return record == null ? "-" : record.getSlot().getName();
    }

    public void setInstallationRecord(InstallationRecord installationRecord) {
        this.installationRecord = installationRecord;
        if (installationRecord != null) {
            this.installedDevice = installationRecord.getDevice();
            this.installationSlot = installationRecord.getSlot();
        } else {
            this.installedDevice = null;
            this.installationSlot = null;
        }
    }

    public InstallationRecord getInstallationRecord() {
        return installationRecord;
    }

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

    public void installDevice(Device device) {
        // we must check whether the selected slot is already occupied or selected device is already installed
        final InstallationRecord slotCheck = installationEJB
                .getActiveInstallationRecordForSlot(((SlotView)selectedSlot.getData()).getSlot());
        if (slotCheck != null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Slot already occupied.", ""));
        }
        final InstallationRecord deviceCheck = installationEJB.getActiveInstallationRecordForDevice(device);
        if (deviceCheck != null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Device already installed.", ""));
        }

        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        newRecord.setDevice(device);
        newRecord.setSlot(((SlotView)selectedSlot.getData()).getSlot());
        installationEJB.add(newRecord);
    }

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

    public boolean isSameDeviceType(ComponentType type1, ComponentType type2) {
        return type1 != null && type2 != null && type1.equals(type2);
    }

    public void uninstallDevice(Device device) {
        final InstallationRecord deviceInstallationRecord = installationEJB.getActiveInstallationRecordForDevice(device);
        if (deviceInstallationRecord == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No installation record for the device exists.", ""));
        }
        deviceInstallationRecord.setUninstallDate(new Date());
        installationEJB.save(deviceInstallationRecord);
        RequestContext.getCurrentInstance().update(":deviceDetailsForm");
    }
}
