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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author vuppala
 */
public abstract class DataLoader {

    protected static final Logger logger = Logger.getLogger("org.openepics.conf.dl");
    protected EntityManager entityManager;

    protected abstract void updateRecord(DSRecord record) throws Exception;
    
    protected String dataSource = ""; // source for the data
    private int invalidRecords = 0;
    private int totalRecords = 0, updatedRecords = 0;

    DataLoader(EntityManager em) {
        entityManager = em;
    }
    
    public int getInvalidRecords() {
        return invalidRecords;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getUpdatedRecords() {
        return updatedRecords;
    }

    public void incInvalidRecords() {
        ++invalidRecords;
    }

    public void load(String streamName, String subStreamName, String dataSource) throws Exception {
        DSRecord record;
        // String propertyId;
        RecordStream recordStream = new RecordStream();
        EntityTransaction transaction = entityManager.getTransaction();

        this.dataSource = dataSource;
        //try {
            recordStream.open(streamName, subStreamName);
            transaction.begin();
            while (recordStream.hasNextRecord()) {
                totalRecords++;
                record = recordStream.getNextRecord();
                if (record == null) {
                    logger.log(Level.WARNING, "Record should not be null but is");
                    continue;
                }
                switch (record.getCommand()) {
                    case 'c': //ignore comments                       
                        break;
                    case 'h': // header
                        break;
                    case 'e': // end of stream record.
                        break;
                    case 'u': // update record
                        updateRecord(record);
                        updatedRecords++;
                        break;
                    case 'd': // delete record
                        logger.log(Level.WARNING, "Delete command not yet implemented");
                        break;
                    default:
                        logger.log(Level.WARNING, "Invalid command: " + record.getCommand());
                        // throw new CDLException(CDLExceptionCode.INVALIDROW, "Invalid command");
                        break;
                }
            }
            transaction.commit();
       /* 
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println(e);
        } finally {           
            if (recordStream != null) {
                recordStream.close();
            }
        } 
        */
        System.out.format("Total number of Records: %d\nNumber of updated records: %d\nNumber of invalid Records: %d\n", totalRecords, updatedRecords, invalidRecords);
    }
}
