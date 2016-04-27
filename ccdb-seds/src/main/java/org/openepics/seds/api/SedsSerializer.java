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

import javax.json.JsonObject;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.SedsException;

/**
 * Converts {@link SedsType} data structures in memory into {@link JsonObject}
 * data in memory according to the SEDS protocol.
 * <p>
 * Any implementation SHOULD also include validation using the JSON schema
 * provided by the SEDS protocol.
 *
 * @author Aaron Barber
 */
public interface SedsSerializer {

    /**
     * Maps the {@link SedsType} data structure into a {@link JsonObject}
     * containing the relevant data according to SEDS protocol: the meta data
     * and the raw data is included in this serialization.
     *
     * @param value SEDS data structure in memory to convert into JSON
     * (represented as a {@link JsonObject} in memory)
     * @return serializes the meta data and raw data from the value into JSON
     * data
     * @throws SedsException if the SEDS value does not conform to the JSON
     * schema provided by the SEDS protocol
     */
    public JsonObject serializeSEDS(SedsType value) throws SedsException;

    /**
     * Maps the meta data of the {@link SedsType} data structure into a
     * {@link JsonObject} according to SEDS protocol: only the meta data is
     * included in this serialization.
     *
     * @param value SEDS data structure in memory to convert <b>the meta data
     * of</b> into JSON (represented as a {@link JsonObject} in memory)
     * @return serializes the meta data from the value into JSON data
     * @throws SedsException if the SEDS value does not conform to the JSON
     * schema provided by the SEDS protocol
     */
    public JsonObject serializeMeta(SedsType value) throws SedsException;

    /**
     * Maps the raw data of the {@link SedsType} data structure into a
     * {@link JsonObject} according to SEDS protocol: only the raw data is
     * included in this serialization.
     *
     * @param value SEDS data structure in memory to convert <b>the raw data
     * of</b> into JSON (represented as a {@link JsonObject} in memory)
     * @return serializes the raw data from the value into JSON data
     * @throws SedsException if the SEDS value does not conform to the JSON
     * schema provided by the SEDS protocol
     */
    public JsonObject serializeData(SedsType value) throws SedsException;

}
