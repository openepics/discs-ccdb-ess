/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.seds.util;

import org.epics.util.time.Timestamp;
import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.core.Seds;

/**
 *
 * @author asbarber
 */
public class SampleSedsFactory {

    public SedsAlarm sampleSedsAlarm() {
        SedsAlarm obj = Seds.newFactory().newAlarm(AlarmType.fromOrdinal(2), "3", "sampleAlarm");
        return obj;
    }

    public SedsDisplay sampleSedsDisplay() {
        SedsDisplay obj = Seds.newFactory().newDisplay(1d, 2d, 10d, 100d, 11d, 12d, "sampleDisplay", "meters");
        return obj;
    }

    public SedsEnum sampleSedsEnum() {
        SedsEnum obj = Seds.newFactory().newEnum("SPADES", new String[]{"SPADES", "HEARTS", "DIAMONDS", "CLUBS"});
        return obj;
    }

    public SedsControl sampleSedsControl() {
        SedsControl obj = Seds.newFactory().newControl(10.0, 100.0);
        return obj;
    }

    public SedsTime sampleSedsTime() {
        SedsTime obj = Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), null);
        return obj;
    }

    public SedsScalar<Boolean> sampleSedsScalarBoolean() {
        SedsScalar<Boolean> obj = Seds.newFactory().newScalar(
                true,
                "TRUE",
                null,
                null,
                null,
                null
        );
        return obj;
    }

    public SedsScalar<SedsEnum> sampleSedsScalarEnum() {
        return Seds.newFactory().newScalar(
                Seds.newFactory().newEnum("B", new String[]{"A", "B", "C"}),
                "Label B",
                Seds.newFactory().newAlarm(AlarmType.fromOrdinal(2), "3", "sampleAlarm"),
                null,
                null,
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), null)
        );
    }

    public SedsScalar<Integer> sampleSedsScalarInteger() {
        return Seds.newFactory().newScalar(
                1,
                "0x1",
                null,
                null,
                null,
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), null)
        );
    }

    public SedsScalar<Number> sampleSedsScalarNumber() {
        SedsScalar<Number> obj = Seds.newFactory().newScalar(
                3.1415,
                "3.1415",
                Seds.newFactory().newAlarm(AlarmType.fromOrdinal(2), "3", "sampleAlarm"),
                Seds.newFactory().newControl(10.0, 100.0),
                Seds.newFactory().newDisplay(1d, 2d, 10d, 100d, 11d, 12d, "sampleDisplay", "meters"),
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), 1)
        );
        return obj;
    }

    public SedsScalar<String> sampleSedsScalarString() {
        SedsScalar<String> obj = Seds.newFactory().newScalar(
                "A",
                Seds.newFactory().newAlarm(AlarmType.fromOrdinal(2), "3", null),
                null,
                Seds.newFactory().newDisplay(1d, 2d, 10d, 100d, 11d, 12d, "sampleDisplay", "meters"),
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), 1)
        );
        return obj;
    }

    public SedsScalarArray<Boolean> sampleSedsScalarArrayBoolean() {
        SedsScalarArray<Boolean> obj = Seds.newFactory().newScalarArray(
                new Boolean[]{true, false, true},
                null,
                null,
                null,
                null
        );
        return obj;
    }

    public SedsScalarArray<SedsEnum> sampleSedsScalarArrayEnum() {
        return Seds.newFactory().newScalarArray(
                new SedsEnum[]{
                    Seds.newFactory().newEnum("A", new String[]{"A", "B", "C"}),
                    Seds.newFactory().newEnum("AA", new String[]{"AA", "BB", "CC"})
                },
                null,
                null,
                null,
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), null)
        );
    }

    public SedsScalarArray<Integer> sampleSedsScalarArrayInteger() {
        SedsScalarArray<Integer> obj = Seds.newFactory().newScalarArray(
                new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                null,
                null,
                null,
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), null)
        );
        return obj;
    }

    public SedsScalarArray<Number> sampleSedsScalarArrayNumber() {
        SedsScalarArray<Number> obj = Seds.newFactory().newScalarArray(
                new Number[]{3.1415, -1, -1000000000, 0, 10},
                Seds.newFactory().newAlarm(AlarmType.fromOrdinal(2), "3", "sampleAlarm"),
                Seds.newFactory().newControl(10.0, 100.0),
                Seds.newFactory().newDisplay(1d, 2d, 10d, 100d, 11d, 12d, "sampleDisplay", "meters"),
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), 1)
        );
        return obj;
    }

    public SedsScalarArray<String> sampleSedsScalarArrayString() {
        SedsScalarArray<String> obj = Seds.newFactory().newScalarArray(
                new String[]{"A", "B", "", null, "E"},
                Seds.newFactory().newAlarm(AlarmType.fromOrdinal(2), "3", null),
                null,
                Seds.newFactory().newDisplay(1d, 2d, 10d, 100d, 11d, 12d, "sampleDisplay", "meters"),
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), 1)
        );
        return obj;
    }

    public SedsTable sampleSedsTable() {
        SedsTable obj = Seds.newFactory().newTable(
                4,
                3,
                new String[]{"A", "B", "C"},
                new SedsScalarArray[]{
                    Seds.newFactory().newScalarArray(
                            new Boolean[]{true, false, true, false},
                            Seds.newFactory().newAlarm(AlarmType.NONE, "driver", "msgA"),
                            null,
                            null,
                            null
                    ),
                    Seds.newFactory().newScalarArray(
                            new Integer[]{1, 2, 3, 4},
                            Seds.newFactory().newAlarm(AlarmType.NONE, "driver", "msgB"),
                            null,
                            Seds.newFactory().newDisplay(null, null, 0d, 0d, null, null, null, "meters"),
                            null
                    ),
                    Seds.newFactory().newScalarArray(
                            new SedsEnum[]{
                                Seds.newFactory().newEnum("A", new String[]{"A", "B"}),
                                Seds.newFactory().newEnum("D", new String[]{"C", "D"}),
                                Seds.newFactory().newEnum("G", new String[]{"E", "F", "G"}),
                                Seds.newFactory().newEnum("I", new String[]{"H", "I", "J"})
                            },
                            null,
                            null,
                            null,
                            null
                    )
                }
        );
        return obj;
    }

    public SedsScalarArray<SedsEnum> sampleDB_SedsScalarArrayEnum() {
        return Seds.newFactory().newScalarArray(
                new SedsEnum[]{
                    Seds.newFactory().newEnum("A", new String[]{"A", "B", "C"}),
                    Seds.newFactory().newEnum("C", new String[]{"A", "B", "C"}),
                    Seds.newFactory().newEnum("C", new String[]{"A", "B", "C"})
                },
                null,
                null,
                null,
                Seds.newFactory().newTime(Timestamp.of(1354719441L, 521786982), null)
        );
    }
}
