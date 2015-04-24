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

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * Exception class to be used for authentication &amp; authorization purposes
 *
 * @author Miroslav Pavleski &lt;miroslav.pavleski@cosylab.com&gt;
 */
public class SecurityException extends RuntimeException {
    private static final long serialVersionUID = 7871852787337165333L;

    /**
     * @param message the message
     * @param cause the cause
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message the message
     * @see RuntimeException#RuntimeException(String)
     */
    public SecurityException(String message) {
        super(message);
    }

    /**
     * Generates {@link SecurityException} with a user-friendly exception message
     *
     * @param entity Entity for which security check has failed
     * @param entityType {@link EntityType} for which the security check has failed
     * @param operationType {@link EntityTypeOperation} the type of operation
     * @return A constructed {@link SecurityException}
     */
    public static SecurityException generateExceptionMessage(Object entity,
            EntityType entityType, EntityTypeOperation operationType)
    {
        final StringBuilder sb = new StringBuilder("Access Denied: No permission to perform ");
        sb.append(operationType.toString());
        sb.append(" on entity ");
        sb.append(entityType.toString());

        return new SecurityException(sb.toString());
    }
}
