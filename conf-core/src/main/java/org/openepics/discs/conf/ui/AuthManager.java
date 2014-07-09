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
package org.openepics.discs.conf.ui;
import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.AuthEJB;

/**
 *
 * @author Vasu V <vuppala@frib.msu.org>
 */
// todo: the whole auth module needs to be improved.
@Named
@ViewScoped
public class AuthManager implements Serializable {

    @EJB
    private AuthEJB authEJB;
    private static final Logger logger = Logger.getLogger(AuthManager.class.getCanonicalName());
    private String userID;
    private String token;
    private boolean loggedIn = false;
    private boolean authorized = false;
    // private boolean editor = false;

    /**
     * Creates a new instance of UserManager
     */
    public AuthManager() {
    }

    @PostConstruct
    public void init() {
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if (principal == null) {
            userID = "";
            loggedIn = false;
            token = null;
            // editor = false;
            logger.info("AuthManager: Not logged in");
        } else {
            userID = principal.getName();
            token = userID;
            loggedIn = true;
            // Editor = configurationEJB.isEditor(User);
            // authorized = authEJB.userHasAuth(userID, "any"); // todo: need to improve
            logger.info("AuthManager: Logged in user is " + userID);
        }
    }



}
