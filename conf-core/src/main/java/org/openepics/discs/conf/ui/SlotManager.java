/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.openepics.discs.conf.ejb.SlotEJBLocal;
import org.openepics.discs.conf.ent.LsArtifact;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotProperty;
import org.openepics.discs.conf.ent.SlotPropertyPK;
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
@ManagedBean
@ViewScoped
public class SlotManager implements Serializable {

    @EJB
    private SlotEJBLocal slotEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    private static String folderName = "/var/proteus/"; // ToDo: get it from configuration
    private List<Slot> objects;
    private List<Slot> sortedObjects;
    private List<Slot> filteredObjects;
    private Slot selectedObject;
    private Slot inputObject;
    private List<SlotProperty> selectedProperties;
    private List<LsArtifact> selectedArtifacts;
    private LsArtifact inputArtifact;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one
    
    private String uploadedFileName;
    private String downloadFileName;
    /**
     * Creates a new instance of SlotManager
     */
    public SlotManager() {
    }

    public List<SlotProperty> getSelectedProperties() {
        return selectedProperties;
    }
    
    @PostConstruct
    public void init() {
        try {
            objects = slotEJB.findLayoutSlot();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve component types");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting component types", " ");
        }
    }

    // ----------------- Slot  ------------------------------
    public void onSlotSelect(SelectEvent event) {
        inputObject = selectedObject;

        selectedProperties = selectedObject.getSlotPropertyList();
        selectedArtifacts = selectedObject.getLsArtifactList();
        
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }

    public void onSlotAdd(ActionEvent event) { 
        selectedOp = 'a';
        inputObject = new Slot();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onSlotEdit(ActionEvent event) {   
        selectedOp = 'e';
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onSlotDelete(ActionEvent event) {
        try {
            slotEJB.deleteLayoutSlot(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } finally {

        }
    }

    public void onSlotSave(ActionEvent event) {
        logger.info("Saving slot");
        try {
            // inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            inputObject.setIsAbstract(true);
            // inputObject.setSuperComponentType(null);
            // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved 2", "");
            slotEJB.saveLayoutSlot(inputObject);

            if (selectedOp == 'a') {
                selectedObject = inputObject;
                objects.add(selectedObject);
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error", "");
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

            SlotPropertyPK key = new SlotPropertyPK();
            key.setSlot(selectedObject.getSlotId());
            SlotProperty prop = new SlotProperty(key);

            // CTP.setComponentType1(inputObject);
            selectedProperties.add(prop);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding property", "");
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(SlotProperty ctp) {
        try {
            if (ctp == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            selectedProperties.remove(ctp); // ToDo: should this be done before or after delete from db?
            slotEJB.deleteSlotProp(selectedObject, ctp);

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

    public void onPropertySave(RowEditEvent event) {
        SlotProperty ctp = (SlotProperty) event.getObject();

        try {
            slotEJB.saveSlotProp(selectedObject, ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");

        } catch (Exception e) {
            selectedProperties.remove(ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not saved");
        }
    }

    public void onPropertyCancel(RowEditEvent event) {
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Row Cancel", "");
    }

    // --------------------------------- Artifact ------------------------------------------------
    public void onArtifactAdd(ActionEvent event) {
        try {
            if (selectedArtifacts == null) {
                selectedArtifacts = new ArrayList<>();
            }
            inputArtifact = new LsArtifact();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding artifact", "");
            logger.severe(e.getMessage());
        }

    }

    public void onArtifactCancel(RowEditEvent event) {
        try {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit cancelled");
        } catch (Exception e) {
            //selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not saved");
        }
    }

    public void onArtifactCreate(ActionEvent event) {
        
        try {
            inputArtifact.setUri(folderName + uploadedFileName);
            selectedArtifacts.add(inputArtifact);           
            slotEJB.saveSlotArtifact(selectedObject, inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact created", "");
        } catch (Exception e) {
            selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not created");
        }
    }

    public void onArtifactDelete(LsArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }
            selectedArtifacts.remove(art); // ToDo: should this be done before or after delete from db?
            slotEJB.deleteSlotArtifact(selectedObject, art);

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting artifact", "Refresh the page");
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactEdit(RowEditEvent event) {
        try {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated");
        } catch (Exception e) {
            //selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not saved");
        }
    }

    private void copyFile(InputStream is, OutputStream os) throws IOException  {
        int len;
        byte[] buffer = new byte[1024];
        
        while ( (len = is.read(buffer)) > 0) {
            os.write(buffer, 0 , len);
        }
    }
    
    public void onArtifactSave(RowEditEvent event) {
        LsArtifact art = (LsArtifact) event.getObject();

        try {
            slotEJB.saveSlotArtifact(selectedObject, art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
        } catch (Exception e) {
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not saved");
        }
    }

    // -------------------------- File upload/download ---------------------------
    //ToDo: most of this should be done in EJB, as a transaction
    public void handleFileUpload(FileUploadEvent event) {
        // String msg = event.getFile().getFileName() + " is uploaded.";
        // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Succesful", msg);
        InputStream istream;
        OutputStream ostream;
                 
        try {         
            UploadedFile uploadedFile = event.getFile();
            uploadedFileName = uploadedFile.getFileName();
            istream = uploadedFile.getInputstream();
            
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "File ", "Name: " + uploadedFileName);
            if (istream == null) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "istream is null");
                return;
            }
            if (folderName == null || folderName.isEmpty()) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Folder name not given.");
                return;
            }
            File folder = new File(folderName);
            if (! folder.exists() ) {
                if ( !folder.mkdirs()) {
                    logger.log(Level.SEVERE, "Could not create repository folder " + folderName);
                }; 
            }
            File ofile = new File(folderName + uploadedFileName);
            ostream = new FileOutputStream(ofile);
            copyFile(istream,  ostream);                     
            // repBean.putFile(folderName, fname, istream);           
            ostream.close();
            istream.close();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "File uploaded", "Name: " + uploadedFileName);
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Uploading file");
            logger.severe(e.getMessage());
        } finally {
            
        }
    }
    
    public void downloadFile() {
        try {
            logger.log(Level.INFO, "download file name: 2 " + downloadFileName); 
            
            // InputStream stream = new FileInputStream(pathName);                       
            // downloadedFile = new DefaultStreamedContent(stream, "application/octet-stream", "file.jpg"); //ToDo" replace with actual filename
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Downloading file");
            logger.log(Level.SEVERE, "Error in downloading the file");
            logger.log(Level.SEVERE, e.toString());
        }
    }
    // --------------------- Setters and getters --------------------
    public List<Slot> getObjects() {
        return objects;
    }

    public List<Slot> getSortedObjects() {
        return sortedObjects;
    }

    public void setSortedObjects(List<Slot> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<Slot> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<Slot> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public Slot getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Slot selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Slot getInputObject() {
        return inputObject;
    }

    public void setInputObject(Slot inputObject) {
        this.inputObject = inputObject;
    }

    public List<LsArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public void setInputArtifact(LsArtifact inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public LsArtifact getInputArtifact() {
        return inputArtifact;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }
        
    public StreamedContent getDownloadedFile() throws IOException {
        // TODO: this should not be in getter but p:filedownload leaves no option.....
        // return downloadedFile;
        logger.log(Level.INFO, "Opening stream from repository: " + downloadFileName);
        return new DefaultStreamedContent(new FileInputStream(downloadFileName), "application/octet-stream", downloadFileName); 
    }
}
