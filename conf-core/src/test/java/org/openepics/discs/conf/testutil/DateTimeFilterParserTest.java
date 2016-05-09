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
package org.openepics.discs.conf.testutil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Test;
import org.openepics.discs.conf.ui.util.UiUtility;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class DateTimeFilterParserTest {

    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void singleNumberTests() {
        final LocalDate today = LocalDate.now();
        final LocalDateTime todayAtMidnight = today.atStartOfDay();
        final LocalDate startOfMillenium = LocalDate.of(today.getYear() / 1000 * 1000, 1, 1);

        Assert.assertEquals("'0' should return 'start of millenuim'",
                startOfMillenium.atStartOfDay(),
                UiUtility.processUIDateTime("0"));
        // the number of today's date only
        Assert.assertEquals("'" + today.getDayOfMonth() + "' should return 'today'",
                todayAtMidnight,
                UiUtility.processUIDateTime(Integer.toString(today.getDayOfMonth())));
        // test for the first of the month, skipped if today is the first day of the month, ...
        //   but then this is already covered by previous test
        if (today.getDayOfMonth() > 1) {
            final LocalDateTime startOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1).atStartOfDay();
            Assert.assertEquals("'1' should return '" + startOfMonth.format(dateTimeFormat) +"'",
                    startOfMonth,
                    UiUtility.processUIDateTime("1"));
        }
        // test for number above today's date, skipped if today is the last day of the month, ...
        //   but then this is already covered by the 'today' test
        if (today.getDayOfMonth() < today.lengthOfMonth()) {
            final LocalDateTime tomorrow = LocalDate.of(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth() + 1).
                                                    atStartOfDay();
            Assert.assertEquals("'tomorrow' should return '" + tomorrow.format(dateTimeFormat) +"'",
                    tomorrow,
                    UiUtility.processUIDateTime(Integer.toString(today.getDayOfMonth() + 1)));
        }

        Assert.assertEquals("'32' should return '1932-01-01 00:00:00'",
                LocalDate.of(1932, 1, 1).atStartOfDay(),
                UiUtility.processUIDateTime("32"));

        Assert.assertEquals("'2017' should return '2017-01-01 00:00:00'",
                LocalDate.of(2017, 1, 1).atStartOfDay(),
                UiUtility.processUIDateTime("2017"));
    }

    @Test
    public void dateAndTimeTests() {
        final LocalDateTime todayAtNoon = LocalDate.now().atTime(12, 0);

        Assert.assertEquals("'1973-6' should return '1973-06-01 00:00:00'",
                LocalDate.of(1973, 6, 1).atStartOfDay(),
                UiUtility.processUIDateTime("1973-6"));

        Assert.assertEquals("'1973-6-20' should return '1973-06-20 00:00:00'",
                LocalDate.of(1973, 6, 20).atStartOfDay(),
                UiUtility.processUIDateTime("1973-6-20"));

        Assert.assertEquals("'1973-006-20' should return '1973-06-20 00:00:00'",
                LocalDate.of(1973, 6, 20).atStartOfDay(),
                UiUtility.processUIDateTime("1973-006-20"));


        Assert.assertEquals("'12:0' should return '" + todayAtNoon.format(dateTimeFormat) + "'",
                todayAtNoon,
                UiUtility.processUIDateTime("12:0"));

        Assert.assertEquals("'12:0:0' should return '" + todayAtNoon.format(dateTimeFormat) + "'",
                todayAtNoon,
                UiUtility.processUIDateTime("12:0:0"));

        Assert.assertEquals("'1973-6-20  13:50:12' should return '1973-06-20 13:50:12'",
                LocalDateTime.of(1973, 6, 20, 13, 50, 12),
                UiUtility.processUIDateTime("1973-6-20  13:50:12"));
    }

    @Test
    public void invalidInputs() {
        final LocalDateTime epochStart = LocalDate.ofEpochDay(0).atStartOfDay();

        Assert.assertEquals("'' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime(""));
        Assert.assertEquals("'a' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("a"));
        Assert.assertEquals("'9a' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("9a"));
        Assert.assertEquals("'2016/01/01' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016/01/01"));
        Assert.assertEquals("'900' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("900"));
        Assert.assertEquals("'2016-13-01' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016-13-01"));
        Assert.assertEquals("'2016-01-01-00' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016-01-01-00"));
        Assert.assertEquals("'2016-A-01' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016-A-01"));
        Assert.assertEquals("'2016-01-01 12:70:00' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016-01-01 12:70:00"));
        Assert.assertEquals("'2016-01-01 25:0:0' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016-01-01 25:0:0"));
        Assert.assertEquals("'-01' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("-01"));
        Assert.assertEquals("'2016--01-01' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("2016--01-01"));
        Assert.assertEquals("'12:00:00:00' should return 'Epoch start'", epochStart, UiUtility.processUIDateTime("12:00:00:00"));
    }
}
