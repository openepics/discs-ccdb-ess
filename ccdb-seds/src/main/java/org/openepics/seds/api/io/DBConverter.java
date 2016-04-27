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
package org.openepics.seds.api.io;

import javax.json.JsonObject;
import org.openepics.seds.api.datatypes.SedsType;

/**
 * Database processing object that converts {@link SedsType} data structures in
 * memory into {@link JsonObject} data in memory and vice versa. The conversion
 * is based off the SEDS protocol, but optimizes the conversion for storing the
 * data structures in a database.
 *
 * <p>
 * <b>Warning:</b>
 * No validation is done against the JSON schemas provided by the SEDS protocol
 * because a database converter is NOT intended to directly follow the protocol,
 * but rather optimize the protocol for storing large amounts of data.
 *
 * <p>
 * The parsing methods are intended to split up a JsonObject when the data is
 * actually stored.
 *
 * <p>
 * <b>Warning:</b>
 * It is NOT appropriate to mix the methods of this processing objects
 * {@link SedsSerializer}, {@link SedsDesserializer}, and {@link SedsValidator}
 * because those processing objects follow SEDS protocol directly while this
 * processing object does not.
 *
 * <p>
 * <b>Note:</b>
 * It is appropriate to mix the methods of this processing objects with
 * {@link SedsReader} and {@link SedsWriter}.
 *
 * @author Aaron Barber
 */
public interface DBConverter {

    //API Layer
    //--------------------------------------------------------------------------
    /**
     * Splits the parameter JsonObject and parses only the metadata component
     * (that contains protocol and version information).
     *
     * @param value overall JsonObject to parse
     * @return metadata component (datatype and version info)
     */
    public JsonObject parseMetaComponent(JsonObject value);

    /**
     * Splits the parameter JsonObject and parses only the raw data component
     * (the values, without properties that would be redundant when storing the
     * JSON value to a database).
     *
     * <p>
     * For example, the 'selected' property of a {@link SedsEnum} should be
     * stored in the raw information and the 'elements' property should NOT be
     * stored in the raw information.
     *
     * @param value overall JsonObject to parse
     * @return raw data component (values)
     */
    public JsonObject parseRawComponent(JsonObject value);

    /**
     * Splits the parameter JsonObject and parses only the type data component
     * (the properties that should only be stored once when storing multiple
     * objects of the same type).
     *
     * <p>
     * For example, the 'elements' property of a {@link SedsEnum} should be
     * stored in the type information and the 'selected' property should NOT be
     * stored in the type information.
     *
     * @param value overall JsonObject to parse
     * @return type data component (database values that only need to be stored
     * once for a set of similar JsonObjects)
     */
    public JsonObject parseTypeComponent(JsonObject value);

    /**
     * Combines the meta, raw, and type components into one JsonObject.
     *
     * @param meta meta data component (datatype and version info)
     * @param raw raw data component (values)
     * @param type type data component (database values that only need to be
     * stored once for a set of similar JsonObjects)
     * @return JsonObject containing meta, raw, and type components
     */
    public JsonObject combineComponents(JsonObject meta, JsonObject raw, JsonObject type);
    //-------------------------------------------------------------------------- 

    //API Layer
    //-------------------------------------------------------------------------- 
    /**
     * Serializes the SEDS data into JSON that is intended to be stored in a
     * database.
     *
     * <p>
     * No validation is done against the JSON schemas provided by the SEDS
     * protocol because a database converter is NOT intended to directly follow
     * the protocol, but rather optimize the protocol for storing large amounts
     * of data.
     *
     * @param value SEDS data to serialize into JSON (based on the SEDS protocol
     * but also modified to be optimized for persistence)
     * @return JSON from the SEDS data
     */
    public JsonObject serialize(SedsType value);

    /**
     * Deserializes the JSON data into a SEDS data structure.
     *
     * <p>
     * No validation is done against the JSON schemas provided by the SEDS
     * protocol because a database converter is NOT intended to directly follow
     * the protocol, but rather optimize the protocol for storing large amounts
     * of data.
     *
     * @param value JSON data to deserialize into SEDS
     * @return JSON from the SEDS data
     */
    public SedsType deserialize(JsonObject value);
    //--------------------------------------------------------------------------  

}
