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

package org.openepics.discs.conf.util;


/**
 * 
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * 
 */
public interface AppProperties {

    // Entity types for Audit records, authorization, etc
    public static final String EN_DEVICE = "device";
    public static final String EN_SLOT = "slot";
    public static final String EN_COMPTYPE = "comptype";
    public static final String EN_USER = "user";
    public static final String EN_INSTALLREC = "installrec";
    public static final String EN_ALIGNREC = "alignrec";

    // operations for authorization
    public static final char OPER_UPDATE = 'u';
    public static final char OPER_CREATE = 'c';
    public static final char OPER_DELETE = 'd';
    public static final char OPER_LOGIN = 'l';
    public static final char OPER_LOGOUT = 'o';

    String getProperty(String name);
}
