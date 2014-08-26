/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 * Copyright (c) 2041 FRIB
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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

    private AuditRecord displayRecord;

    /**
     * Creates a new instance of AuditManager
     */
    public AuditManager() {
    }

    @PostConstruct
    public void init() {
        // TODO remove after new-webapp becomes the only user.
        try {
            objects = configurationEJB.findAuditRecords();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve audit records");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting audit records", " ");
        }
    }

    // TODO remove after new-webapp becomes the only user.
    public List<AuditRecord> getObjects() {
        return objects;
    }

    // TODO remove after new-webapp becomes the only user.
    public List<AuditRecord> getFilteredObjects() {
        return filteredObjects;
    }

    // TODO remove after new-webapp becomes the only user.
    public void setFilteredObjects(List<AuditRecord> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public void chooseDisplayRecord(Long id) { this.displayRecord = configurationEJB.findAuditRecord(id); }

    public AuditRecord getDisplayRecord() { return displayRecord; }

    public String getDisplayRecordEntry() {
        if (displayRecord == null) return "";

        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(displayRecord.getEntry(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            return "";
        }
    }

}
