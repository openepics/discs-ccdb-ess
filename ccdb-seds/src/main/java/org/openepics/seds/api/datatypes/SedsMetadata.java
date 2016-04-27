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
package org.openepics.seds.api.datatypes;

/**
 * Metadata information for a value. Represents the information that is needed
 * to be capable of serializing and deserializing a value.
 *
 * @author Aaron Barber
 */
public interface SedsMetadata extends SedsType {

    /**
     * Returns the SEDS type of the value that is represented by the metadata.
     *
     * @return SEDS type of value
     */
    public String getType();

    /**
     * Returns the serialization protocol of the value that is represented by
     * the metadata.
     *
     * @return serialization protocol of value
     */
    public String getProtocol();

    /**
     * Returns the version of the value that is represented by the metadata.
     *
     * @return version of value
     */
    public String getVersion();

}
