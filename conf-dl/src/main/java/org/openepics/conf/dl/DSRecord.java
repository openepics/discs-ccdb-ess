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

import java.util.*;

/**
 * Record format:
 *     Command  field1 field2 .....
 * where 'Command' can be: HEADER, UPDATE, DELETE, END etc
 * 
 * @author vuppala
 */
public class DSRecord {
    private Map<String,String> fieldMap; // field-name, value
    private char command = 'n'; // command associated with the record. n - none, h - HEADER, u - UPDATE, d - DELETE, e - END
    
    public DSRecord() {
        fieldMap = new HashMap<String,String>();
    }
    
    public Map<String,String> getMap() {
        return fieldMap;
    }
    
    public void setEntry(String name, String value) {
        fieldMap.put(name, value);
    }
    
    public String getField(String fieldName) throws Exception {
        String value=fieldMap.get(fieldName);
        if ( value == null ) {
            throw new CDLException(CDLExceptionCode.IFNAME, "Could not get field: " + fieldName);
        }
        
        return value;
    }

    public void clear() {
        fieldMap.clear();
    }
    
    public char getCommand() {
        return command;
    }

    public void setCommand(char command) {
        this.command = command;
    }
}
