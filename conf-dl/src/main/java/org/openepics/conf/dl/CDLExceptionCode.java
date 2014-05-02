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

/**
 *
 * @author vuppala
 */
public enum CDLExceptionCode {
    HEADERNF("Header Not Found."),
    HEADERMF("Malformed Header."),
    RECSNF("No records found."),
    NMROWS("No more rows. End of file reached."),
    INVALIDROW("Invalid (null) row."),
    NONAME("Component name is empty or invalid"),
    IFNAME("Invalid field name."),
    IHEADER("Invalid Header"),
    EOSTREAM("End of Stream"),
    INVALIDFLD("Invalid value in field"),
    OSTREAM("Cannot open data stream.");
    
    private final String description;
    
    private CDLExceptionCode(String msg) {
        this.description = msg;
    }
    
    @Override
    public String toString() {
        return description;
    }
}