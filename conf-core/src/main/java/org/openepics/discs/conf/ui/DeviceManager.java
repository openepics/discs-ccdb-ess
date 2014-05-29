/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import org.openepics.discs.conf.util.Utility;
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
import org.openepics.discs.conf.ejb.DeviceEJBLocal;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceProperty;
import org.openepics.discs.conf.util.BlobStore;
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
public class DeviceManager implements Serializable {
    @EJB
    private DeviceEJBLocal deviceEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    @Inject
    private BlobStore blobStore;
    // ToDo: Remove the injection. Not a good way to authorize.
    @Inject
    private LoginManager loginManager;
    // private String loggedInUser; // logged in user
    
    private List<Device> objects;
    private List<Device> sortedObjects;
    private List<Device> filteredObjects;
    private Device selectedObject;
    private Device inputObject;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one
    
    // Property
    private List<DeviceProperty> selectedProperties;
    private DeviceProperty selectedProperty;
    private DeviceProperty inputProperty;
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
        inputObject = new Device();
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
        String token = loginManager.getToken();
        logger.info("Saving device");
        
        try {   
            deviceEJB.saveDevice(token, inputObject);

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

            inputProperty = new DeviceProperty();
            inputProperty.setDevice(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding property", "");
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(DeviceProperty ctp) {
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

    public void onPropertyEdit(DeviceProperty prop) {
        try {
            if (prop == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            propertyOperation = 'e';
            inputProperty = prop;
            uploadedFileName = prop.getProperty().getName();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " + inputProperty.getDevPropId());
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
            deviceEJB.saveDeviceProp(inputProperty, propertyOperation == 'a');
            logger.log(Level.INFO, "returned artifact id is " + inputProperty.getDevPropId());
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
            inputArtifact = new DeviceArtifact();
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
                inputArtifact.setIsInternal(internalArtifact);
                if (inputArtifact.getIsInternal()) { // internal artifact
                    if (!fileUploaded) {
                        Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "You must upload a file");
                        RequestContext.getCurrentInstance().addCallbackParam("success", false);
                        return;
                    }
                }               
            }
            
            // deviceEJB.saveDeviceArtifact(selectedObject, inputArtifact);
            deviceEJB.saveDeviceArtifact(inputArtifact, artifactOperation == 'a');
            logger.log(Level.INFO,"returned artifact id is " + inputArtifact.getArtifactId());
            
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
        String token = loginManager.getToken();
        try {
            inputAsmDevice.setAsmPosition(inputAsmPosition);
            inputAsmDevice.setAsmDescription(inputAsmComment);
            inputAsmDevice.setAsmParent(selectedObject);
            deviceEJB.saveDevice(token,inputAsmDevice);
            selectedObject.getDeviceList().add(inputAsmDevice);
                    
            deviceEJB.saveDevice(token, selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Part saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Part not saved");
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onPartDeviceDelete(Device device) {
        String token = loginManager.getToken();
        try {
            if (device == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "Null slot selected");
                return;
            }
            
            device.setAsmPosition(null);
            device.setAsmDescription(null);
            device.setAsmParent(null);
            deviceEJB.saveDevice(token, device);
            selectedObject.getDeviceList().remove(device);
            deviceEJB.saveDevice(token, selectedObject); 
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

    public List<DeviceProperty> getSelectedProperties() {
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

    public DeviceProperty getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(DeviceProperty selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public DeviceProperty getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(DeviceProperty inputProperty) {
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
