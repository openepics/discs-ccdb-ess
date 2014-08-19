/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Vasu V <vuppala@frib.msu.org>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Named
@SessionScoped
public class LoginManager implements Serializable {
    private static final Logger logger = Logger.getLogger(LoginManager.class.getCanonicalName());

    private String userid;
    private String password;
    
    private boolean loggedin = false;
    

    public String onLogin() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            if (request.getUserPrincipal() == null) {
                request.login(userid, password);

                loggedin = true;
                logger.log(Level.INFO, "Login successful for " + userid);
                showMessage(FacesMessage.SEVERITY_INFO, "You are logged in. Welcome!", userid);
            } else {
                showMessage(FacesMessage.SEVERITY_INFO, "You are already logged in!", userid);
            }
        } catch (ServletException e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Login Failed! Please try again. ", "Status: ");
            logger.log(Level.INFO, "Login failed for " + userid);
            loggedin = false;
        } finally {
            password = "xxxxxx"; // ToDo implement a better way destroy the password (from JVM)
        }
        return null;
    }

    public String onLogout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
            loggedin = false;
            userid = null;
            showMessage(FacesMessage.SEVERITY_INFO, "You have been logged out.", "Thank you!");
        } catch (Exception e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Strangely, logout has failed", "That's odd!");
        } finally {

        }

        return "logout"; // ToDo: replace with null
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    private void showMessage(FacesMessage.Severity severity, String summary, String message) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(severity, summary, message));
        // FacesMessage n = new FacesMessage();
    }
}
