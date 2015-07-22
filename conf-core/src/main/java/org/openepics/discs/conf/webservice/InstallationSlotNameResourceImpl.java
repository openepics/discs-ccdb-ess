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


import javax.inject.Inject;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.jaxb.InstallationSlotNames;
import org.openepics.discs.conf.jaxrs.InstallationSlotNameResource;

import com.google.common.base.Strings;
import java.util.stream.Collectors;
import org.openepics.discs.conf.util.Utility;

/**
 * An implementation of the InstallationSlotBasicResource interface.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
public class InstallationSlotNameResourceImpl implements InstallationSlotNameResource {
    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB comptypeEJB;

    @Override
    public InstallationSlotNames getAllInstallationSlotNames(String deviceTypeName) {
        return new InstallationSlotNames(Strings.isNullOrEmpty(deviceTypeName) ?
                slotEJB.findAll().stream().
                    map(slot -> slot.getName()).
                    collect(Collectors.toList()) :
                Utility.nullableToStream(comptypeEJB.findByName(deviceTypeName)).
                    flatMap(compType -> slotEJB.findByComponentType(compType).stream()).
                    map(slot -> slot.getName()).
                    collect(Collectors.toList()));
    }
}
