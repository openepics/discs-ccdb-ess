/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class InstallationManager implements Serializable {
    @EJB
    private InstallationEJB installationEJB;

    @Inject
    private BlobStore blobStore;

    @Inject LoginManager loginManager;

    private static final Logger logger = Logger.getLogger(InstallationManager.class.getCanonicalName());

    private List<InstallationRecord> objects;
    private List<InstallationRecord> sortedObjects;
    private List<InstallationRecord> filteredObjects;
    private InstallationRecord selectedObject;
    private InstallationRecord inputObject;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    private List<InstallationArtifact> selectedArtifacts;
    private InstallationArtifact inputArtifact;
    private boolean internalArtifact = true;
    private InstallationArtifact selectedArtifact;
    private char artifactOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one
    private String uploadedFileName;
    private boolean fileUploaded = false;
    /**
     * Creates a new instance of InstallationManager
     */
    public InstallationManager() {
    }

    @PostConstruct
    public void init() {
        try {
            objects = installationEJB.findInstallationRec();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve installation records");
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in getting installation records", e.getMessage());
        }
    }

    // ----------------- Installation  ------------------------------
    public void onIRecSelect(SelectEvent event) {
        inputObject = selectedObject;

        // selectedProperties = selectedObject.getDevicePropertyList();
        selectedArtifacts = selectedObject.getInstallationArtifactList();

        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }

    public void onIRecAdd(ActionEvent event) {
        selectedOp = 'a';
        // TODO replaced void constructor (now protected) with default values. Check!
        inputObject = new InstallationRecord("1", new Date(), loginManager.getUserid());
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onIRecEdit(ActionEvent event) {
        selectedOp = 'e';
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onIRecDelete(ActionEvent event) {
        try {
            installationEJB.deleteIRecord(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        } finally {

        }
    }

    public void onIRecSave(ActionEvent event) {
        logger.info("Saving Installation Record");
        try {
            // inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            installationEJB.saveIRecord(inputObject, selectedOp == 'a');

            if (selectedOp == 'a') {
                selectedObject = inputObject;
                objects.add(selectedObject);
            }
            // tell the client if the operation was a success so that it can hide
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            // tell the client  if the operation was a success so that it can hide
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        } finally {
            selectedOp = 'n';
        }
    }

    // --------------------------------- Artifact ------------------------------------------------
    public void onArtifactAdd(ActionEvent event) {
        try {
            artifactOperation = 'a';
            if (selectedArtifacts == null) {
                selectedArtifacts = new ArrayList<>();
            }
            // TODO replaced void constructor (now protected) with default values. Check!
            inputArtifact = new InstallationArtifact("", false, "", "", loginManager.getUserid());
            inputArtifact.setInstallationRecord(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding artifact", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactSave(ActionEvent event) {
        try {
            if (artifactOperation == 'a') {
                inputArtifact.setInternal(internalArtifact);
                if (inputArtifact.isInternal()) { // internal artifact
                    if (!fileUploaded) {
                        Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "You must upload a file");
                        RequestContext.getCurrentInstance().addCallbackParam("success", false);
                        return;
                    }
                }
            }

            // deviceEJB.saveInstallationArtifact(selectedObject, inputArtifact);
            installationEJB.saveInstallationArtifact(inputArtifact, artifactOperation == 'a');
            logger.log(Level.INFO,"returned artifact id is " + inputArtifact.getId());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Artifact not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onArtifactDelete(InstallationArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }

            installationEJB.deleteInstallationArtifact(art);
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting artifact", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactEdit(InstallationArtifact art) {
        if (art == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
            return;
        }
        artifactOperation = 'e';
        inputArtifact = art;
        uploadedFileName = art.getName();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputArtifact.getId());
    }

    public void onArtifactType() {
        // TODO remove it
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact type selected","");
    }
    // -------------------------- File upload/download ---------------------------

    public void handleFileUpload(FileUploadEvent event) {
        // String msg = event.getFile().getFileName() + " is uploaded.";
        // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Succesful", msg);
        InputStream istream;

        try {
            UploadedFile uploadedFile = event.getFile();
            uploadedFileName = uploadedFile.getFileName();
            inputArtifact.setName(uploadedFileName);
            istream = uploadedFile.getInputstream();

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "File ", "Name: " + uploadedFileName);
            String fileId = blobStore.storeFile(istream);
            inputArtifact.setUri(fileId);

            istream.close();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "File uploaded", "Name: " + uploadedFileName);
            fileUploaded = true;
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Uploading file", e.getMessage());
            logger.severe(e.getMessage());
            fileUploaded = false;
        } finally {

        }
    }

    public StreamedContent getDownloadedFile() {
        StreamedContent file = null;

        try {
            // return downloadedFile;
            logger.log(Level.INFO, "Opening stream from repository: " + selectedArtifact.getUri());
            logger.log(Level.INFO, "download file name: 2 " + selectedArtifact.getName());
            InputStream istream = blobStore.retreiveFile(selectedArtifact.getUri());
            file = new DefaultStreamedContent(istream, "application/octet-stream", selectedArtifact.getName());

            // InputStream stream = new FileInputStream(pathName);
            // downloadedFile = new DefaultStreamedContent(stream, "application/octet-stream", "file.jpg"); //ToDo" replace with actual filename
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Downloading file", e.getMessage());
            logger.log(Level.SEVERE, "Error in downloading the file");
            logger.log(Level.SEVERE, e.toString());
        }

        return file;
    }

    // -------------------------- Getter/Setters -----------------------

    public List<InstallationRecord> getObjects() {
        return objects;
    }

    public List<InstallationRecord> getSortedObjects() {
        return sortedObjects;
    }

    public void setSortedObjects(List<InstallationRecord> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<InstallationRecord> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<InstallationRecord> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public InstallationRecord getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(InstallationRecord selectedObject) {
        this.selectedObject = selectedObject;
    }

    public InstallationRecord getInputObject() {
        return inputObject;
    }

    public void setInputObject(InstallationRecord inputObject) {
        this.inputObject = inputObject;
    }

    public InstallationArtifact getInputArtifact() {
        return inputArtifact;
    }

    public void setInputArtifact(InstallationArtifact inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public boolean isInternalArtifact() {
        return internalArtifact;
    }

    public void setInternalArtifact(boolean internalArtifact) {
        this.internalArtifact = internalArtifact;
    }

    public InstallationArtifact getSelectedArtifact() {
        return selectedArtifact;
    }

    public void setSelectedArtifact(InstallationArtifact selectedArtifact) {
        this.selectedArtifact = selectedArtifact;
    }

    public List<InstallationArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public char getArtifactOperation() {
        return artifactOperation;
    }

}
