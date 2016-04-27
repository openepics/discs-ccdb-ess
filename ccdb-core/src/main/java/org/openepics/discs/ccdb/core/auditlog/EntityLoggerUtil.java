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
package org.openepics.discs.ccdb.core.auditlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openepics.discs.ccdb.model.Tag;

/**
 * Class with static methods that are used in many entity loggers
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 */
public class EntityLoggerUtil {

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
