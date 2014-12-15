/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 * @author Vasu V <vuppala@frib.msu.org>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Named
@SessionScoped
public class LoginManager implements Serializable {
    private static final Logger logger = Logger.getLogger(LoginManager.class.getCanonicalName());

    private String userId;
    private String password;

    private boolean loggedIn = false;

    @Inject private SecurityPolicy securityPolicy;


    public String onLogin() {
        try {
            securityPolicy.login(userId, password);
            showMessage(FacesMessage.SEVERITY_INFO, "You are logged in. Welcome!", userId);
            loggedIn = true;
        } catch (Exception e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Login Failed! Please try again. ", "Status: ");
            logger.log(Level.INFO, "Login failed for " + userId);
            loggedIn = false;
        } finally {
            password = "xxxxxx"; // ToDo implement a better way destroy the password (from JVM)
        }
        return null;
    }

    public String onLogout() {
        try {
            securityPolicy.logout();
            loggedIn = false;
            userId = null;
            final ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath());
            showMessage(FacesMessage.SEVERITY_INFO, "You have been logged out.", "Thank you!");
        } catch (Exception e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Strangely, logout has failed", "That's odd!");
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

    private void showMessage(FacesMessage.Severity severity, String summary, String message) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(severity, summary, message));
    }
}
