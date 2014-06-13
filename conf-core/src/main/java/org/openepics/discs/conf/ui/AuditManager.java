/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import org.openepics.discs.conf.util.Utility;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

import org.openepics.discs.conf.ejb.ConfigurationEJBLocal;
import org.openepics.discs.conf.ent.AuditRecord;

/**
 *
 * @author vuppala
 */
@Named(value = "auditManager")
@ViewScoped
public class AuditManager implements Serializable {
    @EJB
    private ConfigurationEJBLocal configurationEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    private List<AuditRecord> objects;
    private List<AuditRecord> sortedObjects;
    private List<AuditRecord> filteredObjects;
    private AuditRecord selectedObject;
    /**
     * Creates a new instance of AuditManager
     */
    public AuditManager() {
    }
    
    @PostConstruct
    public void init() {
        try {
            objects = configurationEJB.findAuditRecord();          
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve audit records");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting audit records", " ");
        }
    }

    public List<AuditRecord> getObjects() {
        return objects;
    }

    public List<AuditRecord> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<AuditRecord> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }
      
}
