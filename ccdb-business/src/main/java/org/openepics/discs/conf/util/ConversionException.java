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

/**
 * Runtime exception for the conversion utility.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class ConversionException extends RuntimeException {
    private static final long serialVersionUID = 9060559072643456765L;

    /**
     * A new conversion exception with no message and no reference
     */
    public ConversionException() {
        super();
    }

    /** A new conversion exception with no reference
     * @param msg The text message
     */
    public ConversionException(String msg) {
        super(msg);
    }

    /** A new conversion exception
     * @param msg The text message
     * @param cause The original cause of the exception
     */
    public ConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /** A new conversion exception with no message
     * @param cause The original cause of the exception
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }
}
