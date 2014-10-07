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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
public class Utility {
    private Utility() {}


    /**
     * Utility method used to display a message to the user
     *
     * @param severity Severity of the message
     * @param summary Summary of the message
     * @param message Detailed message contents
     */
    public static void showMessage(FacesMessage.Severity severity, String summary, String message) {
        final FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, summary, message));
    }

    /** The method determines whether the cause of the exception is a
     * {@link javax.persistence.PersistenceException} or not.
     *
     * @param t - the exception to inspect
     * @return <code>true</code> if the cause of the exception is javax.persistence.PersistenceException,
     * <code>false</code> otherwise.
     */
    public static boolean causedByPersistenceException(Throwable t) {
        if (t instanceof PersistenceException) {
            return true;
        } else if (t != null && t.getCause() != null) {
            return causedByPersistenceException(t.getCause());
        } else {
            return false;
        }
    }


}
