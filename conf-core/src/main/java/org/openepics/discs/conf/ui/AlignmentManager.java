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

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.AlignmentEJB;
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentPropertyValue;
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.Value;
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
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class AlignmentManager implements Serializable{
    private static final Logger logger = Logger.getLogger(AlignmentManager.class.getCanonicalName());

    @EJB private AlignmentEJB alignmentEJB;
    @Inject private BlobStore blobStore;

    private List<AlignmentRecord> objects;
    private List<AlignmentRecord> sortedObjects;
    private List<AlignmentRecord> filteredObjects;
    private AlignmentRecord selectedObject;
    private AlignmentRecord inputObject;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    // Property
    private List<AlignmentPropertyValue> selectedProperties;
    private AlignmentPropertyValue selectedProperty;
    private AlignmentPropertyValue inputProperty;
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
        inputObject = new AlignmentRecord(UUID.randomUUID().toString(), new Date());
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onAlignRecEdit(ActionEvent event) {
        selectedOp = 'e';
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onAlignRecDelete(ActionEvent event) {
        try {
            alignmentEJB.delete(selectedObject);
            getObjects().remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e))
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The alignment could not be deleted because it is used.");
            else
                throw e;
        }
    }

    public void onAlignRecSave(ActionEvent event) {
        logger.info("Saving slot");
        try {
            // inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            alignmentEJB.save(inputObject);

            if (selectedOp == 'a') {
                selectedObject = inputObject;
                getObjects().add(selectedObject);
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
            inputProperty = new AlignmentPropertyValue(false);
            inputProperty.setAlignmentRecord(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding property", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(AlignmentPropertyValue prop) {
        try {
            if (prop == null) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Strange", "No property selected");
                return;
            }
            alignmentEJB.deleteChild(prop);
            selectedProperties.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted property", "");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e))
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The property could not be deleted because it is used.");
            else
                throw e;
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

    public void onPropertyEdit(AlignmentPropertyValue prop) {
        try {
            if (prop == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            artifactOperation = 'e';
            inputProperty = prop;
            uploadedFileName = prop.getProperty().getName();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputProperty.getId());
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
                inputProperty.setPropValue(new StrValue(repoFileId));
            }

            if (propertyOperation == 'a') {
                alignmentEJB.addChild(inputProperty);
            } else {
                alignmentEJB.saveChild(inputProperty);
            }

            logger.log(Level.INFO, "returned artifact id is " + inputProperty.getId());
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
            Value propValue = selectedProperty.getPropValue();
            if (!(propValue instanceof StrValue))
                throw new Exception("Selected property type incorrect");

            InputStream istream = blobStore.retreiveFile(((StrValue)propValue).getStrValue());
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
            inputArtifact = new AlignmentArtifact("", false, "", "");
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
                inputArtifact.setInternal(internalArtifact);
                if (inputArtifact.isInternal()) { // internal artifact
                    if (!fileUploaded) {
                        Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "You must upload a file");
                        RequestContext.getCurrentInstance().addCallbackParam("success", false);
                        return;
                    }
                }
            }

            if (artifactOperation == 'a') {
                alignmentEJB.addChild(inputArtifact);
            } else {
                alignmentEJB.saveChild(inputArtifact);
            }

            logger.log(Level.INFO,"returned artifact id is " + inputArtifact.getId());

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
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Strange", "No artifact selected");
                return;
            }

            alignmentEJB.deleteChild(art);
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e))
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The artifact could not be deleted because it is used.");
            else
                throw e;
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
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputArtifact.getId());
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
        if (objects == null) objects = alignmentEJB.findAll();
        return objects;
    }

    public char getSelectedOp() {
        return selectedOp;
    }

    public List<AlignmentPropertyValue> getSelectedProperties() {
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

    public AlignmentPropertyValue getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(AlignmentPropertyValue selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public AlignmentPropertyValue getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(AlignmentPropertyValue inputProperty) {
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
