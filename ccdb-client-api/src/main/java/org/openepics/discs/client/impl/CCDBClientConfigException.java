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
package org.openepics.discs.client.impl;

/**
 * ResponseException is an exception thrown when response couldn't be retrieved from the REST service.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
public class CCDBClientConfigException extends RuntimeException {
    /** Constructs a new exception. */
    public CCDBClientConfigException() {
        super();
    }

    /**
     * Constructs a new exception.
     *
     * @param message
     *            the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method)
     */
    public CCDBClientConfigException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param message
     *            the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method)
     *
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *            (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CCDBClientConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
