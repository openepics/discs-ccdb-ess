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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.ComponentTypesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
@Named
@ViewScoped
public class ComponentTypeManager implements Serializable {
    private static final Logger logger = Logger.getLogger(ComponentTypeManager.class.getCanonicalName());

    @EJB private ComptypeEJB comptypeEJB;

    @Inject private BlobStore blobStore;
    @Inject private ConfigurationEJB configurationEJB;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @ComponentTypesLoaderQualifier private DataLoader compTypesDataLoader;

    private byte[] importData;
    private String importFileName;
    private DataLoaderResult loaderResult;

    private List<ComponentType> objects;
    private List<ComponentType> sortedObjects;
    private List<ComponentType> filteredObjects;
    private ComponentType selectedObject;
    private ComponentType inputObject;
    private List<ComptypeAsm> selectedParts;

    // properties
    private List<ComptypePropertyValue> selectedProperties;
    private ComptypePropertyValue selectedProperty;
    private ComptypePropertyValue inputProperty;
    private boolean inRepository = false;
    private char propertyOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // artifacts
    private List<ComptypeArtifact> selectedArtifacts;
    private ComptypeArtifact inputArtifact;
    private boolean internalArtifact = true;
    private ComptypeArtifact selectedArtifact;
    private char artifactOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // File upload/download
    private String uploadedFileName;
    private boolean fileUploaded = false;
    private String repoFileId; // identifier of the file stored in content repo

    private boolean inTrans = false; // in the middle of an operations
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    private String name;
    private String description;
    private List<ComptypePropertyValue> propertyValues;
    private ComponentType selectedDeviceType;
    private List<AuditRecord> auditRecordsForEntity;

    /**
     * Creates a new instance of ComponentTypeMananger
     */
    public ComponentTypeManager() {
    }

    @PostConstruct
    public void init() {
        objects = comptypeEJB.findComponentType();
        resetFields();
    }

    public void deviceTypePropertyRedirect(Long propertyId) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("device-type-attributes-manager.xhtml?id=" + propertyId);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void prepareAddPopup() {
        resetFields();
        RequestContext.getCurrentInstance().update("addDeviceTypeForm:addDeviceType");
    }

    public void prepareModifyPopup() {
        name = selectedDeviceType.getName();
        description = selectedDeviceType.getDescription();
        propertyValues = selectedDeviceType.getComptypePropertyList();
        RequestContext.getCurrentInstance().update("modifyDeviceTypeForm:modifyDeviceType");
    }

    private void resetFields() {
        name = null;
        description = null;
        propertyValues = null;
    }

    public void onAdd() {
        final ComponentType componentTypeToAdd = new ComponentType(name);
        componentTypeToAdd.setDescription(description);
        comptypeEJB.addComponentType(componentTypeToAdd);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New device type has been created");
        init();
    }

    public void onModify() {
        selectedDeviceType.setName(name);
        selectedDeviceType.setDescription(description);
        comptypeEJB.saveComponentType(selectedDeviceType);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type was modified");
        init();
    }

    public void onDelete() {
        comptypeEJB.deleteComponentType(selectedDeviceType);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type was deleted");
        init();
    }

    // ----------------- Component Type ------------------------------
    public void onCompTypeSelect(SelectEvent event) {
        inputObject = selectedObject;

        selectedProperties = selectedObject.getComptypePropertyList();
        selectedArtifacts = selectedObject.getComptypeArtifactList();
        selectedParts = selectedObject.getComptypeAsmList();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }

