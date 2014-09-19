/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonReader;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.TagEJB;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyDataType;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.core.Seds;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

/**
 * Parent class for all classes that handle entity attributes manipulation
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 * @param <T>
 * @param <S>
 *
 */
public abstract class AbstractAttributesController<T extends PropertyValue,S extends Artifact> implements Serializable{

    @Inject protected BlobStore blobStore;
    @Inject protected TagEJB tagEJB;

    protected Property property;
    protected Value propertyValue;
    protected List<String> enumSelections;
    protected List<Property> filteredProperties;
    private boolean propertyNameChangeDisabled;
    protected PropertyValueUIElement propertyValueUIElement;

    protected String tag;
    protected List<String> tagsForAutocomplete;

    protected String artifactDescription;
    protected boolean isArtifactInternal;
    protected String artifactURI;
    protected boolean isArtifactBeingModified;

    protected List<EntityAttributeView> attributes;
    protected EntityAttributeView selectedAttribute;

    protected byte[] importData;
    protected String importFileName;

    private DAO<? extends ConfigurationEntity> dao;
    private Class<T> propertyValueClass;
    private Class<S> artifactClass;

    protected List<ComptypePropertyValue> parentProperties;
    protected T propertyValueInstance;

    protected void resetFields() {
        property = null;
        propertyValue = null;
        tag = null;
        artifactDescription = null;
        isArtifactInternal = false;
        artifactURI = null;
        importData = null;
        importFileName = null;
        enumSelections = null;
        propertyValueUIElement = PropertyValueUIElement.NONE;
    }

