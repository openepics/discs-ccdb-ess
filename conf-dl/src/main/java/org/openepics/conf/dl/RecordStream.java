/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 * 
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 * 
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 * 
 */

package org.openepics.conf.dl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vuppala
 */
public class RecordStream {
    boolean endOfStream;
    private DataStream dataStream;
    private DSHeader header = null;
    private DSRecord currentRecord = null;
    
    private static final Logger logger = Logger.getLogger("org.openepics.conf.dl");
    private static final String HEADER_CMD = "HEADER";
    private static final String UPDATE_CMD = "UPDATE";
    private static final String DELETE_CMD = "DELETE";
    private static final String END_CMD = "END";

    public void open(String streamName, String subStreamName) throws Exception {
        dataStream = new DataStreamExcel();
        dataStream.open(streamName, subStreamName);
        currentRecord = new DSRecord();
        endOfStream = false;
        header = new DSHeader();
    }

    private void setHeader() throws Exception {
        int colCount = dataStream.getRowSize();
        String colName;

        header.clear(); // clear old header, if any         
        for (int c = 1; c < colCount; c++) {
            colName = dataStream.getColumn(c);
            if (colName != null && !colName.trim().isEmpty()) {
                header.setEntry(colName, c);
                logger.log(Level.FINER, "Added a column header " + colName);
            }
        }
    }

    private void readRecord() throws Exception {
        if ( currentRecord.getCommand() == 'c' ) return; // ignore comments

        if (header.get().isEmpty()) {
            logger.log(Level.WARNING, "Header not set. Ignoring record.");
            throw new CDLException(CDLExceptionCode.HEADERNF, "Cannot read record without header");
        }

        currentRecord.clear();
        for (Map.Entry<String, Integer> e : header.get().entrySet()) {
            String val = dataStream.getColumn(e.getValue());
            if (val != null) {
                currentRecord.setEntry(e.getKey(), val);
                logger.finer(e.getKey() + ":" + val + ":");
            }
        }
    }

    private boolean readNextRecord() throws Exception {
        if (dataStream.endOfStream()) {
            endOfStream = true;
            return false;
        }

        dataStream.nextRow();
        String command = dataStream.getColumn(0);

        logger.log(Level.FINER, "Input command: " + command);
        if (command.equals(HEADER_CMD)) { // check if it is a control record i.e. starts with HEADER, UPDATE, END etc
            currentRecord.setCommand('h');
            setHeader();
        } else if (command.equals(DELETE_CMD)) {
            currentRecord.setCommand('d');
        } else if (command.equals(UPDATE_CMD)) {
            currentRecord.setCommand('u');
        } else if (command.equals(END_CMD)) {
            currentRecord.setCommand('e');
            endOfStream = true;
        } else if ( command.trim().isEmpty() ) {
            currentRecord.setCommand('c');
        } else {
            currentRecord.setCommand('x');
        }

        readRecord();

        return true;
    }

    public DSRecord getNextRecord() throws Exception {
        if (endOfStream) {
            return null;
        } else {
            readNextRecord();
            return currentRecord;
        }
    }

    public boolean hasNextRecord() throws Exception {
        return !endOfStream;
    }

    public DSRecord getCurrentRecord() {
        return currentRecord;
    }

    public void close() throws Exception {
        if (dataStream != null) {
            dataStream.close();
        }
        header = null;
        currentRecord = null;
    }
}
