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
package org.openepics.seds.api;

import java.util.Map.Entry;
import javax.json.JsonObject;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.SedsException;

/**
 * Converts {@link JsonObject} data in memory into {@link SedsType} data
 * structure in memory according to the SEDS protocol.
 *
 * <p>
 * Note that meta data is REQUIRED in order to correctly deserialize into SEDS
 * data.
 *
 * <p>
 * Any implementation SHOULD also include validation using the JSON schema
 * provided by the SEDS protocol.
 *
 * @author Aaron Barber
 */
public interface SedsDeserializer {

    /**
     * Maps the {@link JsonObject} data in memory into a {@link SedsType} data
     * structure containing the relevant data according to SEDS protocol: the
     * meta data and the raw data are parsed in this deserialization.
     * <p>
     * It is required that the JsonObject contains correct meta data in order to
     * deserialize.
     * <p>
     * Any implementation should validate using the JSON schema provided by the
     * SEDS protocol.
     *
     * @param value
     * @return SEDS data structure with the data taken from the JSON data
     * structure according to the SEDS protocol, where the type is determined by
     * the meta data
     * @throws SedsException if the meta data is not correct or deserialization
     * is unable to occur
     */
    public SedsType deserializeSEDS(JsonObject value) throws SedsException;

    /**
     * Maps the {@link JsonObject} data in memory into a {@link SedsMetadat}
     * data structure containing meta data according to SEDS protocol: only the
     * meta data should be included in the JSON object.
     *
     * @param value meta data in the form of JSON
     * @return SEDS meta data structure with the data taken from the JSON data
     * according to the SEDS protocol
     * @throws SedsException if unable to deserialize correctly
     */
    public SedsMetadata deserializeMeta(JsonObject value) throws SedsException;

    /**
     * Separates the meta data and the raw data into an entry obtained from the
     * JSON value: the separation process is determined by the structure of a
     * SEDS type that contains both meta and raw data that is defined by the
     * SEDS protocol.
     *
     * @param value full SEDS data structure containing meta data and raw data
     * @return the meta data and raw data that was separated from the value
     * parameter
     * @throws SedsException if unable to separate the raw data and meta data
     * correctly
     */
    public Entry<SedsMetadata, JsonObject> separateMetadata(JsonObject value) throws SedsException;
}
