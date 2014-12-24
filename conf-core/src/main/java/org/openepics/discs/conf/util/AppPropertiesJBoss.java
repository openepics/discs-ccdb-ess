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
package org.openepics.discs.conf.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * Implementation of {@link AppProperties} that is suitable for non-Glassfish App. Servers (JBoss, Wildfly...)
 *
 * @author Miroslav Pavleski
 */
@Alternative
@ApplicationScoped
public class AppPropertiesJBoss implements AppProperties {
    private static final String PREFIX = "org.openepics.discs.conf.props.";

    /**
     * @see AppProperties#getProperty(String)
     */
    @Override
    public String getProperty(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return System.getProperty(PREFIX + name);
    }
}
