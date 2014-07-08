/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.UnitLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.Utility;
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


    private static final long serialVersionUID = 1L;
    @Inject private ConfigurationEJB configurationEJB;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @UnitLoaderQualifier private DataLoader unitsDataLoader;
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
                authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.UPDATE) ||
                authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.RENAME);
    }

    public String getImportFileName() { return importFileName; }

    public void setFilteredUnits(List<Unit> filteredUnits) {
        this.filteredUnits = filteredUnits;
    }

    public void importUnits() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        dataLoaderHandler.loadData(inputStream, unitsDataLoader);
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
