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
import org.openepics.discs.conf.ejb.ConfigurationEJBLocal;
import org.openepics.discs.conf.ent.CompTypeAsm;
import org.openepics.discs.conf.ent.CompTypeAsmPK;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComponentTypeProperty;
import org.openepics.discs.conf.ent.ComponentTypePropertyPK;
import org.openepics.discs.conf.ent.CtArtifact;
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
public class ComponentTypeMananger implements Serializable {

    @EJB
    private ConfigurationEJBLocal configurationEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    private static String folderName = "/var/proteus/"; // ToDo: get it from configuration
    
    private List<ComponentType> objects;
    private List<ComponentType> sortedObjects;
    private List<ComponentType> filteredObjects;
    private ComponentType selectedObject;
    private ComponentType inputObject;
    private List<ComponentTypeProperty> selectedCompProps;
    private List<CtArtifact> selectedArtifacts;
    private List<CompTypeAsm> selectedParts;
    private ComponentTypeProperty selectedCTP;
    private String uploadedFileName;
    private String downloadFileName;
    //private StreamedContent downloadedFile;

    CtArtifact inputArtifact;

    private boolean inTrans = false; // in the middle of an operations  
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    /**
     * Creates a new instance of ComponentTypeMananger
     */
    public ComponentTypeMananger() {
    }

    @PostConstruct
    public void init() {
        try {
            objects = configurationEJB.findComponentType();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve component types");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting component types", " ");
        }
    }

    // ----------------- Component Type ------------------------------
    public void onCompTypeSelect(SelectEvent event) {
        inputObject = selectedObject;

        selectedCompProps = selectedObject.getComponentTypePropertyList();
        selectedArtifacts = selectedObject.getCtArtifactList();
        selectedParts = selectedObject.getCompTypeAsmList();
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
            configurationEJB.deleteComponentType(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } finally {

        }
    }

    public void onCompTypeSave(ActionEvent event) {
        try {
            // inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            // inputObject.setSuperComponentType(null);
            // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved 2", "");
            configurationEJB.saveComponentType(inputObject);

            if (selectedOp == 'a') {
                selectedObject = inputObject;
                objects.add(selectedObject);
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error", "");
        } finally {
            inTrans = false;
            selectedOp = 'n';

        }
    }

    // --------------------------------- Property ------------------------------------------------
    public void onPropertyAdd(ActionEvent event) {
        try {
            if (selectedCompProps == null) {
                selectedCompProps = new ArrayList<>();
            }

            ComponentTypePropertyPK CTPpk = new ComponentTypePropertyPK();
            CTPpk.setComponentType(selectedObject.getComponentTypeId());
            ComponentTypeProperty ctp = new ComponentTypeProperty(CTPpk);

            // CTP.setComponentType1(inputObject);
            selectedCompProps.add(ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding property", "");
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(ComponentTypeProperty ctp) {
        try {
            if (ctp == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            selectedCompProps.remove(ctp); // ToDo: should this be done before or after delete from db?
            configurationEJB.deleteCompTypeProp(selectedObject, ctp);

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
            // selectedCompProps.remove(prop);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Property not saved");
        }
    }

    public void onPropertySave(RowEditEvent event) {
        ComponentTypeProperty ctp = (ComponentTypeProperty) event.getObject();

        try {
            configurationEJB.saveCompTypeProp(selectedObject, ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");

        } catch (Exception e) {
            selectedCompProps.remove(ctp);
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
            inputArtifact = new CtArtifact();
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
            configurationEJB.saveCompTypeArtifact(selectedObject, inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact created", "");
        } catch (Exception e) {
            selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not created");
        }
    }

    public void onArtifactDelete(CtArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }
            selectedArtifacts.remove(art); // ToDo: should this be done before or after delete from db?
            configurationEJB.deleteCompTypeArtifact(selectedObject, art);

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
        CtArtifact art = (CtArtifact) event.getObject();

        try {
            configurationEJB.saveCompTypeArtifact(selectedObject, art);
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
    
    // todo: parameter should be the file name not pathname
    /*
    public void downloadFile(String pathName) {
        try {
            logger.log(Level.INFO, "download file name: " + pathName); 
            downloadFileName = pathName;
            // InputStream stream = new FileInputStream(pathName);                       
            // downloadedFile = new DefaultStreamedContent(stream, "application/octet-stream", "file.jpg"); //ToDo" replace with actual filename
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error", "Downloading file");
            logger.log(Level.SEVERE, "Error in downloading the file");
            logger.log(Level.SEVERE, e.toString());
        }
    }
    */
    
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
    // --------------------------------- Assembly ------------------------------------------------
    public void onAsmAdd(ActionEvent event) {
        try {
            if (selectedParts == null) {
                selectedParts = new ArrayList<>();
            }

            CompTypeAsmPK Asmpk = new CompTypeAsmPK();
            Asmpk.setParentType(selectedObject.getComponentTypeId());
            CompTypeAsm prt = new CompTypeAsm(Asmpk);

            // CTP.setComponentType1(inputObject);
            selectedParts.add(prt);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New assembly element", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding assembly element", "");
            logger.severe(e.getMessage());
        }

    }

    public void onAsmDelete(CompTypeAsm prt) {
        try {
            if (prt == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No assembly element selected");
                return;
            }
            selectedParts.remove(prt); // ToDo: should this be done before or after delete from db?
            configurationEJB.deleteCompTypeAsm(selectedObject, prt);

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted assembly element", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting assembly element", "Refresh the page");
            logger.severe(e.getMessage());
        }

    }

    public void onAsmEdit(RowEditEvent event) {
        CompTypeAsm prt = (CompTypeAsm) event.getObject();

        try {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated");
        } catch (Exception e) {
            selectedParts.remove(prt);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Error");
        }
    }

    public void onAsmCancel(RowEditEvent event) {
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Row Cancel", "");
    }

    public void onAsmSave(RowEditEvent event) {
        CompTypeAsm prt = (CompTypeAsm) event.getObject();

        try {
            configurationEJB.saveCompTypeAsm(selectedObject, prt);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");

        } catch (Exception e) {
            selectedParts.remove(prt);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error:", "Artifact not saved");
        }
    }

    // -------------------- Getters and Setters ---------------------------------------
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

    public List<ComponentTypeProperty> getSelectedCompProps() {
        return selectedCompProps;
    }

    public ComponentTypeProperty getSelectedCTP() {
        return selectedCTP;
    }

    public void setSelectedCTP(ComponentTypeProperty selectedCTP) {
        this.selectedCTP = selectedCTP;
    }

    public List<CtArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public List<CompTypeAsm> getSelectedParts() {
        return selectedParts;
    }

    public CtArtifact getInputArtifact() {
        return inputArtifact;
    }

    public void setInputArtifact(CtArtifact inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public StreamedContent getDownloadedFile() throws IOException {
        // TODO: this should not be in getter but p:filedownload leaves no option.....
        // return downloadedFile;
        logger.log(Level.INFO, "Opening stream from repository: " + downloadFileName);
        return new DefaultStreamedContent(new FileInputStream(downloadFileName), "application/octet-stream", downloadFileName); 
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

}
