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
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.DevicesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
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
 * 
 */
@Named
@ViewScoped
public class DeviceManager implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private DeviceEJB deviceEJB;
    private static final Logger logger = Logger.getLogger(DeviceManager.class.getCanonicalName());

    @Inject
    private BlobStore blobStore;
    // ToDo: Remove the injection. Not a good way to authorize.
    @Inject
    private LoginManager loginManager;
    // private String loggedInUser; // logged in user

    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @DevicesLoaderQualifier private DataLoader devicesDataLoader;

    private byte[] importData;
    private String importFileName;
    private DataLoaderResult loaderResult;

    private List<Device> objects;
    private List<Device> sortedObjects;
    private List<Device> filteredObjects;
    private Device selectedObject;
    private Device inputObject;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    // Property
    private List<DevicePropertyValue> selectedProperties;
    private DevicePropertyValue selectedProperty;
    private DevicePropertyValue inputProperty;
    private boolean inRepository = false;
    private char propertyOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // Artifacts
    private List<DeviceArtifact> selectedArtifacts;
    private DeviceArtifact inputArtifact;
    private boolean internalArtifact = true;
    private DeviceArtifact selectedArtifact;
    private char artifactOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one


    // File upload/download
    private String uploadedFileName;
    private boolean fileUploaded = false;
    private String repoFileId; // identifier of the file stored in content repo

    // Assembly
    private List<Device> asmDevices; // parts of this slot
    private Device inputAsmDevice;
    private String inputAsmComment;
    private String inputAsmPosition;

    /**
     * Creates a new instance of DeviceManager
     */
    public DeviceManager() {
    }

    @PostConstruct
    public void init() {
        try {
            objects = deviceEJB.findDevice();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve component types");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting component types", " ");
        }
    }


    // ----------------- Device  ------------------------------
    public void onDeviceSelect(SelectEvent event) {
        inputObject = selectedObject;

        selectedProperties = selectedObject.getDevicePropertyList();
        selectedArtifacts = selectedObject.getDeviceArtifactList();
        asmDevices = selectedObject.getDeviceList();

        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }

    public void onDeviceAdd(ActionEvent event) {
        selectedOp = 'a';
        // TODO replaced void constructor (now protected) with default values. Check!
        inputObject = new Device("1", loginManager.getUserid());
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onDeviceEdit(ActionEvent event) {
        selectedOp = 'e';
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onDeviceDelete(ActionEvent event) {
        try {
            deviceEJB.deleteDevice(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Device not deleted", e.getMessage());
        } finally {

        }
    }

    public void onDeviceSave(ActionEvent event) {
        logger.info("Saving device");

        try {
            if (selectedOp == 'a') {
                deviceEJB.addDevice(inputObject);
                selectedObject = inputObject;
                objects.add(selectedObject);
            } else {
                deviceEJB.saveDevice(inputObject);
            }
            // tell the client if the operation was a success so that it can hide
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            // tell the client  if the operation was a success so that it can hide
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Device not saved.", e.getMessage());
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

            // TODO replaced void constructor (now protected) with default values. Check!
            inputProperty = new DevicePropertyValue(false, loginManager.getUserid());
            inputProperty.setDevice(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding property", "");
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(DevicePropertyValue ctp) {
        try {
            if (ctp == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            deviceEJB.deleteDeviceProp(ctp);
            selectedProperties.remove(ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting property", "Refresh the page");
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyInit(RowEditEvent event) {
        // ComponentTypeProperty prop = (ComponentTypeProperty) event.getObject();

        try {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Property Edit", "");

        } catch (Exception e) {
            // selectedProperties.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Property not saved");
        }
    }

    public void onPropertyEdit(DevicePropertyValue prop) {
        try {
            if (prop == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            propertyOperation = 'e';
            inputProperty = prop;
            uploadedFileName = prop.getProperty().getName();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputProperty.getId());
        } catch (Exception e) {
            // selectedCompProps.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Property can not be edited");
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
            
            if (propertyOperation == 'a') {
                deviceEJB.addDeviceProperty(inputProperty);
            } else {
                deviceEJB.saveDeviceProp(inputProperty);
            }
         
            logger.log(Level.INFO, "returned artifact id is " + inputProperty.getId());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Property saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Property not saved");
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
            // TODO replaced void constructor (now protected) with default values. Check!
            inputArtifact = new DeviceArtifact("", false, "", "", loginManager.getUserid());
            inputArtifact.setDevice(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding artifact", "");
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
                deviceEJB.addDeviceArtifact(inputArtifact);
            } else {
                deviceEJB.saveDeviceArtifact(inputArtifact);
            }
            
            logger.log(Level.INFO,"returned artifact id is " + inputArtifact.getId());

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Artifact not saved");
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onArtifactDelete(DeviceArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }

            deviceEJB.deleteDeviceArtifact(art);
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting artifact", "Refresh the page");
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactEdit(DeviceArtifact art) {
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Uploading file");
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Downloading file");
            logger.log(Level.SEVERE, "Error in downloading the file");
            logger.log(Level.SEVERE, e.toString());
        }

        return file;
    }

    // --------------------------------- Part Slots (assembly)------------------------------------------------
    public void onPartDeviceAdd(ActionEvent event) {
        try {
            // relationOperation = 'a';
            if (asmDevices == null) {
                asmDevices = new ArrayList<>();
            }
            // inputPartDevice = new Slot();
            // inputPartDevice.setParentSlot(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New part ", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding part", "");
            logger.severe(e.getMessage());
        }
    }

    public void onPartDeviceSave(ActionEvent event) {
        try {
            inputAsmDevice.setAssemblyPosition(inputAsmPosition);
            inputAsmDevice.setAssemblyDescription(inputAsmComment);
            inputAsmDevice.setAssemblyParent(selectedObject);
            deviceEJB.saveDevice(inputAsmDevice);
            selectedObject.getDeviceList().add(inputAsmDevice);

            deviceEJB.saveDevice(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Part saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Part not saved");
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onPartDeviceDelete(Device device) {
        try {
            if (device == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "Null slot selected");
                return;
            }

            device.setAssemblyPosition(null);
            device.setAssemblyDescription(null);
            device.setAssemblyParent(null);
            deviceEJB.saveDevice(device);
            selectedObject.getDeviceList().remove(device);
            deviceEJB.saveDevice(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting artifact", "Refresh the page");
            logger.severe(e.getMessage());
        }
    }

    public void onPartDeviceEdit(Device device) {
        if (device == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
            return;
        }
        artifactOperation = 'e';
        inputAsmDevice = device;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " );
    }

    public String getImportFileName() { return importFileName; }

    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, devicesDataLoader);
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

    // ------------Getters/Setters -------------

    public List<Device> getSortedObjects() {
        return sortedObjects;
    }

    public void setSortedObjects(List<Device> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<Device> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<Device> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public Device getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Device selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Device getInputObject() {
        return inputObject;
    }

    public void setInputObject(Device inputObject) {
        this.inputObject = inputObject;
    }

    public DeviceArtifact getInputArtifact() {
        return inputArtifact;
    }

    public void setInputArtifact(DeviceArtifact inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public char getSelectedOp() {
        return selectedOp;
    }

    public void setSelectedOp(char selectedOp) {
        this.selectedOp = selectedOp;
    }

    public boolean isInternalArtifact() {
        return internalArtifact;
    }

    public void setInternalArtifact(boolean internalArtifact) {
        this.internalArtifact = internalArtifact;
    }

    public List<Device> getObjects() {
        return objects;
    }

    public List<DevicePropertyValue> getSelectedProperties() {
        return selectedProperties;
    }

    public List<DeviceArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public char getArtifactOperation() {
        return artifactOperation;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public DeviceArtifact getSelectedArtifact() {
        return selectedArtifact;
    }

    public void setSelectedArtifact(DeviceArtifact selectedArtifact) {
        this.selectedArtifact = selectedArtifact;
    }

    public Device getInputAsmDevice() {
        return inputAsmDevice;
    }

    public void setInputAsmDevice(Device inputAsmDevice) {
        this.inputAsmDevice = inputAsmDevice;
    }

    public String getInputAsmComment() {
        return inputAsmComment;
    }

    public void setInputAsmComment(String inputAsmComment) {
        this.inputAsmComment = inputAsmComment;
    }

    public String getInputAsmPosition() {
        return inputAsmPosition;
    }

    public void setInputAsmPosition(String inputAsmPosition) {
        this.inputAsmPosition = inputAsmPosition;
    }

    public List<Device> getAsmDevices() {
        return asmDevices;
    }

    public DevicePropertyValue getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(DevicePropertyValue selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public DevicePropertyValue getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(DevicePropertyValue inputProperty) {
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
