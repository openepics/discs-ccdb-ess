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
package org.openepics.discs.conf.ui.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ejb.TagEJB;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityWithArtifacts;
import org.openepics.discs.conf.ent.EntityWithProperties;
import org.openepics.discs.conf.ent.EntityWithTags;
import org.openepics.discs.conf.ent.NamedEntity;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.EntityAttrArtifactView;
import org.openepics.discs.conf.views.EntityAttrPropertyValueView;
import org.openepics.discs.conf.views.EntityAttrTagView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Parent class for all classes that handle entity attributes manipulation
 *
 * @param <T> There are 4 property value tables in the database, but all have the same columns and interface.
 * @param <S> There are 4 artifact tables in the database, but all have the same columns and interface.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public abstract class AbstractAttributesController
        <C extends ConfigurationEntity & NamedEntity & EntityWithTags & EntityWithArtifacts,
        T extends PropertyValue,
        S extends Artifact> implements Serializable {
    private static final long serialVersionUID = 523935015308933240L;

    protected static enum DefinitionTarget { SLOT, DEVICE }

    @Inject protected BlobStore blobStore;
    @Inject protected TagEJB tagEJB;
    @Inject protected DataTypeEJB dataTypeEJB;

    private List<String> tagsForAutocomplete;

    protected List<EntityAttributeView<C>> attributes;
    protected List<EntityAttributeView<C>> filteredAttributes;
    private final List<SelectItem> attributeKinds = Utility.buildAttributeKinds();
    protected List<EntityAttributeView<C>> selectedAttributes;
    protected List<EntityAttributeView<C>> nonDeletableAttributes;
    private List<EntityAttributeView<C>> filteredDialogAttributes;

    protected EntityAttributeView<C> dialogAttribute;

    protected EntityAttrArtifactView<C> downloadArtifactView;

    private DAO<C> dao;

    protected void resetFields() {
        dialogAttribute = null;
    }

    /**
     * Adds or modifies an artifact
     *
     * @throws IOException thrown if file in the artifact could not be stored on the file system
     */
    public void modifyArtifact() throws IOException {
        try {
            final EntityAttrArtifactView<C> artifactView = getDialogAttrArtifact();

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

            if (artifactView.isArtifactBeingModified()) {
                dao.saveChild(artifactInstance);
            } else {
                dao.addChild(artifactInstance);
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    artifactView.isArtifactBeingModified() ? "Artifact has been modified" : "New artifact has been created");
        } finally {
            resetFields();
            internalPopulateAttributesList();
        }
    }

    /** Adds new {@link Tag} to parent {@link ConfigurationEntity} */
    public void addNewTag() {
        try {
            final EntityAttrTagView<C> tagView = getDialogAttrTag();
            Tag tag = tagEJB.findById(tagView.getTag());
            if (tag == null) {
                tag = tagView.getEntity();
            }

            C ent = tagView.getParentEntity();
            final Set<Tag> existingTags = ent.getTags();
            if (!existingTags.contains(tag)) {
                existingTags.add(tag);
                dao.save(ent);
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Tag added", tag.getName());
            }
        } finally {
            resetFields();
            internalPopulateAttributesList();
        }
    }

    /**
     * The method builds a list of units that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkAttributesForDeletion() {
        Preconditions.checkNotNull(selectedAttributes);
        Preconditions.checkState(!selectedAttributes.isEmpty());

        filteredDialogAttributes = Lists.newArrayList();
        nonDeletableAttributes = Lists.newArrayList();
        for (final EntityAttributeView<C> attrToDelete : selectedAttributes) {
            if (!canDelete(attrToDelete)) {
                nonDeletableAttributes.add(attrToDelete);
            }
        }
    }

    /**
     * Deletes selected attributes from parent {@link ConfigurationEntity}.
     * These attributes can be {@link Tag}, {@link PropertyValue} or {@link Artifact}
     *
     * @throws IOException attribute deletion failure
     */
    @SuppressWarnings("unchecked")
    public void deleteAttributes() throws IOException {
        Preconditions.checkNotNull(selectedAttributes);
        Preconditions.checkState(!selectedAttributes.isEmpty());
        Preconditions.checkNotNull(dao);
        Preconditions.checkNotNull(nonDeletableAttributes);
        Preconditions.checkState(nonDeletableAttributes.isEmpty());

        int deletedAttributes = 0;
        for (final EntityAttributeView<C> attributeToDelete : selectedAttributes) {
            if (attributeToDelete instanceof EntityAttrPropertyValueView<?>) {
                deletePropertyValue((T) attributeToDelete.getEntity());
            } else if (attributeToDelete instanceof EntityAttrArtifactView<?>) {
                deleteArtifact((S) attributeToDelete.getEntity());
            } else if (attributeToDelete instanceof EntityAttrTagView<?>) {
                final Tag tagAttr = (Tag) attributeToDelete.getEntity();
                deleteTagFromParent(attributeToDelete.getParentEntity(), tagAttr);
            } else {
                throw new UnhandledCaseException();
            }

            ++deletedAttributes;
        }

        selectedAttributes = null;
        nonDeletableAttributes = null;
        internalPopulateAttributesList();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                "Deleted " + deletedAttributes + " attributes.");
    }

    protected void deletePropertyValue(final T propValueToDelete) {
        if (!isPropertyValueInherited(propValueToDelete)) {
            dao.deleteChild(propValueToDelete);
        } else {
            propValueToDelete.setPropValue(null);
            dao.saveChild(propValueToDelete);
        }
    }

    private void deleteArtifact(final S artifactToDelete) throws IOException {
        if (artifactToDelete.isInternal()) {
            blobStore.deleteFile(artifactToDelete.getUri());
        }
        dao.deleteChild(artifactToDelete);
    }

    /**
     * The main method that prepares the fields for any of the following dialogs:
     * <ul>
     * <li>the dialog to modify a property value</li>
     * <li>the dialog to modify an artifact data</li>
     *</ul>
     */
    public void prepareModifyPropertyPopUp() {
        Preconditions.checkNotNull(selectedAttributes);
        Preconditions.checkState(selectedAttributes.size() == 1);
        dialogAttribute = selectedAttributes.get(0);

        if (getDialogAttrPropertyValue() != null) {
            final EntityAttrPropertyValueView<C> propertyValueView = getDialogAttrPropertyValue();
            PropertyValue propertyValue = propertyValueView.getEntity();
            propertyValueView.setPropertyNameChangeDisabled(propertyValue instanceof DevicePropertyValue
                        || propertyValue instanceof SlotPropertyValue
                        || isPropertyValueInherited(propertyValue));
            filterProperties();

            RequestContext.getCurrentInstance().update("modifyPropertyValueForm:modifyPropertyValue");
            RequestContext.getCurrentInstance().execute("PF('modifyPropertyValue').show();");
        }

        if (getDialogAttrArtifact() != null) {
            RequestContext.getCurrentInstance().update("modifyArtifactForm:modifyArtifact");
            RequestContext.getCurrentInstance().execute("PF('modifyArtifact').show();");
        }
    }

    /** Modifies {@link PropertyValue} */
    public void modifyPropertyValue() {
        Preconditions.checkNotNull(dialogAttribute);

        try {
            dao.saveChild(dialogAttribute.getEntity());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                        "Property value has been modified");
        } catch (EJBException e) {
            if (Utility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                FacesContext.getCurrentInstance().addMessage("uniqueMessage",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                "Value is not unique."));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                throw e;
            }
        } finally {
            resetFields();
            internalPopulateAttributesList();
        }
    }

    /**
     * Finds artifact file that was uploaded on the file system and returns it to be downloaded
     *
     * @return Artifact file to be downloaded
     * @throws FileNotFoundException Thrown if file was not found on file system
     */
    @SuppressWarnings("unchecked")
    public StreamedContent getDownloadFile() throws FileNotFoundException {
        final S selectedArtifact = (S) downloadArtifactView.getEntity();
        final String filePath = blobStore.getBlobStoreRoot() + File.separator + selectedArtifact.getUri();
        // guess mime type based on the original file name, not on the name of the blob (UUID).
        final String contentType = FacesContext.getCurrentInstance().getExternalContext()
                                                                .getMimeType(selectedArtifact.getName());

        return new DefaultStreamedContent(new FileInputStream(filePath), contentType, selectedArtifact.getName());
    }

    public EntityAttributeView<C> getDownloadArtifact() {
        return downloadArtifactView;
    }

    public void setDownloadArtifact(EntityAttrArtifactView<C> downloadArtifact) {
        this.downloadArtifactView = downloadArtifact;
    }

    /** This method determines whether the entity attribute should have the "pencil" icon displayed in the UI.
     * @param attributeView The object containing the UI info for the attribute table row.
     * @return <code>true</code> if the attribute can be edited, <code>false</code> otherwise.
     */
    public abstract boolean canEdit(EntityAttributeView<C> attributeView);

    /** This method determines whether the entity attribute can be deleted - is not used anywhere.
     *
     * @param attributeView The object containing the UI info for the attribute table row.
     * @return <code>true</code> if the attribute can be deleted, <code>false</code> otherwise.
     */
    protected abstract boolean canDelete(EntityAttributeView<C> attributeView);

    private boolean isPropertyValueInherited(PropertyValue propValue) {
        List<ComptypePropertyValue> parentProperties = null;
        EntityWithProperties parentEntity = propValue.getPropertiesParent();
        if (parentEntity != null) {
            if (parentEntity instanceof Slot) {
                if (((Slot) parentEntity).isHostingSlot()) {
                    parentProperties = ((Slot) parentEntity).getComponentType().getComptypePropertyList();
                } else {
                    return false;
                }
            } else if (parentEntity instanceof Device) {
                parentProperties = ((Device) parentEntity).getComponentType().getComptypePropertyList();
            } else if (parentEntity instanceof ComponentType) {
                return false;
            } else {
                throw new UnhandledCaseException();
            }
        }

        if (Utility.isNullOrEmpty(parentProperties)) {
            return false;
        }

        final String propertyName = propValue.getProperty().getName();
        for (final ComptypePropertyValue inheritedPropVal : parentProperties) {
            if (inheritedPropVal.isPropertyDefinition() && (
                    (inheritedPropVal.isDefinitionTargetDevice() && parentEntity instanceof Device) ||
                    (inheritedPropVal.isDefinitionTargetSlot() && parentEntity instanceof Slot) )
                    && propertyName.equals(inheritedPropVal.getProperty().getName())) {
                return true;
            }
        }
        return false;
    }

    protected abstract C getSelectedEntity();

    protected abstract T newPropertyValue();

    protected abstract S newArtifact();

    protected void deleteTagFromParent(C parent, Tag tag) {
        Preconditions.checkNotNull(parent);
        parent.getTags().remove(tag);
        dao.save(parent);
    }

    /** Filters a list of possible properties to attach to the entity based on the association type. */
    protected abstract void filterProperties();

    protected abstract void populateAttributesList();

    private void internalPopulateAttributesList() {
        fillTagsAutocomplete();
        populateAttributesList();
    }

    private void fillTagsAutocomplete() {
        tagsForAutocomplete = ImmutableList.copyOf(Lists.transform(tagEJB.findAllSorted(), Tag::getName));
    }

    /**
     * Returns list of all attributes for current {@link ConfigurationEntity}
     * @return the list of attributes
     */
    public List<EntityAttributeView<C>> getAttributes() {
        return attributes;
    }

    /** Prepares the UI data for addition of {@link Tag} */
    public void prepareForTagAdd() {
        fillTagsAutocomplete();
        dialogAttribute = new EntityAttrTagView<C>(getSelectedEntity());
    }

    /** Prepares the UI data for addition of {@link Artifact} */
    public void prepareForArtifactAdd() {
        final Artifact artifact = newArtifact();
        final C selectedEntity = getSelectedEntity();
        artifact.setArtifactsParent(selectedEntity);
        dialogAttribute = new EntityAttrArtifactView<C>(artifact, selectedEntity);
    }

    /** @return the selected table rows (UI view presentation)
     */
    public List<EntityAttributeView<C>> getSelectedAttributes() {
        return selectedAttributes;
    }
    /** @param selectedAttributes a list of  property values, tags and  artifacts */
    public void setSelectedAttributes(List<EntityAttributeView<C>> selectedAttributes) {
        this.selectedAttributes = selectedAttributes;
    }

    protected void setDao(DAO<C> dao) {
        this.dao = dao;
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

    /** @return the filteredAttributes */
    public List<EntityAttributeView<C>> getFilteredAttributes() {
        return filteredAttributes;
    }

    /** @param filteredAttributes the filteredAttributes to set */
    public void setFilteredAttributes(List<EntityAttributeView<C>> filteredAttributes) {
        this.filteredAttributes = filteredAttributes;
    }

    /** @return the attributeKinds */
    public List<SelectItem> getAttributeKinds() {
        return attributeKinds;
    }

    /**
     * @return <code>true</code> if a single attribute is selected, <code>false</code> otherwise.
     */
    public boolean isSingleAttributeSelected() {
        return (selectedAttributes != null) && (selectedAttributes.size() == 1);
    }

    /** This method is called from the UI and resets a table with the implicit ID "propertySelect" in the form
     * indicated by the parameter.
     * @param id the ID of the from containing a table #propertySelect
     */
    public void resetPropertySelection(final String id) {
        final DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent(id + ":propertySelect");
        dataTable.setSortBy(null);
        dataTable.setFirst(0);
        dataTable.setFilteredValue(null);
        dataTable.setFilters(null);
    }

    /** @return the nonDeletableAttributes */
    public List<EntityAttributeView<C>> getNonDeletableAttributes() {
        return nonDeletableAttributes;
    }

    /** @return the filteredDialogAttributes */
    public List<EntityAttributeView<C>> getFilteredDialogAttributes() {
        return filteredDialogAttributes;
    }

    /** @param filteredDialogAttributes the filteredDialogAttributes to set */
    public void setFilteredDialogAttributes(List<EntityAttributeView<C>> filteredDialogAttributes) {
        this.filteredDialogAttributes = filteredDialogAttributes;
    }

    /** @return the dialogAttribute */
    public EntityAttrTagView<C> getDialogAttrTag() {
        if (dialogAttribute instanceof EntityAttrTagView<?>) {
            return (EntityAttrTagView<C>)dialogAttribute;
        }
        return null;
    }

    /** @return the dialogAttribute */
    public EntityAttrPropertyValueView<C> getDialogAttrPropertyValue() {
        if (dialogAttribute instanceof EntityAttrPropertyValueView<?>) {
            return (EntityAttrPropertyValueView<C>)dialogAttribute;
        }
        return null;
    }

    /** @return the dialogAttribute */
    public EntityAttrArtifactView<C> getDialogAttrArtifact() {
        if (dialogAttribute instanceof EntityAttrArtifactView<?>) {
            return (EntityAttrArtifactView<C>)dialogAttribute;
        }
        return null;
    }
}
