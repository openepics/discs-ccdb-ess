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

import com.github.fge.jsonschema.core.report.ProcessingReport;
import javax.json.JsonObject;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.util.SedsException;

/**
 * Interface to validate a {@link JsonObject} according to the SEDS protocol. As
 * of this version, the SEDS protocol specifies 3 types of JSON structures to
 * represent SEDS data:
 * <ol>
 * <li>SEDS Type (contains raw and meta data)
 * <li>SEDS Meta (contains only meta data: self-describing information of a
 * datatype)
 * <li>SEDS Raw (contains only raw data: values of a datatype
 * </ol>
 *
 * <p>
 * The validation SHOULD be done by comparing JSON data to the JSON schemas
 * provided by the SEDS protocol.
 *
 * @author Aaron Barber
 */
public interface SedsValidator {

    /**
     * Determines whether the JSON object is valid according to the SEDS
     * protocol, where the SEDS contains both <b>raw and meta data</b>.
     *
     * @param instance object containing JSON name/value pairings, to check
     * validity of
     * @return true if valid by SEDS protocol (for both raw and meta data),
     * otherwise false
     * @throws SedsException if malformed JSON or JSON processing errors occur
     */
    public boolean isValidSEDS(JsonObject instance) throws SedsException;

    /**
     * Determines whether the JSON object is valid according to the SEDS
     * protocol, where the SEDS contains only <b>raw data</b>.
     *
     * @param instance object containing JSON name/value pairings, to check
     * validity of
     * @return true if valid by SEDS protocol (for only raw data), otherwise
     * false
     * @throws SedsException if malformed JSON or JSON processing errors occur
     */
    public boolean isValidSEDSRaw(JsonObject instance) throws SedsException;

    /**
     * Determines whether the JSON object is valid according to the SEDS
     * protocol, where the SEDS contains only <b>meta data</b>.
     *
     * @param instance object containing JSON name/value pairings, to check
     * validity of
     * @return true if valid by SEDS protocol (for only meta data), otherwise
     * false
     * @throws SedsException if malformed JSON or JSON processing errors occur
     */
    public boolean isValidSEDSMeta(JsonObject instance) throws SedsException;

    /**
     * Compares the JSON object to the JSON schema (defined by SEDS protocol)
     * for a SEDS object containing both <b>raw and meta data</b>.
     *
     * @param instance object containing JSON name/value pairings, to check
     * validity of
     * @return report containing why the JSON object is not (or is) valid based
     * on the JSON schema provided by SEDS protocol
     * @throws SedsException if malformed JSON or JSON processing errors occur
     */
    public ProcessingReport validateSEDS(JsonObject instance) throws SedsException;

    /**
     * Compares the JSON object to the JSON schema (defined by SEDS protocol)
     * for a SEDS object containing only <b>raw data</b>.
     *
     * @param instance object containing JSON name/value pairings, to check
     * validity of
     * @return report containing why the JSON object is not (or is) valid based
     * on the JSON schema provided by SEDS protocol
     * @throws SedsException if malformed JSON or JSON processing errors occur
     */
    public ProcessingReport validateSEDSRaw(JsonObject instance) throws SedsException;

    /**
     * Compares the JSON object to the JSON schema (defined by SEDS protocol)
     * for a SEDS object containing only <b>meta data</b>.
     *
     * @param instance object containing JSON name/value pairings, to check
     * validity of
     * @return report containing why the JSON object is not (or is) valid based
     * on the JSON schema provided by SEDS protocol
     * @throws SedsException if malformed JSON or JSON processing errors occur
     */
    public ProcessingReport validateSEDSMeta(JsonObject instance) throws SedsException;

    //Versioning, Protocol, Type
    /**
     * Fails the program if the SEDS meta data is invalid according to SEDS
     * protocol.
     *
     * <p>
     * The validation SHOULD verify a correct version, a correct SEDS protocol,
     * and a correct SEDS object type specified by the meta data.
     *
     * @param instance SEDS meta data to check validity ofs
     * @throws SedsException if invalid meta data
     */
    public void validateMetadataValues(SedsMetadata instance) throws SedsException;
}
