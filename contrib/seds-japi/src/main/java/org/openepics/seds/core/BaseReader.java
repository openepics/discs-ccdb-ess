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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import org.openepics.seds.api.SedsReader;
import static org.openepics.seds.util.SedsException.assertNotNull;

class BaseReader implements SedsReader {

    BaseReader() {

    }

    @Override
    public JsonObject read(String src) {
        assertNotNull(src, String.class, "Json text for the reader");
        return read(new StringReader(src));
    }

    @Override
    public JsonObject read(Reader src) {
        assertNotNull(src, Reader.class, "Reader of the json text");
        return Json.createReader(src).readObject();
    }

    @Override
    public JsonObject read(File src) throws IOException {
        assertNotNull(src, File.class, "File of the json text");
        try (Reader r = new FileReader(src)) {
            return read(r);
        }
    }

    @Override
    public JsonObject read(InputStream src) {
        assertNotNull(src, InputStream.class, "Stream of the json text");
        return Json.createReader(src).readObject();
    }

}
