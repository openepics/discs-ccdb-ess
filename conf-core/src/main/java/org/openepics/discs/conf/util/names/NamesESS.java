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
package org.openepics.discs.conf.util.names;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;

import org.openepics.names.client.NamesClient;
import org.openepics.names.jaxb.DeviceNameElement;

/**
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
@Alternative
@Stateless
public class NamesESS implements Names {
    private static final long serialVersionUID = -7527895029962617807L;

    @Override
    public Set<String> getAllNames() {
        final Set<String> names = new HashSet<String>();

        try {
            final NamesClient client = new NamesClient();
            for (DeviceNameElement element : client.getNamesResource().getAllDeviceNames()) {
                names.add(element.getName());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("There was an error retriving data from the naming service.", e);
        }
        return names;
    }
}
