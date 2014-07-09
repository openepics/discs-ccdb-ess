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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.AlignmentEJB;
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentProperty;
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
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
public class AlignmentManager implements Serializable{

    @EJB
    private AlignmentEJB alignmentEJB;
    private static final Logger logger = Logger.getLogger(AlignmentManager.class.getCanonicalName());
    @Inject
    private BlobStore blobStore;
    @Inject private LoginManager loginManager;

    private List<AlignmentRecord> objects;
    private List<AlignmentRecord> sortedObjects;
    private List<AlignmentRecord> filteredObjects;
    private AlignmentRecord selectedObject;
    private AlignmentRecord inputObject;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    // Property
    private List<AlignmentProperty> selectedProperties;
    private AlignmentProperty selectedProperty;
    private AlignmentProperty inputProperty;
    private boolean inRepository = false;
    private char propertyOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // Artifacts
    private List<AlignmentArtifact> selectedArtifacts;
    private AlignmentArtifact inputArtifact;
    private boolean internalArtifact = true;
    private AlignmentArtifact selectedArtifact;
    private char artifactOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // File upload/download
    private String uploadedFileName;
    private boolean fileUploaded = false;
    private String repoFileId; // identifier of the file stored in content repo

    /**
     * Creates a new instance of AlignmentManager
     */
    public AlignmentManager() {
    }

