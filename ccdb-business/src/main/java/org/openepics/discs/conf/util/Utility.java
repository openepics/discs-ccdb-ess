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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.openepics.discs.conf.ejb.ReadOnlyDAO;
import org.openepics.discs.conf.ent.SlotRelationName;

import com.google.common.base.Preconditions;

/**
 *
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class Utility {

    private Utility() {}

    /**
     * Converts an {@link Optional} object into a stream.
     * @param <T> the class of the object
     * @param optional The {@link Optional} object to convert to a stream
     * @return a {@link Stream} of an {@link Optional} object or an empty stream
     */
    public static <T> Stream<T> optionalToStream(Optional<T> optional) {
        return optional.map(Stream::of).orElse(Stream.empty());
    }

    /**
     * Converts an object into a stream.
     * @param <T> the class of the object
     * @param object the object to convert to a stream
     * @return a {@link Stream} of an object or an empty stream, if the object was <code>null</code>
     */
    public static <T> Stream<T> nullableToStream(@Nullable T object) {
        return object != null ? Stream.of(object) : Stream.empty();
    }

    /**
     * Checks whether a {@link Collection} is <code>null</code> or empty.
     * @param collection the collection to test
     * @return <code>true</code> if collection is <code>null</code> or empty, <code>false</code> otherwise
     */
    public static boolean isNullOrEmpty(final @Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks whether a {@link Map} is <code>null</code> or empty.
     * @param map the map to test
     * @return <code>true</code> if map is <code>null</code> or empty, <code>false</code> otherwise
     */
    public static boolean isNullOrEmpty(final @Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Find a next available name given a candidate. If a candidate ends with an underscore and a number (e.g.:
     * someName_3), then the method returns the first name "someName_&lt;X&gt;" where X &gt; 3 and the name is not
     * already used in CCDB.
     * <p>
     * If the name does not end with a number, then the _ and a number is appended to it and again the method returns
     * the first name that is not used. The methods starts to search for non-used names at number 1.
     * </p>
     * <p>
     * Please note that the method does not check whether the name is valid in regards to the currently active naming
     * system.
     * </p>
     * @param candidateName the candidate name
     * @param dao The DAO object to use for testing the name candidates
     * @return an non-existing name based on the candidate
     */
    public static String findFreeName(final String candidateName, ReadOnlyDAO<?> dao) {
        Preconditions.checkNotNull(candidateName);
        Preconditions.checkNotNull(dao);
        final String nameRoot;
        int numPostfix;
        if (candidateName.matches(".*_(\\d)*$")) {
            final int delimiter = candidateName.lastIndexOf('_');
            nameRoot = candidateName.substring(0, delimiter);
            numPostfix = Integer.valueOf(candidateName.substring(delimiter + 1));
        } else {
            nameRoot = candidateName;
            numPostfix = 1;
        }

        while (dao.findByName(nameRoot + "_" + numPostfix) != null) {
            ++numPostfix;
        }
        return nameRoot + "_" + numPostfix;
    }

    /**
     * @param name The name of the slot relationship
     * @return the {@link SlotRelationName} corresponding to the name, <code>null</code> if such enumeration constant
     * does not exist.
     */
    public static @Nullable SlotRelationName getRelationByName(final String name) {
        try {
            Preconditions.checkNotNull(name);
            return SlotRelationName.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * @param inverseName the inverse name of the relationship
     * @return the {@link SlotRelationName} corresponding to the inverse name, <code>null</code> if enumeration
     * constant with such an inverse name does not exist.
     */
    public static @Nullable SlotRelationName getRelationBasedOnInverseName(final String inverseName) {
        Preconditions.checkNotNull(inverseName);
        final String ucInverseName = inverseName.toUpperCase();
        for (final SlotRelationName slotRelationName : SlotRelationName.values()) {
            if (ucInverseName.equals(slotRelationName.inverseName().toUpperCase())) {
                return slotRelationName;
            }
        }
        return null;
    }
}
