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
package org.openepics.seds.core.io;

import org.openepics.seds.api.io.CSVConverter;
import org.openepics.seds.api.io.DBConverter;
import org.openepics.seds.core.JsonMapper;
import org.openepics.seds.core.Seds;

/**
 * Factory class for creating SEDS processing objects related to input/output
 * (IO). This class provides the most commonly used methods for creating these
 * processing objects.
 *
 * <pre>
 * <code>
 * File file = ...
 * SedsType sedsData = Seds.newFactory().newEnum("A", new String[]{"A", "B"});
 * JsonObject jsonData = Seds.newDBConverter().serialize(sedsData);
 * Seds.newWriter().write(jsonData, file);
 * </code>
 * </pre>
 *
 * @author Aaron Barber
 */
public class SedsIO {

    /**
     * Builds a converter to convert between SEDS types and CSV.
     *
     * <p>
     * CSV (comma-separated value) uses the comma as the default separator.
     *
     * <p>
     * The converter provides conversions for only the most common SEDS types.
     *
     * @return converter to convert between common SEDS types and CSV
     */
    public static CSVConverter newCSVConverter() {
        return new BaseCSVConverter(",");
    }

    /**
     * Builds a converter to convert between SEDS types and JSON for a database.
     *
     * <p>
     * The conversion is tailored for storing SEDS types in a database by
     * separating metadata into a separate portion of the JSON object that can
     * be stored separately.
     *
     * @return converter to convert between SEDS types and JSON for a database
     */
    public static DBConverter newDBConverter() {
        return new BaseDBConverter(Seds.newFactory(), new JsonMapper(Seds.newFactory()));
    }
}
