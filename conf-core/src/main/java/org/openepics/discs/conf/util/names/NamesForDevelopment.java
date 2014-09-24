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

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Alternative
@Stateless
public class NamesForDevelopment implements Names {

    /**
     * Returns an empty {@link HashSet} for the purposes of development so that the application doesn't
     * have to connect to naming server every time.
     */
    @Override
    public Set<String> getAllNames() {
       return new HashSet<>();
    }

}
