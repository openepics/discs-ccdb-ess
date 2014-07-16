/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 *
 * @author Vasu V <vuppala@frib.msu.org>
 */
public class testConfigLoader {
 /*
    public static void mainTestRS(String[] args) {
        if (args.length != 1) {
            System.err.println("You must specify one input file on the command line.");
            System.exit(1);
        }

        try {
            fh = new FileHandler("cdl-log.txt");
            fh.setFormatter(formatter);
            logger.addHandler(fh);
            logger.setLevel(Level.WARNING);
            RecordStream recordStream = new RecordStream();
            // recordStream.open(args[0], LCSTREAM);
            DSRecord record;
            String compName;
            int totalRecords = 0, namedRecords = 0, nullRecords = 0;

            while (recordStream.hasNextRecord()) {
                totalRecords++;
                record = recordStream.getNextRecord();
                if (record == null) {
                    logger.log(Level.WARNING, "Record should not be null but is");
                    nullRecords++;
                    continue;
                }
                compName = record.getField("NAME");
                System.out.println("Comp Name:" + compName + ":");
                if (!compName.trim().isEmpty()) {
                    namedRecords++;
                    System.out.println("-------------- Update Start:" + compName + "------");
                    //printComponent(record);
                    System.out.println("-------------- Update End   ------");
                }
            }
            System.out.format("Total number of Records: %d\nNumber of Named Records: %d\nNumber of Null Records: %d\n", totalRecords, namedRecords, nullRecords);
            recordStream.close();
        } catch (Exception e) {
            //System.out.println(e);
            logger.log(Level.SEVERE, "Cannot load data", e);
        }

    }

    private void TestDSE(String[] args) {
        if (args.length != 1) {
            System.err.println("You must specify one input file on the command line.");
            System.exit(1);
        }

        try {
            fh = new FileHandler("cdl-log.txt");
            fh.setFormatter(formatter);
            logger.addHandler(fh);
            logger.setLevel(Level.WARNING);

            DataStream dataStream = new DataStreamExcel();
            // dataStream.open(args[0], LCSTREAM);

            String compName = "";
            int totalRecords = 0, namedRecords = 0, nullRecords = 0;

            while (!compName.equals("DATA-END")) {
                totalRecords++;
                dataStream.nextRow();
                compName = dataStream.getColumn(0);
                System.out.println("Comp Name:" + compName + ":");
                if (!compName.trim().isEmpty()) {
                    namedRecords++;
                }
            }
            System.out.format("Total number of Records: %d\nNumber of Named Records: %d\nNumber of Null Records: %d\n", totalRecords, namedRecords, nullRecords);
            dataStream.close();
        } catch (Exception e) {
            //System.out.println(e);
            logger.log(Level.SEVERE, "Cannot load data", e);
        }
   */
}
