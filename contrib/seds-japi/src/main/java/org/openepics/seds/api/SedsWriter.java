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
import java.io.OutputStream;
import java.io.Writer;
import javax.json.JsonObject;

/**
 * Interface to write a {@link JsonObject} to an output source.
 * <p>
 * Note that the writer does not write
 * {@link org.openepics.seds.api.datatypes.SedsType} data structures into JSON
 * or to a file.
 *
 * @author Aaron Barber
 */
public interface SedsWriter {

    /**
     * Writes the Json object to the output source (a string).
     * <p>
     * Writes using UTF-8 encoding, according to SEDS protocol.
     *
     * @param value Json object written to the output source (a string)
     * @return the string representation of the {@code JsonObject}
     * @throws IllegalArgumentException if the value is null
     */
    public String write(JsonObject value);

    /**
     * Writes the Json object to the output source (a writer).
     * <p>
     * Writes using UTF-8 encoding, according to SEDS protocol.
     *
     * @param value Json object written to the output source (a writer)
     * @param src a stream for which Json is to be written
     * @throws IllegalArgumentException if the value is null
     * @throws IllegalArgumentException if the source is null
     */
    public void write(JsonObject value, Writer src);

    /**
     * Writes the Json object to the output source (a file).
     * <p>
     * Writes using UTF-8 encoding, according to SEDS protocol.
     *
     * @param value Json object written to the output source (a file)
     * @param src a stream for which Json is to be written
     * @throws java.io.IOException if unable to find or write to the file
     * @throws IllegalArgumentException if the value is null
     * @throws IllegalArgumentException if the source is null
     */
    public void write(JsonObject value, File src) throws IOException;

    /**
     * Writes the Json object to the output source (a stream).
     *
     * @param value Json object written to the output source (a stream)
     * @param src a stream for which Json is to be written
     * @throws IllegalArgumentException if the value is null
     * @throws IllegalArgumentException if the source is null
     */
    public void write(JsonObject value, OutputStream src);
}
