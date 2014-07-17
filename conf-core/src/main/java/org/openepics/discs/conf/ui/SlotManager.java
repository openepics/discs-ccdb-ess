/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ui;

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

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotProperty;
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
public class SlotManager implements Serializable {

    @EJB
    private SlotEJB slotEJB;
    private static final Logger logger = Logger.getLogger(SlotManager.class.getCanonicalName());
    @Inject
    private BlobStore blobStore;
    @Inject private LoginManager loginManager;

    private List<Slot> objects;
    private List<Slot> sortedObjects;
    private List<Slot> filteredObjects;
    private Slot selectedObject;
    private Slot inputObject;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one

    // Property
    private List<SlotProperty> selectedProperties;
    private SlotProperty selectedProperty;
    private SlotProperty inputProperty;
    private boolean inRepository = false;
    private char propertyOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // Artifact
    private List<SlotArtifact> selectedArtifacts;
    private SlotArtifact inputArtifact;
    private boolean internalArtifact = true;
    private SlotArtifact selectedArtifact;
    private char artifactOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // File upload/download
    private String uploadedFileName;
    private boolean fileUploaded = false;
    private String repoFileId; // identifier of the file stored in content repo

    // Relationships
    private List<SlotPair> relatedSlots;
    private SlotPair inputSlotPair;
    private char relationOperation = 'n'; // selected operation on artifact: [a]dd, [e]dit, [d]elete, [n]one

    // Assembly
    private List<Slot> asmSlots; // parts of this slot
    private Slot inputAsmSlot;
    private String inputAsmComment;
    private String inputAsmPosition;

    /**
     * Creates a new instance of SlotManager
     */
    public SlotManager() {
    }

    @PostConstruct
    private void init() {
        try {
            objects = slotEJB.findLayoutSlot();
            // inputPartSlot = new Slot();
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
        selectedArtifacts = selectedObject.getSlotArtifactList();
        relatedSlots = selectedObject.getSlotPairList1();
        asmSlots = selectedObject.getSlotList();


        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Selected", "");
    }

    public void onSlotAdd(ActionEvent event) {
        selectedOp = 'a';
        // TODO replaced void constructor (now protected) with default values. Check!
        inputObject = new Slot("", false, loginManager.getUserid());
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Not deleted", e.getMessage());
        } finally {

        }
    }

