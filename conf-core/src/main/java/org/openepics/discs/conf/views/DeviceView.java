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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Preconditions;

/**
 * An UI view object for showing {@link Device} entity in a table.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class DeviceView {
    private String inventoryId;
    private final String installedIn;
    private final String installedSlotId;
    private final String installationDate;
    private final Date installationTimestamp;

    private Device device;

    /** Creates a new immutable instance of the DeviceView object to be used in UI.
     * @param device device entity
     * @param installedIn name of the installation slot.
     * @param installedSlotId the id of the installation slot, <code>null</code> if none
     * @param installationDate the timestamp of the installation, <code>null</code> if none
     */
    public DeviceView(Device device, String installedIn, String installedSlotId, Date installationDate) {
        Preconditions.checkNotNull(device);
        Preconditions.checkNotNull(installedIn);
        this.device = device;
        inventoryId = device.getSerialNumber();
        this.installedIn = installedIn;
        this.installedSlotId = installedSlotId;
        installationTimestamp = installationDate;
        if (installationDate == null) {
            this.installationDate = "-";
        } else {
            final SimpleDateFormat timestampFormatter = new SimpleDateFormat(Conversion.DATE_TIME_FORMAT);
            this.installationDate = timestampFormatter.format(installationDate);
        }
    }

    /** Default constructor creating empty view object */
    public DeviceView() {
        device = null;
        inventoryId = null;
        installedIn = null;
        installedSlotId = null;
        installationTimestamp = null;
        installationDate = null;
    }

    /** @return the inventoryId */
    public String getInventoryId() {
        return inventoryId;
    }

    /** @return the inventoryId for display in multi-delete confirmation dialog */
    public String getName() {
        return inventoryId;
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
    public String getInstallationDate() {
        return installationDate;
    }

    /** @return the installedSlotId */
    public String getInstalledSlotId() {
        return installedSlotId;
    }

    /** Updates the view information from the database. The installation status and information is unaffected.
     * @param device the device from the database
     */
    public void refreshDevice(Device device) {
        Preconditions.checkArgument(this.device.getId().equals(device.getId()));
        this.device = device;
        inventoryId = device.getSerialNumber();
    }

    /** @return the installationTimestamp */
    public Date getInstallationTimestamp() {
        return installationTimestamp;
    }
}
