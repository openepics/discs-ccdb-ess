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
package org.openepics.seds.alpha;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.json.JsonObject;
import org.epics.util.array.ArrayDouble;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.newAlarm;
import static org.epics.vtype.ValueFactory.newDisplay;
import static org.epics.vtype.ValueFactory.newTime;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openepics.seds.api.SedsDeserializer;
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.SedsReader;
import org.openepics.seds.api.SedsSerializer;
import org.openepics.seds.api.SedsWriter;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.AlarmType;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SedsException;

/**
 * Examples.
 *
 * @author Aaron Barber
 */
public class Alpha {

    @Test
    public void serialize_simple() throws SedsException, IOException {
        SedsFactory factory = Seds.newFactory();
        SedsSerializer mapper = Seds.newSerializer();
        SedsWriter writer = Seds.newWriter();

        //Creates SEDS Data
        SedsScalar data = factory.newScalar(
                "A",
                factory.newAlarm(AlarmType.MAJOR, "noStatus", "simpleAlarm"),
                null, //Control
                null, //Display
                factory.newTime(Timestamp.of(1354719441, 521786982), -1)
        );

        //Converts SEDS to JSON (in memory)
        JsonObject jsonData = mapper.serializeSEDS(data);

        //Write JSON to file
        writer.write(jsonData, FileUtil.get(FileUtil.ALPHA, "simple"));
    }

    @Test
    public void serialize_chained() throws SedsException, IOException {
        Seds.newWriter().write(
                Seds.newSerializer().serializeSEDS(
                        Seds.newFactory().newScalar(
                                1234,
                                "1234",
                                Seds.newFactory().newAlarm(AlarmType.MAJOR, "noStatus", "simpleAlarm"),
                                Seds.newFactory().newControl(10d, 100d),
                                null,
                                Seds.newFactory().newTime(Timestamp.of(1354719441, 521786982), -1)
                        )
                ),
                FileUtil.get(FileUtil.ALPHA, "chained")
        );
    }

    @Test
    public void serialize_complex() throws SedsException, IOException {
        SedsFactory factory = Seds.newFactory();
        SedsSerializer mapper = Seds.newSerializer();
        SedsWriter writer = Seds.newWriter();

        //Creates SEDS Data
        SedsScalarArray data = factory.newScalarArray(
                new Number[]{ //values
                    -1547157980,
                    89324,
                    3.1415,
                    1,
                    0,
                    -1.0000,
                    1.0000,
                    4.57
                },
                new String[] {
                    "-1547157980",
                    "0x15CEC",
                    "3.1415",
                    "1",
                    "0",
                    "-1e1",
                    "1.0000",
                    "4.57"
                },
                factory.newAlarm(
                        AlarmType.MAJOR, //severity
                        "noStatus", //status
                        "simpleAlarm" //message
                ),
                factory.newControl(
                        10d, //low limit
                        100d //high limit
                ),
                factory.newDisplay(
                        0.0, //low alarm
                        100.0, //high alarm
                        Math.pow(10, -3), //low display
                        Math.pow(10, 3), //high display
                        10.0, //low warning
                        90.0, //high warning
                        "distance", //description
                        "meters" //units
                ),
                factory.newTime(
                        Timestamp.of(1354719441, 521786982), //timestamp
                        -1 //user tag
                )
        );

        JsonObject jsonData = mapper.serializeSEDS(data);
        writer.write(jsonData, FileUtil.get(FileUtil.ALPHA, "complex"));
    }

    @Test
    public void serialize_vtype() throws SedsException, IOException {
        //Creates VType Data
        VDoubleArray vData = ValueFactory.newVDoubleArray(
                new ArrayDouble(3.14, 6.28, 1.41, 0.0, 1.0),
                newAlarm(AlarmSeverity.MINOR, "LOW"),
                newTime(Timestamp.of(1354719441, 521786982)),
                newDisplay(10d, 5d, 0d, "unit", NumberFormats.toStringFormat(), 1000d, 500d, 100d, 50d, 60d)
        );

        //Converts
        SedsScalarArray sedsData = (SedsScalarArray) Seds.newVTypeConverter().toSEDS(vData);
        JsonObject jsonData = Seds.newVTypeConverter().toJSON(vData);

        //Writes
        Seds.newWriter().write(jsonData, FileUtil.get(FileUtil.ALPHA, "vtype"));
    }

