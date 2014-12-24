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
package org.openepics.discs.conf.dl.common;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a result of load operation, consisting of the error status,
 * load {@link ValidationMessage}s, and the list of objects affected by the load.
 *
 * @author Sunil Sah <sunil.sah@cosylab.com>
 */
public class DataLoaderResult {

    private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
    private boolean error;

    /** @return the messages of this report */
    public List<ValidationMessage> getMessages() {
        return messages;
    }

    /** @return the report error state */
    public boolean isError() {
        return error;
    }

    /**
     * Adds the message to the report
     *
     * @param message the message to add
     */
    public void addMessage(ValidationMessage message) {
        messages.add(message);
        if (message.isError()) error = true;
    }

    /**
     * Appends the content of the given result to this one.
     *
     * @param result the result to append
     */
    public void addResult(DataLoaderResult result) {
        for (ValidationMessage message : result.getMessages()) {
            addMessage(message);
        }
    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (ValidationMessage message: messages) {
            builder.append(message.toString());
            builder.append("\n");
        }

        if (isError()) {
            builder.append("There were some errors.\n");
        }
        return builder.toString();
    }
}
