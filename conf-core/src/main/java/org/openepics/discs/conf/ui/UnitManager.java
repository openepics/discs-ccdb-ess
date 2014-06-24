/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.util.IllegalImportFileFormatException;
import org.openepics.discs.conf.util.NotAuthorizedException;
import org.openepics.discs.conf.util.Utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.dl.UnitsDataLoader;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 */
@Named
@ManagedBean
@ViewScoped
public class UnitManager implements Serializable {

    @EJB
    private ConfigurationEJB configurationEJB;
    @Inject private UnitsDataLoader unitDataLoader;
    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    
    private List<Unit> units;
    private List<Unit> filteredUnits;
    
    private byte[] importData;
    private String importFileName;
    
    /**
     * Creates a new instance of UnitManager
     */
    public UnitManager() {
    }
    
    @PostConstruct
    public void init() {
        try {
            units = configurationEJB.findUnits();           
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting units", " ");
        }
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Unit> getFilteredUnits() {
        return filteredUnits;
    }
    
    public boolean canImportUnits() {
        return authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.CREATE) || 
                authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.DELETE) ||
                authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.UPDATE);
    }
    
    public String getImportFileName() { return importFileName; }

    public void setFilteredUnits(List<Unit> filteredUnits) {
        this.filteredUnits = filteredUnits;
    }
    
    public void importUnits() {      
        InputStream inputStream = new ByteArrayInputStream(importData);
        try {
            unitDataLoader.loadData(inputStream);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Import Success", "Units were successfully imported");
        } catch (IllegalImportFileFormatException | NotAuthorizedException e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Import Fail", e.getMessage());
        }         
    }
    
    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();           
        }
    }
    
    
}
