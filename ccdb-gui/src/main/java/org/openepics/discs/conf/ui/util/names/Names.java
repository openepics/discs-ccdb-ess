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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.openepics.discs.conf.ent.Slot;
import org.openepics.names.jaxb.DeviceNameElement;

/**
 * Interface for providing custom implementation to get names used
 * for auto complete when creating new installation {@link Slot}
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public interface Names extends Serializable {

    /**
     * @return <code>true</code> if there was an error constructing a list of names, <code>false</code> otherwise
     */
    public boolean isError();

    /**
     * Returns a set of all names that can be used for installation {@link Slot} name
     *
     * @return {@link Set}&lt;{@link String}&gt; of names that can be used for installation {@link Slot} name
     */
    public Map<String, DeviceNameElement> getAllNames();
}
