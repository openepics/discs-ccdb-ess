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
package org.openepics.discs.conf.ui.util.names;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.inject.Alternative;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.names.client.NamesClient;
import org.openepics.names.jaxb.DeviceNameElement;

/**
 * Obtains a list of valid names from the ESS Naming Service.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named("names")
@ViewScoped
@Alternative
public class NamesESS implements Names {
    private static final long serialVersionUID = -7527895029962617807L;

    private static final Logger LOGGER = Logger.getLogger(NamesESS.class.getCanonicalName());

    private boolean isError = false;

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public Map<String, DeviceNameElement> getAllNames() {
        try {
            final NamesClient client = new NamesClient();
            return client.getAllDeviceNames().stream()
                    .collect(Collectors.toMap(DeviceNameElement::getName, Function.identity()));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "There was an error retriving data from the naming service.", e);
            isError = true;
            return new HashMap<>();
        }
    }
}