    /**
     * Adds new {@link PropertyValue} to parent {@link ConfigurationEntity} defined in {@link AbstractAttributesController#setPropertyValueParent(PropertyValue)}
     */
    public void addNewPropertyValue() {
        try {
            propertyValueInstance = propertyValueClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        propertyValueInstance.setInRepository(false);
        propertyValueInstance.setProperty(property);
        propertyValueInstance.setPropValue(propertyValue);
        setPropertyValueParent(propertyValueInstance);

        try {
            dao.addChild(propertyValueInstance);

            if (propertyValue == null) {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New property definition has been created");
            } else {
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New property value has been created");
            }
        } finally {
            internalPopulateAttributesList();
        }
    }

    /**
     * Adds new {@link PropertyValue} to parent {@link ConfigurationEntity} defined in {@link AbstractAttributesController#setArtifactParent(Artifact)}
     *
     * @throws IOException thrown if file in the artifact could not be stored on the file system
     */
    public void addNewArtifact() throws IOException {
        if (importData != null) {
            artifactURI = blobStore.storeFile(new ByteArrayInputStream(importData));
        }

        final S artifactInstance;
        try {
            artifactInstance = artifactClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        artifactInstance.setName(importData != null ? importFileName : artifactURI);
        artifactInstance.setInternal(isArtifactInternal);
        artifactInstance.setDescription(artifactDescription);
        artifactInstance.setUri(artifactURI);

        setArtifactParent(artifactInstance);

        try {
            dao.addChild(artifactInstance);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New artifact has been created");
        } finally {
            internalPopulateAttributesList();
        }
    }

    /**
     * Adds new {@link Tag} to parent {@link ConfigurationEntity}
     */
    public void addNewTag() {
        Tag existingTag = new Tag(tag);
        setTagParent(existingTag);
        internalPopulateAttributesList();

        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Tag added", tag);
    }

    /**
     * Deletes attribute from parent {@link ConfigurationEntity}. This attribute can be {@link Tag}, {@link PropertyValue} or {@link Artifact}
     * @throws IOException
     */

    public void deleteAttribute() throws IOException {
        try {
            if (selectedAttribute.getEntity().getClass().equals(propertyValueClass)) {
                deletePropertyValue();
            } else if (selectedAttribute.getEntity().getClass().equals(artifactClass)) {
                deleteArtifact();
            } else if (selectedAttribute.getEntity().getClass().equals(Tag.class)) {
                final Tag tag = (Tag) selectedAttribute.getEntity();
                deleteTagFromParent(tag);
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Tag removed", tag.getName());
                return;
            } else {
                throw new UnhandledCaseException();
            }
        } finally {
            internalPopulateAttributesList();
        }
    }

    @SuppressWarnings("unchecked")
    protected void deletePropertyValue() {
        final T propValue = (T) selectedAttribute.getEntity();
        dao.deleteChild(propValue);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Property value has been deleted");
    }

    @SuppressWarnings("unchecked")
    private void deleteArtifact() throws IOException {
        final S artifact = (S) selectedAttribute.getEntity();
        if (artifact.isInternal()) {
            blobStore.deleteFile(artifact.getUri());
        }
        dao.deleteChild(artifact);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type artifact has been deleted");
    }

    @SuppressWarnings("unchecked")
    protected void prepareModifyPropertyPopUp() {
        if (selectedAttribute.getEntity().getClass().equals(propertyValueClass)) {
            final T selectedPropertyValue = (T) selectedAttribute.getEntity();
            property = selectedPropertyValue.getProperty();
            propertyValue = selectedPropertyValue.getPropValue();

            if (selectedAttribute.getEntity() instanceof PropertyValue) {
                propertyNameChangeDisabled = isInherited((PropertyValue)selectedAttribute.getEntity());
            }

            propertyValueUIElement = Conversion.getUIElementFromProperty(property);
            if (Conversion.getDataType(property) == PropertyDataType.ENUM) {
                // if it is an enumeration, get the list of its options from the data type definition field
                enumSelections = prepareEnumSelections(property);
            } else {
                enumSelections = null;
            }


            RequestContext.getCurrentInstance().update("modifyPropertyValueForm:modifyPropertyValue");
            RequestContext.getCurrentInstance().execute("PF('modifyPropertyValue').show()");
        } else if (selectedAttribute.getEntity().getClass().equals(artifactClass)) {
            final S selectedArtifact = (S) selectedAttribute.getEntity();
            if (selectedArtifact.isInternal()) {
                importFileName = selectedArtifact.getName();
            }
            importData = null;
            artifactDescription = selectedArtifact.getDescription();
            isArtifactInternal = selectedArtifact.isInternal();
            artifactURI = selectedArtifact.getUri();
            isArtifactBeingModified = true;

            RequestContext.getCurrentInstance().update("modifyArtifactForm:modifyArtifact");
            RequestContext.getCurrentInstance().execute("PF('modifyArtifact').show()");
        } else {
            throw new UnhandledCaseException();
        }
    }

    /**
     * Modifies {@link PropertyValue}
     */
    @SuppressWarnings("unchecked")
    public void modifyPropertyValue() {
        final T selectedPropertyValue = (T) selectedAttribute.getEntity();
        selectedPropertyValue.setProperty(property);
        selectedPropertyValue.setPropValue(propertyValue);

        try {
            dao.saveChild(selectedPropertyValue);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Property value has been modified");
        } finally {
            internalPopulateAttributesList();
        }
    }

    /**
     * Modifies {@link Artifact}
     */
    @SuppressWarnings("unchecked")
    public void modifyArtifact() {
        final S selectedArtifact = (S) selectedAttribute.getEntity();
        selectedArtifact.setDescription(artifactDescription);
        selectedArtifact.setUri(artifactURI);
        if (!selectedArtifact.isInternal()) {
            selectedArtifact.setName(artifactURI);
        }

        try {
            dao.saveChild(selectedArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Artifact has been modified");
        } finally {
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
        final S selectedArtifact = (S) selectedAttribute.getEntity();
        final String filePath = blobStore.getBlobStoreRoot() + File.separator + selectedArtifact.getUri();
        final String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(filePath);

        return new DefaultStreamedContent(new FileInputStream(filePath), contentType, selectedArtifact.getName());
    }

    public boolean canDelete(Object attribute) {
        // TODO check whether to show inherited artifacts and prevent their deletion
        return attribute instanceof Artifact || (attribute instanceof PropertyValue && !isInherited((PropertyValue)attribute))
                || attribute instanceof Tag;
    }

    private boolean isInherited(PropertyValue propertyValue) {
        if (parentProperties == null) {
            return false;
        }

        final String propertyName = propertyValue.getProperty().getName();
        for (PropertyValue inheritedPropVal : parentProperties) {
            if (propertyName.equals(inheritedPropVal.getProperty().getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean canEdit(Object attribute) {
        // TODO check whether to show inherited artifacts and prevent their editing
        return attribute instanceof Artifact || attribute instanceof PropertyValue && !(attribute instanceof ComptypePropertyValue) ;
    }

    protected abstract void setPropertyValueParent(T child);

    protected abstract void setArtifactParent(S child);

    protected abstract void setTagParent(Tag tag);

    protected abstract void deleteTagFromParent(Tag tag);

    /**
     * Filters a list of possible properties to attach to the entity based on the association type.
     */
    protected abstract void filterProperties();

    protected abstract void populateAttributesList();

    private void internalPopulateAttributesList() {
        fillTagsAutocomplete();

        populateAttributesList();
    }

    private void fillTagsAutocomplete() {
        tagsForAutocomplete = ImmutableList.copyOf(Lists.transform(tagEJB.findAllSorted(), new Function<Tag, String>() {

            @Override
            public String apply(Tag input) {
                return input.getName();
            }
        }));
    }

    /**
     * Returns list of all attributes for current {@link ConfigurationEntity}
     * @return the list of attributes
     */
    public List<EntityAttributeView> getAttributes() {
        return attributes;
    }

    /**
     * Prepares data for addition of {@link PropertyValue}
     */
    public void prepareForPropertyValueAdd() {
        propertyNameChangeDisabled = false;
        property = null;
        propertyValue = null;
        enumSelections = null;
        propertyValueUIElement = PropertyValueUIElement.NONE;
    }

    public void prepareForTagAdd() {
        fillTagsAutocomplete();
        tag = null;
    }

    /**
     * Prepares data for addition of {@link Artifact}
     */
    public void prepareForArtifactAdd() {
        artifactDescription = null;
        isArtifactInternal = false;
        artifactURI = null;
        isArtifactBeingModified = false;
        importData = null;
        importFileName = null;
    }

    /**
     * Uploads file to be saved in the {@link Artifact}
     * @param event the {@link FileUploadEvent}
     */
    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * If user changes the type of the artifact, any previously uploaded file gets deleted
     */
    public void artifactTypeChanged() {
        importData = null;
        importFileName = null;
    }

    public String getImportFileName() { return importFileName; }

    public void setProperty(Property property) { this.property = property; }
    public Property getProperty() { return property; }

    public void setPropertyValue(String propertyValue) { this.propertyValue = Conversion.stringToValue(propertyValue, property); }
    public String getPropertyValue() { return Conversion.valueToString(propertyValue); }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public List<Property> getFilteredProperties() { return filteredProperties; }

    public String getArtifactDescription() { return artifactDescription; }
    public void setArtifactDescription(String artifactDescription) { this.artifactDescription = artifactDescription; }

    public boolean getIsArtifactInternal() { return isArtifactInternal; }
    public void setIsArtifactInternal(boolean isArtifactInternal) { this.isArtifactInternal = isArtifactInternal; }

    public String getArtifactURI() { return artifactURI; }
    public void setArtifactURI(String artifactURI) { this.artifactURI = artifactURI; }

    public boolean getIsArtifactBeingModified() { return isArtifactBeingModified; }
    public void setIsArtifactBeingModified(boolean isArtifactBeingModified) { this.isArtifactBeingModified = isArtifactBeingModified; }

    public EntityAttributeView getSelectedAttribute() { return selectedAttribute; }
    public void setSelectedAttribute(EntityAttributeView selectedAttribute) { this.selectedAttribute = selectedAttribute; }

    public EntityAttributeView getSelectedAttributeToModify() { return selectedAttribute; }
    public void setSelectedAttributeToModify(EntityAttributeView selectedAttribute) {
        this.selectedAttribute = selectedAttribute;
        prepareModifyPropertyPopUp();
    }

    protected void setDao(DAO<? extends ConfigurationEntity> dao) { this.dao = dao; }

    protected void setPropertyValueClass(Class<T> propertyValueClass) { this.propertyValueClass = propertyValueClass; }

    protected void setArtifactClass(Class<S> artifactClass) { this.artifactClass = artifactClass; }

    public boolean isPropertyNameChangeDisabled() { return propertyNameChangeDisabled; }

    public List<String> tagAutocompleteText(String query) {
        final List<String> resultList = new ArrayList<String>();
        final String queryUpperCase = query.toUpperCase();
        for (String element : tagsForAutocomplete) {
            if (element.toUpperCase().startsWith(queryUpperCase))
                resultList.add(element);
        }

        return resultList;
    }

    private List<String> prepareEnumSelections(Property prop) {
        JsonReader reader = Json.createReader(new StringReader(prop.getDataType().getDefinition()));
        final SedsType seds = Seds.newDBConverter().deserialize(reader.readObject());
        final SedsEnum sedsEnum = (SedsEnum) seds;
        return Arrays.asList(sedsEnum.getElements());
    }

    public void propertyChangeListener(ValueChangeEvent event) {
        // get the newly selected property
        if (event.getNewValue() instanceof Property) {
            final Property newProperty = (Property) event.getNewValue();
            propertyValueUIElement = Conversion.getUIElementFromProperty(newProperty);
            propertyValue = null;
            if (Conversion.getDataType(newProperty) == PropertyDataType.ENUM) {
                // if it is an enumeration, get the list of its options from the data type definition field
                enumSelections = prepareEnumSelections(newProperty);
            } else {
                enumSelections = null;
            }
        }
    }

    public PropertyValueUIElement getPropertyValueUIElement() { return propertyValueUIElement; }
    public void setPropertyValueUIElement(PropertyValueUIElement propertyValueUIElement) { this.propertyValueUIElement = propertyValueUIElement; }

    public List<String> getEnumSelections() { return enumSelections; }
    public void setEnumSelections(List<String> enumSelections) { this.enumSelections = enumSelections; }

    public void areaValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "No value to parse."));
        }
        if (property == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "You must select a property first."));
        }

        String strValue = value.toString();
        switch (Conversion.getDataType(property)) {
        case DBL_TABLE:
            validateTable(strValue);
            break;
        case DBL_VECTOR:
            validateDblVector(strValue);
            break;
        case INT_VECTOR:
            validateIntVector(strValue);
            break;
        case STRING_LIST:
            break;
        default:
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect property data type."));
        }
    }

    private void validateTable(String value) throws ValidatorException {
        try (Scanner lineScanner = new Scanner(value)) {
            lineScanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            int lineLength = -1;
            while (lineScanner.hasNext()) {
                final String line = lineScanner.next().replaceAll("\u00A0", " ");

                try (Scanner valueScanner = new Scanner(line)) {
                    valueScanner.useDelimiter(",\\s*");
                    int currentLineLength = 0;
                    while (valueScanner.hasNext()) {
                        final String dblValue = valueScanner.next().trim();
                        currentLineLength++;
                        try {
                            Double.valueOf(dblValue);
                        } catch (NumberFormatException e) {
                            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                    "Incorrect value: " + dblValue));
                        }
                    }
                    if (lineLength < 0) {
                        lineLength = currentLineLength;
                    } else if (currentLineLength != lineLength) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "All rows must contain the same number of elements."));
                    }
                }
            }
        }
    }

    private void validateIntVector(String value) throws ValidatorException {
        try (Scanner scanner = new Scanner(value)) {
            scanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                String intValue = "<error>";
                try {
                    intValue = scanner.next().replaceAll("\\u00A0", " ").trim();
                    Integer.parseInt(intValue);
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect value: " + intValue));
                }
            }
        }
    }

    private void validateDblVector(String value) throws ValidatorException {
        try (Scanner scanner = new Scanner(value)) {
            scanner.useDelimiter(Pattern.compile("(\\r\\n)|\\r|\\n"));

            // replace unicode no-break spaces with normal ones
            while (scanner.hasNext()) {
                String dblValue = "<error>";
                try {
                    dblValue = scanner.next().replaceAll("\\u00A0", " ").trim();
                    Double.parseDouble(dblValue);
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect value: " + dblValue));
                }
            }
        }
    }

    public void inputValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "No value to parse."));
        }
        if (property == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "You must select a property first."));
        }

        String strValue = value.toString();
        switch (Conversion.getDataType(property)) {
        case DOUBLE:
            try {
                Double.parseDouble(strValue.trim());
            } catch (NumberFormatException e) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not a double value."));
            }
            break;
        case INTEGER:
            try {
                Integer.parseInt(strValue.trim());
            } catch (NumberFormatException e) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not an integer number."));
            }
            break;
        case STRING:
            break;
        case URL:
            validateUrl(strValue);
            break;
        case TIMESTAMP:
            try {
                Conversion.toTimestamp(strValue);
            } catch (RuntimeException e) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
            break;
        default:
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect property data type."));
        }
    }

    private void validateUrl(String value) throws ValidatorException {
        try {
            final URL retUrl = new URL(value);
            if (!retUrl.getProtocol().startsWith("http") && !retUrl.getProtocol().equals("ftp")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error",
                        "Protocol must be either http, https or ftp."));
            }
        } catch (MalformedURLException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error",
                    "The input cannot be parsed into URL."));
        }
    }
}
