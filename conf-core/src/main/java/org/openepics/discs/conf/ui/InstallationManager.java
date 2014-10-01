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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.google.common.base.Preconditions;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class InstallationManager implements Serializable {
    private static final Logger logger = Logger.getLogger(InstallationManager.class.getCanonicalName());

    @EJB private InstallationEJB installationEJB;
    @EJB private SlotEJB slotEJB;
    @Inject private BlobStore blobStore;
    @Inject protected SlotsTreeBuilder slotsTreeBuilder;

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

    /* * * * * *
     *
     * CCDB variables
     * TODO do cleanup of legacy variables after development. Remove this comment block.
     * * * * * */
    private Device installedDevice;
    private InstallationRecord installationRecord;
    private Slot installationSlot;
    private TreeNode installableSlots;
    private TreeNode selectedSlot;

    /**
     * Creates a new instance of InstallationManager
     */
    public InstallationManager() {
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
        inputObject = new InstallationRecord("1", new Date());
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Add", "");
    }

    public void onIRecEdit(ActionEvent event) {
        selectedOp = 'e';
        inputObject = selectedObject;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit", "");
    }

    public void onIRecDelete(ActionEvent event) {
        try {
            installationEJB.delete(selectedObject);
            getObjects().remove(selectedObject);
            selectedObject = null;
            inputObject = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted", "");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e))
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The installation record could not be deleted because it is used.");
            else
                throw e;
        }
    }

    public void onIRecSave(ActionEvent event) {
        logger.info("Saving Installation Record");
        inputObject.setModifiedBy("test-user");

        if (selectedOp == 'a') {
            installationEJB.add(inputObject);
        } else {
            installationEJB.save(inputObject);
        }

        if (selectedOp == 'a') {
            selectedObject = inputObject;
            getObjects().add(selectedObject);
        }

        // tell the client if the operation was a success so that it can hide
        RequestContext.getCurrentInstance().addCallbackParam("success", true);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        selectedOp = 'n';
    }

    // --------------------------------- Artifact ------------------------------------------------
    public void onArtifactAdd(ActionEvent event) {
        artifactOperation = 'a';
        if (selectedArtifacts == null) {
            selectedArtifacts = new ArrayList<>();
        }
        inputArtifact = new InstallationArtifact("", false, "", "");
        inputArtifact.setInstallationRecord(selectedObject);
        fileUploaded = false;
        uploadedFileName = null;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "New artifact", "");
    }

    public void onArtifactSave(ActionEvent event) {
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
            installationEJB.addChild(inputArtifact);
        } else {
            installationEJB.saveChild(inputArtifact);
        }

        logger.log(Level.INFO,"returned artifact id is " + inputArtifact.getId());
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
        RequestContext.getCurrentInstance().addCallbackParam("success", true);
    }

    public void onArtifactDelete(InstallationArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Strange", "No artifact selected");
                return;
            }

            installationEJB.deleteChild(art);
            selectedArtifacts.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e))
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The artifact could not be deleted because it is used.");
            else
                throw e;
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
        final InputStream istream;

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
        if (objects == null) objects = installationEJB.findAll();
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

    /* * * * * * *
     * CCDB methods.
     * TODO do cleanup of legacy methods after development. Remove this comment block.
     * * * * * * */

    public boolean isDeviceInstalled(Device device) {
        if (!device.equals(this.installedDevice)) {
            setInstallationRecord(installationEJB.getActiveInstallationRecordForDevice(device));
        }
        return this.installedDevice != null;
    }

    public String getInstalledSlotForDevice(Device device) {
        final InstallationRecord record = installationEJB.getActiveInstallationRecordForDevice(device);
        return record == null ? "-" : record.getSlot().getName();
    }

    public void setInstallationRecord(InstallationRecord installationRecord) {
        this.installationRecord = installationRecord;
        if (installationRecord != null) {
            this.installedDevice = installationRecord.getDevice();
            this.installationSlot = installationRecord.getSlot();
        } else {
            this.installedDevice = null;
            this.installationSlot = null;
        }
    }

    public InstallationRecord getInstallationRecord() {
        return installationRecord;
    }

    public TreeNode getInstallationTree(Device device) {
        Preconditions.checkNotNull(device);
        if (installableSlots != null) {
            return installableSlots;
        }

        final List<Slot> allSlots = slotEJB.findAll();
        final ComponentType componentType = device.getComponentType();

        installableSlots = slotsTreeBuilder.newSlotsTree(allSlots, null, new HashSet<Long>(), true, componentType);
        return installableSlots;
    }

    /**
     * @return the selectedSlot
     */
    public TreeNode getSelectedSlot() {
        return selectedSlot;
    }

    /**
     * @param selectedSlot the selectedSlot to set
     */
    public void setSelectedSlot(TreeNode selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public void installDevice(Device device) {
        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        newRecord.setDevice(device);
        newRecord.setSlot(((SlotView)selectedSlot.getData()).getSlot());
        installationEJB.add(newRecord);
    }

    public List<String> getInstalledSlotInformation(Device device) {
        List<String> installationInformation = new ArrayList<>();
        final InstallationRecord record = installationEJB.getActiveInstallationRecordForDevice(device);
        if(record != null) {
            installationInformation = buildInstalledSlotInformation(record.getSlot());
        }
        return installationInformation;
    }

    private List<String> buildInstalledSlotInformation(Slot slot) {
        if (slot.getComponentType().getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
            final List<String> list = new ArrayList<>();
            list.add(slot.getName());
            return list;
        } else {
            final List<String> list = new ArrayList<>();
            for (SlotPair pair : slot.getChildrenSlotsPairList()) {
                if (pair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                    for (String parentPath : buildInstalledSlotInformation(pair.getParentSlot())) {
                        list.add(parentPath + "\u00A0\u00A0\u00BB\u00A0\u00A0" + slot.getName());
                    }
                }
            }
            return list;
        }
    }

    public boolean isSameDeviceType(ComponentType type1, ComponentType type2) {
        return type1 != null && type2 != null && type1.equals(type2);
    }
}
