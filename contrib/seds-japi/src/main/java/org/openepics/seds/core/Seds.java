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
package org.openepics.seds.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.json.JsonObject;
import org.openepics.seds.api.AbstractMapper;
import org.openepics.seds.api.SedsConverter;
import org.openepics.seds.api.SedsDeserializer;
import org.openepics.seds.api.SedsReader;
import org.openepics.seds.api.SedsSerializer;
import org.openepics.seds.api.SedsValidator;
import org.openepics.seds.api.SedsWriter;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.api.io.CSVConverter;
import org.openepics.seds.api.io.DBConverter;
import org.openepics.seds.core.datatypes.ImmutableSedsFactory;
import org.openepics.seds.core.datatypes.SimpleSedsFactory;
import org.openepics.seds.core.io.SedsIO;
import org.openepics.seds.core.vtype.VTypeMapper;

/**
 * Factory class for creating SEDS processing objects. This class provides the
 * most commonly used methods for creating these objects and their corresponding
 * factories. The factory classes provide all the various ways to create these
 * objects.
 *
 * <p>
 * Common processing tasks are creating a SEDS object from raw data, mapping a
 * SEDS object to/from JSON, mapping a SEDS object to/from a <i>client</i>
 * type, and mapping a SEDS object to/from a (.json) file.
 *
 * <p>
 * The following example shows how to create a SEDS object from raw data,
 * convert the {@link SedsType} to a {@link JsonObject} and then write to the
 * given {@link File}:
 *
 * <pre>
 * <code>
 * File file = ...
 * SedsType sedsData = Seds.newFactory().newAlarm(2, 3, "sampleAlarm");
 * JsonObject jsonData = Seds.newSerializer().serializeSEDS(sedsData);
 * Seds.newWriter().write(jsonData, file);
 * </code>
 * </pre>
 *
 * @author Aaron Barber
 */
public class Seds {

    //JSON
    //--------------------------------------------------------------------------
    /**
     * Builds a serializer to serialize SEDS data structures into JSON data
     * structures: validation using the JSON schema provided by SEDS protocol IS
     * performed.
     *
     * @return converter for taking SEDS into JSON
     */
    public static SedsSerializer newSerializer() {
        return new BaseSerializer(newFactory(), newValidator(), new JsonMapper(newFactory()));
    }

    /**
     * Builds a deserializer to deserialize JSON data structures into SEDS data
     * structures: validation using the JSON schema provided by SEDS protocol IS
     * performed.
     *
     * @return converter for taking JSON into SEDS
     */
    public static SedsDeserializer newDeserializer() {
        return new BaseDeserializer(newValidator(), new JsonMapper(newFactory()));
    }
    //--------------------------------------------------------------------------

    //Conversion
    //--------------------------------------------------------------------------
    /**
     * Builds a converter to convert between SEDS types, JSON, and the client
     * types represented in the mapper.
     *
     * @param mapper conversion helper to map a set of client types to and from
     * SEDS types
     * @return converter for SEDS types, JSON, and the client types (specified
     * by the mapper parameter)
     */
    public static SedsConverter newConverter(AbstractMapper mapper) {
        return new BaseConverter(mapper, newSerializer(), newDeserializer());
    }

    /**
     * Builds a converter to convert between SEDS types, EPICS VTypes, and JSON.
     *
     * @return converter for SEDS types, EPICS VTypes, and JSON
     */
    public static SedsConverter<VTypeMapper> newVTypeConverter() {
        return newConverter(new VTypeMapper(newFactory()));
    }

    /**
     * Builds a converter to convert between SEDS types and CSV.
     *
     * <p>
     * CSV (comma-separated value) uses the comma as the default separator.
     *
     * <p>
     * The converter provides conversions for only the most common SEDS types.
     *
     * @return converter to convert between common SEDS types and CSV
     */
    public static CSVConverter newCSVConverter() {
        return SedsIO.newCSVConverter();
    }

    /**
     * Builds a converter to convert between SEDS types and JSON for a database.
     *
     * <p>
     * The conversion is tailored for storing SEDS types in a database by
     * separating metadata into a separate portion of the JSON object that can
     * be stored separately.
     *
     * @return converter to convert between SEDS types and JSON for a database
     */
    public static DBConverter newDBConverter() {
        return SedsIO.newDBConverter();
    }
    //--------------------------------------------------------------------------

    //IO
    //--------------------------------------------------------------------------
    /**
     * Builds a reader to deserialize an input source into JSON structures in
     * memory.
     *
     * @return converter for taking an input source into JSON
     * ({@link JsonObject})
     */
    public static SedsReader newReader() {
        return new BaseReader();
    }

    /**
     * Builds a reader to serialize a JSON structure in memory to an output
     * source
     *
     * @return converter for taking JSON ({@link JsonObject)} into an output
     * source
     */
    public static SedsWriter newWriter() {
        return new BaseWriter();
    }
    //--------------------------------------------------------------------------

    //Other
    //--------------------------------------------------------------------------
    /**
     * Builds a validator to validate JSON structures according to the JSON
     * schemas provided by the SEDS protocol.
     *
     * @return validator
     */
    public static SedsValidator newValidator() {
        return new BaseValidator();
    }

    /**
     * Builds a factory to create immutable SEDS data structures in memory.
     *
     * @return factory to build SEDS data structures
     */
    public static ImmutableSedsFactory newFactory() {
        return new ImmutableSedsFactory();
    }

    /**
     * Builds a factory to create immutable SEDS data structures in memory very
     * quickly and without having to attach metadata (alarm data, etc.).
     *
     * @return factory to build common SEDS data structures
     */
    public static SimpleSedsFactory newSimpleFactory() {
        return new SimpleSedsFactory();
    }
    //--------------------------------------------------------------------------

    /**
     * <pre>
     * TODO: implementation tasks
     *
     * Topic            Notes
     * -------------------------------------------------------------------------
     * Project Structure
     *                  use central maven (move the project to maven central)
     *                  use jenkins       (so that project builds when I push)
     *
     * Other
     *                  email zip to miha
     * -------------------------------------------------------------------------
     * </pre>
     */
}
