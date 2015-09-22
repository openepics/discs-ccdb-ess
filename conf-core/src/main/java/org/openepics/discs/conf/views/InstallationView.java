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

import javax.annotation.Nullable;

import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;

import com.google.common.base.Preconditions;

/**
 * The class contains installation data to be shown in the installation table on the main screen of the application.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class InstallationView {
    private InstallationRecord record;
    private Slot slot;

    /** Constructs a new installation view with the {@link Slot} information and installation record information. */
    public InstallationView(final Slot slot, @Nullable final InstallationRecord installationRecord) {
        Preconditions.checkNotNull(slot);
        this.slot = slot;
        this.record = installationRecord;
    }

    /** Constructs a new installation view with the {@link Slot} information and no installation record information. */
    public InstallationView(final Slot slot) {
        this(slot, null);
    }

    public InstallationRecord getInstallationRecord() {
        return record;
    }

    public void setInstallationRecord(InstallationRecord installationRecord) {
        this.record = installationRecord;
    }

    public String getSlotName() {
        return slot.getName();
    }

    public void setSlot(Slot slot) {
        this.slot = Preconditions.checkNotNull(slot);
    }

    public Slot getSlot() {
        return slot;
    }

    public String getDeviceInventoryId() {
        return record != null ? record.getDevice().getSerialNumber() : null;
    }

    public Date getInstallDate() {
        return record != null ? record.getInstallDate() : null;
    }

    public boolean isDeviceInstalled() {
        return record != null;
    }
}
