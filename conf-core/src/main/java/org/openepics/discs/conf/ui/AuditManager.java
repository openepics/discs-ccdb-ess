/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.EntityType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named(value = "auditManager")
@ViewScoped
public class AuditManager implements Serializable {
    private static final Logger logger = Logger.getLogger(AuditManager.class.getCanonicalName());

    @EJB private ConfigurationEJB configurationEJB;

    private List<AuditRecord> objects;
    private List<AuditRecord> filteredObjects;

    private List<AuditRecord> auditRecordsForEntity;
    private AuditRecord displayRecord;

    /**
     * Creates a new instance of AuditManager
     */
    public AuditManager() {
    }

    @PostConstruct
    public void init() {
        // TODO remove after new-webapp becomes the only user.
        objects = null;
    }

    // TODO remove after new-webapp becomes the only user.
    public List<AuditRecord> getObjects() {
        if (objects == null) objects = configurationEJB.findAuditRecords();
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

    /**
     * This method is called from xhtml to set the audit record for which the details will be shown in the dialog.
     * The audit record is selected by its database ID.
     * @param id - the database id of the audit log record
     */
    public void chooseDisplayRecord(final Long id) { this.displayRecord = configurationEJB.findAuditRecord(id); }

    /**
     * @return The audit record used in the <i>display details</i> dialog.
     */
    public AuditRecord getDisplayRecord() { return displayRecord; }

    /**
     * @return A pretty printed representation of the log entry JSON.
     */
    public String getDisplayRecordEntry() {
        if (displayRecord == null) return "";

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final Object json = mapper.readValue(displayRecord.getEntry(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * The method sets the audit log list for the selected entity. This method is called from the table button "i" in
     * the xhtml file.
     * @param selectedEntity - the entity to set the audit log list for.
     * @param entityType - the type of the entity. To set this parameter from xhtml, use a string representation of
     * the enumeration constant.
     */
    public void selectEntityForLog(final ConfigurationEntity selectedEntity, final EntityType entityType) {
        auditRecordsForEntity = configurationEJB.findAuditRecordsByEntityId(selectedEntity.getId(), entityType);
    }

    /**
     * @return A list of audit log entries for a selected entity to show in the table.
     */
    public List<AuditRecord> getAuditRecordsForEntity() { return auditRecordsForEntity; }


}
