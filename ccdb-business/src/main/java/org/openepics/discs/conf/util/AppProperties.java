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
/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 *
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 *
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 *
 */

package org.openepics.discs.conf.util;

import javax.enterprise.context.ApplicationScoped;


/**
 * Interface used to represent generic application properties facility.
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@ApplicationScoped
public class AppProperties {
    public static final String BLOB_STORE_PROPERTY_NAME = "org.openepics.discs.conf.props.BlobStoreRoot";
    public static final String NAMING_APPLICATION_URL = "org.openepics.discs.conf.props.namingAppURL";
    public static final String NAMING_DETECT_STATUS = "org.openepics.discs.conf.props.detectNamingStatus";
    public static final String RESTRICT_TO_CONVENTION_NAMES = "org.openepics.discs.conf.props.restrictToConventionNames";
    public static final String CABLEDB_STATUS = "org.openepics.discs.conf.props.cableDBStatus";
    public static final String CABLEDB_APPLICATION_URL = "org.openepics.discs.conf.props.cableDBAppURL";
    public static final String AUDIT_LOG_ROWS_PROPERTY_NAME= "org.openepics.discs.conf.props.auditLogRows";
    public static final String AUDIT_LOG_COLUMNS_PROPERTY_NAME = "org.openepics.discs.conf.props.auditLogColumns";

    /**
     * Retrieves the string property with the given key-name
     *
     * @param name a {@link String} key for the property
     * @return the property value, or <code>null</code> if it does not exist
     */
    public String getProperty(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return System.getProperty(name);
    }
}
