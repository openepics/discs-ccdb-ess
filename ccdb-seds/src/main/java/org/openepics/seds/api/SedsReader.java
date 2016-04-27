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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.json.JsonObject;

/**
 * Interface to read a {@link JsonObject} from an input source.
 * <p>
 * Note that the reader does no mapping into
 * {@link org.openepics.seds.api.datatypes.SedsType} data structures.
 *
 * @author Aaron Barber
 */
public interface SedsReader {

    /**
     * Returns the Json object represented in the input source (a string).
     *
     * @param src a string from which Json is to be read
     * @return Json object read from the input source
     * @throws IllegalArgumentException if the source is null
     */
    public JsonObject read(String src);

    /**
     * Returns the Json object represented in the input source (a reader).
     *
     * @param src a reader from which Json is to be read
     * @return Json object read from the input source
     * @throws IllegalArgumentException if the source is null
     */
    public JsonObject read(Reader src);

    /**
     * Returns the Json object represented in the input source (a file).
     *
     * @param src a file from which Json is to be read
     * @return Json object read from the input source
     * @throws IllegalArgumentException if the source is null
     * @throws IOException if an error occurred reading the file
     */
    public JsonObject read(File src) throws IOException;

    /**
     * Returns the Json object represented in the input source (a stream).
     *
     * @param src a stream from which Json is to be read
     * @return Json object read from the input source
     * @throws IllegalArgumentException if the source is null
     */
    public JsonObject read(InputStream src);

}
