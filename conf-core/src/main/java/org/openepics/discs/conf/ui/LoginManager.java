/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.security.SecurityPolicy;
import org.openepics.discs.conf.util.Utility;

/**
 * @author <a href="mailto:vuppala@frib.msu.org">Vasu V</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Named
@SessionScoped
public class LoginManager implements Serializable {
    private static final long serialVersionUID = -3247078884319476134L;

    private static final Logger LOGGER = Logger.getLogger(LoginManager.class.getCanonicalName());

    private String userId;
    private String password;

    private boolean loggedIn = false;

    @Inject private SecurityPolicy securityPolicy;


    /** Called when the user clicks on the "Login" button in the UI.
     * @return <code>null</code>
     */
    public String onLogin() {
        try {
            securityPolicy.login(userId, password);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "You are logged in. Welcome!", userId);
            loggedIn = true;
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Login Failed! Please try again. ", "Status: ");
            LOGGER.log(Level.INFO, "Login failed for " + userId);
            LOGGER.log(Level.FINE, "Login failed for " + userId, e);
            loggedIn = false;
        } finally {
            password = "xxxxxx"; // ToDo implement a better way destroy the password (from JVM)
        }
        return null;
    }

    /** Called when the user clicks on the "Login" button in the UI.
     * @return "logout"
     */
    public String onLogout() {
        try {
            securityPolicy.logout();
            loggedIn = false;
            userId = null;
            final ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "You have been logged out.", "Thank you!");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Strangely, logout has failed", "That's odd!");
            LOGGER.log(Level.FINE,  "Strangely, logout has failed", e);
        }

        return "logout"; // ToDo: replace with null
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
