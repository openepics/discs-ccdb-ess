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
package org.openepics.discs.conf.ui.common;

/**
 * A checked exception class to declare UI methods that throw.
 * Needed to have JSF properly handle exceptional situations.
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public class UIException extends RuntimeException {
    private static final long serialVersionUID = -7498669540239916884L;

    /** A new UI exception with no reference
     * @param message The text message
     */
    public UIException(String message) {
        super(message);
    }

    /** A new UI exception
     * @param message The text message
     * @param cause The original cause of the exception
     */
    public UIException(String message, Throwable cause) {
        super(message, cause);
    }
}
