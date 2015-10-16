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

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.PersistenceException;

import org.openepics.discs.conf.ejb.ReadOnlyDAO;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.EntityAttributeViewKind;

import com.google.common.collect.Lists;
/**
 *
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class Utility {

    public static final String MESSAGE_SUMMARY_SUCCESS = "Success";
    public static final String MESSAGE_SUMMARY_ERROR = "Error";
    public static final String MESSAGE_SUMMARY_DELETE_FAIL = "Deletion failed";

    private static final String PATH_SEPARATOR = "\u00A0\u00A0\u00BB\u00A0\u00A0";

    private Utility() {}

    /**
     * Utility method used to display a message to the user
     *
     * @param severity Severity of the message
     * @param summary Summary of the message
     * @param message Detailed message contents
     */
    public static void showMessage(FacesMessage.Severity severity, String summary, String message) {
        final FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, summary, message));
    }

    /** The method determines whether the cause of the exception is a
     * {@link javax.persistence.PersistenceException} or not.
     *
     * @param t - the exception to inspect
     * @return <code>true</code> if the cause of the exception is javax.persistence.PersistenceException,
     * <code>false</code> otherwise.
     */
    public static boolean causedByPersistenceException(Throwable t) {
        return causedBySpecifiedExceptionClass(t, PersistenceException.class);
    }

    /** <p>
     * The method determines whether the cause of the exception is an exception of a specified class or not.
     * </p>
     * <p>
     * <strong>Please note</strong> that the <code>cause</code> must explicitly be the exception class you are looking
     * for, the method will not work for descendants of that class.
     * </p>
     * @param t the exception to inspect
     * @param cause the cause Exception type to test for
     * @return <code>true</code> if the cause of the exception is <code>cause</code> Class,
     * <code>false</code> otherwise.
     */
    public static boolean causedBySpecifiedExceptionClass(Throwable t, Class<? extends Exception> cause) {
        if (t != null && t.getClass() == cause) {
            return true;
        } else if (t != null && t.getCause() != null) {
            return causedBySpecifiedExceptionClass(t.getCause(), cause);
        } else {
            return false;
        }
    }

    /**
     * Builds a list that represents a path of slot names from root slot to current slot.
     *
     * @param slot {@link Slot} for which the path should be built
     * @return {@link List} containing slot names from root slot to current slot
     */
    public static List<String> buildSlotPath(Slot slot) {
        if (SlotEJB.ROOT_COMPONENT_TYPE.equals(slot.getComponentType().getName())) {
            return new ArrayList<>();
        } else {
            final List<String> list = new ArrayList<>();
            for (SlotPair pair : slot.getPairsInWhichThisSlotIsAChildList()) {
                if (pair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                    List<String> parentList = buildSlotPath(pair.getParentSlot());
                    if (!parentList.isEmpty()) {
                        for (String parentPath : parentList) {
                            list.add(parentPath + PATH_SEPARATOR + slot.getName());
                        }
                    } else {
                        // this is the "user" root node
                        list.add(slot.getName());
                    }
                }
            }
            return list;
        }
    }

    /** @return the list of {@link SelectItem}s to show in the table filter */
    public static List<SelectItem> buildAttributeKinds() {
        List<SelectItem> attributeKinds = Lists.newArrayList();
        attributeKinds.add(new SelectItem("", "Select one", "", false, false, true));
        EntityAttributeViewKind[] displayedKinds = new EntityAttributeViewKind[] {
                EntityAttributeViewKind.DEVICE_TYPE_PROPERTY,
                EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT,
                EntityAttributeViewKind.DEVICE_TYPE_TAG,
                EntityAttributeViewKind.INSTALL_SLOT_PROPERTY,
                EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT,
                EntityAttributeViewKind.INSTALL_SLOT_TAG,
                EntityAttributeViewKind.DEVICE_PROPERTY,
                EntityAttributeViewKind.DEVICE_ARTIFACT,
                EntityAttributeViewKind.DEVICE_TAG,
                EntityAttributeViewKind.ARTIFACT,
                EntityAttributeViewKind.PROPERTY,
                EntityAttributeViewKind.TAG,
        };
        for (EntityAttributeViewKind kind : displayedKinds) attributeKinds.add(new SelectItem(kind, kind.toString()));
        return attributeKinds;
    }

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
