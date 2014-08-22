/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.util.Utility;

/**
 *
 * @author vuppala
 */
@Named(value = "auditManager")
@ViewScoped
public class AuditManager implements Serializable {
    private static final Logger logger = Logger.getLogger(AuditManager.class.getCanonicalName());

    @EJB private ConfigurationEJB configurationEJB;

    private List<AuditRecord> objects;
    private List<AuditRecord> filteredObjects;

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