    public void onCompTypeAdd(ActionEvent event) {
        selectedOp = 'a';
        inTrans = true;

        inputObject = new ComponentType();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onCompTypeEdit(ActionEvent event) {
        selectedOp = 'e';
        inTrans = true;
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onCompTypeDelete(ActionEvent event) {
        try {
            comptypeEJB.deleteComponentType(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deleted", e.getMessage());
        } finally {

        }
    }

    public void onCompTypeSave(ActionEvent event) {
        try {
            // inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            // inputObject.setSuperComponentType(null);
            // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved 2", "");
            if (selectedOp == 'a') {
                comptypeEJB.addComponentType(inputObject);
            } else {
                comptypeEJB.saveComponentType(inputObject);
            }

            if (selectedOp == 'a') {
                selectedObject = inputObject;
                objects.add(selectedObject);
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        } finally {
            inTrans = false;
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

            inputProperty = new ComptypePropertyValue(false);
            inputProperty.setComponentType(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding property", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(ComptypePropertyValue ctp) {
        try {
            if (ctp == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            comptypeEJB.deleteCompTypeProp(ctp);
            selectedProperties.remove(ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting property", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyEdit(ComptypePropertyValue prop) {
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
        // ComptypeProperty ctp = (ComptypeProperty) event.getObject();
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

            if (propertyOperation == 'a') {
                comptypeEJB.addCompTypeProp(inputProperty);
            } else {
                comptypeEJB.saveCompTypeProp(inputProperty);
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Uploading file");
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Downloading file");
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
            inputArtifact = new ComptypeArtifact();
            inputArtifact.setComponentType(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding artifact",e.getMessage());
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
                comptypeEJB.addCompTypeArtifact(inputArtifact);
            } else {
                comptypeEJB.saveCompTypeArtifact(inputArtifact);
            }
            logger.log(Level.INFO, "returned artifact id is " + inputArtifact.getId());

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Artifact not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onArtifactDelete(ComptypeArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }

            comptypeEJB.deleteCompTypeArtifact(art);
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting artifact", "Refresh the page");
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactEdit(ComptypeArtifact art) {
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
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact type selected", "");
    }

    // -------------------------- File upload/download Artifact ---------------------------
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error Uploading file", e.getMessage());
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error Downloading file", e.getMessage());
            logger.log(Level.SEVERE, "Error in downloading the file");
            logger.log(Level.SEVERE, e.toString());
        }

        return file;
    }

    // --------------------------------- Assembly ------------------------------------------------
    public void onAsmAdd(ActionEvent event) {
        try {
            if (selectedParts == null) {
                selectedParts = new ArrayList<>();
            }
            ComptypeAsm prt = new ComptypeAsm("");

            prt.setParentType(selectedObject);

            selectedParts.add(prt);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New assembly element", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding assembly element", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onAsmDelete(ComptypeAsm prt) {
        try {
            if (prt == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No assembly element selected");
                return;
            }
            selectedParts.remove(prt); // ToDo: should this be done before or after delete from db?
            comptypeEJB.deleteComptypeAsm(selectedObject, prt);

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted assembly element", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting assembly element", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onAsmEdit(RowEditEvent event) {
        ComptypeAsm prt = (ComptypeAsm) event.getObject();

        try {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated");
        } catch (Exception e) {
            selectedParts.remove(prt);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", e.getMessage());
        }
    }

    public void onAsmCancel(RowEditEvent event) {
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Row Cancel", "");
    }

    public void onAsmSave(RowEditEvent event) {
        ComptypeAsm prt = (ComptypeAsm) event.getObject();

        try {
            comptypeEJB.saveComptypeAsm(selectedObject, prt);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Assembly item saved", "");

        } catch (Exception e) {
            selectedParts.remove(prt);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Assembly item not saved", e.getMessage());
        }
    }


    public String getImportFileName() { return importFileName; }

    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, compTypesDataLoader);
    }

    public DataLoaderResult getLoaderResult() { return loaderResult; }

    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    // -------------------- Getters and Setters ---------------------------------------

    public ComponentType getSelectedDeviceType() { return selectedDeviceType; }
    public void setSelectedDeviceType(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
    }

    public ComponentType getSelectedDeviceTypeToModify() { return selectedDeviceType; }
    public void setSelectedDeviceTypeToModify(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
        prepareModifyPopup();
    }

    public ComponentType getSelectedDeviceTypeForLog() { return selectedDeviceType; }
    public void setSelectedDeviceTypeForLog(ComponentType selectedDeviceType) {
        this.selectedDeviceType = selectedDeviceType;
        auditRecordsForEntity = configurationEJB.findAuditRecordsByEntityId(selectedDeviceType.getId(), EntityType.COMPONENT_TYPE);
        RequestContext.getCurrentInstance().update("deviceTypeLogForm:deviceTypeLog");
    }

    public List<AuditRecord> getAuditRecordsForEntity() {
        return auditRecordsForEntity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ComponentType> getSortedObjects() {
        return sortedObjects;
    }

    public void setSortedObjects(List<ComponentType> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<ComponentType> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<ComponentType> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public ComponentType getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(ComponentType selectedObject) {
        this.selectedObject = selectedObject;
    }

    public ComponentType getInputObject() {
        return inputObject;
    }

    public void setInputObject(ComponentType inputObject) {
        this.inputObject = inputObject;
    }

    public List<ComponentType> getObjects() {
        return objects;
    }

    public boolean isInTrans() {
        return inTrans;
    }

    public ComptypePropertyValue getSelectedCTP() {
        return selectedProperty;
    }

    public void setSelectedCTP(ComptypePropertyValue selectedCTP) {
        this.selectedProperty = selectedCTP;
    }

    public List<ComptypeArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public List<ComptypeAsm> getSelectedParts() {
        return selectedParts;
    }

    public ComptypeArtifact getInputArtifact() {
        return inputArtifact;
    }

    public void setInputArtifact(ComptypeArtifact inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public char getArtifactOperation() {
        return artifactOperation;
    }

    public boolean isInternalArtifact() {
        return internalArtifact;
    }

    public void setInternalArtifact(boolean internalArtifact) {
        this.internalArtifact = internalArtifact;
    }

    public ComptypeArtifact getSelectedArtifact() {
        return selectedArtifact;
    }

    public void setSelectedArtifact(ComptypeArtifact selectedArtifact) {
        this.selectedArtifact = selectedArtifact;
    }

    public List<ComptypePropertyValue> getSelectedProperties() {
        return selectedProperties;
    }

    public char getPropertyOperation() {
        return propertyOperation;
    }

    public ComptypePropertyValue getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(ComptypePropertyValue selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public ComptypePropertyValue getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(ComptypePropertyValue inputProperty) {
        this.inputProperty = inputProperty;
    }

    public boolean isInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
    }

}
