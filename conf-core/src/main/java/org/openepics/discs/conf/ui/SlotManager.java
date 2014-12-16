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
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class SlotManager implements Serializable {
    private static final Logger logger = Logger.getLogger(SlotManager.class.getCanonicalName());

    @EJB private SlotEJB slotEJB;
    @EJB private SlotPairEJB slotPairEJB;

    @Inject private DataLoaderHandler dataLoaderHandler;

    private byte[] importSlotData, importSlotRelationshipsData;
    private String firstFileName, secondFileName;
    private DataLoaderResult loaderResult;

    /**
     * Creates a new instance of SlotManager
     */
    public SlotManager() {
    }

    public String getFirstFileName() {
        return firstFileName;
    }
    public String getSecondFileName() {
        return secondFileName;
    }

    public void doImport() {
        loaderResult = dataLoaderHandler.loadDataFromTwoFiles(importSlotData != null ? new ByteArrayInputStream(importSlotData) : null, importSlotRelationshipsData != null ? new ByteArrayInputStream(importSlotRelationshipsData) : null, firstFileName, secondFileName);
    }

    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    public void prepareImportPopup() {
        importSlotData = null;
        firstFileName = null;
        importSlotRelationshipsData = null;
        secondFileName = null;
    }

    public void handleFirstImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importSlotData = ByteStreams.toByteArray(inputStream);
            this.firstFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void handleSecondImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importSlotRelationshipsData= ByteStreams.toByteArray(inputStream);
            this.secondFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
