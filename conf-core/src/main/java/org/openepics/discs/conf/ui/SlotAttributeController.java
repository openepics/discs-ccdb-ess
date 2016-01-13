/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.TagEJB;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.views.EntityAttrArtifactView;
import org.openepics.discs.conf.views.EntityAttrPropertyValueView;
import org.openepics.discs.conf.views.EntityAttrTagView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class SlotAttributeController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private transient SlotEJB slotEJB;
    @Inject private transient InstallationEJB installationEJB;
    @Inject private transient PropertyEJB propertyEJB;
    @Inject private transient TagEJB tagEJB;
    @Inject private transient BlobStore blobStore;

    private transient HierarchiesController hierarchiesController;

    private transient List<EntityAttributeView<Slot>> attributes;
    private transient List<EntityAttributeView<Slot>> filteredAttributes;
    private transient EntityAttributeView<Slot> dialogAttribute;
    private transient List<SelectItem> attributeKinds;

    // ------ variables for attribute manipulation ------
    private transient List<EntityAttributeView<Slot>> selectedAttributes;
    private transient EntityAttributeView<Slot> downloadAttribute;
    private transient List<Property> filteredProperties;
    private transient List<String> tagsForAutocomplete;

    public SlotAttributeController() {}

    @PostConstruct
    public void init() {
        attributeKinds = UiUtility.buildAttributeKinds();
    }

    /** Tell this bean which {@link HierarchiesController} is its "master"
     * @param hierarchiesController the {@link HierarchiesController} master bean
     */
    protected void setUIParent(HierarchiesController hierarchiesController) {
        this.hierarchiesController = hierarchiesController;
    }

    /** Add the attributes connected to one {@link Slot} to the attribute list.
     * @param slot the {@link Slot} to add the attributes for
     * @param forceInit if <code>true</code> recreate the attribute list even if it already contains some information
     * and add just the one {@link Slot} info to the new list. If <code>false</code> add the attribute information to
     * the already existing list.
     */
    protected void initAttributeList(final Slot slot, final boolean forceInit) {
        if (forceInit || attributes == null) {
            attributes = Lists.newArrayList();
        }
        addPropertyValues(slot);
        addArtifacts(slot);
        addTags(slot);
    }

    /** Called from {@link HierarchiesController} when the user deselects everything. */
    protected void clearAttributeInformation() {
        attributes = null;
        filteredAttributes = null;
        selectedAttributes = null;
    }

    private void refreshAttributeList(final Slot slot, final SlotPropertyValue propertyValue) {
        // Use iterator. If the property value is found, then update it.
        // If not, add new property value to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView<Slot>> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView<Slot> tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
                // the entity's real sibling

                if (tableAttribute.getEntity().equals(propertyValue)) {
                    // found the existing artifact, update it and exit!
                    attributesIter.set(new EntityAttrPropertyValueView<Slot>(propertyValue, slot));
                    return;
                }

                if (tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT) {
                    // we just encountered our sibling ARTIFACT. Insert before that.
                    attributesIter.previous();
                    break;
                }
            } else if (encounteredParentSiblings) {
                // we just moved past all our siblings. Move one back and break;
                attributesIter.previous();
                break;
            }
        }
        // the insertion pointer is at the right spot. This is either the last property value for this parent,
        //   the last attribute for this parent (no artifacts and tags), or the very last attribute in the entire table
        attributesIter.add(new EntityAttrPropertyValueView<Slot>(propertyValue, slot));
    }

    private void refreshAttributeList(final Slot slot, final Tag tag) {
        // Use iterator. Add new Tag to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView<Slot>> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView<Slot> tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
            } else if (encounteredParentSiblings) {
                // we just moved past all our siblings. Move one back and break.
                attributesIter.previous();
                break;
            }
        }
        // the insertion pointer is at the right spot. This is either the last attribute for this parent,
        //     or the very last attribute in the entire table
        attributesIter.add(new EntityAttrTagView<Slot>(tag, slot));
    }

    private void refreshAttributeList(final Slot slot, final SlotArtifact artifact) {
        // Use iterator. If the artifact is found, then update it.
        // If not, add new artifact to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView<Slot>> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView<Slot> tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
                // the entity's real sibling

                if (tableAttribute.getEntity().equals(artifact)) {
                    // found the existing artifact, update it and exit!
                    attributesIter.set(new EntityAttrArtifactView<Slot>(artifact, slot));
                    return;
                }

                if (tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_TYPE_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.INSTALL_SLOT_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.CONTAINER_SLOT_TAG) {
                    // we just encountered our sibling TAG. Insert before that.
                    attributesIter.previous();
                    break;
                }
            } else if (encounteredParentSiblings) {
                // we just moved past all our siblings. Move one back and break;
                attributesIter.previous();
                break;
            }
        }
        // the insertion pointer is at the right spot. This is either the last artifact for this parent,
        //     the last attribute for this parent (no tags), or the very last attribute in the entire table
        attributesIter.add(new EntityAttrArtifactView<Slot>(artifact, slot));
    }

    /** Remove the attributes connected to one {@link Slot} from the attribute list and the
     * list of selected attributes if necessary.
     * @param slot the {@link Slot} to remove the information for
     */
    protected void removeRelatedAttributes(Slot slot) {
        final ListIterator<EntityAttributeView<Slot>> slotAttributes = attributes.listIterator();
        while (slotAttributes.hasNext()) {
            final EntityAttributeView<Slot> attribute = slotAttributes.next();
            if (slot.getName().equals(attribute.getParent())) {
                slotAttributes.remove();
            }
        }
        if (selectedAttributes != null) {
            Iterator<EntityAttributeView<Slot>> i = selectedAttributes.iterator();
            while (i.hasNext()) {
                EntityAttributeView<Slot> selectedAttribute = i.next();
                if (selectedAttribute.getParent().equals(slot.getName())) i.remove();
            }
        }
    }

    private void addPropertyValues(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final ComptypePropertyValue value : slot.getComponentType().getComptypePropertyList()) {
            if (!value.isPropertyDefinition()) {
                attributes.add(new EntityAttrPropertyValueView<Slot>(value, slot, slot.getComponentType()));
            }
        }

        for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
            attributes.add(new EntityAttrPropertyValueView<Slot>(value, slot));
        }

        if (activeInstallationRecord != null) {
            for (final DevicePropertyValue devicePropertyValue : activeInstallationRecord.getDevice().
                                                                                        getDevicePropertyList()) {
                attributes.add(new EntityAttrPropertyValueView<Slot>(devicePropertyValue,
                                                            EntityAttributeViewKind.DEVICE_PROPERTY,
                                                            slot,
                                                            activeInstallationRecord.getDevice()));
            }
        }
    }

    private void addArtifacts(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final ComptypeArtifact artifact : slot.getComponentType().getComptypeArtifactList()) {
            attributes.add(new EntityAttrArtifactView<Slot>(artifact, slot, slot.getComponentType()));
        }

        for (final SlotArtifact artifact : slot.getSlotArtifactList()) {
            attributes.add(new EntityAttrArtifactView<Slot>(artifact, slot));
        }

        if (activeInstallationRecord != null) {
            for (final DeviceArtifact deviceArtifact : activeInstallationRecord.getDevice().getDeviceArtifactList()) {
                attributes.add(new EntityAttrArtifactView<Slot>(deviceArtifact, slot, activeInstallationRecord.getDevice()));
            }
        }
    }

    private void addTags(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final Tag tagInstance : slot.getComponentType().getTags()) {
            attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot, slot.getComponentType()));
        }

        for (final Tag tagInstance : slot.getTags()) {
            attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot));
        }

        if (activeInstallationRecord != null) {
            for (final Tag tagInstance : activeInstallationRecord.getDevice().getTags()) {
                attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot, activeInstallationRecord.getDevice()));
            }
        }
    }

    /** @return <code>true</code> if the attribute "Delete" button can be enabled, <code>false</code> otherwise */
    public boolean canDeleteAttributes() {
        if (selectedAttributes == null || selectedAttributes.size() == 0) {
            return false;
        }
        for (EntityAttributeView<Slot> selectedAttribute : selectedAttributes)
            switch (selectedAttribute.getKind()) {
                case CONTAINER_SLOT_ARTIFACT:
                case CONTAINER_SLOT_TAG:
                case CONTAINER_SLOT_PROPERTY:
                case INSTALL_SLOT_ARTIFACT:
                case INSTALL_SLOT_TAG:
                    continue;
                case INSTALL_SLOT_PROPERTY:
                    if (selectedAttribute.getValue() != null) {
                        continue;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        return true;
    }

    /** The handler called from the "Delete confirmation" dialog. This actually deletes an attribute */
    public void deleteAttributes() {
        Preconditions.checkNotNull(selectedAttributes);
        int props = 0;
        for (EntityAttributeView<Slot> selectedAttribute : selectedAttributes) {
            final Slot slot = slotEJB.findByName(selectedAttribute.getParent());
            switch (selectedAttribute.getKind()) {
                case INSTALL_SLOT_ARTIFACT:
                case CONTAINER_SLOT_ARTIFACT:
                case CONTAINER_SLOT_PROPERTY:
                    slotEJB.deleteChild(selectedAttribute.getEntity());
                    hierarchiesController.refreshSlot(slot);
                    props++;
                    break;
                case INSTALL_SLOT_TAG:
                case CONTAINER_SLOT_TAG:
                    slot.getTags().remove(selectedAttribute.getEntity());
                    hierarchiesController.saveSlotAndRefresh(slot);
                    props++;
                    break;
                case INSTALL_SLOT_PROPERTY:
                    SlotPropertyValue prop = ((SlotPropertyValue)selectedAttribute.getEntity());
                    prop.setPropValue(null);
                    slotEJB.saveChild(prop);
                    hierarchiesController.refreshSlot(slot);
                    props++;
                    final SlotPropertyValue freshPropertyValue = slotEJB.refreshPropertyValue(prop);
                    refreshAttributeList(slot, freshPropertyValue);
                    break;
                default:
                    throw new RuntimeException("Trying to delete an attribute that cannot be removed on home screen.");
            }
            attributes.remove(selectedAttribute);
        }
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                "Deleted " + props + " properties.");
        selectedAttributes = null;
    }

    /** @return <code>true</code> if the attribute "Edit" button can be enables, <code>false</code> otherwise */
    public boolean canEditAttribute() {
        if (selectedAttributes == null || selectedAttributes.size() != 1) {
            return false;
        }
        switch (selectedAttributes.get(0).getKind()) {
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_PROPERTY:
            case INSTALL_SLOT_ARTIFACT:
            case INSTALL_SLOT_PROPERTY:
                return true;
            default:
                return false;
        }
    }

    /** Prepares the information for the "Edit" attribute dialog. */
    public void prepareModifyAttributePopup() {
        Preconditions.checkNotNull(selectedAttributes);
        Preconditions.checkState(selectedAttributes.size() == 1);

        final EntityAttributeView<Slot> selectedAttrView = selectedAttributes.get(0);

        if (selectedAttrView instanceof EntityAttrPropertyValueView<?>) {
            final EntityAttrPropertyValueView<Slot> propertyValueView = new EntityAttrPropertyValueView<Slot>(
                    slotEJB.refreshPropertyValue((PropertyValue)selectedAttrView.getEntity()), selectedAttrView.getParentEntity());
            final boolean propertyNameChangeDisabled = propertyValueView.getParentEntity().isHostingSlot();
            propertyValueView.setPropertyNameChangeDisabled(propertyNameChangeDisabled);
            if (!propertyNameChangeDisabled) {
                filterProperties();
            }
            dialogAttribute = propertyValueView;

            RequestContext.getCurrentInstance().update("modifyPropertyValueForm:modifyPropertyValue");
            RequestContext.getCurrentInstance().execute("PF('modifyPropertyValue').show();");
        }

        if (selectedAttrView instanceof EntityAttrArtifactView<?>) {
            final EntityAttrArtifactView<Slot> view = new EntityAttrArtifactView<Slot>(
                    slotEJB.refreshArtifact((Artifact)selectedAttrView.getEntity()), selectedAttrView.getParentEntity());
            dialogAttribute = view;

            RequestContext.getCurrentInstance().update("modifyArtifactForm:modifyArtifact");
            RequestContext.getCurrentInstance().execute("PF('modifyArtifact').show();");
        }
    }

    /** Prepares data for addition of {@link PropertyValue}. Only valid for containers. */
    public void prepareForPropertyValueAdd() {
        selectedAttributes = null;
        final SlotPropertyValue slotValueInstance = new SlotPropertyValue(false);
        slotValueInstance.setPropertiesParent(hierarchiesController.getSelectedNodeSlot());
        dialogAttribute = new EntityAttrPropertyValueView<Slot>(slotValueInstance, hierarchiesController.getSelectedEntity());
        filterProperties();
    }

    private void refreshAllPropertyValues() {
        final List<SlotPropertyValue> propertyValues = hierarchiesController.getSelectedNodeSlot().getSlotPropertyList();
        for (final SlotPropertyValue propValue : propertyValues) {
            refreshAttributeList(hierarchiesController.getSelectedNodeSlot(), propValue);
        }
    }

    /** The handler called to save or add a new value of the {@link SlotPropertyValue} after modification */
    public void modifyPropertyValue() {
        try {
            final EntityAttrPropertyValueView<Slot> view = getDialogAttrPropertyValue();
            final PropertyValue slotValueInstance = view.getEntity();
            if (view.isBeingAdded()) {
                slotEJB.addChild(slotValueInstance);
                hierarchiesController.refreshSlot(view.getParentEntity());
                refreshAllPropertyValues();
                UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                        "New property has been created");
            } else {
                slotEJB.saveChild(slotValueInstance);
                hierarchiesController.refreshSlot(view.getParentEntity());
                final SlotPropertyValue freshPropertyValue = slotEJB.refreshPropertyValue((SlotPropertyValue)slotValueInstance);
                refreshAttributeList(view.getParentEntity(), freshPropertyValue);;
                UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                        "Property value has been modified");
            }
        } catch (EJBException e) {
            if (UiUtility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                FacesContext.getCurrentInstance().addMessage("uniqueMessage",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                "Value is not unique."));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                throw e;
            }
        }
    }

    private void filterProperties() {
        final List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();

        final Property dialogProperty = getDialogAttrPropertyValue() != null
                                            ? getDialogAttrPropertyValue().getProperty() : null;

        // remove all properties that are already defined.
        for (final SlotPropertyValue slotPropertyValue : hierarchiesController.getSelectedNodeSlot().getSlotPropertyList()) {
            if (!slotPropertyValue.getProperty().equals(dialogProperty)) {
                propertyCandidates.remove(slotPropertyValue.getProperty());
            }
        }

        filteredProperties = propertyCandidates;
    }

    /** Prepares the UI data for addition of {@link Tag} */
    public void prepareForTagAdd() {
        fillTagsAutocomplete();
        dialogAttribute = new EntityAttrTagView<Slot>(hierarchiesController.getSelectedEntity());
    }

    /** Adds new {@link Tag} to parent {@link ConfigurationEntity} */
    public void addNewTag() {
        try {
            final EntityAttrTagView<Slot> tagView = getDialogAttrTag();
            Tag tag = tagEJB.findById(tagView.getTag());
            if (tag == null) {
                tag = tagView.getEntity();
            }

            Slot ent = tagView.getParentEntity();
            final Set<Tag> existingTags = ent.getTags();
            if (!existingTags.contains(tag)) {
                existingTags.add(tag);
                hierarchiesController.saveSlotAndRefresh(ent);
                refreshAttributeList(ent, tag);
                UiUtility.showMessage(FacesMessage.SEVERITY_INFO, "Tag added", tag.getName());
            }
        } finally {
            fillTagsAutocomplete();
            selectedAttributes = null;
            dialogAttribute = null;
        }
    }

    private void fillTagsAutocomplete() {
        tagsForAutocomplete = ImmutableList.copyOf(Lists.transform(tagEJB.findAllSorted(), Tag::getName));
    }

    /** Used by the {@link Tag} input value control to display the list of auto-complete suggestions. The list contains
     * the tags already stored in the database.
     * @param query The text the user typed so far.
     * @return The list of auto-complete suggestions.
     */
    public List<String> tagAutocompleteText(String query) {
        final List<String> resultList = new ArrayList<String>();
        final String queryUpperCase = query.toUpperCase();
        for (final String element : tagsForAutocomplete) {
            if (element.toUpperCase().startsWith(queryUpperCase))
                resultList.add(element);
        }
        return resultList;
    }

    /** Prepares the UI data for addition of {@link Artifact} */
    public void prepareForArtifactAdd() {
        final Artifact artifact = new SlotArtifact();
        final Slot selectedEntity = hierarchiesController.getSelectedEntity();
        artifact.setArtifactsParent(selectedEntity);
        dialogAttribute = new EntityAttrArtifactView<Slot>(artifact, selectedEntity);
    }

    private void refreshAllArtifacts() {
        final List<SlotArtifact> slotArtifacts = hierarchiesController.getSelectedNodeSlot().getSlotArtifactList();
        for (final SlotArtifact artifact : slotArtifacts) {
            refreshAttributeList(hierarchiesController.getSelectedNodeSlot(), artifact);
        }
    }

    /** Modifies the selected artifact properties
     * @throws IOException if attachment file operation has failed. */
    public void modifyArtifact() throws IOException {
        try {
            final EntityAttrArtifactView<Slot> artifactView = getDialogAttrArtifact();

            if (artifactView.isArtifactInternal()) {
                final byte[] importData = artifactView.getImportData();
                if (importData != null) {
                    if (artifactView.isArtifactBeingModified()) {
                        blobStore.deleteFile(artifactView.getArtifactURI());
                    }
                    artifactView.setArtifactURI(blobStore.storeFile(new ByteArrayInputStream(importData)));
                }
            }

            final Artifact artifactInstance = artifactView.getEntity();
            final Slot parentSlot = artifactView.getParentEntity();

            if (artifactView.isArtifactBeingModified()) {
                slotEJB.saveChild(artifactInstance);
                final SlotArtifact freshArtifact = slotEJB.refreshArtifact((SlotArtifact)artifactInstance);
                refreshAttributeList(parentSlot, freshArtifact);
            } else {
                slotEJB.addChild(artifactInstance);
                refreshAllArtifacts();
            }
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    artifactView.isArtifactBeingModified() ? "Artifact has been modified" : "New artifact has been created");

            hierarchiesController.refreshSlot(parentSlot);
        } finally {
            dialogAttribute = null;
        }
    }

    /**
     * Finds artifact file that was uploaded on the file system and returns it to be downloaded
     *
     * @return Artifact file to be downloaded
     * @throws FileNotFoundException Thrown if file was not found on file system
     */
    public StreamedContent getDownloadFile() throws FileNotFoundException {
        final Artifact downloadArtifact = (Artifact) downloadAttribute.getEntity();
        final String filePath = blobStore.getBlobStoreRoot() + File.separator + downloadArtifact.getUri();
        final String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(filePath);

        return new DefaultStreamedContent(new FileInputStream(filePath), contentType, downloadArtifact.getName());
    }

    /** Called from various attribute related dialogs. */
    public void resetFields() {
        dialogAttribute = null;
    }

    /**
     * @return The list of attributes (property values, artifacts and tags) for at all levels:
     * <ul>
     * <li>device type properties</li>
     * <li>container or installation slot properties</li>
     * </ul>
     */
    public List<EntityAttributeView<Slot>> getAttributes() {
        return attributes;
    }
    public void setAttrbutes(List<EntityAttributeView<Slot>> attributes) {
        this.attributes = attributes;
    }

    /** @return the filteredAttributes */
    public List<EntityAttributeView<Slot>> getFilteredAttributes() {
        return filteredAttributes;
    }
    /** @param filteredAttributes the filteredAttributes to set */
    public void setFilteredAttributes(List<EntityAttributeView<Slot>> filteredAttributes) {
        this.filteredAttributes = filteredAttributes;
    }

    public List<SelectItem> getAttributeKinds() {
        return attributeKinds;
    }

    /** @return the selectedAttributes */
    public List<EntityAttributeView<Slot>> getSelectedAttributes() {
        return selectedAttributes;
    }
    /** @param selectedAttributes the selectedAttributes to set */
    public void setSelectedAttributes(List<EntityAttributeView<Slot>> selectedAttributes) {
        this.selectedAttributes = selectedAttributes;
    }

    /** @return the downloadAttribute */
    public EntityAttributeView<Slot> getDownloadAttribute() {
        return downloadAttribute;
    }
    /** @param downloadAttribute the downloadAttribute to set */
    public void setDownloadAttribute(EntityAttributeView<Slot> downloadAttribute) {
        this.downloadAttribute = downloadAttribute;
    }

    /** @return the filteredProperties */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    /** @return the dialogAttribute */
    public EntityAttrTagView<Slot> getDialogAttrTag() {
        if (dialogAttribute instanceof EntityAttrTagView<?>) {
            return (EntityAttrTagView<Slot>)dialogAttribute;
        }
        return null;
    }

    /** @return the dialogAttribute */
    public EntityAttrPropertyValueView<Slot> getDialogAttrPropertyValue() {
        if (dialogAttribute instanceof EntityAttrPropertyValueView<?>) {
            return (EntityAttrPropertyValueView<Slot>)dialogAttribute;
        }
        return null;
    }

    /** @return the dialogAttribute */
    public EntityAttrArtifactView<Slot> getDialogAttrArtifact() {
        if (dialogAttribute instanceof EntityAttrArtifactView<?>) {
            return (EntityAttrArtifactView<Slot>)dialogAttribute;
        }
        return null;
    }
}
