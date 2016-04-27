/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.ccdb.gui.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

/**
 * State for the login dialog
 *
 * @author <a href="mailto:vuppala@frib.msu.org">Vasu V</a>
 */
@Named
@SessionScoped
public class LoginManager implements Serializable {

    
    private static final Logger logger = Logger.getLogger(LoginManager.class.getName());
    private String userid;
    private char[] password;  // better to use char array than string for passwords
    private boolean loggedin = false;

    public LoginManager() {
    }

    @PostConstruct
    public void init() {
        // FacesContext context = FacesContext.getCurrentInstance();
        // originalURL = (String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);
        // logger.log(Level.FINE, "Forwarded from: " + originalURL);
    }

    /**
     * Things to do on a user login
     *
     * @return next view
     * @throws IOException if problem in authentication
     */
    public String onLogin() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String nextView = "";

        try {
            if (request.getUserPrincipal() == null) {
                request.login(userid, String.valueOf(password));

                loggedin = true;         
                logger.log(Level.FINE, "Login: {0}", userid);               
                
                showMessage(FacesMessage.SEVERITY_INFO, "Welcome " + userid, "");
                // RequestContext.getCurrentInstance().update("@(.ui-panel)");
                RequestContext.getCurrentInstance().addCallbackParam("loginSuccess", true); // To inform the login form
                nextView = context.getViewRoot().getViewId() + "?faces-redirect=true"; // redirect to the current page
                context.getExternalContext().getFlash().setKeepMessages(true); // Keep messages in Flash
            } else {
                showMessage(FacesMessage.SEVERITY_INFO, "Strange, you are already logged in!", userid);
                logger.log(Level.FINE, "Strange, you are already logged in: {0}", userid);
            }
        } catch (ServletException e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "That did not work. Please try again.", "Probably your password was wrong?");
            RequestContext.getCurrentInstance().addCallbackParam("loginSuccess", false); // For  view
            logger.log(Level.FINE, "Authentication failed for {0}", userid);
            loggedin = false;
        } catch (Exception e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Cannot set user session! ", e.getMessage());
            logger.log(Level.SEVERE, "Cannot set user session for {0}", userid);
            // logger.log(Level.FINE, "LoginManager error: {0}", e);
            System.out.print(e);
        } finally {
            if (password != null) {
                for (int i = 0; i < password.length; i++) {
                    password[i] = 'X'; // Erase password         
                }
            }
        }

        return nextView;
    }

    /**
     * Things to do on a logout
     *
     * @return view id for the next view
     */
    public String onLogout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String nextView = "logout?faces-redirect=true";

        try {
            request.logout();
            logger.log(Level.FINE, "Logout: {0}", userid);
            loggedin = false;
            userid = null;
            showMessage(FacesMessage.SEVERITY_INFO, "You have been logged out.", "Have a nice day!");
            nextView = context.getViewRoot().getViewId() + "?faces-redirect=true"; // redirect to the current page
            context.getExternalContext().getFlash().setKeepMessages(true); // Keep messages in Flash
            context.getExternalContext().invalidateSession(); // invalidate session
        } catch (Exception e) {
            showMessage(FacesMessage.SEVERITY_ERROR, "Strangely, logout has failed", "That's odd!");
            logger.log(Level.SEVERE, "Strangely, logout has failed: {0}", userid);
            System.out.print(e);
        } finally {

        }

        return nextView;
    }

    // -- getters and setters
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    private void showMessage(FacesMessage.Severity severity, String summary, String message) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(severity, summary, message));
    }
}
