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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.util.BlobStore;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import com.google.common.io.ByteStreams;

/**
 * Parent class for all classes that handle entity attributes manipulation
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@ViewScoped
public abstract class AbstractAttributesController implements Serializable {

    @Inject protected BlobStore blobStore;

    protected Property property;
    protected String propertyValue;
    protected List<Property> filteredProperties;

    protected String tag;

    protected String artifactDescription;
    protected boolean isArtifactInternal;
    protected String artifactURI;
    protected boolean isArtifactBeingModified;

    protected List<EntityAttributeView> attributes;
    protected EntityAttributeView selectedAttribute;

    protected byte[] importData;
    protected String importFileName;

    protected void resetFields() {
        property = null;
        propertyValue = null;
        tag = null;
        artifactDescription = null;
        isArtifactInternal = false;
        artifactURI = null;
        importData = null;
        importFileName = null;
    }

    protected abstract void filterProperties();

    protected abstract void populateAttributesList();

    public abstract void addNewPropertyValue();

    public abstract void addNewTag();

    public abstract void addNewArtifact() throws IOException;

    public abstract void deleteAttribute();

    protected abstract void preparedModifyPropertyPopUp();

    public abstract void modifyPropertyValue();

    public abstract void modifyArtifact();

    public List<EntityAttributeView> getAttributes() {
        return attributes;
    }

    public void prepareForPropertyValueAdd() {
        property = null;
        propertyValue = null;
    }

    public void prepareForArtifactAdd() {
        artifactDescription = null;
        isArtifactInternal = false;
        artifactURI = null;
        isArtifactBeingModified = false;
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

    public void artifactTypeChanged() {
        importData = null;
        importFileName = null;
    }


    public abstract StreamedContent getDownloadFile() throws FileNotFoundException;

    public String getImportFileName() { return importFileName; }

    public void setProperty(Property property) { this.property = property; }
    public Property getProperty() { return property; }

    public void setPropertyValue(String propertyValue) { this.propertyValue = propertyValue; }
    public String getPropertyValue() { return propertyValue; }

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
        preparedModifyPropertyPopUp();
    }
}
