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
package org.openepics.seds.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;

/**
 * Compares file contents.
 *
 * @author asbarber
 */
public class FileUtil {

    //PATHS
    //--------------------------------------------------------------------------
    public static final String RESOURCES = "src/test/resources/";

    public static final String ALPHA = "org/openepics/seds/alpha/";
    public static final String API = "org/openepics/seds/api/";
    public static final String CORE = "org/openepics/seds/core/";
    public static final String CORE_SEDS = "org/openepics/seds/core/seds/";
    public static final String CORE_VTYPE = "org/openepics/seds/core/vtype/";
    public static final String CORE_IO = "org/openepics/seds/core/io/";

    public static final String EXT = ".json";
    public static final String FAILED = ".failed";
    //--------------------------------------------------------------------------

    //File Finding
    //--------------------------------------------------------------------------
    public static final File get(String resourcePath, String name) {
        return new File(RESOURCES + resourcePath + name + EXT);
    }

    public static final File get(String resourcePath, String name, String ext) {
        return new File(RESOURCES + resourcePath + name + ext);
    }

    public static final File[] get(String resourcePath, String name, String[] ends) {
        File[] list = new File[ends.length];

        for (int i = 0; i < list.length; ++i) {
            list[i] = get(resourcePath, name + ends[i]);
        }

        return list;
    }

    public static final File get(String name) {
        return get("", name);
    }

    public static final String pathOf(String resourcePath, String name) {
        return RESOURCES + resourcePath + name + EXT;
    }

    public static final String pathOf(String name) {
        return pathOf("", name);
    }
    //--------------------------------------------------------------------------

    //Read / Write
    //--------------------------------------------------------------------------
    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void writeFile(String content, File file, Charset encoding)
            throws IOException {
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), encoding
                )
        )) {
            out.write(content);
        }
    }
    //--------------------------------------------------------------------------

    //Content Comparison
    //--------------------------------------------------------------------------
    public static void compareFileContent(String content, String name, Charset encoding)
            throws IOException {
        boolean done = false;
        try {
            String expected = readFile(pathOf(name), encoding);

            assertEquals("Contents are not the same", content, expected);

            done = true;
        } finally {
            if (!done) {
                writeFile(
                        content,
                        get(name + FAILED),
                        encoding
                );
            } else {
                File file = get(name + FAILED);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public static boolean equalFileContent(File a, File b, Charset encoding)
            throws IOException {
        return readFile(a.getAbsolutePath(), encoding)
                .equals(readFile(b.getAbsolutePath(), encoding));
    }
    //--------------------------------------------------------------------------

}
