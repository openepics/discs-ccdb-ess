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
package org.openepics.discs.conf.auditlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.util.AppProperties;

/**
 * Class with static methods that are used in many entity loggers
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class EntityLoggerUtil {
    private static final Logger LOGGER = Logger.getLogger(EntityLoggerUtil.class.getCanonicalName());

    /** If the number of rows is smaller than this value, all the rows are logged into the audit log (100) */
    public final static int AUDIT_LOG_ROWS;
    /** If the number of columns is smaller than this value, all the rows are logged into the audit log (50) */
    public final static int AUDIT_LOG_COLUMNS;

    static {
        // user can redefine defaults using the system properties
        final String auditLogRowsStr = System.getProperty(AppProperties.AUDIT_LOG_ROWS_PROPERTY_NAME, "100");
        final String auditLogColumnsStr = System.getProperty(AppProperties.AUDIT_LOG_COLUMNS_PROPERTY_NAME, "50");
        int rowsValue, colsValue;
        try {
            rowsValue = Integer.valueOf(auditLogRowsStr);
        } catch (NumberFormatException e) {
            rowsValue = 100;
        }
        try {
            colsValue = Integer.valueOf(auditLogColumnsStr);
        } catch (NumberFormatException e) {
            colsValue = 50;
        }
        AUDIT_LOG_ROWS = rowsValue > 10 ? rowsValue : 10;
        AUDIT_LOG_COLUMNS = colsValue > 10 ? colsValue : 10;
        LOGGER.log(Level.INFO, "Table/Vector audit logging row and column values: ROWS=" + AUDIT_LOG_ROWS
                                                                            + ", COLUMNS=" + AUDIT_LOG_COLUMNS);
    }

    private EntityLoggerUtil() {
        // utility class. No public constructor.
    }

    /**
     * Creates and returns a {@link List} of tag names from given {@link Set} of tags
     *
     * @param tagsSet   {@link Set} of tags for certain entity
     * @return          {@link List} of tag names for certain entity
     */
    public static List<String> getTagNamesFromTagsSet(Set<Tag> tagsSet) {
        final List<String> tags = new ArrayList<String>();
        for (final Tag tag : tagsSet) {
            tags.add(tag.getName());
        }
        return tags;
    }
}
