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
package org.openepics.discs.conf.security;

import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * Exception class to be used for authentication & authorization purposes
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public class SecurityException extends RuntimeException {
    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     * @param arg0
     * @param arg1
     */
    public SecurityException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     * @param arg0
     */
    public SecurityException(String arg0) {
        super(arg0);
    }

    /**
     * Generates {@link SecurityException} with a user-friendly exception message
     *
     * @param entity Entity for which security check has failed
     * @param entityType {@link EntityType} for which the security check has failed
     * @param operationType
     * @return A constructed {@link SecurityException}
     */
    public static SecurityException generateExceptionMessage(Object entity,
            EntityType entityType, EntityTypeOperation operationType)
    {
        final StringBuilder sb = new StringBuilder("Access Denied: No permission to access entity type ");
        sb.append(entityType.toString());
        sb.append(" with id ");
        sb.append(((ConfigurationEntity) entity).getId());
        sb.append(" using operation ");
        sb.append(operationType.toString());

        return new SecurityException(sb.toString());
    }
}