    public void onSlotSave(ActionEvent event) {
        logger.info("Saving slot");
        try {
            // inputObject.setAssociation("T");
            // inputObject.setModifiedBy("test-user");
            slotEJB.saveLayoutSlot(inputObject);

            if (selectedOp == 'a') {
                selectedObject = inputObject;
                objects.add(selectedObject);
            }
            // tell the dialog panel if the operation was a success so that it can hide
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            // tell the dialog panel if the operation was a success so that it can hide
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

            // TODO replaced void constructor (now protected) with default values. Check!
            inputProperty = new SlotProperty(false, loginManager.getUserid());
            inputProperty.setSlot(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding property", e.getMessage());
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyDelete(SlotProperty ctp) {
        try {
            if (ctp == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No property selected");
                return;
            }
            slotEJB.deleteSlotProp(ctp);
            selectedProperties.remove(ctp);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted property", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting property", "Refresh the page");
            logger.severe(e.getMessage());
        }

    }

    public void onPropertyEdit(SlotProperty prop) {
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", e.getMessage());
        }
    }

    public void onPropertySave(ActionEvent event) {
        // SlotProperty ctp = (SlotProperty) event.getObject();
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

            slotEJB.saveSlotProp(inputProperty, propertyOperation == 'a');
            logger.log(Level.INFO, "returned artifact id is " + inputProperty.getId());

            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Property saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error:", e.getMessage());
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
            inputArtifact = new SlotArtifact("", false, "", "", loginManager.getUserid());
            inputArtifact.setSlot(selectedObject);
            fileUploaded = false;
            uploadedFileName = null;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding artifact", e.getMessage());
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

            slotEJB.saveSlotArtifact(inputArtifact, artifactOperation == 'a');
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Artifact not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onArtifactDelete(SlotArtifact art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
                return;
            }
            selectedArtifacts.remove(art); // ToDo: should this be done before or after delete from db?
            slotEJB.deleteSlotArtifact(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting artifact", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onArtifactEdit(SlotArtifact art) {
        if (art == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
            return;
        }
        artifactOperation = 'e';
        inputArtifact = art;
        uploadedFileName = art.getName();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated ");
    }

    public void onArtifactType() {
        // Toto: remove it
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Artifact type selected","");
    }

    // --------------------------------- Related Slots ------------------------------------------------
    public void onRelSlotAdd(ActionEvent event) {
        try {
            relationOperation = 'a';
            if (relatedSlots == null) {
                relatedSlots = new ArrayList<>();
            }
            inputSlotPair = new SlotPair();
            inputSlotPair.setParentSlot(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New related slot", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in adding related slot", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onRelSlotSave(ActionEvent event) {
        try {
            slotEJB.saveSlotPair(inputSlotPair, relationOperation == 'a');
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Related slot saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Related slot not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onRelSlotDelete(SlotPair art) {
        try {
            if (art == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No slot pair selected");
                return;
            }

            slotEJB.deleteSlotPair(art);
            relatedSlots.remove(art);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error in deleting related slot", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onRelSlotEdit(SlotPair art) {
        if (art == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
            return;
        }
        artifactOperation = 'e';
        inputSlotPair = art;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " );
    }

    // --------------------------------- Part Slots (assembly)------------------------------------------------
    public void onPartSlotAdd(ActionEvent event) {
        try {
            // relationOperation = 'a';
            if (asmSlots == null) {
                asmSlots = new ArrayList<>();
            }
            // inputPartSlot = new Slot();
            // inputPartSlot.setParentSlot(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "New part ", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in adding part", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onPartSlotSave(ActionEvent event) {
        try {
            inputAsmSlot.setAssemblyPosition(inputAsmPosition);
            inputAsmSlot.setAssemblyComment(inputAsmComment);
            inputAsmSlot.setAssemblySlot(selectedObject);
            slotEJB.saveLayoutSlot(inputAsmSlot);
            selectedObject.getSlotList().add(inputAsmSlot);
            slotEJB.saveLayoutSlot(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Part saved", "");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            // selectedArtifacts.remove(inputArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error: Part not saved", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
        }
    }

    public void onPartSlotDelete(Slot slot) {
        try {
            if (slot == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "Null slot selected");
                return;
            }

            slot.setAssemblyPosition(null);
            slot.setAssemblyComment(null);
            slot.setAssemblySlot(null);
            slotEJB.saveLayoutSlot(slot);
            selectedObject.getSlotList().remove(slot);
            slotEJB.saveLayoutSlot(selectedObject);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Deleted Artifact", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting artifact", e.getMessage());
            logger.severe(e.getMessage());
        }
    }

    public void onPartSlotEdit(Slot slot) {
        if (slot == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Strange", "No artifact selected");
            return;
        }
        // artifactOperation = 'e';
        inputAsmSlot = slot;
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Edit:", "Edit initiated " );
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

    public List<SlotArtifact> getSelectedArtifacts() {
        return selectedArtifacts;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }


    public SlotArtifact getInputArtifact() {
        return inputArtifact;
    }

    public boolean isInternalArtifact() {
        return internalArtifact;
    }

    public void setInternalArtifact(boolean internalArtifact) {
        this.internalArtifact = internalArtifact;
    }

    public char getArtifactOperation() {
        return artifactOperation;
    }

    public SlotArtifact getSelectedArtifact() {
        return selectedArtifact;
    }

    public void setSelectedArtifact(SlotArtifact selectedArtifact) {
        this.selectedArtifact = selectedArtifact;
    }

    public List<SlotPair> getRelatedSlots() {
        return relatedSlots;
    }

    public char getRelationOperation() {
        return relationOperation;
    }

    public SlotPair getInputSlotPair() {
        return inputSlotPair;
    }

    public void setInputSlotPair(SlotPair inputSlotPair) {
        this.inputSlotPair = inputSlotPair;
    }

    public List<Slot> getAsmSlots() {
        return asmSlots;
    }

    public Slot getInputAsmSlot() {
        return inputAsmSlot;
    }

    public void setInputAsmSlot(Slot inputAsmSlot) {
        this.inputAsmSlot = inputAsmSlot;
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

    public char getPropertyOperation() {
        return propertyOperation;
    }

    public SlotProperty getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(SlotProperty selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public SlotProperty getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(SlotProperty inputProperty) {
        this.inputProperty = inputProperty;
    }

    public boolean isInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
    }

    public List<SlotProperty> getSelectedProperties() {
        return selectedProperties;
    }

}
