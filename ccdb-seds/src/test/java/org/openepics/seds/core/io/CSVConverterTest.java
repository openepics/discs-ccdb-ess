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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SedsException;

/**
 *
 * @author Aaron Barber
 */
public class CSVConverterTest {

    private void doImport(String name) throws IOException, SedsException {
        //Message
        System.out.println("import_" + name);
        SedsTable data;

        //Files
        File input = FileUtil.get(FileUtil.CORE_IO, name, ".csv");

        //Reads
        try (BufferedReader in = new BufferedReader(new FileReader(input))) {
            data = Seds.newCSVConverter().importTable(in);
        }

        //JSON
        File expected = FileUtil.get(FileUtil.CORE_IO, name);
        File actual = FileUtil.get(FileUtil.CORE_IO, name + FileUtil.FAILED);
        actual.createNewFile();
        Seds.newWriter().write(Seds.newSerializer().serializeSEDS(data), actual);

        //Tests
        assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));

        //Cleans-up
        actual.delete();
    }

    private void doExport(String name, SedsTable data) throws IOException {
        //Message
        System.out.println("export_" + name);

        //Files
        File expected = FileUtil.get(FileUtil.CORE_IO, name, ".csv");
        File actual = FileUtil.get(FileUtil.CORE_IO, name + FileUtil.FAILED, ".csv");
        actual.createNewFile();

        //Writes
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(actual), StandardCharsets.UTF_8))) {

            Seds.newCSVConverter().exportTable(data, out);

            //Tests
            assertTrue(FileUtil.equalFileContent(actual, expected, StandardCharsets.UTF_8));
        }

        actual.delete();
    }

    @Test
    public void testImportTable1() throws Exception {
        doImport("table1");
    }

    @Test
    public void testImportTable2() throws Exception {
        doImport("table2");
    }

    @Test
    public void testImportTable3() throws Exception {
        doImport("table3");
    }

    @Test
    public void testExportTable1() throws Exception {
        doExport("table1", Seds.newSimpleFactory().newTable(
                new String[]{"A", "B", "C"},
                new String[]{"unitA", "unitB", "unitC"},
                new Object[][]{
                    new Double[]{1d, 2d, 3d},
                    new Double[]{10d, 20d, 30d},
                    new String[]{"a", "b", "c"}
                }
        ));
    }

    @Test
    public void testExportTable2() throws Exception {
        doExport("table2", Seds.newSimpleFactory().newTable(
                new String[]{"A", "B", "C"},
                new String[]{"unitA", "", "unitC"},
                new Object[][]{
                    new Boolean[]{true, false, true, false},
                    new Double[]{1.0, 1.1, 1.2, 1.3},
                    new String[]{"a", "b", "c", "d"}
                }
        ));
    }

    @Test
    public void testExportTable3() throws Exception {
        doExport("table3", Seds.newSimpleFactory().newTable(
                new String[]{"distance", "time", "shape"},
                new String[]{"meters", "seconds", ""},
                new Object[][]{
                    new Double[]{10.0, 20.0, 30.0, 100.0},
                    new Double[]{5.0, 5.0, 10.0, 10.0},
                    new String[]{"flat", "round", "flat", "round"}
                }
        ));
    }
}
