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
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
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
    @Inject private ConfigurationEJB configurationEJB;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @UnitLoaderQualifier private DataLoader unitsDataLoader;

    private List<Unit> units;
    private List<Unit> filteredUnits;
    private DataLoaderResult loaderResult;

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

    public String getImportFileName() { return importFileName; }

    public void setFilteredUnits(List<Unit> filteredUnits) {
        this.filteredUnits = filteredUnits;
    }

    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, unitsDataLoader);
        return;
    }

    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    public DataLoaderResult getLoaderResult() { return loaderResult; }

    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


}
