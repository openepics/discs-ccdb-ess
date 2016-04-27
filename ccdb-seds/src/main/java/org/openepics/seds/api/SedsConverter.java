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
 * Interface to convert between: JSON, SEDS, and a <i>client's</i> type system.
 *
 * @author Aaron Barber
 * @param <T> the {@link AbstractMapper mapper} describing the function to map a
 * system of <i>client</i> datatypes to and from {@link SedsType} datatypes.
 */
public interface SedsConverter<T extends AbstractMapper> {

    //Co-domain: Client Type
    //--------------------------------------------------------------------------
    /**
     * Converts the {@code SedsType} into the <i>client's</i> type.
     *
     * @param value instance of SEDS data to map into a <i>client's</i> type
     * @return mapping of the SEDS data into the <i>client's</i> type
     * @throws SedsException if the SEDS type does not have a corresponding
     * Client Type
     */
    public Object toClientType(SedsType value) throws SedsException;

    /**
     * Converts the {@code JsonObject} into the <i>client's</i> type using SEDS
     * Types as an intermediary.
     *
     * <p>
     * Uses {@link SedsMetadata} to determine the correct SEDS type to parse the
     * JSON data as.
     *
     * @param value instance of JSON data to map into a <i>client's</i> type
     * @return mapping of the JSON into the <i>client's</i> type conforming to
     * the schema of a SEDS type
     * @throws SedsException if the JSON data does not conform to a JSON schema
     * provided by the SEDS protocol
     */
    public Object toClientType(JsonObject value) throws SedsException;
    //--------------------------------------------------------------------------

    //Co-domain: SEDS
    //--------------------------------------------------------------------------
    /**
     * Converts the <i>client's</i> into a SEDS type.
     *
     * @param value instance of <i>client's</i> data to map into a SEDS type
     * @return mapping of the <i>client's</i> data into a SEDS type
     * @throws SedsException if the Client Type does not have a corresponding
     * SEDS type
     */
    public SedsType toSEDS(Object value) throws SedsException;

    /**
     * Converts the JSON into a SEDS type.
     *
     * @param value instance of JSON data to map into a SEDS type
     * @return mapping of the JSON into the SEDS type conforming to the schema
     * of a SEDS type
     * @throws SedsException if the JSON data does not conform to a JSON schema
     * provided by the SEDS protocol
     */
    public SedsType toSEDS(JsonObject value) throws SedsException;
    //--------------------------------------------------------------------------

    //Co-domain: JSON
    //--------------------------------------------------------------------------
    /**
     * Converts the <i>client's</i> data into JSON using SEDS types as an
     * intermediary.
     *
     * @param value instance of the <i>client's</i> data to map into JSON
     * @return mapping of the <i>client's</i> data into JSON conforming to the
     * schema of a SEDS type
     * @throws SedsException if the Client Type does not have a corresponding
     * SEDS type
     */
    public JsonObject toJSON(Object value) throws SedsException;

    /**
     * Converts the SEDS data into JSON.
     *
     * @param value instance of the SEDS data to map into JSON
     * @return mapping of the SEDS data into JSON conforming to the schema of a
     * SEDS type
     * @throws SedsException if the JSON data does not conform to a JSON schema
     * provided by the SEDS protocol
     */
    public JsonObject toJSON(SedsType value) throws SedsException;
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    /**
     * Determines whether the value is a <i>client</i> type AND is mappable into
     * SEDS type (and therefore JSON).
     *
     * @param value data to determine if it represents a <i>client</i> type
     * @return true if the value represents a <i>client</i> type AND is mappable
     * into SEDS, otherwise false
     * @see #toJSON(java.lang.Object) is usable if this method returns true
     */
    public boolean isClientType(Object value);
    //--------------------------------------------------------------------------
}