    @Test
    public void deserialize_simple() throws IOException, SedsException {
        File input = FileUtil.get(FileUtil.ALPHA, "simple");
        File output = FileUtil.get(FileUtil.ALPHA, "simple" + FileUtil.FAILED);

        //Reads
        //----------------------------------------------------------------------
        SedsReader reader = Seds.newReader();
        SedsDeserializer deserializer = Seds.newDeserializer();

        //Reads file to JSON
        JsonObject jsonData = reader.read(input);

        //Converts JSON to SEDS
        SedsType data = deserializer.deserializeSEDS(jsonData);
        //----------------------------------------------------------------------

        //Writes
        //----------------------------------------------------------------------
        SedsSerializer serializer = Seds.newSerializer();
        SedsWriter writer = Seds.newWriter();

        writer.write(serializer.serializeSEDS(data), output);
        //----------------------------------------------------------------------

        //Equality Check
        //----------------------------------------------------------------------
        assertTrue(FileUtil.equalFileContent(output, input, StandardCharsets.UTF_8));
        output.delete();
        //----------------------------------------------------------------------
    }

    @Test
    public void deserialize_chained() throws IOException, SedsException {
        File input = FileUtil.get(FileUtil.ALPHA, "chained");
        File output = FileUtil.get(FileUtil.ALPHA, "chained" + FileUtil.FAILED);

        //Reads
        //----------------------------------------------------------------------
        SedsType data = Seds.newDeserializer().deserializeSEDS(
                Seds.newReader().read(
                        input
                )
        );
        //----------------------------------------------------------------------

        //Writes
        //----------------------------------------------------------------------
        Seds.newWriter().write(
                Seds.newSerializer().serializeSEDS(data),
                output
        );
        //----------------------------------------------------------------------

        //Equality Check
        //----------------------------------------------------------------------
        assertTrue(FileUtil.equalFileContent(output, input, StandardCharsets.UTF_8));
        output.delete();
        //----------------------------------------------------------------------
    }

    @Test
    public void deserialize_complex() throws IOException, SedsException {
        File input = FileUtil.get(FileUtil.ALPHA, "complex");
        File output = FileUtil.get(FileUtil.ALPHA, "complex" + FileUtil.FAILED);

        SedsType data = Seds.newDeserializer().deserializeSEDS(Seds.newReader().read(input));
        Seds.newWriter().write(Seds.newSerializer().serializeSEDS(data), output);

        //Equality Check
        //----------------------------------------------------------------------
        assertTrue(FileUtil.equalFileContent(output, input, StandardCharsets.UTF_8));
        output.delete();
        //----------------------------------------------------------------------
    }

    @Test
    public void deserialize_vtype() throws IOException, SedsException {
        File input = FileUtil.get(FileUtil.ALPHA, "vtype");
        File output = FileUtil.get(FileUtil.ALPHA, "vtype" + FileUtil.FAILED);

        //File to VType
        JsonObject jsonData = Seds.newReader().read(input);
        SedsScalarArray sedsData = (SedsScalarArray) Seds.newVTypeConverter().toSEDS(jsonData);
        VDoubleArray vData = (VDoubleArray) Seds.newVTypeConverter().toClientType(sedsData);

        //VType to File
        Seds.newWriter().write(Seds.newVTypeConverter().toJSON(vData), output);

        //Equality Check
        //----------------------------------------------------------------------
        assertTrue(FileUtil.equalFileContent(output, input, StandardCharsets.UTF_8));
        output.delete();
        //----------------------------------------------------------------------
    }
}
