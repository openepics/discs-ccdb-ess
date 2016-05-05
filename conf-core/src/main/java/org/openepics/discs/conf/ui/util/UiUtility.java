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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    /**
     * The method tries to parse the string using the following rules. If input contains
     * <ol>
     * <li>illegal characters (legal: [0-9\-: ]) then today at midnight is assumed</li>
     * <li>a number less than current date (day number in month), the day of this month is assumed</li>
     * <li>a number (XX) above current day (date) and under the number of days of previous month,
     *         the day of previous month is assumed</li>
     * <li> a number above the valid day number (see previous two lines) and below 99,
     *         the first day of 19XX or 20XX is assumed (depending on current year; 20XX will start after 2032)</li>
     * <li>a number above 1900, then the first day of that year is assumed</li>
     * <li>start of a date yyyy-m or yyyy-m-d, then start of the input year is assumed. In this case the year must
     * be above 1900 and month and day must be correct.</li>
     * <li>start of an hour (HH:m or HH:m:s) then the current day is assumed. 24 hour format.</li>
     * <li>the "time" can be preceded by "date". They are separated by a space character</li>
     * </ol>
     *
     * The returned string is normalized to be parse-able by the standard formatter.
     *
     * @param inDateTime the user input we're trying to parse into date and time
     * @return date time string represented by the input, or today at midnight if input is invalid.
     */
    public static String processUIDateTime(final String inDateTime) {
        final String trimmedInput = inDateTime.trim().replaceAll(" +", " ");
        final String[] inputChunks = trimmedInput.split(" ");
        if ((inputChunks.length > 2) || (inputChunks.length < 1)) {
            return LocalDate.now().atStartOfDay().format(dateTimeFormat);
        }

        final String dateOut = tryParsingDate(inputChunks[0]);
        final String timeOut;

        if (inputChunks.length == 2) {
            timeOut = tryParsingTime(inputChunks[1]);
        } else if (dateOut == null) {
            // we don't have date, time parsing must succeed
            timeOut = tryParsingTime(inputChunks[0]);
        } else {
            timeOut = "00:00:00";
        }

        // date and time parsing have failed
        if (timeOut == null) {
            return LocalDate.now().atStartOfDay().format(dateTimeFormat);
        }

        final StringBuilder result = new StringBuilder();
        if (dateOut != null) {
            result.append(dateOut);
        } else {
            result.append(LocalDate.now().toString());
        }
        result.append(' ').append(timeOut);

        return result.toString();
    }

    private static String tryParsingDate(final String inputDate) {
        if (inputDate.isEmpty() || inputDate.matches("(^-.*)|(.*[^0-9\\-].*)")) {
            return null;
        }

        if (!inputDate.contains("-")) {
            return parseSingleDateInput(inputDate);
        } else {
            return parseMultiDateInput(inputDate);
        }
    }

    private static String parseSingleDateInput(final String inputDate) {
        final LocalDate today = LocalDate.now();
        final int currentMillenium = today.getYear() / 1000 * 1000;

        final int inputNumber = Integer.valueOf(inputDate);
        if (inputNumber == 0) {
            // year
            return String.valueOf(currentMillenium) + "-01-01";
        } else if (inputNumber <= today.getDayOfMonth()) {
            // day in this month
            return String.valueOf(today.getYear()) + "-"
                    + numberWithLeadingZero(today.getMonthValue()) + "-"
                    + numberWithLeadingZero(inputNumber);
        } else if (inputNumber <= today.minusMonths(1).lengthOfMonth()) {
            // day in previous month
            return String.valueOf(today.minusMonths(1).getYear()) + "-"
                    + numberWithLeadingZero(today.minusMonths(1).getMonthValue()) + "-"
                    + numberWithLeadingZero(inputNumber); // adjust the day if necessary
        } else if (inputNumber < 100) {
            // year in the century
            if (currentMillenium + inputNumber > today.getYear()) {
                return String.valueOf(currentMillenium - 100 + inputNumber) + "-01-01";
            } else {
                // year in this century
                return String.valueOf(currentMillenium + inputNumber) + "-01-01";
            }
        } else if (inputNumber < 1900) {
            // error in year
            return null;
        } else {
            // it's a year
            return String.valueOf(inputNumber) + "-01-01";
        }
    }

    private static String parseMultiDateInput(final String inputDate) {
        final String[] dateChunks = inputDate.split("-");

        if (dateChunks.length > 3) {
            return null;
        }

        final String yearStr = parseYear(dateChunks);
        final String monthStr = parseMonth(dateChunks);

        if (yearStr == null || monthStr == null) {
            return null;
        }

        final LocalDate testDate = LocalDate.of(Integer.valueOf(yearStr), Integer.valueOf(monthStr), 1);
        final String dayStr = parseDay(dateChunks, testDate.lengthOfMonth());
        if (dayStr == null) {
            return null;
        }

        return yearStr + "-" + monthStr + "-" + dayStr;
    }

    private static String parseYear(final String[] dateChunks) {
        final LocalDate today = LocalDate.now();
        final int currentMillenium = today.getYear() / 1000 * 1000;

        final int inputYear = Integer.valueOf(dateChunks[0]);
        if (inputYear < 100) {
            // year in the century
            if (currentMillenium + inputYear > today.getYear()) {
                // year in previous century
                return String.valueOf(currentMillenium - 100 + inputYear);
            } else {
                // year in this century
                return String.valueOf(currentMillenium + inputYear);
            }
        } else if (inputYear < 1900) {
            // error in year
            return null;
        } else {
            // it's a year
            return String.valueOf(inputYear);
        }
    }

    private static String parseMonth(final String[] dateChunks) {
        if ((dateChunks.length > 1) && !dateChunks[1].isEmpty()) {
            final int inputMonth = Integer.valueOf(dateChunks[1]);
            if ((inputMonth < 1) || (inputMonth > 12)) {
                return null;
            } else {
                return numberWithLeadingZero(inputMonth);
            }
        } else {
            return "01";
        }
    }

    private static String parseDay(final String[] dateChunks, final int lastDayOfMonth) {
        if ((dateChunks.length > 2) && !dateChunks[2].isEmpty()) {
            final int inputDay = Integer.valueOf(dateChunks[2]);
            if ((inputDay < 1) || (inputDay > lastDayOfMonth)) {
                return null;
            } else {
                return numberWithLeadingZero(inputDay);
            }
        } else {
            return "01";
        }
    }

    private static String tryParsingTime(final String inputTime) {
        if (inputTime.matches("(^:.*)|(.*[^0-9:].*)") || !inputTime.contains(":")) {
            return null;
        }

        final String[] timeChunks = inputTime.split(":");
        if (timeChunks.length > 3) {
            return null;
        }

        final String hoursStr = parseHours(timeChunks[0]);
        final String minutesStr = parseMinutesOrSeconds(timeChunks, 1);
        final String secondsStr = parseMinutesOrSeconds(timeChunks, 2);

        if (hoursStr == null || minutesStr == null || secondsStr == null) {
            return null;
        }

        return hoursStr + ":" + minutesStr + ":" + secondsStr;
    }

    private static String parseHours(final String hour) {
        if (!hour.isEmpty()) {
            final int inputHour = Integer.valueOf(hour);
            if ((inputHour < 0) || (inputHour > 23)) {
                return null;
            } else {
                return numberWithLeadingZero(inputHour);
            }
        } else {
            return null;
        }
    }

    private static String parseMinutesOrSeconds(final String[] timeChunks, final int index) {
        if ((timeChunks.length > index) && !timeChunks[index].isEmpty()) {
            final int inputNumber = Integer.valueOf(timeChunks[index]);
            if ((inputNumber < 0) || (inputNumber > 59)) {
                return null;
            } else {
                return numberWithLeadingZero(inputNumber);
            }
        } else {
            return "00";
        }
    }

    private static String numberWithLeadingZero(int num) {
        final String numStr = String.valueOf(num);
        return ("0" + numStr).substring(numStr.length() - 1);
    }
}
