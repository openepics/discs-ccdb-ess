/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.util.IllegalImportFileFormatException;
import org.openepics.discs.conf.util.NotAuthorizedException;
import org.openepics.discs.conf.util.Utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;

import org.openepics.discs.conf.dl.PropertiesDataLoader;
import org.openepics.discs.conf.ent.*;
import org.openepics.discs.conf.ejb.*;

import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class PropertyManager implements Serializable {
    @EJB
    private ConfigurationEJB configurationEJB;
    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private PropertiesDataLoader propertiesDataLoader;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    private List<Property> objects;
    private List<Property> sortedObjects;
    private List<Property> filteredObjects;
    private Property selectedObject;
    private Property inputObject;

    private byte[] importData;
    private String importFileName;
    // private Property newProperty = new Property();

    // 
    private boolean inTrans = false; // in the middle of an operations  
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one
    
    /**
     * Creates a new instance of PropertyManager
     */
    public PropertyManager() {
    }
    
    @PostConstruct
    public void init() {
        try {
            objects = configurationEJB.findProperties();
            // objects.add(new Property());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void onRowSelect(SelectEvent event) {
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }
    
    public void onAdd(ActionEvent event) {
        selectedOp = 'a';
        inTrans = true;
        
        inputObject = new Property();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }
    
    public void onEdit(ActionEvent event) {
        selectedOp = 'e';
        inTrans = true;
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }
    
    public void onDelete(ActionEvent event) {        
        try {
            configurationEJB.deleteProperty(selectedObject);
            objects.remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } finally {           
            
        }       
    }
    
    public void onSave(ActionEvent event) {
        try {
            inputObject.setAssociation("T");
            inputObject.setModifiedBy("test-user");
            configurationEJB.saveProperty(inputObject);
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
    
    public void onCancel(ActionEvent event) {
        selectedOp = 'n';
        inputObject = selectedObject;
        inTrans = false;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Cancelled", "");
    }

    // ---------------------------------
    public List<Property> getSortedObjects() {
        return sortedObjects;
    }

    public void setSortedObjects(List<Property> sortedObjects) {
        this.sortedObjects = sortedObjects;
    }

    public List<Property> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<Property> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public Property getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Property selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Property getInputObject() {
        return inputObject;
    }

    public void setInputObject(Property inputObject) {
        this.inputObject = inputObject;
    }

    public List<Property> getObjects() {
        return objects;
    }

    public boolean isInTrans() {
        return inTrans;
    }
    
    public boolean canImportUnits() {
        return authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.CREATE) || 
                authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.DELETE) ||
                authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.UPDATE);
    }
    
    public String getImportFileName() { return importFileName; }
    
    public void importUnits() {      
        InputStream inputStream = new ByteArrayInputStream(importData);
        try {
            propertiesDataLoader.loadData(inputStream);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Import Success", "Units were successfully imported");
        } catch (IllegalImportFileFormatException | NotAuthorizedException e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Import Fail", e.getMessage());
        }         
    }
    
    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();           
        }
    }
    
    
}
