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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.views.InstallationView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Named
@ViewScoped
public class InstallationController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private transient InstallationEJB installationEJB;
    @Inject private transient SlotEJB slotEJB;
    private transient HierarchiesController hierarchiesController;

    private transient List<InstallationView> selectedInstallationViews;
    private transient List<InstallationView> installationRecords;
    private transient List<InstallationView> filteredInstallationRecords;

    private transient List<Device> uninstalledDevices;
    private transient List<Device> filteredUninstalledDevices;
    private Device deviceToInstall;

    protected void setUIParent(HierarchiesController hierarchiesController) {
        this.hierarchiesController = hierarchiesController;
    }

    public boolean canInstall() {
        return (selectedInstallationViews != null) && (selectedInstallationViews.size() == 1)
                && (selectedInstallationViews.get(0).getInstallationRecord() == null);
    }

    public boolean canUninstall() {
        if (selectedInstallationViews == null || selectedInstallationViews.size() == 0) return false;
        for (InstallationView selectedInstallationView : selectedInstallationViews)
            if (selectedInstallationView.getInstallationRecord() == null) return false;
        return true;
    }

    protected void initInstallationRecordList(final Slot slot, final boolean forceInit) {
        if (forceInit || installationRecords == null) {
            installationRecords = Lists.newArrayList();
        }
        addToInstallationRecordList(slot);
    }

    private void addToInstallationRecordList(final Slot slot) {
        if (slot.isHostingSlot()) {
            final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slot);
            installationRecords.add(new InstallationView(slot, record));
        }
    }

    protected void removeRelatedInstallationRecord(final Slot slot) {
        final ListIterator<InstallationView> recordsIterator = installationRecords.listIterator();
        while (recordsIterator.hasNext()) {
            final InstallationView record = recordsIterator.next();
            if (slot.equals(record.getSlot())) {
                recordsIterator.remove();
                break;
            }
        }

        if (selectedInstallationViews != null) {
            Iterator<InstallationView> i = selectedInstallationViews.iterator();
            while (i.hasNext()) {
                InstallationView selectedInstallationView = i.next();
                if (selectedInstallationView.getSlot().equals(slot)) i.remove();
            }
        }
    }

    protected void clearInstallationInformation() {
        installationRecords = null;
        selectedInstallationViews = null;
    }

    /**
     * This method creates a new installation record for the device selected in the installation dialog. Before that
     * it checks whether a device slot and device are both selected, and that both are also uninstalled at the moment.
     * The checks are performed in the EJB.
     * This method creates a new installation record containing:
     * <ul>
     * <li>record number</li>
     * <li>install date</li>
     * <li>device</li>
     * <li>installation slot</li>
     * </ul>
     * The uninstall date is left empty (NULL).
     */
    public void installDevice() {
        Preconditions.checkNotNull(selectedInstallationViews);
        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        final InstallationView installationView = selectedInstallationViews.get(0);

        newRecord.setDevice(deviceToInstall);
        newRecord.setSlot(installationView.getSlot());
        installationEJB.add(newRecord);

        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                "Device installed.");

        installationView.setSlot(slotEJB.findById(installationView.getSlot().getId()));
        installationView.setInstallationRecord(newRecord);

        final Slot installationSlot = installationView.getSlot();
        hierarchiesController.removeRelatedAttributes(installationSlot);
        hierarchiesController.initAttributeList(installationSlot, false);

        deviceToInstall = null;
    }

    /** This method is called when a user presses the "Uninstall" button in the hierarchies view. */
    public void uninstallDevice() {
        for (InstallationView selectedInstallationView : selectedInstallationViews) {
            final InstallationRecord selectedInstallationRecord = selectedInstallationView.getInstallationRecord();
            Preconditions.checkNotNull(selectedInstallationRecord);
            selectedInstallationRecord.setUninstallDate(new Date());
            installationEJB.save(selectedInstallationRecord);
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Device uninstalled.");
            // signal that nothing is installed
            selectedInstallationView.setInstallationRecord(null);

            final Slot installationSlot = selectedInstallationView.getSlot();
            hierarchiesController.removeRelatedAttributes(installationSlot);
            hierarchiesController.initAttributeList(installationSlot, false);
        }
    }

    /** Prepares a list of a devices that can still be installed into the selected installation slot */
    public void prepareUninstalledDevices() {
        final Slot slotToFill = selectedInstallationViews.get(0).getSlot();
        uninstalledDevices = (slotToFill == null) || !slotToFill.isHostingSlot() ? null
                : installationEJB.getUninstalledDevices(slotToFill.getComponentType());
        filteredUninstalledDevices = null;
    }

    /**
     * @return The latest installation records associated with the selected installation slots, <code>null</code> if no
     * slots are selected.
     */
    public List<InstallationView> getInstallationRecords() {
        return installationRecords;
    }

    /** @return the selectedInstallationView */
    public List<InstallationView> getSelectedInstallationViews() {
        return selectedInstallationViews;
    }

    /** @param selectedInstallationViews the selectedInstallationViews to set */
    public void setSelectedInstallationViews(List<InstallationView> selectedInstallationViews) {
        this.selectedInstallationViews = selectedInstallationViews;
    }

    /** @return installation records for filtering */
    public List<InstallationView> getFilteredInstallationRecords() {
        return filteredInstallationRecords;
    }
    /** @param filteredInstallationRecords installation records for filtering */
    public void setFilteredInstallationRecords(List<InstallationView> filteredInstallationRecords) {
        this.filteredInstallationRecords = filteredInstallationRecords;
    }

    /**
     * @return The path to the installation slot the user is currently installing a device into. Used in the
     * "Install device" dialog.
     */
    public String getInstallationSlotPath() {
        final String slotPath = UiUtility.buildSlotPath(selectedInstallationViews.get(0).getSlot()).toString();
        return slotPath.substring(1, slotPath.length() - 1);
    }

    /**
     * @return Based on the device type of the currently selected slot, this returns a list of all appropriate device
     * instances that are not installed.
     */
    public List<Device> getUninstalledDevices() {
        return uninstalledDevices;
    }
    public void setUninstalledDevices(List<Device> uninstalledDevices) {
        this.uninstalledDevices = uninstalledDevices;
    }

    /** @return the filteredUninstalledDevices */
    public List<Device> getFilteredUninstalledDevices() {
        return filteredUninstalledDevices;
    }

    /** @param filteredUninstalledDevices the filteredUninstalledDevices to set */
    public void setFilteredUninstalledDevices(List<Device> filteredUninstalledDevices) {
        this.filteredUninstalledDevices = filteredUninstalledDevices;
    }

    /** @return the deviceToInstall */
    public Device getDeviceToInstall() {
        return deviceToInstall;
    }
    /** @param deviceToInstall the deviceToInstall to set */
    public void setDeviceToInstall(Device deviceToInstall) {
        this.deviceToInstall = deviceToInstall;
    }

}