    @PostConstruct
    public void init() {
        try {
            objects = alignmentEJB.findAlignmentRec();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve alignment records");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting alignment records", " ");
        }
    }

    // ----------------- Alignment Record  ------------------------------
    public void onAlignRecSelect(SelectEvent event) {
        inputObject = selectedObject;

        selectedProperties = selectedObject.getAlignmentPropertyList();
        selectedArtifacts = selectedObject.getAlignmentArtifactList();

        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }

    public void onAlignRecAdd(ActionEvent event) {
        selectedOp = 'a';
        // TODO replaced void constructor (now protected) with default values. Check.
        inputObject = new AlignmentRecord(UUID.randomUUID().toString(), new Date(), loginManager.getUserid());
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onAlignRecEdit(ActionEvent event) {
        selectedOp = 'e';
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onAlignRecDelete(ActionEvent event) {
        try {
            alignmentEJB.deleteAlignment(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        } finally {

        }
    }

    public void onAlignRecSave(ActionEvent event) {
        logger.info("Saving slot");
        try {
            // inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            alignmentEJB.saveAlignment(inputObject);

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

    // --------------------------------- Property ------------------------------------------------
    public void onPropertyAdd(ActionEvent event) {
        try {
            if (selectedProperties == null) {
                selectedProperties = new ArrayList<>();
            }
            propertyOperation = 'a';

            // TODO replaced void constructor (now protected) with default values. Check.
            inputProperty = new AlignmentProperty(false, loginManager.getUserid());
            inputProperty.setAlignmentRecord(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding property", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(AlignmentProperty prop) {
        try {
            if (prop == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            alignmentEJB.deleteAlignmentProp(prop);
            selectedProperties.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting property", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyInit(RowEditEvent event) {
        // ComponentTypeProperty prop = (ComponentTypeProperty) event.getObject();

        try {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Property Edit", "");

        } catch (Exception e) {
            // selectedProperties.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Property not saved", e.getMessage());
        }
    }

    public void onPropertyEdit(AlignmentProperty prop) {
        try {
            if (prop == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            artifactOperation = 'e';
            inputProperty = prop;
            uploadedFileName = prop.getProperty().getName();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputProperty.getAlignPropId());
        } catch (Exception e) {
            // selectedCompProps.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Property can not be edited", e.getMessage());
        }
    }

    public void onPropertySave(ActionEvent event) {
        try {
            inputProperty.setInRepository(inRepository);
            if (inRepository) { // internal artifact
                if (!fileUploaded) {
                    Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "You must upload a file");
                    RequestContext.getCurrentInstance().addCallbackParam("success", false);
                    return;
                }
                inputProperty.setPropValue(repoFileId);
            }
            alignmentEJB.saveAlignmentProp(inputProperty, propertyOperation == 'a');
            logger.log(Level.INFO, "returned artifact id is " + inputProperty.getAlignPropId());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Property saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Property not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    // -------------------------- File upload/download Property ---------------------------
    // todo: merge with artifact file ops. finally put in blobStore
    public void handlePropertyUpload(FileUploadEvent event) {
        // String msg = event.getFile().getFileName() + " is uploaded.";
        // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Succesful", msg);
        InputStream istream;

        try {
            UploadedFile uploadedFile = event.getFile();
            uploadedFileName = uploadedFile.getFileName();
            // inputArtifact.setName(uploadedFileName);
            istream = uploadedFile.getInputstream();

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "File ", "Name: " + uploadedFileName);
            repoFileId = blobStore.storeFile(istream);
            // inputArtifact.setUri(fileId);

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

    public StreamedContent getDownloadedPropertyFile() {
        StreamedContent file = null;

        try {
            // return downloadedFile;
            logger.log(Level.INFO, "Opening stream from repository: " + selectedProperty.getPropValue());
            // logger.log(Level.INFO, "download file name: 2 " + selectedProperty.getName());
            InputStream istream = blobStore.retreiveFile(selectedProperty.getPropValue());
            file = new DefaultStreamedContent(istream, "application/octet-stream", selectedProperty.getProperty().getName());

            // InputStream stream = new FileInputStream(pathName);
            // downloadedFile = new DefaultStreamedContent(stream, "application/octet-stream", "file.jpg"); //ToDo" replace with actual filename
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Downloading file", e.getMessage());
            logger.log(Level.SEVERE, "Error in downloading the file");
            logger.log(Level.SEVERE, e.toString());
        }

        return file;
    }
// --------------------------------- Artifact ------------------------------------------------
    public void onArtifactAdd(ActionEvent event) {
        try {
            artifactOperation = 'a';
            if (selectedArtifacts == null) {
                selectedArtifacts = new ArrayList<>();
            }
            // TODO replaced void constructor (now protected) with default values. Check.
            inputArtifact = new AlignmentArtifact("", false, "", loginManager.getUserid());
            inputArtifact.setAlignmentRecord(selectedObject);
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
                inputArtifact.setIsInternal(internalArtifact);
                if (inputArtifact.getIsInternal()) { // internal artifact
                    if (!fileUploaded) {
                        Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "You must upload a file");
                        RequestContext.getCurrentInstance().addCallbackParam("success", false);
                        return;
                    }
                }
            }

            // alignmentEJB.saveAlignmentArtifact(selectedObject, inputArtifact);
            alignmentEJB.saveAlignmentArtifact(inputArtifact, artifactOperation == 'a');
            logger.log(Level.INFO,"returned artifact id is " + inputArtifact.getArtifactId());

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Artifact not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onArtifactDelete(AlignmentArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }

            alignmentEJB.deleteAlignmentArtifact(art);
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting artifact", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactEdit(AlignmentArtifact art) {
        if (art == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
            return;
        }
        artifactOperation = 'e';
        inputArtifact = art;
        uploadedFileName = art.getName();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputArtifact.getArtifactId());
    }

    public void onArtifactType() {
        // Toto: remove it
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

    // ------------------------ Getters/Setters -----------------------------

    public List<AlignmentRecord> getSortedObjects() {
        return sortedObjects;
    }

    public void setSortedObjects(List<AlignmentRecord> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<AlignmentRecord> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<AlignmentRecord> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public AlignmentRecord getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AlignmentRecord selectedObject) {
        this.selectedObject = selectedObject;
    }

    public AlignmentRecord getInputObject() {
        return inputObject;
    }

    public void setInputObject(AlignmentRecord inputObject) {
        this.inputObject = inputObject;
    }

    public AlignmentArtifact getInputArtifact() {
        return inputArtifact;
    }

    public void setInputArtifact(AlignmentArtifact inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public boolean isInternalArtifact() {
        return internalArtifact;
    }

    public void setInternalArtifact(boolean internalArtifact) {
        this.internalArtifact = internalArtifact;
    }

    public AlignmentArtifact getSelectedArtifact() {
        return selectedArtifact;
    }

    public void setSelectedArtifact(AlignmentArtifact selectedArtifact) {
        this.selectedArtifact = selectedArtifact;
    }

    public List<AlignmentRecord> getObjects() {
        return objects;
    }

    public char getSelectedOp() {
        return selectedOp;
    }

    public List<AlignmentProperty> getSelectedProperties() {
        return selectedProperties;
    }

    public List<AlignmentArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public char getArtifactOperation() {
        return artifactOperation;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public boolean isFileUploaded() {
        return fileUploaded;
    }

    public AlignmentProperty getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(AlignmentProperty selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public AlignmentProperty getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(AlignmentProperty inputProperty) {
        this.inputProperty = inputProperty;
    }

    public boolean isInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
    }

    public char getPropertyOperation() {
        return propertyOperation;
    }

}
