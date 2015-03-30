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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import org.openepics.seds.api.SedsWriter;
import static org.openepics.seds.util.SedsException.assertNotNull;

/**
 *
 * @author Aaron Barber
 */
class BaseWriter implements SedsWriter {

    BaseWriter() {

    }

    @Override
    public String write(JsonObject value) {
        assertNotNull(value, JsonObject.class, "Json object for the writer");
        StringWriter wr = new StringWriter();
        write(value, wr);
        return wr.toString();
    }

    @Override
    public void write(JsonObject value, Writer stream) {
        assertNotNull(value, JsonObject.class, "Json object for the writer");
        assertNotNull(stream, Writer.class, "Writer with the json data");

        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);

        Json
                .createWriterFactory(config)
                .createWriter(stream)
                .writeObject(value);
    }

    @Override
    public void write(JsonObject value, File json) throws IOException {
        assertNotNull(value, JsonObject.class, "Json object for the writer");
        assertNotNull(json, File.class, "File with the json data");

        try (Writer w = new FileWriter(json)) {
            write(value, w);
        }
    }

    @Override
    public void write(JsonObject value, OutputStream stream) {
        assertNotNull(value, JsonObject.class, "Json object for the writer");
        assertNotNull(stream, OutputStream.class, "Output stream with the json data");

        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);

        Json
                .createWriterFactory(config)
                .createWriter(stream, StandardCharsets.UTF_8)
                .writeObject(value);
    }

}
