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
package org.openepics.discs.ccdb.core.util;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class PropertyValueNotUniqueException extends CCDBRuntimeException {
    private static final long serialVersionUID = 1516835043698546908L;

    /**
     * @param message the message
     * @param cause the cause
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public PropertyValueNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message the message
     * @see RuntimeException#RuntimeException(String)
     */
    public PropertyValueNotUniqueException(String message) {
        super(message);
    }

    /**
     * @param cause the cause
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public PropertyValueNotUniqueException(Throwable cause) {
        super(cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public PropertyValueNotUniqueException() {
        super();
    }
}
