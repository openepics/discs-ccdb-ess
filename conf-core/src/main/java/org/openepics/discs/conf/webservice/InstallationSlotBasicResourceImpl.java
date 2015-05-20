/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Cable Database.
 * Cable Database is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.jaxb.InstallationSlotBasic;
import org.openepics.discs.conf.jaxrs.InstallationSlotBasicResource;

public class InstallationSlotBasicResourceImpl implements InstallationSlotBasicResource {

    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB comptypeEJB;

    @Override
    public List<InstallationSlotBasic> getNamesList(String deviceTypeName) {
        final ComponentType componentType = comptypeEJB.findByName(deviceTypeName);

        final List<InstallationSlotBasic> installationSlots = new ArrayList<InstallationSlotBasic>();
        if (componentType != null) {
            for (Slot installationSlot : slotEJB.findByComponentType(componentType)) {
                installationSlots.add(getInstallationSlotBasic(installationSlot));
            }
        }

        return installationSlots;
    }

    private InstallationSlotBasic getInstallationSlotBasic(Slot slot) {
        final InstallationSlotBasic installationSlotBasic = new InstallationSlotBasic();
        installationSlotBasic.setName(slot.getName());
        return installationSlotBasic;
    }
}
