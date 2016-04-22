/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.conf.ui.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.PersistenceException;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.primefaces.model.SortOrder;

import com.google.common.collect.Lists;

/**
*
* @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
*/
public class UiUtility {

    public static final String MESSAGE_SUMMARY_SUCCESS = "Success";
    public static final String MESSAGE_SUMMARY_ERROR = "Error";
    public static final String MESSAGE_SUMMARY_DELETE_FAIL = "Deletion failed";

    private static final String PATH_SEPARATOR = "\u00A0\u00A0\u00BB\u00A0\u00A0";

    private UiUtility() {}

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
                EntityAttributeViewKind.DEVICE_TAG
        };
        for (EntityAttributeViewKind kind : displayedKinds) attributeKinds.add(new SelectItem(kind, kind.toString()));
        return attributeKinds;
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

    public static org.openepics.discs.conf.util.SortOrder translateToCCDBSortOrder(SortOrder sortOrder) {
        switch (sortOrder) {
        case ASCENDING:
            return org.openepics.discs.conf.util.SortOrder.ASCENDING;
        case DESCENDING:
            return org.openepics.discs.conf.util.SortOrder.DESCENDING;
        case UNSORTED:
            return org.openepics.discs.conf.util.SortOrder.UNSORTED;
        default:
            throw new UnhandledCaseException();
        }
    }

}
