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
package org.openepics.discs.conf.views;

import java.util.Date;

import org.openepics.discs.conf.ent.Device;

import com.google.common.base.Preconditions;

/**
 * An UI view object for showing {@link Device} entity in a table.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class DeviceView {
    private String inventoryId;
    private String statusLabel;
    private final String installedIn;
    private final Date installationDate;

    private Device device;

    /** Creates a new immutable instance of the DeviceView object to be used in UI.
     * @param device device entity
     * @param installedIn name of the installation slot.
     */
    public DeviceView(Device device, String installedIn, Date installationDate) {
        Preconditions.checkNotNull(device);
        Preconditions.checkNotNull(installedIn);
        this.device = device;
        inventoryId = device.getSerialNumber();
        statusLabel = device.getStatus().getLabel();
        this.installedIn = installedIn;
        this.installationDate = installationDate;
    }

    /** @return the inventoryId  */
    public String getInventoryId() {
        return inventoryId;
    }

    /** @return the statusLabel */
    public String getStatusLabel() {
        return statusLabel;
    }

    /** @return the installedIn */
    public String getInstalledIn() {
        return installedIn;
    }

    /** @return the device */
    public Device getDevice() {
        return device;
    }

    /** @return the installationDate */
    public Date getInstallationDate() {
        return installationDate;
    }

    /** Updates the view information from the database. The installation status and information is unaffected. */
    public void refreshDevice(Device device) {
        Preconditions.checkArgument(this.device.getId().equals(device.getId()));
        this.device = device;
        inventoryId = device.getSerialNumber();
        statusLabel = device.getStatus().getLabel();
    }
}
